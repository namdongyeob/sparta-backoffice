package com.sparta.backoffice.customer.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.backoffice.common.exception.CustomException;
import com.sparta.backoffice.common.exception.ErrorCode;
import com.sparta.backoffice.customer.dto.CustomerGetOneResponse;
import com.sparta.backoffice.customer.dto.CustomerGetRequest;
import com.sparta.backoffice.customer.dto.CustomerGetResponse;
import com.sparta.backoffice.customer.dto.CustomerStatusUpdateRequest;
import com.sparta.backoffice.customer.dto.CustomerStatusUpdateResponse;
import com.sparta.backoffice.customer.dto.CustomerUpdateRequest;
import com.sparta.backoffice.customer.dto.CustomerUpdateResponse;
import com.sparta.backoffice.customer.entity.Customer;
import com.sparta.backoffice.customer.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

/**
 * 고객 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class CustomerService {

	private final CustomerRepository customerRepository;

	/**
	 * 고객 목록을 페이징 및 필터링하여 조회합니다.
	 * 각 고객의 총 주문 건수와 총 주문 금액을 함께 조회합니다.
	 *
	 * @param request 목록 조회 요청 정보 (페이징, 키워드, 상태 필터)
	 * @return 고객 정보 목록 페이지
	 */
	@Transactional(readOnly = true)
	public Page<CustomerGetResponse> getAll(CustomerGetRequest request) {

		return customerRepository.searchCustomer(
			request.getKeyword(),
			request.getStatus(),
			request.getPageable()
		).map(customer -> {
			long totalOrderCount = customerRepository.countOrdersByCustomerId(customer.getId());
			long totalOrderAmount = customerRepository.sumOrderAmountByCustomerId(customer.getId());
			return CustomerGetResponse.from(customer, totalOrderCount, totalOrderAmount);
		});

	}

	/**
	 * 특정 고객의 상세 정보를 조회합니다.
	 * 고객의 총 주문 건수와 총 주문 금액을 함께 조회합니다.
	 *
	 * @param customerId 조회할 고객의 ID
	 * @return 고객 상세 정보 응답 객체
	 */
	@Transactional(readOnly = true)
	public CustomerGetOneResponse getOne(Long customerId) {
		Customer customer = findCustomer(customerId);
		long totalOrderCount = customerRepository.countOrdersByCustomerId(customerId);
		long totalOrderAmount = customerRepository.sumOrderAmountByCustomerId(customerId);
		return CustomerGetOneResponse.from(customer, totalOrderCount, totalOrderAmount);
	}

	/**
	 * 고객 정보를 수정합니다.
	 * 이메일 또는 전화번호 변경 시 중복 여부를 확인합니다.
	 *
	 * @param customerId 수정할 고객의 ID
	 * @param request    수정할 고객 정보
	 * @return 수정된 고객 정보
	 * @throws CustomException 이메일 또는 전화번호가 중복될 경우
	 */
	@Transactional
	public CustomerUpdateResponse update(Long customerId, CustomerUpdateRequest request) {
		Customer customer = findCustomer(customerId);

		if (!customer.getEmail().equals(request.getEmail())) {
			if (customerRepository.existsByEmail(request.getEmail())) {
				throw new CustomException(ErrorCode.CUSTOMER_EMAIL_DUPLICATED);
			}
		}

		if (!customer.getPhoneNumber().equals(request.getPhoneNumber())) {
			if (customerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
				throw new CustomException(ErrorCode.CUSTOMER_PHONE_NUMBER_DUPLICATED);
			}
		}
		customer.updateInfo(
			request.getName(),
			request.getEmail(),
			request.getPhoneNumber()
		);

		return CustomerUpdateResponse.from(customer);
	}

	/**
	 * 고객의 상태를 변경합니다.
	 *
	 * @param customerId 상태를 변경할 고객의 ID
	 * @param request    새로운 상태 정보
	 * @return 상태가 변경된 고객 정보
	 */
	@Transactional
	public CustomerStatusUpdateResponse updateStatus(Long customerId, CustomerStatusUpdateRequest request) {
		Customer customer = findCustomer(customerId);

		customer.updateStatus(
			request.getStatus()
		);

		return CustomerStatusUpdateResponse.from(customer);
	}

	/**
	 * 고객 정보를 시스템에서 삭제합니다.
	 *
	 * @param customerId 삭제할 고객의 ID
	 */
	@Transactional
	public void delete(Long customerId) {
		Customer customer = findCustomer(customerId);

		customerRepository.delete(customer);
	}

	/**
	 * 고객 ID로 엔티티를 조회합니다.
	 *
	 * @param customerId 조회할 고객의 ID
	 * @return 고객 엔티티
	 * @throws CustomException 고객을 찾을 수 없는 경우
	 */
	public Customer findCustomer(Long customerId) {
		return customerRepository.findById(customerId).orElseThrow(
			() -> new CustomException(ErrorCode.CUSTOMER_NOT_FOUND)
		);
	}

}