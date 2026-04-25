package com.sparta.backoffice.admin.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import com.sparta.backoffice.admin.dto.AdminUpdateMeRequest;
import com.sparta.backoffice.admin.dto.AdminUpdateMeResponse;
import com.sparta.backoffice.admin.entity.Admin;
import com.sparta.backoffice.admin.enums.AdminStatus;
import com.sparta.backoffice.admin.repository.AdminRepository;
import com.sparta.backoffice.common.config.PasswordEncoder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
	private final AdminRepository adminRepository;
	private final PasswordEncoder passwordEncoder;

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

	@Transactional(readOnly = true)
	public Admin login(AdminLoginRequest request) {
		Admin admin = adminRepository.findByEmail(request.getEmail()).orElseThrow(
			() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));
		if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
			throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
		}
		switch (admin.getStatus()) {
			case PENDING -> throw new IllegalArgumentException("승인 대기 중인 계정입니다.");
			case REJECTED -> throw new IllegalArgumentException("거부된 계정입니다.");
			case SUSPENDED -> throw new IllegalArgumentException("정지된 계정입니다.");
			case INACTIVE -> throw new IllegalArgumentException("비활성화된 계정입니다.");
		}
		return admin;

	}

	@Transactional(readOnly = true)
	public AdminGetResponse getAdmin(Long adminId) {
		Admin admin = adminRepository.findById(adminId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 관리자입니다."));

		return AdminGetResponse.from(admin);
	}

	@Transactional(readOnly = true)
	public Page<AdminGetResponse> getAdmins(AdminGetAllRequest request) {
		String sortBy = request.getSortBy() == null || request.getSortBy().isBlank()
			? "createdAt" : request.getSortBy();
		Sort.Direction direction = "asc".equalsIgnoreCase(request.getDirection())
			? Sort.Direction.ASC : Sort.Direction.DESC;

		Pageable pageable = PageRequest.of(
			request.getPage() - 1,
			request.getSize(),
			Sort.by(direction, sortBy)
		);

		return adminRepository.searchAdmins(
			request.getKeyword(),
			request.getRole(),
			request.getStatus(),
			pageable
		).map(AdminGetResponse::from);

	}

	@Transactional(readOnly = true)
	public AdminGetMeResponse getMe(Long adminId) {
		Admin admin = adminRepository.findById(adminId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 관리자입니다."));

		return AdminGetMeResponse.from(admin);
	}

	@Transactional
	public AdminGetResponse approveAdmin(Long adminId) {
		Admin admin = adminRepository.findById(adminId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않은 관리자입니다."));

		if (admin.getStatus() != AdminStatus.PENDING) {
			throw new IllegalStateException("승인 대기 중인 관리자만 승인할 수 있습니다.");
		}

		admin.approve();
		return AdminGetResponse.from(admin);
	}

	@Transactional
	public AdminGetResponse rejectAdmin(Long adminId, AdminRejectRequest request) {
		Admin admin = adminRepository.findById(adminId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않은 관리자입니다."));

		if (admin.getStatus() != AdminStatus.PENDING) {
			throw new IllegalStateException("승인 대기 중인 관리자만 거절할 수 있습니다.");
		}

		admin.reject(request.getRejectionReason());
		return AdminGetResponse.from(admin);
	}

	@Transactional
	public AdminUpdateMeResponse updateMe(Long adminId, AdminUpdateMeRequest request) {
		Admin admin = adminRepository.findById(adminId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않은 관리자입니다."));
		String newEmail = request.getEmail();
		if (newEmail != null && !newEmail.isBlank() && !newEmail.equals(admin.getEmail())) {
			if (adminRepository.existsByEmail(newEmail)) {
				throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
			}
		}
		admin.updateInfo(request.getName(), newEmail, request.getPhoneNumber());
		return AdminUpdateMeResponse.from(admin);
	}

	@Transactional
	public AdminRoleUpdateResponse updateAdminRole(Long adminId, AdminRoleUpdateRequest request) {
		Admin admin = adminRepository.findById(adminId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 관리자입니다."));

		admin.updateRole(request.getRole());
		return AdminRoleUpdateResponse.from(admin);
	}

	@Transactional
	public void updatePassword(Long adminId, AdminPasswordUpdateRequest request) {
		Admin admin = adminRepository.findById(adminId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 관리자입니다."));

		if (!passwordEncoder.matches(request.getCurrentPassword(), admin.getPassword())) {
			throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
		}

		if (request.getCurrentPassword().equals(request.getNewPassword())) {
			throw new IllegalArgumentException("새 비밀번호는 기존 비밀번호와 달라야 합니다.");
		}

		String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
		admin.updatePassword(encodedNewPassword);
	}
}