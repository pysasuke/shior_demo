package com.py.service.impl;

import com.py.dao.ShiroUserMapper;
import com.py.dto.ShiroUserDTO;
import com.py.entity.ShiroUser;
import com.py.service.ShiroUserService;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;

/**
 * Created by pysasuke on 2017/8/21.
 */
@Service("shiroUserService")
public class ShiroUserServiceImpl implements ShiroUserService {
    @Autowired
    private ShiroUserMapper shiroUserMapper;

    public ShiroUser getByUsername(String username) {
        return shiroUserMapper.getByUsername(username);
    }

    //通过表单注册账号
    @Override
    public void insertUser(ShiroUserDTO shiroUserDTO) {
        ShiroUser shiroUser = converToAddress(shiroUserDTO);
        shiroUserMapper.insert(shiroUser);
    }
    @Override
    public Set<String> getRoles(String username) {
        return shiroUserMapper.getRoles(username);
    }


    @Override
    public Set<String> getPermissions(String username) {
        return shiroUserMapper.getPermissions(username);
    }

    private ShiroUser converToAddress(ShiroUserDTO shiroUserDTO) {
        ShiroUser shiroUser = new ShiroUser();
        BeanUtils.copyProperties(shiroUserDTO, shiroUser);
        passwordEncrypt(shiroUser);
        shiroUser.setCreatetime(new Date());
        shiroUser.setRoleId(1);
        return shiroUser;
    }

    private void passwordEncrypt(ShiroUser shiroUser) {
        String username = shiroUser.getUsername();
        String password = shiroUser.getPassword();
        String salt2 = new SecureRandomNumberGenerator().nextBytes().toHex();
        int hashIterations = 3;
        String algorithmName = "md5";
        SimpleHash hash = new SimpleHash(algorithmName, password,
                username + salt2, hashIterations);
        String encodedPassword = hash.toHex();
        shiroUser.setSalt(salt2);
        shiroUser.setPassword(encodedPassword);
    }
}
