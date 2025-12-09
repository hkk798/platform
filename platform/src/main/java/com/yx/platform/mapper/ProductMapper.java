package com.yx.platform.mapper;

import com.yx.platform.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ProductMapper {

    // 1. 首页展示：查询所有商品（或者可以加 limit 限制数量）
    @Select("SELECT * FROM product")
    List<Product> findAll();

    // 2. 搜索功能：根据商品名称模糊查询
    // CONCAT('%', #{keyword}, '%') 是 MySQL 的模糊匹配写法
    @Select("SELECT * FROM product WHERE prod_name LIKE CONCAT('%', #{keyword}, '%')")
    List<Product> searchByName(String keyword);

    // 3. 筛选功能：根据平台 (Platform) 和类型 (Genre) 筛选 [cite: 23]
    // <script> 标签允许我们在 SQL 里写 if 判断，实现动态筛选
    @Select("<script>" +
            "SELECT * FROM product WHERE 1=1" +
            "<if test='platform != null and platform != \"\"'> AND platform = #{platform} </if>" +
            "<if test='genre != null and genre != \"\"'> AND genre = #{genre} </if>" +
            "</script>")
    List<Product> filter(String platform, String genre);

    // 4. 商品详情：根据 ID 查询单个商品
    @Select("SELECT * FROM product WHERE product_id = #{id}")
    Product findById(Long id);

    // 5. 购买逻辑：扣减库存
    // 只有当库存 > 0 时才扣减，返回影响的行数（1表示成功，0表示库存不足）
    @Update("UPDATE product SET stock = stock - #{quantity} WHERE product_id = #{productId} AND stock >= #{quantity}")
    int reduceStock(Long productId, int quantity);
}