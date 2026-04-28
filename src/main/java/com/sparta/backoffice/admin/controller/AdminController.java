package com.sparta.backoffice.admin.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.backoffice.admin.dto.AdminGetAllRequest;
import com.sparta.backoffice.admin.dto.AdminGetMeResponse;
import com.sparta.backoffice.admin.dto.AdminGetResponse;
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
import com.sparta.backoffice.admin.service.AdminService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admins")
public class AdminController {
	private final AdminService adminService;

	// 관리자 회원가입
	@PostMapping("/signup")
	public ResponseEntity<AdminSignupResponse> signup(@Valid @RequestBody AdminSignupRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(adminService.signup(request));
	}

	// 특정 관리자 상세 조회
	@GetMapping("/{id}")
	public ResponseEntity<AdminGetResponse> getAdmin(
		@PathVariable Long id
	) {
		return ResponseEntity.status(HttpStatus.OK).body(adminService.getAdmin(id));
	}

	// 관리자 목록 페이징 및 필터 조회
	@GetMapping
	public ResponseEntity<Page<AdminGetResponse>> getAdmins(
		@ModelAttribute AdminGetAllRequest request
	) {
		return ResponseEntity.status(HttpStatus.OK).body(adminService.getAdmins(request));
	}

	// 내 프로필 조회
	@GetMapping("/me")
	public ResponseEntity<AdminGetMeResponse> getMe(HttpSession session) {
		return ResponseEntity.status(HttpStatus.OK).body(adminService.getMe(session));
	}

	// 관리자 가입 승인 (슈퍼 관리자 권한 필요)
	@PostMapping("/{id}/approve")
	public ResponseEntity<AdminGetResponse> approveAdmin(
		@PathVariable Long id
	) {
		return ResponseEntity.status(HttpStatus.OK).body(adminService.approveAdmin(id));
	}

	// 관리자 가입 거절 (슈퍼 관리자 권한 필요)
	@PostMapping("/{id}/reject")
	public ResponseEntity<AdminGetResponse> rejectAdmin(
		@PathVariable Long id,
		@Valid @RequestBody AdminRejectRequest request
	) {
		return ResponseEntity.status(HttpStatus.OK).body(adminService.rejectAdmin(id, request));
	}

	// 내 프로필 정보 수정
	@PatchMapping("/me")
	public ResponseEntity<AdminUpdateMeResponse> updateMe(
		HttpSession session,
		@Valid @RequestBody AdminUpdateMeRequest request
	) {
		return ResponseEntity.status(HttpStatus.OK).body(adminService.updateMe(session, request));
	}

	// 특정 관리자 역할(Role) 변경
	@PatchMapping("/{id}/role")
	public ResponseEntity<AdminRoleUpdateResponse> updateAdminRole(
		@PathVariable Long id,
		@Valid @RequestBody AdminRoleUpdateRequest request
	) {
		return ResponseEntity.status(HttpStatus.OK).body(adminService.updateAdminRole(id, request));
	}

	// 내 비밀번호 변경
	@PatchMapping("/me/password")
	public ResponseEntity<Void> updatePassword(
		HttpSession session,
		@Valid @RequestBody AdminPasswordUpdateRequest request
	) {
		adminService.updatePassword(session, request);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	// 특정 관리자 정보 수정
	@PatchMapping("/{id}")
	public ResponseEntity<AdminUpdateResponse> updateAdmin(
		@PathVariable Long id,
		@Valid @RequestBody AdminUpdateRequest request
	) {
		return ResponseEntity.status(HttpStatus.OK).body(adminService.updateAdmin(id, request));
	}

	// 특정 관리자 상태(Status) 변경
	@PatchMapping("/{id}/status")
	public ResponseEntity<AdminStatusUpdateResponse> updateAdminStatus(
		@PathVariable Long id,
		@Valid @RequestBody AdminStatusUpdateRequest request
	) {
		return ResponseEntity.status(HttpStatus.OK).body(adminService.updateAdminStatus(id, request));
	}

	// 관리자 삭제(탈퇴)
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteAdmin(
		@PathVariable Long id
	) {
		adminService.deleteAdmin(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}