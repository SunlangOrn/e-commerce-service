package com.liang.cart.repository;

import com.liang.cart.entity.Cart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("SELECT DISTINCT c FROM Cart c " +
            "LEFT JOIN FETCH c.items i " +
            "LEFT JOIN FETCH i.product " +
            "WHERE c.userId = :userId")
    Optional<Cart> findByUserIdWithDetails(@Param("userId") Long userId);

    Optional<Cart> findByUserId(Long userId);
}