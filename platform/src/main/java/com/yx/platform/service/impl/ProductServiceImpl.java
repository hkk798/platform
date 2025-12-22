package com.yx.platform.service.impl;

import com.yx.platform.entity.Product;
import com.yx.platform.mapper.ProductMapper;
import com.yx.platform.mapper.OrderMapper;
import com.yx.platform.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDate; // 别忘了导入这个
import java.util.List;

@Service // 标记为服务组件，交给 Spring 管理
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private OrderMapper orderMapper;

    @Override
    public List<Product> getHomepageProducts() {
        // 默认逻辑：查询所有商品
        return productMapper.findAll();
    }

    @Override
    public List<Product> searchAndFilter(String keyword, List<String> genres) {
        // 直接透传列表给 Mapper
        return productMapper.Filter(keyword, genres);
    }


    // === 新增实现 ===
    @Override
    public List<Product> getTopCcuProducts() {
        return productMapper.findTop10ByPeakCcu();
    }

    @Override
    public List<Product> getMostReviewedProducts() {
        return productMapper.findTop10ByReviews();
    }
    // === 结束新增 ===


    @Override
    public List<Product> getRecommendedProducts() {
        // 业务逻辑：调用 Mapper 查询最新的 3 款游戏作为推荐
        return productMapper.findNewArrivals();
    }

    @Override
    public Product getProductDetail(Long id) {
        return productMapper.findById(id);
    }

    @Override
    public boolean buyProduct(Long productId, int quantity) {
        if (quantity <= 0) quantity = 1;
        // 执行扣库存
        int rows = productMapper.reduceStock(productId, quantity);
        // 如果影响行数 > 0，说明购买成功
        return rows > 0;
    }

    // === 【新增】实现检查逻辑 ===
    @Override
    public boolean checkOwnership(Long userId, Long productId) {
        if (userId == null || productId == null) {
            return false;
        }
        // 如果查询到的数量大于 0，说明买过
        return orderMapper.countUserPurchased(userId, productId) > 0;
    }
}