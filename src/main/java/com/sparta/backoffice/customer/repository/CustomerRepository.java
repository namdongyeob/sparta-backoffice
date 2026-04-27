package com.sparta.backoffice.customer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sparta.backoffice.customer.entity.Customer;
import com.sparta.backoffice.customer.enums.CustomerStatus;

import jakarta.validation.constraints.Pattern;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	// JPQL을 사용하여 이름 또는 이메일에 키워드가 포함된 데이터를 조회
	@Query("SELECT c FROM Customer c WHERE "
		+ "(:keyword IS NULL OR c.name LIKE CONCAT('%', :keyword, '%') "
		+ "OR c.email LIKE CONCAT('%', :keyword, '%')) "
		+ "AND (:status IS NULL OR c.status = :status)")
	Page<Customer> searchCustomer(
		@Param("keyword") String keyword,
		@Param("status") CustomerStatus status,
		Pageable pageable
	);

	boolean existsByEmail(String email);

	boolean existsByPhoneNumber(
		@Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 형식은 010-XXXX-XXXX여야 합니다.") String phoneNumber);
}
