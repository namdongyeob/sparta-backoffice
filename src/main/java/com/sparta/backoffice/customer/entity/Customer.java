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

@Getter
@Entity
@Table(name = "customers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE customers SET deleted_at = NOW() WHERE id = ?")  // 소프트딜리트는 시간기록이 안됨  지금 사용한 방식이 삭제 조회 가능
@SQLRestriction("deleted_at IS NULL")  // 삭제시간이 null인지 아닌지
public class Customer extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false, unique = true)
	private String phoneNumber;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CustomerStatus status;

	// 정보 수정
	public void updateInfo(String name, String email, String phoneNumber) {
		this.name = name;
		this.email = email;
		this.phoneNumber = phoneNumber;
	}

	// 상태 변경
	public void updateStatus(CustomerStatus status) {
		this.status = status;
	}

}
