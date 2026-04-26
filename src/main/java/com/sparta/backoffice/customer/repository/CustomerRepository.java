package com.sparta.backoffice.customer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sparta.backoffice.customer.entity.Customer;
import com.sparta.backoffice.customer.enums.CustomerStatus;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	// JPQL을 사용하여 이름 또는 이메일에 키워드가 포함된 데이터를 조회
	@Query("SELECT c FROM Customer c WHERE "
		+ "(:keyword IS NULL OR c.name LIKE CONCAT('%', :keyword, '%')"
		+ "OR c.email LIKE CONCAT('%', :keyword, '%')) "
		+ "AND (:status IS NULL OR c.status = :status)")
	Page<Customer> searchCustomer(
		@Param("keyword") String keyword,
		@Param("status") CustomerStatus status,
		Pageable pageable
	);
}
