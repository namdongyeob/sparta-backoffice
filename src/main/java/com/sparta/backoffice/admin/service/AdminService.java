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

@Service
@RequiredArgsConstructor
public class AdminService {
	private final AdminRepository adminRepository;
	private final PasswordEncoder passwordEncoder;

	// 회원가입 (이메일 중복 체크, 비밀번호 암호화)
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

	// 로그인 (이메일/비밀번호 검증, 계정 상태 확인)
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

	// 특정 관리자 상세 조회
	@Transactional(readOnly = true)
	public AdminGetResponse getAdmin(AdminInfo adminInfo, Long adminId) {
		validateSuperAdmin(adminInfo);
		return AdminGetResponse.from(findAdmin(adminId));
	}

	// 관리자 목록 페이징 및 필터 조회
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

	// 내 프로필 조회
	@Transactional(readOnly = true)
	public AdminGetMeResponse getMe(AdminInfo adminInfo) {
		Admin admin = findAdmin(adminInfo.getId());
		return AdminGetMeResponse.from(admin);
	}

	// 관리자 가입 승인 (PENDING -> ACTIVE)
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

	// 관리자 가입 거절 (PENDING -> REJECTED)
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

	// 내 프로필 정보 수정 (이메일 중복 검사 포함)
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

	// 특정 관리자 역할(Role) 변경
	@Transactional
	public AdminRoleUpdateResponse updateAdminRole(AdminInfo adminInfo, Long adminId, AdminRoleUpdateRequest request) {
		validateSuperAdmin(adminInfo);
		Admin admin = findAdmin(adminId);
		admin.updateRole(request.getRole());
		return AdminRoleUpdateResponse.from(admin);
	}

	// 비밀번호 변경 (기존 비밀번호 확인 로직 포함)
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

	// 특정 관리자 정보 수정 (이메일 중복 검사 포함)
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

	// 특정 관리자 상태(Status) 변경
	@Transactional
	public AdminStatusUpdateResponse updateAdminStatus(AdminInfo adminInfo, Long adminId,
		AdminStatusUpdateRequest request) {
		validateSuperAdmin(adminInfo);
		Admin admin = findAdmin(adminId);
		admin.updateStatus(request.getStatus());
		return AdminStatusUpdateResponse.from(admin);
	}

	// 관리자 삭제(탈퇴)
	@Transactional
	public void deleteAdmin(AdminInfo adminInfo, Long adminId) {
		validateSuperAdmin(adminInfo);
		Admin admin = findAdmin(adminId);
		adminRepository.delete(admin);
	}

	// 공통 오류
	public Admin findAdmin(Long adminId) {
		return adminRepository.findById(adminId).orElseThrow(
			() -> new CustomException(ErrorCode.ADMIN_NOT_FOUND));
	}

	public void validateSuperAdmin(AdminInfo adminInfo) {
		if (adminInfo.getAdminRole() != AdminRole.SUPER_ADMIN) {
			throw new CustomException(ErrorCode.ADMIN_ACCESS_DENIED);
		}
	}
}