package com.yx.platform.mapper;

import com.yx.platform.entity.Order; // 记得导入刚才建的类
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.math.BigDecimal;

@Mapper
public interface OrderMapper {

    // 修改：参数改为 Order 对象
    @Insert("INSERT INTO orders(user_id, total_amount, status, shipping_address) VALUES(#{userId}, #{totalAmount}, 1, '默认地址')")
    @Options(useGeneratedKeys = true, keyProperty = "orderId", keyColumn = "order_id")
    void createOrder(Order order);

    // 这个保持不变
    @Insert("INSERT INTO order_item(order_id, product_id, quantity, price) VALUES(#{orderId}, #{productId}, #{quantity}, #{price})")
    void saveOrderItem(@Param("orderId") Long orderId, @Param("productId") Long productId, @Param("quantity") int quantity, @Param("price") BigDecimal price);

    // === 【新增】查询用户是否已购买过某商品 ===
    // 逻辑：关联 order_item 和 orders 表，查找该用户是否有包含该商品的订单
    @Select("SELECT COUNT(*) FROM order_item oi " +
            "JOIN orders o ON oi.order_id = o.order_id " +
            "WHERE o.user_id = #{userId} AND oi.product_id = #{productId}")
    int countUserPurchased(@Param("userId") Long userId, @Param("productId") Long productId);
}