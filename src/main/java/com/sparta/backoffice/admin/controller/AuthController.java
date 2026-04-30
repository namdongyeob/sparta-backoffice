package com.sparta.backoffice.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.backoffice.admin.dto.AdminLoginRequest;
import com.sparta.backoffice.admin.entity.Admin;
import com.sparta.backoffice.admin.service.AdminService;
import com.sparta.backoffice.common.constant.SessionConst;
import com.sparta.backoffice.common.dto.AdminInfo;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 관리자 인증 관련 API를 처리하는 컨트롤러 클래스
 */
@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AuthController {
	private final AdminService adminService;

	/**
	 * 관리자 로그인을 처리합니다.
	 * 성공 시 세션에 관리자 정보를 저장합니다.
	 *
	 * @param request 로그인 요청 정보
	 * @param session HTTP 세션 객체
	 * @return 상태 코드 204 (No Content)
	 */
	@PostMapping("/login")
	public ResponseEntity<Void> login(@Valid @RequestBody AdminLoginRequest request, HttpSession session) {
		Admin admin = adminService.login(request);
		session.setAttribute(SessionConst.ADMIN_INFO, new AdminInfo(
			admin.getId(),
			admin.getEmail(),
			admin.getRole()
		));
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	/**
	 * 관리자 로그아웃을 처리합니다.
	 * 현재 세션을 무효화합니다.
	 *
	 * @param session HTTP 세션 객체
	 * @return 상태 코드 204 (No Content)
	 */
	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpSession session) {
		session.invalidate();
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}