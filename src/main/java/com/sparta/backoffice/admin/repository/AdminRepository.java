package com.sparta.backoffice.admin.repository;

import com.sparta.backoffice.admin.entity.Admin;
import com.sparta.backoffice.admin.enums.AdminRole;
import com.sparta.backoffice.admin.enums.AdminStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
	@Query("SELECT a FROM Admin a WHERE "
		+ "(:keyword IS NULL OR a.name LIKE CONCAT('%', :keyword, '%') "
		+ "OR a.email LIKE CONCAT('%', :keyword, '%')) "
		+ "AND (:role IS NULL OR a.role = :role) "
		+ "AND (:status IS NULL OR a.status = :status)")
	Page<Admin> searchAdmins(
		@Param("keyword") String keyword,
		@Param("role") AdminRole role,
		@Param("status") AdminStatus status,
		Pageable pageable
	);

	boolean existsByEmail(String email);

	Optional<Admin> findByEmail(String email);
}
