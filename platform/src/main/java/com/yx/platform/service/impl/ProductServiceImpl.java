package com.yx.platform.service.impl;

import com.yx.platform.entity.Product;
import com.yx.platform.mapper.ProductMapper;
import com.yx.platform.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate; // 别忘了导入这个
import java.util.List;

@Service // 标记为服务组件，交给 Spring 管理
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<Product> getHomepageProducts() {
        // 默认逻辑：查询所有商品
        return productMapper.findAll();
    }

    @Override
    public List<Product> searchAndFilter(String keyword, String platform, String genre) {
        // 业务逻辑判断：优先搜索，其次筛选，最后查所有
        if (keyword != null && !keyword.isEmpty()) {
            return productMapper.searchByName(keyword);
        } else if ((platform != null && !platform.isEmpty()) || (genre != null && !genre.isEmpty())) {
            return productMapper.filter(platform, genre);
        } else {
            return productMapper.findAll();
        }
    }

    @Override
    public List<Product> getRecommendedProducts() {
        // 业务逻辑：调用 Mapper 查询最新的 3 款游戏作为推荐
        return productMapper.findNewArrivals();
    }

    @Override
    public Product getProductDetail(Long id) {
        return productMapper.findById(id);
    }

    @Override
    public boolean buyProduct(Long productId, int quantity) {
        if (quantity <= 0) quantity = 1;
        // 执行扣库存
        int rows = productMapper.reduceStock(productId, quantity);
        // 如果影响行数 > 0，说明购买成功
        return rows > 0;
    }


    @Override
    public void publishProduct(Product product, Long userId) {
        // 1. 设置卖家ID
        product.setSellerId(userId);

        // 2. 设置默认库存 (如果没填)
        if (product.getStock() == null) {
            product.setStock(1);
        }

        // === 新增修复：补全空缺字段，防止数据库报错 ===
        // 如果没填发行商，默认为“个人闲置”
        if (product.getPublisher() == null || product.getPublisher().isEmpty()) {
            product.setPublisher("个人闲置");
        }
        // 如果没填发售日，默认为“今天”
        if (product.getReleaseDate() == null) {
            product.setReleaseDate(LocalDate.now());
        }
        // ===========================================

        // 3. 保存到数据库
        productMapper.save(product);
    }
}