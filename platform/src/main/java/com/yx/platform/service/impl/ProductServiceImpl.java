package com.yx.platform.service.impl;

import com.yx.platform.entity.Order;
import com.yx.platform.entity.Product;
import com.yx.platform.mapper.OrderMapper;
import com.yx.platform.mapper.ProductMapper;
import com.yx.platform.mapper.UserMapper;
import com.yx.platform.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public List<Product> getHomepageProducts() {
        return productMapper.findAll();
    }

    // === 核心修改：实现分页逻辑 ===
    @Override
    public Map<String, Object> searchAndFilter(String keyword, List<String> genres, int pageNum, int pageSize) {
        // 1. 简单校验页码
        if (pageNum < 1) pageNum = 1;

        // 2. 计算 Offset (数据库偏移量)
        int offset = (pageNum - 1) * pageSize;

        // 3. 查数据
        List<Product> products = productMapper.findByPage(keyword, genres, offset, pageSize);

        // 4. 查总数
        long total = productMapper.countProducts(keyword, genres);

        // 5. 算总页数
        int totalPages = (int) Math.ceil((double) total / pageSize);

        // 6. 封装
        Map<String, Object> result = new HashMap<>();
        result.put("products", products);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("totalPages", totalPages);
        result.put("hasPrevious", pageNum > 1);
        result.put("hasNext", pageNum < totalPages);

        return result;
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


        // 3. 生成订单记录（之前缺失的逻辑）
        try {

            // 1. 计算总价
            BigDecimal totalAmount = product.getPrice().multiply(new BigDecimal(quantity));

            // === 2. 【新增】执行扣款 ===
            int rows = userMapper.deductBalance(userId, totalAmount);
            if (rows == 0) {
                throw new RuntimeException("支付失败：余额不足！需要 " + totalAmount + " 元。");
            }
            // =========================

            // 3.1 创建订单主表记录
            Order order = new Order();
            order.setUserId(userId);
            // 计算总价：单价 * 数量
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


    @Override
    public List<Product> getCarouselProducts() {
        // 1. 获取两个榜单的数据
        List<Product> ccuList = productMapper.findTop10ByPeakCcu();
        List<Product> reviewList = productMapper.findTop10ByReviews();

        // 2. 合并并去重
        List<Product> result = new ArrayList<>();
        Set<Long> addedIds = new HashSet<>();

        // 先加 CCU 榜单
        for (Product p : ccuList) {
            if (addedIds.add(p.getProductId())) {
                result.add(p);
            }
        }
        // 再加评论榜单 (如果有重复的 ID 会被跳过)
        for (Product p : reviewList) {
            if (addedIds.add(p.getProductId())) {
                result.add(p);
            }
        }

        // 3. 截取前 10 个
        if (result.size() > 10) {
            return result.subList(0, 10);
        }
        return result;
    }


    @Override
    public Map<String, Object> getAdminProductList(int pageNum, int pageSize) {
        if (pageNum < 1) pageNum = 1;
        int offset = (pageNum - 1) * pageSize;

        List<Product> products = productMapper.findAdminProducts(offset, pageSize);
        long total = productMapper.countAllProducts();
        int totalPages = (int) Math.ceil((double) total / pageSize);

        Map<String, Object> result = new HashMap<>();
        result.put("products", products);
        result.put("currentPage", pageNum);
        result.put("totalPages", totalPages);
        return result;
    }

    @Override
    public void updateProductStatus(Long productId, Integer status) {
        productMapper.updateStatus(productId, status);
    }
}