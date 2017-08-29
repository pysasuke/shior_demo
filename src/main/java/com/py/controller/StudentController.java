package com.py.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by pysasuke on 2017/8/28.
 */
@Controller
@RequestMapping("/student")
public class StudentController {
    @RequestMapping("/welcome")
    public String student() {
        return "student/success";
    }
}
