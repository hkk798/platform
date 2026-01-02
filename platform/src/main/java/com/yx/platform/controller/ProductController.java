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
import java.util.Map;
import com.yx.platform.mapper.UserMapper;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService; // 改用 Service，不再直接调 Mapper
    @Autowired
    private UserMapper userMapper;

    // === 1. 首页：展示 Top 10 CCU 和 Top 10 Reviews ===
    @GetMapping("/")
    public String index(Model model) {
        // --- 新增：获取轮播图数据 ---
        List<Product> carouselList = productService.getCarouselProducts();
        model.addAttribute("carouselList", carouselList);
        // ------------------------

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
            @RequestParam(defaultValue = "1") int page, // 新增：页码
            Model model) {

        int pageSize = 20; // 每页显示 20 条

        // 调用业务层处理复杂的搜索筛选逻辑
        Map<String, Object> result = productService.searchAndFilter(keyword, genre, page, pageSize);

        // 将结果拆包放入 Model
        model.addAttribute("products", result.get("products"));
        model.addAttribute("currentPage", result.get("pageNum"));
        model.addAttribute("totalPages", result.get("totalPages"));
        model.addAttribute("hasPrevious", result.get("hasPrevious"));
        model.addAttribute("hasNext", result.get("hasNext"));

        // 回显查询条件
        model.addAttribute("keyword", keyword);
        model.addAttribute("platform", platform);
        model.addAttribute("genre", genre);


        return "index";
    }

    // === 修改 product/{id} 方法 ===
    @GetMapping("/product/{id}")
    public String detail(@PathVariable Long id, Model model, HttpSession session) { // 增加 HttpSession 参数
        // 1. 查商品详情
        Product product = productService.getProductDetail(id);
        model.addAttribute("product", product);

        // 2. 【新增】检查用户是否已拥有
        boolean isOwned = false;
        SysUser currentUser = (SysUser) session.getAttribute("currentUser");

        if (currentUser != null) {
            isOwned = productService.checkOwnership(currentUser.getId(), id);
        }

        // 3. 将状态传给前端
        model.addAttribute("isOwned", isOwned);

        return "detail";
    }

    // === 4. 购买商品接口 ===
    // 对应 detail.html 里的表单提交：action="/product/buy"
// === 修改后的购买接口 ===
    @PostMapping("/product/buy")
    public String buy(@RequestParam Long productId, @RequestParam int quantity, HttpSession session, Model model) {
        SysUser currentUser = (SysUser) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        try {
            boolean success = productService.buyProduct(currentUser.getId(), productId, quantity);

            if (success) {
                // === 【新增】刷新 Session 中的余额 ===
                SysUser updatedUser = userMapper.findById(currentUser.getId());
                session.setAttribute("currentUser", updatedUser);
                // ==================================

                return "redirect:/";
            } else {
                return "redirect:/product/" + productId;
            }
        } catch (Exception e) {
            // 如果详情页购买失败（余额不足），通常需要传错误信息回去
            // 简单处理：跳回详情页，这里先打印日志
            System.err.println("购买失败: " + e.getMessage());
            return "redirect:/product/" + productId + "?error=balance"; // 可以在详情页处理这个参数
        }
    }




}