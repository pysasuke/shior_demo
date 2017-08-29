package com.py.entity;

import lombok.Data;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;


/**
 * Created by pysasuke on 2017/8/21.
 */
@Data
public class ShiroUser {
    private Integer id;

    @NotNull(message = "用户名不能为空")
    @Size(min = 3, max = 16, message = "用户名长度必须介于3-16个字符之间")
    private String username;

    @NotNull(message = "密码不能为空")
    @Size(min = 3, max = 16, message = "{密码长度必须介于3-16个字符之间")
    private String password;

    private Date createtime;

    private Date lasttime;

    @Email(message = "请输入正确的邮箱")
    private String email;

    private String sex;

    private String salt;

    private Integer roleId;


}
