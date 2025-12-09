package com.yx.platform.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 商品实体类
 * 对应数据库中的 PRODUCT 表
 */
public class Product {

    /** 主键 ID */
    private Long productId;

    /** 商品名称 */
    private String prodName;

    /** 商品描述 */
    private String description;

    /** 价格 (建议使用 BigDecimal 处理金额) */
    private BigDecimal price;

    /** 库存 */
    private Integer stock;

    /** 游戏平台 (PS5, Xbox, Switch 等) [cite: 22] */
    private String platform;

    /** 游戏类型 (RPG, ACT 等) [cite: 22] */
    private String genre;

    /** 发行商 */
    private String publisher;

    /** 发售日期 */
    private LocalDate releaseDate;

    /** 分类 ID */
    private Long categoryId;

    // ================= Getter & Setter =================

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProdName() { return prodName; }
    public void setProdName(String prodName) { this.prodName = prodName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
}