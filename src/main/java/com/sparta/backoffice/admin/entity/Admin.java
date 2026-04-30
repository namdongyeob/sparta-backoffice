package com.sparta.backoffice.admin.entity;

import com.sparta.backoffice.admin.enums.AdminRole;
import com.sparta.backoffice.admin.enums.AdminStatus;
import com.sparta.backoffice.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

/**
 * 관리자 정보를 나타내는 엔티티 클래스.
 * 데이터베이스의 'admins' 테이블과 매핑됩니다.
 * 소프트 삭제(Soft Delete)가 적용되어 있습니다.
 */
@SQLDelete(sql = "UPDATE admins SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Entity
@Table(name = "admins")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin extends BaseEntity {

	/**
	 * 관리자의 고유 식별자 (기본 키)
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 관리자의 이름
	 */
	@Column(nullable = false)
	private String name;

	/**
	 * 관리자의 이메일 주소 (유일값)
	 */
	@Column(nullable = false, unique = true)
	private String email;

	/**
	 * 관리자의 암호화된 비밀번호
	 */
	@Column(nullable = false)
	private String password;

	/**
	 * 관리자의 전화번호
	 */
	@Column(name = "phone_number")
	private String phoneNumber;

	/**
	 * 관리자의 역할 (예: CS_ADMIN, SUPER_ADMIN 등)
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AdminRole role;

	/**
	 * 관리자의 현재 상태 (예: PENDING, ACTIVE, REJECTED 등)
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AdminStatus status;

	/**
	 * 관리자 가입이 승인된 일시
	 */
	private LocalDateTime approvedAt;

	/**
	 * 관리자 가입이 거부된 일시
	 */
	private LocalDateTime rejectedAt;

	/**
	 * 관리자 가입 거부 사유
	 */
	private String rejectionReason;

	/**
	 * 새로운 관리자 엔티티를 생성하는 생성자.
	 * 초기 상태는 PENDING(대기 중)으로 설정됩니다.
	 *
	 * @param name        이름
	 * @param email       이메일
	 * @param password    암호화된 비밀번호
	 * @param phoneNumber 전화번호
	 * @param role        부여할 역할
	 */
	public Admin(String name, String email, String password,
		String phoneNumber, AdminRole role) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.phoneNumber = phoneNumber;
		this.role = role;
		this.status = AdminStatus.PENDING;
	}

	/**
	 * 관리자의 상태를 변경합니다.
	 *
	 * @param status 변경할 새로운 상태
	 */
	public void updateStatus(AdminStatus status) {
		this.status = status;
	}

	/**
	 * 관리자의 역할을 변경합니다.
	 *
	 * @param role 변경할 새로운 역할
	 */
	public void updateRole(AdminRole role) {
		this.role = role;
	}

	/**
	 * 관리자의 기본 정보를 수정합니다.
	 * null이 아닌 값만 업데이트됩니다.
	 *
	 * @param name        수정할 이름
	 * @param email       수정할 이메일
	 * @param phoneNumber 수정할 전화번호
	 */
	public void updateInfo(String name, String email, String phoneNumber) {
		if (name != null)
			this.name = name;
		if (email != null)
			this.email = email;
		if (phoneNumber != null)
			this.phoneNumber = phoneNumber;
	}

	/**
	 * 관리자의 비밀번호를 변경합니다.
	 *
	 * @param password 변경할 암호화된 비밀번호
	 */
	public void updatePassword(String password) {
		this.password = password;
	}

	/**
	 * 관리자의 가입을 승인합니다.
	 * 상태를 ACTIVE로 변경하고 승인 일시를 현재 시간으로 설정합니다.
	 */
	public void approve() {
		this.status = AdminStatus.ACTIVE;
		this.approvedAt = LocalDateTime.now();
	}

	/**
	 * 관리자의 가입을 거부합니다.
	 * 상태를 REJECTED로 변경하고, 거부 일시와 거부 사유를 설정합니다.
	 *
	 * @param rejectionReason 가입 거부 사유
	 */
	public void reject(String rejectionReason) {
		this.status = AdminStatus.REJECTED;
		this.rejectedAt = LocalDateTime.now();
		this.rejectionReason = rejectionReason;
	}
}
