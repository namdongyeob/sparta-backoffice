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
		+ "(:keyword IS NULL OR c.name LIKE CONCAT('%', :keyword, '%') "
		+ "OR c.email LIKE CONCAT('%', :keyword, '%')) "
		+ "AND (:status IS NULL OR c.status = :status)")
	Page<Customer> searchCustomer(
		@Param("keyword") String keyword,
		@Param("status") CustomerStatus status,
		Pageable pageable
	);
	// 특정 고객의 취소되지 않은 주문 수 조회
	@Query("SELECT COUNT(o) FROM Order o WHERE o.customer.id = :customerId AND o.status != 'CANCELLED'")
	long countOrdersByCustomerId(@Param("customerId") Long customerId);

	// 특정 고객의 취소되지 않은 주문 총액 조회 (주문 없으면 0 반환)
	@Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o WHERE o.customer.id = :customerId AND o.status != 'CANCELLED'")
	long sumOrderAmountByCustomerId(@Param("customerId") Long customerId);

	boolean existsByEmail(String email);

	boolean existsByPhoneNumber(String phoneNumber);
}
