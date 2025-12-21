package com.yx.platform.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 商品实体类 (已适配 Steam Dataset 字段)
 */
public class Product {

    // 对应 app_id
    private Long appId;

    // 对应 name (原 prodName)
    private String name;

    // 对应 short_description (原 description)
    private String shortDescription;

    // 对应 price
    private BigDecimal price;

    // 对应 header_image (新增，存图片URL)
    private String headerImage;

    // 对应 genres (原 genre)
    private String genres;

    // 对应 publishers (原 publisher)
    private String publishers;

    // 对应 developers (新增)
    private String developers;

    // 对应 release_date
    private LocalDate releaseDate;

    // === 新增字段 (对应 CSV 中的 peak_ccu 和 num_reviews_total) ===
    private Integer peakCcu;        // 峰值在线人数
    private Integer numReviewsTotal; // 总评论数
    private String supportedLanguages; // 支持语言 (supported_languages)

    private String detailedDescription; // detailed_description
    private String screenshots;         // screenshots (存的是字符串格式的数组)
    private String movies;              // movies (存的是字符串格式的数组)

    // === 业务保留字段 ===
    private Integer stock;
    private Integer status;
    private Long sellerId;

    // ================= Getter & Setter =================
    // 为了兼容旧代码的 getId() 调用，我们可以手动加一个 getProductId 方法
    public Long getProductId() { return appId; }
    public void setProductId(Long id) { this.appId = id; }

    public Long getAppId() { return appId; }
    public void setAppId(Long appId) { this.appId = appId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getHeaderImage() { return headerImage; }
    public void setHeaderImage(String headerImage) { this.headerImage = headerImage; }

    public String getGenres() { return genres; }
    public void setGenres(String genres) { this.genres = genres; }

    public String getPublishers() { return publishers; }
    public void setPublishers(String publishers) { this.publishers = publishers; }

    public String getDevelopers() { return developers; }
    public void setDevelopers(String developers) { this.developers = developers; }

    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }

    public Integer getPeakCcu() { return peakCcu; }
    public void setPeakCcu(Integer peakCcu) { this.peakCcu = peakCcu; }

    public Integer getNumReviewsTotal() { return numReviewsTotal; }
    public void setNumReviewsTotal(Integer numReviewsTotal) { this.numReviewsTotal = numReviewsTotal; }

    public String getSupportedLanguages() { return supportedLanguages; }
    public void setSupportedLanguages(String supportedLanguages) { this.supportedLanguages = supportedLanguages; }

    public String getDetailedDescription() { return detailedDescription; }
    public void setDetailedDescription(String detailedDescription) { this.detailedDescription = detailedDescription; }

    public String getScreenshots() { return screenshots; }
    public void setScreenshots(String screenshots) { this.screenshots = screenshots; }

    public String getMovies() { return movies; }
    public void setMovies(String movies) { this.movies = movies; }


    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
}