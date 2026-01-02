package com.yx.platform.controller;

import com.yx.platform.entity.Product;
import com.yx.platform.entity.SysUser;
import com.yx.platform.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductService productService;

    // 拦截器简易版：检查是否是管理员
    private boolean isAdmin(HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        return user != null && "admin".equals(user.getRole());
    }

    // 1. 商品管理列表页
    @GetMapping("/products")
    public String productList(@RequestParam(defaultValue = "1") int page, HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login1";

        Map<String, Object> result = productService.getAdminProductList(page, 20);
        model.addAllAttributes(result);

        return "admin_products"; // 对应下面新建的 HTML
    }

    // 2. 切换上下架状态
    @GetMapping("/product/status")
    public String toggleStatus(Long id, Integer status, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login1";

        productService.updateProductStatus(id, status);
        return "redirect:/admin/products"; // 操作完刷新列表
    }
}