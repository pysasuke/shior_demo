package com.py.credentials;

import com.py.constants.Constants;
import com.py.tool.RedisCache;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 处理重试次数
 * <p>User: Zhang Kaitao
 * <p>Date: 14-1-28
 * <p>Version: 1.0
 */
public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {

    @Autowired
    private RedisCache redisCache;


    //匹配用户输入的token的凭证（未加密）与系统提供的凭证（已加密）
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        String username = (String) token.getPrincipal();
        //retry count + 1
        //AtomicInteger是一个提供原子操作的Integer类，通过线程安全的方式操作加减。
        AtomicInteger retryCount = redisCache.getCache(Constants.USER + username, AtomicInteger.class);
        if (retryCount == null) {
            retryCount = new AtomicInteger(0);
        }
        //增长
        if (retryCount.incrementAndGet() > 5) {
            //if retry count > 5 throw
            throw new ExcessiveAttemptsException();
        }
        redisCache.putCacheWithExpireTime(Constants.USER + username, retryCount, 600);
        boolean matches = super.doCredentialsMatch(token, info);
        if (matches) {
            //clear retry count
            redisCache.deleteCache(Constants.USER + username);
        }
        return matches;
    }
}
