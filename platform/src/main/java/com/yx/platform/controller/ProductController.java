package com.yx.platform.controller;

import com.yx.platform.entity.Product;
import com.yx.platform.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller; // 注意这里变了
import org.springframework.ui.Model;            // 引入 Model
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品页面控制器
 * 负责接收请求并返回 HTML 页面
 */
@Controller
public class ProductController {

    @Autowired
    private ProductMapper productMapper;

    // === 1. 首页：展示所有商品 ===
    @GetMapping("/")
    public String index(Model model) {
        // 查询所有商品
        List<Product> products = productMapper.findAll();
        // 把数据放入模型，前端可以用 ${products} 获取
        model.addAttribute("products", products);
        return "index"; // 跳转到 templates/index.html
    }

    // === 2. 搜索 & 筛选接口 ===
    // 这里的逻辑稍微合并了一下，支持 "只搜索"、"只筛选" 或 "既不搜索也不筛选"
    @GetMapping("/search")
    public String search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String platform,
            @RequestParam(required = false) String genre,
            Model model) {

        List<Product> products;

        if (keyword != null && !keyword.isEmpty()) {
            // 如果有关键词，优先走搜索
            products = productMapper.searchByName(keyword);
        } else if ((platform != null && !platform.isEmpty()) || (genre != null && !genre.isEmpty())) {
            // 如果有筛选条件，走筛选
            products = productMapper.filter(platform, genre);
        } else {
            // 啥都没填，查所有
            products = productMapper.findAll();
        }

        model.addAttribute("products", products);
        // 回显查询条件，让输入框里保留刚才填的值
        model.addAttribute("keyword", keyword);
        model.addAttribute("platform", platform);
        model.addAttribute("genre", genre);

        return "index"; // 搜索完还是留在首页
    }

    // === 3. 商品详情页 ===
    @GetMapping("/product/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Product product = productMapper.findById(id);
        model.addAttribute("product", product);
        return "detail"; // 跳转到 templates/detail.html
    }

    // === 4. 购买逻辑 (API) ===
    // 这个接口保留返回 JSON/字符串，因为是提交表单操作
    @PostMapping("/product/buy")
    @ResponseBody // 这里加这个注解，表示返回纯文本，不跳转页面
    public String buy(Long productId, Integer quantity) {
        if (quantity == null || quantity <= 0) quantity = 1;
        int result = productMapper.reduceStock(productId, quantity);
        if (result > 0) {
            return "购买成功！请返回上一页。";
        } else {
            return "购买失败！库存不足。";
        }
    }
}