package com.sparta.backoffice.customer.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.backoffice.customer.dto.CustomerGetOneResponse;
import com.sparta.backoffice.customer.dto.CustomerGetRequest;
import com.sparta.backoffice.customer.dto.CustomerGetResponse;
import com.sparta.backoffice.customer.dto.CustomerStatusUpdateRequest;
import com.sparta.backoffice.customer.dto.CustomerStatusUpdateResponse;
import com.sparta.backoffice.customer.dto.CustomerUpdateRequest;
import com.sparta.backoffice.customer.dto.CustomerUpdateResponse;
import com.sparta.backoffice.customer.service.CustomerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 고객 관련 API를 처리하는 컨트롤러 클래스
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {
	private final CustomerService customerService;

	/**
	 * 고객 목록을 페이징 및 필터링하여 조회합니다.
	 *
	 * @param request 조회 조건 (페이징, 필터링)
	 * @return 고객 목록 페이지와 함께 상태 코드 200 (OK)
	 */
	@GetMapping
	public ResponseEntity<Page<CustomerGetResponse>> getAll(
		@ModelAttribute CustomerGetRequest request
	) {
		return ResponseEntity.status(HttpStatus.OK).body(customerService.getAll(request));
	}

	/**
	 * 특정 고객의 상세 정보를 조회합니다.
	 *
	 * @param customerId 조회할 고객 ID
	 * @return 고객 상세 정보와 함께 상태 코드 200 (OK)
	 */
	@GetMapping("/{customerId}")
	public ResponseEntity<CustomerGetOneResponse> getOne(
		@PathVariable Long customerId
	) {
		return ResponseEntity.status(HttpStatus.OK).body(customerService.getOne(customerId));
	}

	/**
	 * 특정 고객의 정보를 수정합니다.
	 *
	 * @param customerId 수정할 고객 ID
	 * @param request    수정할 고객 정보
	 * @return 수정된 고객 정보와 함께 상태 코드 200 (OK)
	 */
	@PatchMapping("/{customerId}")
	public ResponseEntity<CustomerUpdateResponse> update(
		@PathVariable Long customerId, @Valid @RequestBody CustomerUpdateRequest request
	) {
		return ResponseEntity.status(HttpStatus.OK).body(customerService.update(customerId, request));
	}

	/**
	 * 특정 고객의 상태를 수정합니다.
	 *
	 * @param customerId 상태를 수정할 고객 ID
	 * @param request    새로운 상태 정보
	 * @return 수정된 고객 정보와 함께 상태 코드 200 (OK)
	 */
	@PatchMapping("/{customerId}/status")
	public ResponseEntity<CustomerStatusUpdateResponse> updateStatus(
		@PathVariable Long customerId,
		@Valid @RequestBody CustomerStatusUpdateRequest request
	) {
		return ResponseEntity.status(HttpStatus.OK).body(customerService.updateStatus(customerId, request));
	}

	/**
	 * 특정 고객을 삭제합니다.
	 *
	 * @param customerId 삭제할 고객 ID
	 * @return 상태 코드 204 (No Content)
	 */
	@DeleteMapping("/{customerId}")
	public ResponseEntity<Void> delete(
		@PathVariable Long customerId
	) {
		customerService.delete(customerId);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}