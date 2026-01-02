package com.yx.platform.controller;

import com.yx.platform.entity.Product;
import com.yx.platform.entity.SysUser;
import com.yx.platform.mapper.ProductMapper;
import com.yx.platform.mapper.UserMapper;
import com.yx.platform.service.ProductService;
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

    @Autowired
    private ProductService productService;


    // === 注册接口 ===
    @PostMapping("/register1")
    // 2. 去掉 @RequestBody，直接用 SysUser 接收表单数据
    public String register1(SysUser user) {
        // 保存到数据库
        userMapper.save(user);

        // 3. 注册成功后，重定向跳转到登录页面
        return "redirect:/login1";
    }

    // === 登录接口 ===
    @PostMapping("/login1")
    public String login1(String username, String password, HttpSession session) {
        SysUser user = userMapper.login1(username, password);
        if (user != null) {
            session.setAttribute("currentUser", user);

            // === 【修改】判断角色 ===
            if ("admin".equals(user.getRole())) {
                return "redirect:/admin/products"; // 管理员去后台
            } else {
                return "redirect:/"; // 普通用户去首页
            }
        } else {
            return "redirect:/login1?error=true";
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
        return "redirect:/login1?msg=logout";
    }

    // === 2. 修改密码页面 ===
    @GetMapping("/password1")
    public String passwordPage(HttpSession session, Model model) {
        if (session.getAttribute("currentUser") == null) {
            return "redirect:/login1";
        }
        return "password1"; // 跳转到 password.html
    }

    // === 3. 处理修改密码逻辑 ===
    @PostMapping("/password1/update")
    public String updatePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session,
            Model model) {

        SysUser currentUser = (SysUser) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login1";
        }

        // 1. 校验两次新密码是否一致
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "两次输入的新密码不一致！");
            return "password1";
        }

        // 2. 校验旧密码是否正确 (从数据库查最新数据比对)
        SysUser userInDb = userMapper.findById(currentUser.getId());
        if (!userInDb.getPassword().equals(oldPassword)) {
            model.addAttribute("error", "旧密码错误！");
            return "password1";
        }

        // 3.校验旧密码与新密码是否一致
        if(newPassword.equals(oldPassword)) {
            model.addAttribute("error", "新密码不能与旧密码相同！");
            return "password1";
        }

        // 3. 执行更新
        userMapper.updatePassword(currentUser.getId(), newPassword);

        // 4. 更新 Session 中的用户信息 或 强制登出让用户重新登录
        session.removeAttribute("currentUser");
        model.addAttribute("msg", "密码修改成功，请使用新密码重新登录");

        return "login1";
    }


    @GetMapping("/my-games")
    public String myGames(HttpSession session, Model model) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login1";
        }

        // 查询该用户买过的游戏
        List<Product> myGames = productMapper.findPurchasedByUserId(user.getId());
        model.addAttribute("products", myGames);

        return "my_games"; // 跳转到 my_games.html
    }


    @PostMapping("/refund")
    public String refund(@RequestParam Long productId, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login1";
        }

        try {
            boolean success = productService.refundProduct(user.getId(), productId);
            if (success) {
                // 退款成功，需要从数据库重新查用户，以刷新 Session 里的余额显示
                SysUser updatedUser = userMapper.findById(user.getId());
                session.setAttribute("currentUser", updatedUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 实际开发中可以将错误信息放入 Model 返回给页面
        }

        // 退款完成后刷新“我的游戏”页面
        return "redirect:/my-games";
    }


    // 1. 显示充值页面
    @GetMapping("/recharge")
    public String rechargePage(HttpSession session) {
        if (session.getAttribute("currentUser") == null) {
            return "redirect:/login1";
        }
        return "recharge";
    }

    // 2. 处理充值逻辑
    @PostMapping("/recharge")
    public String doRecharge(@RequestParam java.math.BigDecimal amount, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login1";
        }

        // 简单校验：金额必须大于0
        if (amount.compareTo(java.math.BigDecimal.ZERO) > 0) {
            // 1. 数据库加钱
            userMapper.addBalance(user.getId(), amount);

            // 2. 刷新 Session 里的余额显示（否则页面上看起来还是旧的）
            SysUser updatedUser = userMapper.findById(user.getId());
            session.setAttribute("currentUser", updatedUser);
        }

        // 充值完跳回首页
        return "redirect:/";
    }
}