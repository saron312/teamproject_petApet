package com.teamproject.petapet.web.product.service;

import com.teamproject.petapet.domain.product.Product;
import com.teamproject.petapet.domain.product.ProductType;
import com.teamproject.petapet.web.product.productdtos.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 박채원 22.10.09 작성
 */

public interface ProductService {
    List<ProductDTO> getProductList(String companyId);

    Page<Product> getProductPage(Pageable pageable);

    Page<Product> getProductListByReview(Pageable pageable);

    void updateProductStatus(String selectStatus, Long productStock, Long productId);

    Page<Product> findAllByProductDiv(ProductType productType,Pageable pageable);

    void updateProductStatusOutOfStock(List<String> productId);

    void updateProductRating(Long productId);

    Optional<Product> findOne(Long id);

    void productSave(Product product);

    Optional<Product> findProductWithReview(Long id);

    void addProductReport(Long productId);
}

