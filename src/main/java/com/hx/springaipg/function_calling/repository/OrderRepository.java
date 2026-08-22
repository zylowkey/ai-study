package com.hx.springaipg.function_calling.repository;


import com.hx.springaipg.function_calling.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
