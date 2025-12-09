package com.yx.platform.controller;

import com.yx.platform.entity.Product;
import com.yx.platform.service.ProductService; // 引入 Service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService; // 改用 Service，不再直接调 Mapper

    // === 1. 首页：展示所有商品 + 推荐商品 ===
    @GetMapping("/")
    public String index(Model model) {
        // 获取主列表
        List<Product> products = productService.getHomepageProducts();
        model.addAttribute("products", products);

        // 获取推荐列表 (实现报告中的“首页推荐”)
        List<Product> recommended = productService.getRecommendedProducts();
        model.addAttribute("recommended", recommended);

        return "index";
    }

    // === 2. 搜索 & 筛选接口 ===
    @GetMapping("/search")
    public String search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String platform,
            @RequestParam(required = false) String genre,
            Model model) {

        // 调用业务层处理复杂的搜索筛选逻辑
        List<Product> products = productService.searchAndFilter(keyword, platform, genre);

        model.addAttribute("products", products);

        // 回显查询条件
        model.addAttribute("keyword", keyword);
        model.addAttribute("platform", platform);
        model.addAttribute("genre", genre);

        return "index";
    }

    // ... 详情页和购买接口也可以改为调用 productService ...
}