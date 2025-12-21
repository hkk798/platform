package com.yx.platform.mapper;

import com.yx.platform.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {

    // 1. 首页展示：查 product1 表
    @Select("SELECT * FROM product1 LIMIT 50")
    List<Product> findAll();

    // 2. 搜索
    @Select("SELECT * FROM product1 WHERE name LIKE CONCAT('%', #{keyword}, '%')")
    List<Product> searchByName(String keyword);

    // 3. 筛选
    @Select("<script>" +
            "SELECT * FROM product1 WHERE 1=1 " +
            "<if test='genre != null and genre != \"\"'> AND genres LIKE CONCAT('%', #{genre}, '%') </if>" +
            "LIMIT 50 "+
            "</script>")
    List<Product> filter(@Param("platform") String platform, @Param("genre") String genre);

    // 4. 详情：注意这里用 app_id (请确保您执行了第一步的 SQL 改名)
    @Select("SELECT * FROM product1 WHERE appid = #{id}")
    Product findById(Long id);

    // 5. 扣库存
    @Update("UPDATE product1 SET stock = stock - #{quantity} WHERE appid = #{productId} AND stock >= #{quantity}")
    int reduceStock(Long productId, int quantity);

    // 推荐：最新上架
    @Select("SELECT * FROM product1 ORDER BY release_date DESC LIMIT 3")
    List<Product> findNewArrivals();

    // === 新增：峰值在线人数榜单 (Peak CCU) ===
    // 对应 CSV 中的 peak_ccu 字段
    @Select("SELECT * FROM product1 ORDER BY peak_ccu DESC LIMIT 10")
    List<Product> findTop10ByPeakCcu();

    // === 新增：评论数榜单 (Total Reviews) ===
    // 对应 CSV 中的 num_reviews_total 字段
    @Select("SELECT * FROM product1 ORDER BY num_reviews_total DESC LIMIT 10")
    List<Product> findTop10ByReviews();
}