package com.yx.platform.controller;

import com.yx.platform.entity.SysUser;
import com.yx.platform.mapper.UserMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller; // 注意这里换成了 Controller
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody; // 新增这个

import java.util.List;

@Controller // 1. 改为 @Controller，这样才能实现页面跳转
public class UserController {

    @Autowired
    private UserMapper userMapper;

    // === 注册接口 ===
    @PostMapping("/register")
    // 2. 去掉 @RequestBody，直接用 SysUser 接收表单数据
    public String register(SysUser user) {
        // 保存到数据库
        userMapper.save(user);

        // 3. 注册成功后，重定向跳转到登录页面
        return "redirect:/login";
    }

    // === 登录接口 ===
    @PostMapping("/login")
    public String login(String username, String password, HttpSession session) {
        SysUser user = userMapper.login(username, password);
        if (user != null) {
            session.setAttribute("currentUser", user);
            // 登录成功，跳回首页
            return "redirect:/";
        } else {
            // 登录失败，跳回登录页并带上错误标记
            return "redirect:/login?error=true";
        }
    }

    // === (可选)原来的测试接口，如果你还想看 JSON 数据，就加上 @ResponseBody ===
    @GetMapping("/user")
    @ResponseBody
    public List<SysUser> list() {
        return userMapper.findAll();
    }

    // === 1. 退出登录 ===
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // 销毁 session，相当于把用户踢下线
        session.invalidate();
        // 跳回首页
        return "redirect:/";
    }

    // === 2. 跳转到修改密码页面 ===
    @GetMapping("/user/password")
    public String passwordPage(HttpSession session) {
        // 如果没登录，先去登录
        if (session.getAttribute("currentUser") == null) {
            return "redirect:/login";
        }
        return "password"; // 对应 templates/password.html
    }

    // === 3. 执行修改密码 ===
    @PostMapping("/user/password")
    public String updatePassword(String oldPassword, String newPassword, HttpSession session, org.springframework.ui.Model model) {
        SysUser currentUser = (SysUser) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        // 1. 验证旧密码是否正确（为了安全，建议查库对比）
        // 这里简单直接比对当前 session 里的或者重新查库
        SysUser userInDb = userMapper.login(currentUser.getUsername(), oldPassword);

        if (userInDb == null) {
            model.addAttribute("error", "旧密码错误！");
            return "password";
        }

        // 2. 更新密码
        userMapper.updatePassword(currentUser.getId(), newPassword);

        // 3. 修改成功后，强制退出，让用户重新登录
        session.invalidate();
        return "redirect:/login?msg=passwordChanged";
    }
}