package com.liang.payment.entity;

import com.liang.order.entity.Order;
import com.liang.order.entity.OrderStatus;
import jakarta.persistence.*;
import jdk.jfr.Timespan;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 30)
    private PaymentMethod paymentMethod ;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 30)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING; // PENDING, SUCCESS, FAILED

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency")
    private String currency = "KHR";


    @Column(name = "transaction_reference")
    private String transactionReference;

    @Column(name = "paid_at")
    private Instant paidAt;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

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
