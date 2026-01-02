package com.yx.platform.service;

import com.yx.platform.entity.Product;
import java.util.List;
import java.util.Map;

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
     * @param genres 类型 (RPG/ACT)
     */
    Map<String, Object> searchAndFilter(String keyword, List<String> genres, int pageNum, int pageSize);

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
    boolean buyProduct(Long userId, Long productId, int quantity);



    /**
     * 检查用户是否已拥有该游戏
     */
    boolean checkOwnership(Long userId, Long productId);



    /**
     * 获取首页轮播图数据
     * (合并热门榜单和热议榜单，去重后取前10)
     */
    List<Product> getCarouselProducts();


    // 管理员：获取所有商品
    Map<String, Object> getAdminProductList(int pageNum, int pageSize);

    // 管理员：更新状态
    void updateProductStatus(Long productId, Integer status);

    boolean refundProduct(Long userId, Long productId);
}