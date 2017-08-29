package com.py.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by pysasuke on 2017/8/28.
 */
@Controller
@RequestMapping("/teacher")
public class TeacherController {
    //@RequiresPermissions注解的方法时，会先执行ShiroDbRealm.doGetAuthorizationInfo()进行授权。
    //会xml中filterChainDefinitions的配置一起起作用(两者都要满足)
    @RequiresPermissions("user:update")
    @RequestMapping("/welcome")
    public String teacher() {
        return "teacher/success";
    }
}
