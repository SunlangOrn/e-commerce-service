package com.liang.address.repository;

import com.liang.address.entity.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address,Long> {

    Page<Address> findByUserId(Long userId, Pageable pageable);

    Page<Address> findAll(Pageable pageable);

    Optional<Address> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserId(Long userid);

    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.user.id = :userId AND a.isDefault = true")
    void resetDefaultAddressForUser(@Param("userId") Long userId);
}
