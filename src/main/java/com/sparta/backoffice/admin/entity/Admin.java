package com.sparta.backoffice.admin.entity;

import com.sparta.backoffice.admin.enums.AdminRole;
import com.sparta.backoffice.admin.enums.AdminStatus;
import com.sparta.backoffice.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import org.hibernate.annotations.SoftDelete;

@SoftDelete(columnName = "is_deleted")
@Getter
@Entity
@Table(name = "admins")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(name = "phone_number")
	private String phoneNumber;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AdminRole role;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AdminStatus status;

	private LocalDateTime approvedAt;

	private LocalDateTime rejectedAt;

	private String rejectionReason;

	public Admin(String name, String email, String password,
		String phoneNumber, AdminRole role) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.phoneNumber = phoneNumber;
		this.role = role;
		this.status = AdminStatus.PENDING;
	}

	// 상태 변경
	public void updateStatus(AdminStatus status) {
		this.status = status;
	}

	// 역할 변경
	public void updateRole(AdminRole role) {
		this.role = role;
	}

	// 정보 수정
	public void updateInfo(String name, String email, String phoneNumber) {
		if (name != null)
			this.name = name;
		if (email != null)
			this.email = email;
		if (phoneNumber != null)
			this.phoneNumber = phoneNumber;
	}

	// 비밀번호 변경
	public void updatePassword(String password) {
		this.password = password;
	}

	// 승인 (PENDING -> ACTIVE)
	public void approve() {
		this.status = AdminStatus.ACTIVE;
		this.approvedAt = LocalDateTime.now();
	}

	// 거부 (PENDING -> REJECTED)
	public void reject(String rejectionReason) {
		this.status = AdminStatus.REJECTED;
		this.rejectedAt = LocalDateTime.now();
		this.rejectionReason = rejectionReason;
	}
}