package com.yx.platform.controller;

import com.yx.platform.entity.Product;
import com.yx.platform.entity.SysUser;
import com.yx.platform.service.ProductService; // 引入 Service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService; // 改用 Service，不再直接调 Mapper

    // === 1. 首页：展示 Top 10 CCU 和 Top 10 Reviews ===
    @GetMapping("/")
    public String index(Model model) {
        // 1. 获取在线人数(CCU)最高的 10 个游戏
        List<Product> topCcuList = productService.getTopCcuProducts();
        model.addAttribute("topCcuList", topCcuList);

        // 2. 获取评论数最多的 10 个游戏
        List<Product> topReviewList = productService.getMostReviewedProducts();
        model.addAttribute("topReviewList", topReviewList);

        return "index";
    }

    // === 2. 搜索 & 筛选接口 ===
    @GetMapping("/search")
    public String search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String platform,
            @RequestParam(required = false) List<String> genre,
            Model model) {

        // 调用业务层处理复杂的搜索筛选逻辑
        List<Product> products = productService.searchAndFilter(keyword , genre);

        model.addAttribute("products", products);

        // 回显查询条件
        model.addAttribute("keyword", keyword);
        model.addAttribute("platform", platform);
        model.addAttribute("genre", genre);

        return "index";
    }

    // ... 详情页和购买接口也可以改为调用 productService ...
    @GetMapping("/product/{id}")
    public String detail(@PathVariable Long id, Model model) {
        // 1. 调用 Service 查出商品
        Product product = productService.getProductDetail(id);

        // 2. 放入 Model 给页面用
        model.addAttribute("product", product);

        // 3. 跳转到 detail.html
        return "detail";
    }

    // === 4. 购买商品接口 ===
    // 对应 detail.html 里的表单提交：action="/product/buy"
    @PostMapping("/product/buy")
    public String buy(@RequestParam Long productId, @RequestParam int quantity) {
        // 调用业务层执行购买（扣库存）
        boolean success = productService.buyProduct(productId, quantity);

        if (success) {
            // 购买成功，重定向回首页（或者去订单页，如果以后做的话）
            return "redirect:/";
        } else {
            // 购买失败（比如库存不够），重定向回详情页，或者报错
            // 这里简单处理：跳回详情页
            return "redirect:/product/" + productId;
        }
    }

}