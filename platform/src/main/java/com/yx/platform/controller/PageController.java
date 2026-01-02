package com.yx.platform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // 只保留跳转登录页
    @GetMapping("/login1")
    public String login1Page() {
        return "login1";
    }

    // 只保留跳转注册页
    @GetMapping("/register1")
    public String register1Page() {
        return "register1";
    }

    // ❌ 删除 cartPage 方法 (已在 CartController 中实现)
    // ❌ 删除 dologin1 方法 (已在 UserController 中实现)
    // ❌ 删除 doregister1 方法 (已在 UserController 中实现)
}