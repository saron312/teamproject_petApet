package com.teamproject.petapet.web.product.service;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.teamproject.petapet.domain.company.Company;
import com.teamproject.petapet.domain.product.Product;
import com.teamproject.petapet.domain.product.repository.ProductRepository;
import com.teamproject.petapet.web.product.fileupload.UploadFile;
import com.teamproject.petapet.web.product.productdtos.ProductInsertDTO;
import com.teamproject.petapet.web.product.productdtos.ProductDTO;
import com.teamproject.petapet.web.product.productdtos.ProductUpdateDTO;
import lombok.RequiredArgsConstructor;

import com.teamproject.petapet.domain.product.ProductType;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.teamproject.petapet.domain.product.QProduct.product;

/**
 * 박채원 22.10.09 작성
 */

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ProductDTO> getProductList(String companyId) {
        List<Product> productList = productRepository.findAllByCompany_CompanyId(companyId);
        return productList.stream().map(ProductDTO::fromEntityForManageProduct).collect(Collectors.toList());
    }

    @Override
    public Page<Product> getProductPage(Pageable pageable) {
        return productRepository.findAllByProductStatus(pageable, "판매중");
    }

    @Override
    public Page<Product> getProductListByReview(Pageable pageable) {
        return productRepository.findAllOrderByReviewCount(pageable);
    }

    @Override
    public void updateProductStatus(String selectStatus, Long productStock, Long productId) {
        productRepository.updateProductStatus(selectStatus, productStock, productId);

    }

    @Override
    public void updateProductInfo(String type, Long productId, Long productStock, String productStatus) {
        if (type.equals("stock")) {
            productRepository.updateProductStock(productStock, productId);
        } else if (type.equals("status")) {
            productRepository.updateProductStatus(productStatus, productId);
        }
    }

    @Override
    public Page<Product> findAllByProductDiv(ProductType productType, Pageable pageable) {
        return productRepository.findAllByProductDiv(productType, pageable);
    }

    @Override
    public void updateProductReviewCount(Long productId, Long reviewCount) {
        productRepository.updateProductReviewCount(productId, reviewCount);
    }

    @Override
    public void updateProductStatusOutOfStock(List<String> productId) {
        for (String id : productId) {
            productRepository.updateProductStatus("재고없음", 0L, Long.valueOf(id));
        }
    }

    @Override
    public void updateProductRating(Long productId) {
        productRepository.updateProductRating(productId);
    }

    @Override
    public Optional<Product> findOne(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public Optional<Product> saveProduct(ProductInsertDTO insertDTO, List<UploadFile> uploadFiles, Company company) {
        ProductType productDiv = ProductType.valueOf(insertDTO.getProductDiv());

        Product product = Product.ConvertToEntityByInsertDTO(insertDTO, uploadFiles, productDiv, company);

        Product savedProduct = productRepository.save(product);

        return Optional.of(savedProduct);
    }

    @Override
    public Optional<Product> saveProduct(Product product) {
        return Optional.of(productRepository.save(product));
    }

    @Override
    public void updateProduct(ProductUpdateDTO productUpdateDTO, List<UploadFile> productImg) {
        jpaQueryFactory.update(product)
                .set(product.productName, productUpdateDTO.getProductName())
                .set(product.productContent, productUpdateDTO.getProductContent())
                .set(product.productStatus, productUpdateDTO.getProductStatus())
                .set(product.productDiv, ProductType.valueOf(productUpdateDTO.getProductDiv()))
                .set(product.productUnitPrice, productUpdateDTO.getProductUnitPrice())
                .set(product.productPrice, productUpdateDTO.getProductPrice())
                .set(product.productDiscountRate, productUpdateDTO.getProductDiscountRate())
                .set(product.productStock, productUpdateDTO.getProductStock())
                .set(product.productImg, productImg)
                .where(product.productId.eq(productUpdateDTO.getProductId()))
                .execute();
    }

    @Override
    @Transactional
    public Optional<Product> findProductWithReview(Long id) {
        return productRepository.findProductWithReview(id);
    }

    @Override
    public Page<Product> findPage(String category, ProductType productType, String sortType, String content, Long starRating, String minPrice, String maxPrice, String isPriceRange, Pageable pageable) {
        List<OrderSpecifier> orders = getAllOrderSpecifiers(pageable, sortType);
        List<Product> productList = jpaQueryFactory.select(product)
                .from(product)
                .where(isCategory(productType, category),
                        isContent(content),
                        isRating(starRating),
                        isPriceRange(minPrice, maxPrice, isPriceRange),
                        product.productStatus.eq("판매중"))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orders.toArray(OrderSpecifier[]::new))
                .fetch();
        long count = productRepository.count();
        return new PageImpl<>(productList, pageable, count);
    }

    @Override
    public void addProductReport(Long productId) {
        productRepository.addProductReport(productId);
    }

    @Override
    public void updateCounterView(Long productId) {
        jpaQueryFactory.update(product)
                .set(product.productViewCount, product.productViewCount.add(1))
                .where(product.productId.eq(productId))
                .execute();
    }

    @Override
    public void updateCounterSell(Long productId) {
        jpaQueryFactory.update(product)
                .set(product.productSellCount, product.productSellCount.add(1))
                .where(product.productId.eq(productId))
                .execute();
    }

    private BooleanExpression isContent(String content) {
        if (StringUtils.hasText(content) && !content.equals("false")) {
            return product.productName.contains(content);
        }
        return null;
    }

    private BooleanExpression isCategory(ProductType productType, String category) {
        if (!category.equals("all")) {
            return product.productDiv.eq(productType);
        }
        return null;
    }

    private BooleanExpression isRating(Long rating) {
        if (rating != 0) {
            return product.productRating.goe(rating);
        }
        return null;
    }

    private BooleanExpression isPriceRange(String minPrice, String maxPrice, String isRange) {
        if (isRange.equals("true")) {
            Long parsedMinPrice = minPrice.equals("") ? null : Long.parseLong(minPrice);
            Long parsedMaxPrice = maxPrice.equals("") ? null : Long.parseLong(maxPrice);
            return product.productPrice.between(parsedMinPrice, parsedMaxPrice);
        }
        return null;
    }

    private OrderSpecifier<?> getSorted(Order order, Path<?> parent, String fieldName) {
        Path<Object> fieldPath = Expressions.path(Object.class, parent, fieldName);

        return new OrderSpecifier(order, fieldPath);
    }

    private List<OrderSpecifier> getAllOrderSpecifiers(Pageable pageable, String sortType) {
        List<OrderSpecifier> ORDERS = new ArrayList<>();

        if (!ObjectUtils.isEmpty(pageable.getSort())) {
            for (Sort.Order order : pageable.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                if (order.getProperty().equals(sortType)) {
                    OrderSpecifier<?> createdDate = getSorted(direction, product, sortType);
                    ORDERS.add(createdDate);
                }
            }
        }
        return ORDERS;
    }
}
