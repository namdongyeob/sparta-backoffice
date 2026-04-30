package com.sparta.backoffice.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 모든 엔티티가 공통으로 상속받는 기본 엔티티 클래스입니다.
 * 생성일시, 수정일시, 삭제일시 필드를 포함하며, JPA Auditing 기능을 사용하여 자동으로 값을 관리합니다.
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
	/**
	 * 엔티티가 생성된 일시를 저장합니다.
	 */
	@CreatedDate
	private LocalDateTime createdAt;

	/**
	 * 엔티티가 마지막으로 수정된 일시를 저장합니다.
	 */
	@LastModifiedDate
	private LocalDateTime modifiedAt;

	/**
	 * 엔티티가 소프트 삭제(Soft Delete)된 일시를 저장합니다.
	 */
	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;
}