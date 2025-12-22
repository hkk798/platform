package com.yx.platform.service;

import com.yx.platform.entity.Order;
import com.yx.platform.entity.CartItemVo;
import com.yx.platform.mapper.CartMapper;
import com.yx.platform.mapper.OrderMapper;
import com.yx.platform.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {

    @Autowired private CartMapper cartMapper;
    @Autowired private OrderMapper orderMapper;
    @Autowired private ProductMapper productMapper;

    // 获取当前用户的购物车ID，如果没有就创建一个
    public Long getOrCreateCartId(Long userId) {
        Long cartId = cartMapper.findCartIdByUserId(userId);
        if (cartId == null) {
            // 这里有个小技巧，需要定义一个对象来接收回填的ID，或者直接分两步写
            // 为简化演示，我们假设 insert 语句里直接处理了
            // 实际项目中建议新建一个 Cart 实体对象传进去
            try {
                cartMapper.createCart(userId);
                cartId = cartMapper.findCartIdByUserId(userId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cartId;
    }

    // 添加商品到购物车
    public void addToCart(Long userId, Long productId) {
        // 1. 先检查是否已购买 (防止重复买)
        if (orderMapper.countUserPurchased(userId, productId) > 0) {
            // 如果为了用户体验好，这里可以不报错，直接返回
            return;
        }

        Long cartId = getOrCreateCartId(userId);
        Long itemId = cartMapper.findItemId(cartId, productId);

        // 2. 检查购物车里是否已经有了
        if (itemId != null) {
            // 【修改点】如果已经在购物车里了，什么都不做（不再增加数量）
            // 或者是 log.info("商品已在购物车");
            return;
        } else {
            // 【修改点】不存在则新增，强制数量为 1
            cartMapper.addItem(cartId, productId, 1);
        }
    }

    // 获取我的购物车列表
    public List<CartItemVo> getMyCart(Long userId) {
        Long cartId = getOrCreateCartId(userId);
        return cartMapper.findCartItems(cartId);
    }

    public BigDecimal calculateTotal(List<CartItemVo> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItemVo item : items) {
            // 防止空指针
            if (item.getPrice() != null) {
                // 数字游戏通常数量为1，直接累加单价即可
                // 如果数据库里存了数量，为了稳妥也可以乘一下：item.getPrice().multiply(new BigDecimal(item.getQuantity()));
                total = total.add(item.getPrice());
            }
        }
        return total;
    }

    // 结算下单 (核心逻辑)
    @Transactional // 开启事务，保证原子性
    public boolean checkout(Long userId) {
        Long cartId = getOrCreateCartId(userId);
        List<CartItemVo> items = cartMapper.findCartItems(cartId);

        if (items.isEmpty()) return false;

// 1. 计算总价
        BigDecimal totalAmount = calculateTotal(items);

        // ================= 修复开始 =================
        // 2. 创建真实订单
        Order order = new Order();
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);

        // 保存到数据库 (MyBatis 会自动把生成的 ID 填回 order 对象里)
        orderMapper.createOrder(order);

        // 获取真正的数据库 ID
        Long orderId = order.getOrderId();
        // ================= 修复结束 =================

        // 3. 转移数据：购物车 -> 订单项
        for (CartItemVo item : items) {
            // 3.1 扣库存 (利用之前的 ProductMapper)
            int rows = productMapper.reduceStock(item.getProductId(), item.getQuantity());
            if (rows <= 0) {
                throw new RuntimeException("库存不足，商品ID：" + item.getProductId()); // 触发回滚
            }
            // 3.2 保存订单项
            orderMapper.saveOrderItem(orderId, item.getProductId(), item.getQuantity(), item.getPrice());
        }

        // 4. 清空购物车
        cartMapper.clearCart(cartId);

        return true;
    }

    public void removeItem(Long itemId) {
        cartMapper.deleteItem(itemId);
    }
}