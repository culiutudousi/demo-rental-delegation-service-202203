package com.example.demo.repository;

import com.example.demo.entity.CommissionPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommissionPaymentRepository extends JpaRepository<CommissionPayment, String> {
}
