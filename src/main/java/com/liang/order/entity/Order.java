package com.liang.order.entity;

import com.liang.address.entity.Address;
import com.liang.payment.entity.Payment;
import com.liang.payment.entity.PaymentStatus;
import com.liang.shared.security.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "shipping_full_name", nullable = false, length = 100)
    private String shippingFullName;

    @Column(name = "shipping_phone", nullable = false, length = 20)
    private String shippingPhone;

    @Column(name = "shipping_address_line", nullable = false)
    private String shippingAddressLine;

    @Column(name = "shipping_city", nullable = false, length = 100)
    private String shippingCity;

    @Column(name = "shipping_province", length = 100)
    private String shippingProvince;

    @Column(name = "shipping_postal_code", length = 20)
    private String shippingPostalCode;

    // --- Financial & Status Fields ---
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "currency" )
    private String currency = "KHR";

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false, length = 30)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 30)
    private PaymentStatus paymentStatus;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

    @PrePersist
    void prePersist() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public void snapshotAddress(Address address) {
        this.shippingFullName = address.getFullName();
        this.shippingPhone = address.getPhone();
        this.shippingAddressLine = address.getAddressLine();
        this.shippingCity = address.getCity();
    }

    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
        this.totalAmount = this.totalAmount.add(item.getSubtotal());
    }
}