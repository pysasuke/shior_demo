package com.py.service;

import com.py.dto.ShiroUserDTO;
import com.py.entity.ShiroUser;

import java.util.Set;

/**
 * Created by pysasuke on 2017/8/21.
 */
public interface ShiroUserService {
    ShiroUser getByUsername(String username);

    //注册账号
    void insertUser(ShiroUserDTO shiroUserDTO);

    Set<String> getRoles(String username);

    Set<String> getPermissions(String username);

}
