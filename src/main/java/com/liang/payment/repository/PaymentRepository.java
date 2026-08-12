package com.liang.payment.repository;

import com.liang.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment,Long> {

    Optional<Payment> findByOrderIdAndOrderUserId(Long orderId, Long userId);

    Optional<Payment> findByTransactionReference(String transactionReference);

}
