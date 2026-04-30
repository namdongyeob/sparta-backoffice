package com.sparta.backoffice.customer.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.sparta.backoffice.common.entity.BaseEntity;
import com.sparta.backoffice.customer.enums.CustomerStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 고객 정보를 나타내는 엔티티 클래스.
 * 데이터베이스의 'customers' 테이블과 매핑됩니다.
 * 소프트 삭제(Soft Delete)가 적용되어 있습니다.
 */
@Getter
@Entity
@Table(name = "customers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE customers SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Customer extends BaseEntity {
	/**
	 * 고객의 고유 식별자 (기본 키)
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 고객의 이름
	 */
	@Column(nullable = false)
	private String name;

	/**
	 * 고객의 이메일 주소 (유일값)
	 */
	@Column(nullable = false, unique = true)
	private String email;

	/**
	 * 고객의 전화번호 (유일값)
	 */
	@Column(nullable = false, unique = true)
	private String phoneNumber;

	/**
	 * 고객의 현재 상태 (예: ACTIVE, INACTIVE)
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CustomerStatus status;

	/**
	 * 고객의 기본 정보를 수정합니다.
	 *
	 * @param name        수정할 고객명
	 * @param email       수정할 이메일 주소
	 * @param phoneNumber 수정할 전화번호
	 */
	public void updateInfo(String name, String email, String phoneNumber) {
		this.name = name;
		this.email = email;
		this.phoneNumber = phoneNumber;
	}

	/**
	 * 고객의 상태를 수정합니다.
	 *
	 * @param status 변경할 새로운 상태
	 */
	public void updateStatus(CustomerStatus status) {
		this.status = status;
	}

}