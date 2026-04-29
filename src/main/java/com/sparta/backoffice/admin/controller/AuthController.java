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
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AuthController {
	private final AdminService adminService;

	// 관리자 로그인
	@PostMapping("/login")
	public ResponseEntity<Void> login(@RequestBody AdminLoginRequest request, HttpSession session) {
		Admin admin = adminService.login(request);
		session.setAttribute(SessionConst.ADMIN_INFO, new AdminInfo(
			admin.getId(),
			admin.getEmail(),
			admin.getRole()
		));
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	// 관리자 로그아웃
	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpSession session) {
		session.invalidate();
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}