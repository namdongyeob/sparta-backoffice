package com.sparta.backoffice.product.entity;

import com.sparta.backoffice.admin.entity.Admin;
import com.sparta.backoffice.common.entity.BaseEntity;
import com.sparta.backoffice.product.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "products")
@NoArgsConstructor
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;

    public Product(String name, String category, int price,
                   int stock, ProductStatus status, Admin admin) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.status = status;
        this.admin = admin;
    }

    // 상품 정보 수정
    public void updateInfo(String name, String category, int price) {
        this.name = name;
        this.category = category;
        this.price = price;
    }

    // 재고 변경 + 상태 자동 전환
    public void updateStock(int stock) {
        this.stock = stock;
        if (this.status != ProductStatus.DISCONTINUED) {
            this.status = stock <= 0 ? ProductStatus.SOLD_OUT : ProductStatus.ON_SALE;
        }
    }

    // 상태 변경
    public void updateStatus(ProductStatus status) {
        this.status = status;
    }
}
