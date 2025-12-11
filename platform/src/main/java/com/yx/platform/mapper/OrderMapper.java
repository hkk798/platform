package com.yx.platform.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import com.yx.platform.entity.CartItemVo; // 复用一下VO或者新建Order实体
import java.math.BigDecimal;

@Mapper
public interface OrderMapper {

    // 1. 创建主订单
    @Insert("INSERT INTO orders(user_id, total_amount, status, shipping_address) VALUES(#{userId}, #{amount}, 1, '默认地址')")
    @Options(useGeneratedKeys = true, keyProperty = "orderId", keyColumn = "order_id")
    void createOrder(@Param("userId") Long userId, @Param("amount") BigDecimal amount, @Param("orderId") Object orderIdHolder);
    // 注：这里为了简化，实际开发建议传入 Order 对象

    // 2. 插入订单项
    @Insert("INSERT INTO order_item(order_id, product_id, quantity, price) VALUES(#{orderId}, #{productId}, #{quantity}, #{price})")
    void saveOrderItem(@Param("orderId") Long orderId, @Param("productId") Long productId, @Param("quantity") int quantity, @Param("price") BigDecimal price);
}