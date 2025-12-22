package com.yx.platform.controller;

import com.yx.platform.entity.CartItemVo;
import com.yx.platform.entity.SysUser;
import com.yx.platform.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    // 辅助方法：获取当前登录用户ID
    private Long getUserId(HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        return user != null ? user.getId() : null;
    }

    // 1. 查看购物车页面
    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        Long userId = getUserId(session);
        if (userId == null) return "redirect:/login"; // 没登录就去登录

        List<CartItemVo> cartItems = cartService.getMyCart(userId);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", cartService.calculateTotal(cartItems));

        return "cart";
    }

    // 2. 添加商品到购物车
    @PostMapping("/cart/add")
    // 【修改点】去掉 int quantity 参数，或者保留但不用它
    public String addToCart(Long productId, HttpSession session) {
        Long userId = getUserId(session);
        if (userId == null) return "redirect:/login";

        // 调用 Service 时也不传 quantity，默认内部处理为 1
        cartService.addToCart(userId, productId);

        return "redirect:/cart";
    }

    // 3. 删除购物车商品
    @GetMapping("/cart/remove")
    public String remove(Long id) {
        cartService.removeItem(id);
        return "redirect:/cart";
    }

    // 4. 结算下单
    @GetMapping("/order/confirm")
    public String checkout(HttpSession session, Model model) {
        Long userId = getUserId(session);
        if (userId == null) return "redirect:/login";

        try {
            boolean success = cartService.checkout(userId);
            if (success) {
                return "redirect:/"; // 下单成功回首页，或者去订单列表页
            } else {
                model.addAttribute("error", "购物车为空或库存不足");
                return "cart";
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "cart";
        }
    }
}