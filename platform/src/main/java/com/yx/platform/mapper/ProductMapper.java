package com.yx.platform.mapper;

import com.yx.platform.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ProductMapper {

    // 1. 首页展示：只查前 50 条，防止浏览器卡死
    @Select("SELECT * FROM product LIMIT 50")
    List<Product> findAll();

    // 2. 搜索：prod_name 变成了 name
    @Select("SELECT * FROM product WHERE name LIKE CONCAT('%', #{keyword}, '%')")
    List<Product> searchByName(String keyword);

    // 3. 筛选：platform 没了，genre 变成了 genres
    // 我们可以把原来的“平台筛选”改成“开发商筛选”或者暂时去掉平台筛选
    // 下面代码保留了 genres 的筛选（模糊匹配，因为现在 genres 可能是 "Action, RPG"）
    @Select("<script>" +
            "SELECT * FROM product WHERE 1=1 " +
            "<if test='genre != null and genre != \"\"'> AND genres LIKE CONCAT('%', #{genre}, '%') </if>" +
            "LIMIT 50 "+
            "</script>")
    List<Product> filter(String platform, String genre);

    // 4. 详情：product_id 变成了 app_id
    @Select("SELECT * FROM product WHERE app_id = #{id}")
    Product findById(Long id);

    // 5. 扣库存：product_id 变成了 app_id
    @Update("UPDATE product SET stock = stock - #{quantity} WHERE app_id = #{productId} AND stock >= #{quantity}")
    int reduceStock(Long productId, int quantity);

    // 新增：最新上架
    @Select("SELECT * FROM product ORDER BY release_date DESC LIMIT 3")
    List<Product> findNewArrivals();
}