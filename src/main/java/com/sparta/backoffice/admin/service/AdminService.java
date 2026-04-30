package com.sparta.backoffice.admin.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.backoffice.admin.dto.AdminGetAllRequest;
import com.sparta.backoffice.admin.dto.AdminGetMeResponse;
import com.sparta.backoffice.admin.dto.AdminGetResponse;
import com.sparta.backoffice.admin.dto.AdminLoginRequest;
import com.sparta.backoffice.admin.dto.AdminPasswordUpdateRequest;
import com.sparta.backoffice.admin.dto.AdminRejectRequest;
import com.sparta.backoffice.admin.dto.AdminRoleUpdateRequest;
import com.sparta.backoffice.admin.dto.AdminRoleUpdateResponse;
import com.sparta.backoffice.admin.dto.AdminSignupRequest;
import com.sparta.backoffice.admin.dto.AdminSignupResponse;
import com.sparta.backoffice.admin.dto.AdminStatusUpdateRequest;
import com.sparta.backoffice.admin.dto.AdminStatusUpdateResponse;
import com.sparta.backoffice.admin.dto.AdminUpdateMeRequest;
import com.sparta.backoffice.admin.dto.AdminUpdateMeResponse;
import com.sparta.backoffice.admin.dto.AdminUpdateRequest;
import com.sparta.backoffice.admin.dto.AdminUpdateResponse;
import com.sparta.backoffice.admin.entity.Admin;
import com.sparta.backoffice.admin.enums.AdminRole;
import com.sparta.backoffice.admin.enums.AdminStatus;
import com.sparta.backoffice.admin.repository.AdminRepository;
import com.sparta.backoffice.common.config.PasswordEncoder;
import com.sparta.backoffice.common.dto.AdminInfo;
import com.sparta.backoffice.common.exception.CustomException;
import com.sparta.backoffice.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

/**
 * 관리자 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class AdminService {
	private final AdminRepository adminRepository;
	private final PasswordEncoder passwordEncoder;

	/**
	 * 관리자 회원가입을 처리합니다.
	 *
	 * @param request 회원가입 요청 정보
	 * @return 생성된 관리자 정보
	 * @throws CustomException 이메일이 중복될 경우
	 */
	@Transactional
	public AdminSignupResponse signup(AdminSignupRequest request) {
		if (adminRepository.existsByEmail(request.getEmail())) {
			throw new CustomException(ErrorCode.ADMIN_EMAIL_DUPLICATED);
		}
		String encodedPassword = passwordEncoder.encode(request.getPassword());
		Admin admin = request.toEntity(encodedPassword);
		Admin savedAdmin = adminRepository.save(admin);
		return AdminSignupResponse.from(savedAdmin);
	}

	/**
	 * 관리자 로그인을 처리합니다.
	 * 이메일과 비밀번호를 검증하고 계정 상태를 확인합니다.
	 *
	 * @param request 로그인 요청 정보
	 * @return 로그인에 성공한 관리자 엔티티
	 * @throws CustomException 이메일/비밀번호가 불일치하거나 계정 상태가 활성(ACTIVE)이 아닌 경우
	 */
	@Transactional(readOnly = true)
	public Admin login(AdminLoginRequest request) {
		Admin admin = adminRepository.findByEmail(request.getEmail()).orElseThrow(
			() -> new CustomException(ErrorCode.ADMIN_INVALID_CREDENTIALS));
		if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
			throw new CustomException(ErrorCode.ADMIN_INVALID_CREDENTIALS);
		}
		if (admin.getStatus() != AdminStatus.ACTIVE) {
			switch (admin.getStatus()) {
				case PENDING -> throw new CustomException(ErrorCode.ADMIN_PENDING);
				case REJECTED -> throw new CustomException(ErrorCode.ADMIN_REJECTED);
				case SUSPENDED -> throw new CustomException(ErrorCode.ADMIN_SUSPENDED);
				case INACTIVE -> throw new CustomException(ErrorCode.ADMIN_INACTIVE);
				default -> throw new CustomException(ErrorCode.ADMIN_LOGIN_NOT_ALLOWED);
			}
		}

		return admin;

	}

	/**
	 * 특정 관리자 상세 정보를 조회합니다.
	 * SUPER_ADMIN 권한이 필요합니다.
	 *
	 * @param adminInfo 로그인한 관리자 정보
	 * @param adminId   조회할 관리자의 ID
	 * @return 관리자 상세 정보 응답 객체
	 * @throws CustomException SUPER_ADMIN이 아니거나 대상 관리자를 찾을 수 없는 경우
	 */
	@Transactional(readOnly = true)
	public AdminGetResponse getAdmin(AdminInfo adminInfo, Long adminId) {
		validateSuperAdmin(adminInfo);
		return AdminGetResponse.from(findAdmin(adminId));
	}

	/**
	 * 관리자 목록을 페이징 및 필터링하여 조회합니다.
	 * SUPER_ADMIN 권한이 필요합니다.
	 *
	 * @param adminInfo 로그인한 관리자 정보
	 * @param request   목록 조회 요청 정보 (페이징, 키워드, 역할, 상태 필터)
	 * @return 관리자 정보 목록 페이지
	 * @throws CustomException SUPER_ADMIN 권한이 없는 경우
	 */
	@Transactional(readOnly = true)
	public Page<AdminGetResponse> getAdmins(AdminInfo adminInfo, AdminGetAllRequest request) {
		validateSuperAdmin(adminInfo);
		return adminRepository.searchAdmins(
			request.getKeyword(),
			request.getRole(),
			request.getStatus(),
			request.getPageable()
		).map(AdminGetResponse::from);
	}

	/**
	 * 로그인한 관리자 본인의 프로필 정보를 조회합니다.
	 *
	 * @param adminInfo 로그인한 관리자 정보
	 * @return 본인의 프로필 정보 응답 객체
	 */
	@Transactional(readOnly = true)
	public AdminGetMeResponse getMe(AdminInfo adminInfo) {
		Admin admin = findAdmin(adminInfo.getId());
		return AdminGetMeResponse.from(admin);
	}

	/**
	 * 대기(PENDING) 상태인 관리자의 가입을 승인하여 활성(ACTIVE) 상태로 변경합니다.
	 * SUPER_ADMIN 권한이 필요합니다.
	 *
	 * @param adminInfo 로그인한 관리자 정보
	 * @param adminId   승인할 관리자의 ID
	 * @return 상태가 변경된 관리자 정보
	 * @throws CustomException SUPER_ADMIN이 아니거나 대상 관리자가 PENDING 상태가 아닌 경우
	 */
	@Transactional
	public AdminGetResponse approveAdmin(AdminInfo adminInfo, Long adminId) {
		validateSuperAdmin(adminInfo);
		Admin admin = findAdmin(adminId);
		if (admin.getStatus() != AdminStatus.PENDING) {
			throw new CustomException(ErrorCode.ADMIN_INVALID_STATUS);
		}
		admin.approve();
		return AdminGetResponse.from(admin);
	}

	/**
	 * 대기(PENDING) 상태인 관리자의 가입을 거절하여 거절(REJECTED) 상태로 변경합니다.
	 * SUPER_ADMIN 권한이 필요합니다.
	 *
	 * @param adminInfo 로그인한 관리자 정보
	 * @param adminId   거절할 관리자의 ID
	 * @param request   가입 거절 사유 정보
	 * @return 상태가 변경된 관리자 정보
	 * @throws CustomException SUPER_ADMIN이 아니거나 대상 관리자가 PENDING 상태가 아닌 경우
	 */
	@Transactional
	public AdminGetResponse rejectAdmin(AdminInfo adminInfo, Long adminId, AdminRejectRequest request) {
		validateSuperAdmin(adminInfo);
		Admin admin = findAdmin(adminId);
		if (admin.getStatus() != AdminStatus.PENDING) {
			throw new CustomException(ErrorCode.ADMIN_INVALID_STATUS);
		}
		admin.reject(request.getRejectionReason());
		return AdminGetResponse.from(admin);
	}

	/**
	 * 로그인한 관리자 본인의 정보를 수정합니다.
	 * 이메일 변경 시 중복 검사를 수행합니다.
	 *
	 * @param adminInfo 로그인한 관리자 정보
	 * @param request   정보 수정 요청 데이터
	 * @return 수정된 프로필 정보
	 * @throws CustomException 변경하려는 이메일이 이미 존재하는 경우
	 */
	@Transactional
	public AdminUpdateMeResponse updateMe(AdminInfo adminInfo, AdminUpdateMeRequest request) {
		Admin admin = findAdmin(adminInfo.getId());
		String newEmail = request.getEmail();
		if (newEmail != null && !newEmail.isBlank() && !newEmail.equals(admin.getEmail())) {
			if (adminRepository.existsByEmail(newEmail)) {
				throw new CustomException(ErrorCode.CUSTOMER_EMAIL_DUPLICATED);
			}
		}
		admin.updateInfo(request.getName(), newEmail, request.getPhoneNumber());
		return AdminUpdateMeResponse.from(admin);
	}

	/**
	 * 특정 관리자의 역할(Role)을 변경합니다.
	 * SUPER_ADMIN 권한이 필요합니다.
	 *
	 * @param adminInfo 로그인한 관리자 정보
	 * @param adminId   역할을 변경할 관리자의 ID
	 * @param request   새로운 역할 정보
	 * @return 역할이 변경된 관리자 정보
	 * @throws CustomException SUPER_ADMIN 권한이 없는 경우
	 */
	@Transactional
	public AdminRoleUpdateResponse updateAdminRole(AdminInfo adminInfo, Long adminId, AdminRoleUpdateRequest request) {
		validateSuperAdmin(adminInfo);
		Admin admin = findAdmin(adminId);
		admin.updateRole(request.getRole());
		return AdminRoleUpdateResponse.from(admin);
	}

	/**
	 * 로그인한 관리자의 비밀번호를 변경합니다.
	 * 기존 비밀번호가 일치하는지 확인합니다.
	 *
	 * @param adminInfo 로그인한 관리자 정보
	 * @param request   비밀번호 변경 요청 데이터 (기존 비밀번호, 새 비밀번호)
	 * @throws CustomException 기존 비밀번호가 불일치하거나 새 비밀번호가 기존과 동일한 경우
	 */
	@Transactional
	public void updatePassword(AdminInfo adminInfo, AdminPasswordUpdateRequest request) {
		Admin admin = findAdmin(adminInfo.getId());
		if (!passwordEncoder.matches(request.getCurrentPassword(), admin.getPassword())) {
			throw new CustomException(ErrorCode.ADMIN_PASSWORD_MISMATCH);
		}
		if (request.getCurrentPassword().equals(request.getNewPassword())) {
			throw new CustomException(ErrorCode.ADMIN_SAME_PASSWORD);
		}
		admin.updatePassword(passwordEncoder.encode(request.getNewPassword()));
	}

	/**
	 * 특정 관리자의 정보를 수정합니다.
	 * SUPER_ADMIN 권한이 필요하며, 이메일 변경 시 중복 검사를 수행합니다.
	 *
	 * @param adminInfo 로그인한 관리자 정보
	 * @param adminId   정보를 수정할 관리자의 ID
	 * @param request   정보 수정 요청 데이터
	 * @return 수정된 관리자 정보
	 * @throws CustomException SUPER_ADMIN 권한이 없거나, 변경하려는 이메일이 이미 존재하는 경우
	 */
	@Transactional
	public AdminUpdateResponse updateAdmin(AdminInfo adminInfo, Long adminId, AdminUpdateRequest request) {
		validateSuperAdmin(adminInfo);
		Admin admin = findAdmin(adminId);
		String newEmail = request.getEmail();
		if (newEmail != null && !newEmail.isBlank() && !newEmail.equals(admin.getEmail())) {
			if (adminRepository.existsByEmail(newEmail)) {
				throw new CustomException(ErrorCode.CUSTOMER_EMAIL_DUPLICATED);
			}
		}
		admin.updateInfo(request.getName(), newEmail, request.getPhoneNumber());
		return AdminUpdateResponse.from(admin);
	}

	/**
	 * 특정 관리자의 상태(Status)를 변경합니다.
	 * SUPER_ADMIN 권한이 필요합니다.
	 *
	 * @param adminInfo 로그인한 관리자 정보
	 * @param adminId   상태를 변경할 관리자의 ID
	 * @param request   새로운 상태 정보
	 * @return 상태가 변경된 관리자 정보
	 * @throws CustomException SUPER_ADMIN 권한이 없는 경우
	 */
	@Transactional
	public AdminStatusUpdateResponse updateAdminStatus(AdminInfo adminInfo, Long adminId,
		AdminStatusUpdateRequest request) {
		validateSuperAdmin(adminInfo);
		Admin admin = findAdmin(adminId);
		admin.updateStatus(request.getStatus());
		return AdminStatusUpdateResponse.from(admin);
	}

	/**
	 * 특정 관리자를 시스템에서 삭제(탈퇴 처리)합니다.
	 * SUPER_ADMIN 권한이 필요합니다.
	 *
	 * @param adminInfo 로그인한 관리자 정보
	 * @param adminId   삭제할 관리자의 ID
	 * @throws CustomException SUPER_ADMIN 권한이 없는 경우
	 */
	@Transactional
	public void deleteAdmin(AdminInfo adminInfo, Long adminId) {
		validateSuperAdmin(adminInfo);
		Admin admin = findAdmin(adminId);
		adminRepository.delete(admin);
	}

	/**
	 * 관리자 ID로 엔티티를 조회합니다.
	 *
	 * @param adminId 조회할 관리자의 ID
	 * @return 관리자 엔티티
	 * @throws CustomException 관리자를 찾을 수 없는 경우
	 */
	public Admin findAdmin(Long adminId) {
		return adminRepository.findById(adminId).orElseThrow(
			() -> new CustomException(ErrorCode.ADMIN_NOT_FOUND));
	}

	/**
	 * 로그인한 관리자가 SUPER_ADMIN 권한을 가지고 있는지 검증합니다.
	 *
	 * @param adminInfo 검증할 관리자 정보
	 * @throws CustomException SUPER_ADMIN 권한이 없는 경우
	 */
	public void validateSuperAdmin(AdminInfo adminInfo) {
		if (adminInfo.getAdminRole() != AdminRole.SUPER_ADMIN) {
			throw new CustomException(ErrorCode.ADMIN_ACCESS_DENIED);
		}
	}
}