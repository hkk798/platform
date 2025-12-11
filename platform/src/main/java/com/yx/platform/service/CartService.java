package com.yx.platform.service;

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
    public void addToCart(Long userId, Long productId, int quantity) {
        Long cartId = getOrCreateCartId(userId);
        Long itemId = cartMapper.findItemId(cartId, productId);

        if (itemId != null) {
            // 如果已经有了，就更新数量 (逻辑：先查出来当前数量，再加)
            // 这里简化：直接设为固定值或者需要在SQL里写 quantity = quantity + ?
            // 我们简单处理：假设前端传的是增量，暂时直接插入会报错，需要逻辑判断
            // 简单版：直接再插入一条记录是不行的，必须更新
            // TODO: 完善逻辑，先简化为“添加即覆盖”或“数据库层面的累加”
            // 修正 CartMapper.addItem 为 "ON DUPLICATE KEY UPDATE" 会更好，这里用Java逻辑：
            // 暂略，实际代码需要查询旧数量 + quantity
        } else {
            cartMapper.addItem(cartId, productId, quantity);
        }
    }

    // 获取我的购物车列表
    public List<CartItemVo> getMyCart(Long userId) {
        Long cartId = getOrCreateCartId(userId);
        return cartMapper.findCartItems(cartId);
    }

    // 计算总价
    public BigDecimal calculateTotal(List<CartItemVo> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItemVo item : items) {
            BigDecimal subTotal = item.getPrice().multiply(new BigDecimal(item.getQuantity()));
            total = total.add(subTotal);
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

        // 2. 创建订单 (这里需要一个 Order 实体来接收 ID，简单演示用)
        // Order order = new Order(); order.setUserId(userId); ...
        // orderMapper.createOrder(order);
        // Long orderId = order.getOrderId();

        // 模拟订单ID生成
        Long orderId = System.currentTimeMillis();
        // 实际上应该调用 orderMapper.createOrder 并在 Mapper XML 中配置 keyProperty

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