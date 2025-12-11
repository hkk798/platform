package com.yx.platform.mapper;

import com.yx.platform.entity.Order; // 记得导入刚才建的类
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
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
}