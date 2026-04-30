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
import org.springframework.web.bind.annotation.SessionAttribute;

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
import com.sparta.backoffice.common.constant.SessionConst;
import com.sparta.backoffice.common.dto.AdminInfo;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 관리자 CRUD 및 기타 관리 기능 관련 API를 처리하는 컨트롤러 클래스
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admins")
public class AdminController {
	private final AdminService adminService;

	/**
	 * 관리자 회원가입을 처리합니다.
	 *
	 * @param request 회원가입 요청 정보
	 * @return 생성된 관리자 정보와 함께 상태 코드 201 (Created)
	 */
	@PostMapping("/signup")
	public ResponseEntity<AdminSignupResponse> signup(@Valid @RequestBody AdminSignupRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(adminService.signup(request));
	}

	/**
	 * 특정 관리자의 상세 정보를 조회합니다. (SUPER_ADMIN 권한 필요)
	 *
	 * @param adminInfo 로그인한 관리자 정보
	 * @param id        조회할 관리자 ID
	 * @return 관리자 상세 정보와 함께 상태 코드 200 (OK)
	 */
	@GetMapping("/{id}")
	public ResponseEntity<AdminGetResponse> getAdmin(
		@SessionAttribute(name = SessionConst.ADMIN_INFO) AdminInfo adminInfo
		, @PathVariable Long id
	) {
		return ResponseEntity.status(HttpStatus.OK).body(adminService.getAdmin(adminInfo, id));
	}

	/**
	 * 관리자 목록을 페이징 및 필터링하여 조회합니다. (SUPER_ADMIN 권한 필요)
	 *
	 * @param adminInfo 로그인한 관리자 정보
	 * @param request   조회 조건 (페이징, 필터링)
	 * @return 관리자 목록 페이지와 함께 상태 코드 200 (OK)
	 */
	@GetMapping
	public ResponseEntity<Page<AdminGetResponse>> getAdmins(
		@SessionAttribute(name = SessionConst.ADMIN_INFO) AdminInfo adminInfo
		, @ModelAttribute AdminGetAllRequest request
	) {
		return ResponseEntity.status(HttpStatus.OK).body(adminService.getAdmins(adminInfo, request));
	}

	/**
	 * 현재 로그인된 관리자의 프로필 정보를 조회합니다.
	 *
	 * @param adminInfo 로그인한 관리자 정보
	 * @return 내 프로필 정보와 함께 상태 코드 200 (OK)
	 */
	@GetMapping("/me")
	public ResponseEntity<AdminGetMeResponse> getMe(
		@SessionAttribute(name = SessionConst.ADMIN_INFO) AdminInfo adminInfo
	) {
		return ResponseEntity.status(HttpStatus.OK).body(adminService.getMe(adminInfo));
	}

	/**
	 * 관리자 가입을 승인합니다. (SUPER_ADMIN 권한 필요)
	 *
	 * @param adminInfo 로그인한 관리자 정보
	 * @param id        승인할 관리자 ID
	 * @return 승인된 관리자 정보와 함께 상태 코드 200 (OK)
	 */
	@PostMapping("/{id}/approve")
	public ResponseEntity<AdminGetResponse> approveAdmin(
		@SessionAttribute(name = SessionConst.ADMIN_INFO) AdminInfo adminInfo
		, @PathVariable Long id
	) {
		return ResponseEntity.status(HttpStatus.OK).body(adminService.approveAdmin(adminInfo, id));
	}

	/**
	 * 관리자 가입을 거절합니다. (SUPER_ADMIN 권한 필요)
	 *
	 * @param adminInfo 로그인한 관리자 정보
	 * @param id        거절할 관리자 ID
	 * @param request   거절 사유
	 * @return 거절 처리된 관리자 정보와 함께 상태 코드 200 (OK)
	 */
	@PostMapping("/{id}/reject")
	public ResponseEntity<AdminGetResponse> rejectAdmin(
		@SessionAttribute(name = SessionConst.ADMIN_INFO) AdminInfo adminInfo
		, @PathVariable Long id,
		@Valid @RequestBody AdminRejectRequest request
	) {
		return ResponseEntity.status(HttpStatus.OK).body(adminService.rejectAdmin(adminInfo, id, request));
	}

	/**
	 * 현재 로그인된 관리자의 프로필 정보를 수정합니다.
	 *
	 * @param adminInfo 로그인한 관리자 정보
	 * @param request   수정할 프로필 정보
	 * @return 수정된 내 프로필 정보와 함께 상태 코드 200 (OK)
	 */
	@PatchMapping("/me")
	public ResponseEntity<AdminUpdateMeResponse> updateMe(
		@SessionAttribute(name = SessionConst.ADMIN_INFO) AdminInfo adminInfo,
		@Valid @RequestBody AdminUpdateMeRequest request
	) {
		return ResponseEntity.status(HttpStatus.OK).body(adminService.updateMe(adminInfo, request));
	}

	/**
	 * 특정 관리자의 역할을 변경합니다. (SUPER_ADMIN 권한 필요)
	 *
	 * @param adminInfo 로그인한 관리자 정보
	 * @param id        역할을 변경할 관리자 ID
	 * @param request   새로운 역할 정보
	 * @return 역할이 변경된 관리자 정보와 함께 상태 코드 200 (OK)
	 */
	@PatchMapping("/{id}/role")
	public ResponseEntity<AdminRoleUpdateResponse> updateAdminRole(
		@SessionAttribute(name = SessionConst.ADMIN_INFO) AdminInfo adminInfo,
		@PathVariable Long id,
		@Valid @RequestBody AdminRoleUpdateRequest request
	) {
		return ResponseEntity.status(HttpStatus.OK).body(adminService.updateAdminRole(adminInfo, id, request));
	}

	/**
	 * 현재 로그인된 관리자의 비밀번호를 변경합니다.
	 *
	 * @param adminInfo 로그인한 관리자 정보
	 * @param request   비밀번호 변경 요청 정보
	 * @return 상태 코드 204 (No Content)
	 */
	@PatchMapping("/me/password")
	public ResponseEntity<Void> updatePassword(
		@SessionAttribute(name = SessionConst.ADMIN_INFO) AdminInfo adminInfo,
		@Valid @RequestBody AdminPasswordUpdateRequest request
	) {
		adminService.updatePassword(adminInfo, request);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	/**
	 * 특정 관리자의 정보를 수정합니다. (SUPER_ADMIN 권한 필요)
	 *
	 * @param adminInfo 로그인한 관리자 정보
	 * @param id        정보를 수정할 관리자 ID
	 * @param request   수정할 관리자 정보
	 * @return 수정된 관리자 정보와 함께 상태 코드 200 (OK)
	 */
	@PatchMapping("/{id}")
	public ResponseEntity<AdminUpdateResponse> updateAdmin(
		@SessionAttribute(name = SessionConst.ADMIN_INFO) AdminInfo adminInfo,
		@PathVariable Long id,
		@Valid @RequestBody AdminUpdateRequest request
	) {
		return ResponseEntity.status(HttpStatus.OK).body(adminService.updateAdmin(adminInfo, id, request));
	}

	/**
	 * 특정 관리자의 상태를 변경합니다. (SUPER_ADMIN 권한 필요)
	 *
	 * @param adminInfo 로그인한 관리자 정보
	 * @param id        상태를 변경할 관리자 ID
	 * @param request   새로운 상태 정보
	 * @return 상태가 변경된 관리자 정보와 함께 상태 코드 200 (OK)
	 */
	@PatchMapping("/{id}/status")
	public ResponseEntity<AdminStatusUpdateResponse> updateAdminStatus(
		@SessionAttribute(name = SessionConst.ADMIN_INFO) AdminInfo adminInfo,
		@PathVariable Long id,
		@Valid @RequestBody AdminStatusUpdateRequest request
	) {
		return ResponseEntity.status(HttpStatus.OK).body(adminService.updateAdminStatus(adminInfo, id, request));
	}

	/**
	 * 특정 관리자를 삭제합니다. (SUPER_ADMIN 권한 필요)
	 *
	 * @param adminInfo 로그인한 관리자 정보
	 * @param id        삭제할 관리자 ID
	 * @return 상태 코드 204 (No Content)
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteAdmin(
		@SessionAttribute(name = SessionConst.ADMIN_INFO) AdminInfo adminInfo,
		@PathVariable Long id
	) {
		adminService.deleteAdmin(adminInfo, id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}