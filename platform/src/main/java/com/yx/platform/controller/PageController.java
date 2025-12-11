package com.yx.platform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // 只保留跳转登录页
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // 只保留跳转注册页
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    // ❌ 删除 cartPage 方法 (已在 CartController 中实现)
    // ❌ 删除 doLogin 方法 (已在 UserController 中实现)
    // ❌ 删除 doRegister 方法 (已在 UserController 中实现)
}