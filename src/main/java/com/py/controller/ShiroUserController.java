package com.py.controller;

import com.py.dto.ShiroUserDTO;
import com.py.entity.ShiroUser;
import com.py.service.ShiroUserService;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * Created by pysasuke on 2017/8/21.
 */
@Controller
@RequestMapping("/user")
public class ShiroUserController {
    private final static Logger log = Logger.getLogger(ShiroUserController.class);
    @Autowired
    private ShiroUserService shiroUserService;

    /**
     * 用户登录
     *
     * @param shiroUser
     * @param request
     * @return
     */
    @RequestMapping("/login")
    public String login(ShiroUser shiroUser, HttpServletRequest request) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(shiroUser.getUsername(), shiroUser.getPassword());
        try {
            subject.login(token);//会跳到我们自定义的realm中
            request.getSession().setAttribute("user", shiroUser);
            log.info(shiroUser.getUsername() + "登录");
            return "user/success";
        } catch (UnknownAccountException e) {
            return "user/login";
        } catch (IncorrectCredentialsException e) {
            request.setAttribute("error", "用户名或密码错误");
            return "user/login";
        } catch (ExcessiveAttemptsException e) {
            request.setAttribute("error", "输入密码错误太多次,请稍后再试！");
            return "user/login";
        } catch (Exception e) {
            request.setAttribute("error", "未知错误");
            return "user/login";
        }
    }

    /**
     * 主页跳转
     *
     * @return
     */
    @RequestMapping("/index")
    public String logout() {
        return "index";
    }

    /**
     * 未授权页跳转
     *
     * @return
     */
    @RequestMapping("/unauthorized")
    public String unauthorized() {
        return "unauthorized";
    }

    /**
     * 注册页面跳转
     *
     * @return
     */
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registerView() {
        return "user/register";
    }

    /**
     * 用户注册
     *
     * @param model
     * @param shiroUserDTO
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(Model model,
                           @Valid @ModelAttribute ShiroUserDTO shiroUserDTO, BindingResult bindingResult) {
        //数据校验
        if (bindingResult.hasErrors()) {
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            for (ObjectError objectError : allErrors) {
                //输出错误信息
                System.out.println(objectError.getDefaultMessage());
            }
            model.addAttribute("error", "填入信息有误");
            model.addAttribute("user", shiroUserDTO);
            return "/user/register";
        }
        if (shiroUserService.getByUsername(shiroUserDTO.getUsername()) == null) {
            shiroUserService.insertUser(shiroUserDTO);
            return "redirect:/";
        } else {
            model.addAttribute("user", shiroUserDTO);
            model.addAttribute("error", "userName has been registered!");
            return "/user/register";
        }
    }
}
