package com.yx.platform.service;

import com.yx.platform.entity.Product;
import java.util.List;

/**
 * 商品业务逻辑接口
 * 对应设计报告中的“业务逻辑层”
 */
public interface ProductService {

    /**
     * 获取首页商品列表
     * (对应报告：商品浏览)
     */
    List<Product> getHomepageProducts();

    /**
     * 搜索与筛选商品
     * (对应报告：智能筛选、搜索)
     * @param keyword 关键词
     * @param platform 平台 (PS5/Switch/Xbox)
     * @param genre 类型 (RPG/ACT)
     */
    List<Product> searchAndFilter(String keyword, String platform, String genre);

    /**
     * 获取推荐商品 (例如：最新上架的前4个)
     * (对应报告：首页推荐)
     */
    List<Product> getRecommendedProducts();


    // === 新增接口方法 ===
    List<Product> getTopCcuProducts();      // 获取 CCU 前10
    List<Product> getMostReviewedProducts(); // 获取 评论数 前10


    /**
     * 获取商品详情
     */
    Product getProductDetail(Long id);

    /**
     * 购买商品（扣减库存）
     */
    boolean buyProduct(Long productId, int quantity);

}