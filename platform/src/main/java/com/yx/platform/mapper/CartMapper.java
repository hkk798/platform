package com.yx.platform.mapper;

import com.yx.platform.entity.CartItemVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CartMapper {

    // 1. 根据用户ID查找购物车ID
    @Select("SELECT cart_id FROM cart WHERE user_id = #{userId}")
    Long findCartIdByUserId(Long userId);

    // 2. 创建新购物车
    @Insert("INSERT INTO cart(user_id) VALUES(#{userId})")
    void createCart(Long userId); // 这里稍微修改一下参数传递方式，或者用对象

    // 3. 查找购物车里是否已经有这个商品
    @Select("SELECT item_id FROM cart_item WHERE cart_id = #{cartId} AND product_id = #{productId}")
    Long findItemId(@Param("cartId") Long cartId, @Param("productId") Long productId);

    // 4. 添加商品到购物车
    @Insert("INSERT INTO cart_item(cart_id, product_id, quantity) VALUES(#{cartId}, #{productId}, #{quantity})")
    void addItem(@Param("cartId") Long cartId, @Param("productId") Long productId, @Param("quantity") int quantity);

    // 5. 更新商品数量 (增加或减少)
    @Update("UPDATE cart_item SET quantity = #{quantity} WHERE item_id = #{itemId}")
    void updateQuantity(@Param("itemId") Long itemId, @Param("quantity") int quantity);

    // 6. 获取购物车详情列表 (多表关联查询：CartItem -> Product)
    @Select("SELECT ci.item_id as cartItemId, ci.product_id as productId, ci.quantity, " +
            "p.prod_name as productName, p.price, p.platform " +
            "FROM cart_item ci " +
            "LEFT JOIN product p ON ci.product_id = p.product_id " +
            "WHERE ci.cart_id = #{cartId}")
    List<CartItemVo> findCartItems(Long cartId);

    // 7. 删除购物车项
    @Delete("DELETE FROM cart_item WHERE item_id = #{itemId}")
    void deleteItem(Long itemId);

    // 8. 清空购物车 (下单后使用)
    @Delete("DELETE FROM cart_item WHERE cart_id = #{cartId}")
    void clearCart(Long cartId);
}