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
            // 1. 如果没传关键词，也没选任何标签 -> 查所有
            "  <if test='(keyword == null or keyword == \"\") and (genres == null or genres.size() == 0)'>" +
            "    1=1" +
            "  </if>" +
            // 2. 如果有关键词 或 有标签 -> 进入混合 OR 查询
            "  <if test='(keyword != null and keyword != \"\") or (genres != null and genres.size() > 0)'>" +
            "    AND (" +
            "      1=0 " + // 占位符
            "      <if test='keyword != null and keyword != \"\"'> OR name LIKE CONCAT('%', #{keyword}, '%') </if>" +
            // 遍历标签列表
            "      <if test='genres != null and genres.size() > 0'>" +
            "        <foreach item='g' collection='genres'>" +
            "          OR genres LIKE CONCAT('%', #{g}, '%') " +
            "        </foreach>" +
            "      </if>" +
            "    )" +
            "  </if>" +
            "</where>" +
            "LIMIT 50" +
            "</script>")
    List<Product> Filter(@Param("keyword") String keyword, @Param("genres") List<String> genres);

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
}