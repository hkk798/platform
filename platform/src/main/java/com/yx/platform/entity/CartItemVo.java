package com.yx.platform.entity;

import java.math.BigDecimal;

// 这是一个 "视图对象" (VO)，专门用来把数据组装好传给前端页面
public class CartItemVo {
    private Long cartItemId;
    private Long productId;
    private String productName;
    private String platform;
    private BigDecimal price;
    private Integer quantity;

    // Getters and Setters
    public Long getCartItemId() { return cartItemId; }
    public void setCartItemId(Long cartItemId) { this.cartItemId = cartItemId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}