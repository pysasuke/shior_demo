package com.py.dao;

import com.py.entity.ShiroUser;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * Created by pysasuke on 2017/8/21.
 */
public interface ShiroUserMapper {
    ShiroUser getByUsername(@Param("username") String username);

    Set<String> getRoles(@Param("username") String username);

    Set<String> getPermissions(@Param("username") String username);

    int insert(ShiroUser shiroUser);
}
