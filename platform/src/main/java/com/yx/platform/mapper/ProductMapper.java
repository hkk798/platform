package com.yx.platform.mapper;

import com.yx.platform.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ProductMapper {

    // 1. 首页展示：改为查 product1
    @Select("SELECT * FROM product1 LIMIT 50")
    List<Product> findAll();

    // 2. 搜索：改为查 product1
    @Select("SELECT * FROM product1 WHERE name LIKE CONCAT('%', #{keyword}, '%')")
    List<Product> searchByName(String keyword);

    // 3. 筛选：改为查 product1
    // === 【修改重点】
    // 注意：如果两个都为空，就查所有
    @Select("<script>" +
            "SELECT * FROM product1 " +
            "<where>" +
            // 1. 关键词 (如果有，必须满足)
            "  <if test='keyword != null and keyword != \"\"'>" +
            "    AND name LIKE CONCAT('%', #{keyword}, '%')" +
            "  </if>" +

            // 2. 标签循环 (改为 AND 逻辑)
            // 解释：遍历每一个选中的标签 g，生成 AND genres LIKE '%g%'
            // 效果：AND genres LIKE '%Action%' AND genres LIKE '%RPG%'
            "  <if test='genres != null and genres.size() > 0'>" +
            "    <foreach item='g' collection='genres'>" +
            "       AND genres LIKE CONCAT('%', #{g}, '%') " +
            "    </foreach>" +
            "  </if>" +
            "</where>" +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<Product> findByPage(@Param("keyword") String keyword,
                             @Param("genres") List<String> genres,
                             @Param("offset") int offset,
                             @Param("pageSize") int pageSize);


    // === 4. 查询总数 (保持逻辑一致) ===
    @Select("<script>" +
            "SELECT COUNT(*) FROM product1 " +
            "<where>" +
            "  <if test='keyword != null and keyword != \"\"'>" +
            "    AND name LIKE CONCAT('%', #{keyword}, '%')" +
            "  </if>" +
            "  <if test='genres != null and genres.size() > 0'>" +
            "    <foreach item='g' collection='genres'>" +
            "       AND genres LIKE CONCAT('%', #{g}, '%') " +
            "    </foreach>" +
            "  </if>" +
            "</where>" +
            "</script>")
    long countProducts(@Param("keyword") String keyword, @Param("genres") List<String> genres);


    // 4. 详情：改为查 product1
    // ⚠️注意：请确认您的 product1 表里的主键列名是 app_id 还是 appid
    // 如果报错 "Unknown column 'app_id'"，请把下面的 app_id 改为 appid
    @Select("SELECT * FROM product1 WHERE appid = #{id}")
    Product findById(Long id);

    // 5. 扣库存：改为操作 product1
    @Update("UPDATE product1 SET stock = stock - #{quantity} WHERE appid = #{productId} AND stock >= #{quantity}")
    int reduceStock(@Param("productId") Long productId, @Param("quantity") int quantity);

    // 推荐：改为查 product1
    @Select("SELECT * FROM product1 ORDER BY release_date DESC LIMIT 3")
    List<Product> findNewArrivals();

    // CCU 榜单：改为查 product1
    @Select("SELECT * FROM product1 ORDER BY peak_ccu DESC LIMIT 10")
    List<Product> findTop10ByPeakCcu();

    // 评论数榜单：改为查 product1
    @Select("SELECT * FROM product1 ORDER BY num_reviews_total DESC LIMIT 10")
    List<Product> findTop10ByReviews();

    @Select("SELECT DISTINCT p.* FROM product1 p " +
            "JOIN order_item oi ON p.appid = oi.product_id " +
            "JOIN orders o ON oi.order_id = o.order_id " +
            "WHERE o.user_id = #{userId}")
    List<Product> findPurchasedByUserId(Long userId);

}