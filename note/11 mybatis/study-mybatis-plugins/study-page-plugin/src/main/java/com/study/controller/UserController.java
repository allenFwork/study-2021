package com.study.controller;

import com.study.service.UserService;
import com.study.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/selectAll")
    public R selectAll() {
        return userService.getUser();
    }

    @RequestMapping("/selectAllByPage")
    public R selectAlByPage() {
        return userService.getUserByPage(2, 2);
    }

}
