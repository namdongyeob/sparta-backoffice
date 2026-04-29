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
import com.sparta.backoffice.admin.dto.AdminSignupResponse;
import com.sparta.backoffice.admin.dto.AdminSignupRequest;
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
			throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
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
			() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));
		if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
			throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
		}
		if (admin.getStatus() != AdminStatus.ACTIVE){
			switch (admin.getStatus()) {
				case PENDING -> throw new IllegalArgumentException("승인 대기 중인 계정입니다.");
				case REJECTED -> throw new IllegalArgumentException("거부된 계정입니다.");
				case SUSPENDED -> throw new IllegalArgumentException("정지된 계정입니다.");
				case INACTIVE -> throw new IllegalArgumentException("비활성화된 계정입니다.");
				default ->  throw new IllegalArgumentException("로그인 할수 없는 계정입니다.");
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
			throw new IllegalStateException("승인 대기 중인 관리자만 승인할 수 있습니다.");
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
			throw new IllegalStateException("승인 대기 중인 관리자만 거절할 수 있습니다.");
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
				throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
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
			throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
		}
		if (request.getCurrentPassword().equals(request.getNewPassword())) {
			throw new IllegalArgumentException("새 비밀번호는 기존 비밀번호와 달라야 합니다.");
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
				throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
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
		return adminRepository.findById(adminId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 관리자입니다."));
	}

	public void validateSuperAdmin(AdminInfo adminInfo) {
		if (adminInfo.getAdminRole() != AdminRole.SUPER_ADMIN) {
			throw new IllegalArgumentException("슈퍼 관리자만 접근 가능합니다.");
		}
	}
}