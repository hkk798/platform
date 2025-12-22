package com.yx.platform.mapper;

import com.yx.platform.entity.CartItemVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CartMapper {

    @Select("SELECT cart_id FROM cart WHERE user_id = #{userId}")
    Long findCartIdByUserId(Long userId);

    @Insert("INSERT INTO cart(user_id) VALUES(#{userId})")
    void createCart(Long userId);

    @Select("SELECT item_id FROM cart_item WHERE cart_id = #{cartId} AND product_id = #{productId}")
    Long findItemId(@Param("cartId") Long cartId, @Param("productId") Long productId);

    @Insert("INSERT INTO cart_item(cart_id, product_id, quantity) VALUES(#{cartId}, #{productId}, #{quantity})")
    void addItem(@Param("cartId") Long cartId, @Param("productId") Long productId, @Param("quantity") int quantity);



    // === ⚠️重点修改在这里 ===
    // 将 LEFT JOIN product 改为 LEFT JOIN product1
    @Select("SELECT ci.item_id as cartItemId, ci.product_id as productId, ci.quantity, " +
            "p.name as productName, p.price, p.header_image as platform " +
            "FROM cart_item ci " +
            "LEFT JOIN product1 p ON ci.product_id = p.appid " +
            "WHERE ci.cart_id = #{cartId}")
    List<CartItemVo> findCartItems(Long cartId);

    @Delete("DELETE FROM cart_item WHERE item_id = #{itemId}")
    void deleteItem(Long itemId);

    @Delete("DELETE FROM cart_item WHERE cart_id = #{cartId}")
    void clearCart(Long cartId);
}