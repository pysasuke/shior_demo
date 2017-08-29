package com.py.realm;

import com.py.entity.ShiroUser;
import com.py.service.ShiroUserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import javax.annotation.Resource;

/**
 * Created by pysasuke on 2017/8/21.
 */
public class MyRealm extends AuthorizingRealm {

    @Resource
    private ShiroUserService shiroUserService;

    // 为当前登陆成功的用户授予权限和角色，已经登陆成功了
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(
            PrincipalCollection principals) {
        String username = (String) principals.getPrimaryPrincipal(); //获取用户名
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.setRoles(shiroUserService.getRoles(username));
        authorizationInfo.setStringPermissions(shiroUserService.getPermissions(username));
        return authorizationInfo;
    }

    // 验证当前登录的用户，获取认证信息
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            AuthenticationToken token) throws AuthenticationException {
        String username = (String) token.getPrincipal(); // 获取用户名
        ShiroUser shiroUser = shiroUserService.getByUsername(username);
        if (shiroUser != null) {
            SimpleAuthenticationInfo authcInfo = new SimpleAuthenticationInfo(shiroUser.getUsername(), shiroUser.getPassword(), "myRealm");
            //通过SimpleAuthenticationInfo的credentialsSalt设置盐，HashedCredentialsMatcher会自动识别这个盐。
            authcInfo.setCredentialsSalt(ByteSource.Util.bytes(shiroUser.getUsername() + shiroUser.getSalt()));
            return authcInfo;
        } else {
            return null;
        }
    }
}