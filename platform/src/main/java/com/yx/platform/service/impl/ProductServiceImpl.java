package com.yx.platform.service.impl;

import com.yx.platform.entity.Order;
import com.yx.platform.entity.Product;
import com.yx.platform.mapper.OrderMapper;
import com.yx.platform.mapper.ProductMapper;
import com.yx.platform.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private OrderMapper orderMapper;

    @Override
    public List<Product> getHomepageProducts() {
        return productMapper.findAll();
    }

    @Override
    public List<Product> searchAndFilter(String keyword, List<String> genres) {
        return productMapper.Filter(keyword, genres);
    }

    @Override
    public List<Product> getRecommendedProducts() {
        return productMapper.findNewArrivals();
    }

    @Override
    public List<Product> getTopCcuProducts() {
        return productMapper.findTop10ByPeakCcu();
    }

    @Override
    public List<Product> getMostReviewedProducts() {
        return productMapper.findTop10ByReviews();
    }

    @Override
    public Product getProductDetail(Long id) {
        return productMapper.findById(id);
    }

    // === 核心修复方法 ===
    @Override
    @Transactional // 开启事务，保证扣库存和生成订单要么都成功，要么都失败
    public boolean buyProduct(Long userId, Long productId, int quantity) {
        if (quantity <= 0) quantity = 1;

        // 1. 查询商品信息（主要是为了获取价格）
        Product product = productMapper.findById(productId);
        if (product == null) {
            return false;
        }

        // 2. 尝试扣减库存
        int rows = productMapper.reduceStock(productId, quantity);
        if (rows <= 0) {
            return false; // 库存不足，购买失败
        }

        // 3. 生成订单记录（之前缺失的逻辑）
        try {
            // 3.1 创建订单主表记录
            Order order = new Order();
            order.setUserId(userId);
            // 计算总价：单价 * 数量
            BigDecimal totalAmount = product.getPrice().multiply(new BigDecimal(quantity));
            order.setTotalAmount(totalAmount);

            // 插入数据库 (MyBatis 会自动回填 orderId)
            orderMapper.createOrder(order);

            // 3.2 创建订单项记录 (order_item)
            orderMapper.saveOrderItem(order.getOrderId(), productId, quantity, product.getPrice());

            return true; // 购买成功

        } catch (Exception e) {
            e.printStackTrace();
            // 抛出运行时异常，触发 @Transactional 回滚，恢复库存
            throw new RuntimeException("购买失败，订单生成异常", e);
        }
    }

    @Override
    public boolean checkOwnership(Long userId, Long productId) {
        if (userId == null || productId == null) return false;
        return orderMapper.countUserPurchased(userId, productId) > 0;
    }
}