package com.sparta.backoffice.customer.repository;

import com.sparta.backoffice.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

    public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
