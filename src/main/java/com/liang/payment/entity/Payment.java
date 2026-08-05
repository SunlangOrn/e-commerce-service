package com.liang.payment.entity;

import com.liang.order.entity.Order;
import com.liang.order.entity.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "payment_method", nullable = false, length = 30)
    private String paymentMethod; // COD, KHQR, ABA_PAY

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 30)
    private PaymentStatus paymentStatus; // PENDING, SUCCESS, FAILED

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "transaction_reference", length = 100)
    private String transactionReference;

    private Instant paidAt;
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        this.createdAt = Instant.now();
    }

    public void markAsPaid(String reference) {
        this.paymentStatus = PaymentStatus.SUCCESS;
        this.transactionReference = reference;
        this.paidAt = Instant.now();

        if (this.order != null) {
            this.order.setPaymentStatus(PaymentStatus.SUCCESS);
            this.order.setOrderStatus(OrderStatus.PROCESSING);
        }
    }

    public void markAsFailed() {
        this.paymentStatus = PaymentStatus.FAILED;
        if (this.order != null) {
            this.order.setPaymentStatus(PaymentStatus.FAILED);
        }
    }
}
