package com.yx.platform.controller;

import com.yx.platform.entity.Product;
import com.yx.platform.entity.SysUser;
import com.yx.platform.mapper.ProductMapper;
import com.yx.platform.mapper.UserMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller; // 注意这里换成了 Controller
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody; // 新增这个

import java.util.List;

@Controller // 1. 改为 @Controller，这样才能实现页面跳转
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProductMapper productMapper;


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
        return "redirect:/login?msg=logout";
    }

    // === 2. 修改密码页面 ===
    @GetMapping("/password")
    public String passwordPage(HttpSession session, Model model) {
        if (session.getAttribute("currentUser") == null) {
            return "redirect:/login";
        }
        return "password"; // 跳转到 password.html
    }

    // === 3. 处理修改密码逻辑 ===
    @PostMapping("/password/update")
    public String updatePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session,
            Model model) {

        SysUser currentUser = (SysUser) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        // 1. 校验两次新密码是否一致
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "两次输入的新密码不一致！");
            return "password";
        }

        // 2. 校验旧密码是否正确 (从数据库查最新数据比对)
        SysUser userInDb = userMapper.findById(currentUser.getId());
        if (!userInDb.getPassword().equals(oldPassword)) {
            model.addAttribute("error", "旧密码错误！");
            return "password";
        }

        // 3. 执行更新
        userMapper.updatePassword(currentUser.getId(), newPassword);

        // 4. 更新 Session 中的用户信息 或 强制登出让用户重新登录
        session.removeAttribute("currentUser");
        model.addAttribute("msg", "密码修改成功，请使用新密码重新登录");

        return "login";
    }


    @GetMapping("/my-games")
    public String myGames(HttpSession session, Model model) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        // 查询该用户买过的游戏
        List<Product> myGames = productMapper.findPurchasedByUserId(user.getId());
        model.addAttribute("products", myGames);

        return "my_games"; // 跳转到 my_games.html
    }
}