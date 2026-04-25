package com.sparta.backoffice.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.backoffice.admin.dto.AdminLoginRequest;
import com.sparta.backoffice.admin.entity.Admin;
import com.sparta.backoffice.admin.service.AdminService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AuthController {
	private final AdminService adminService;

	@PostMapping("/login")
	public ResponseEntity<Void> login(@RequestBody AdminLoginRequest request, HttpSession session) {
		Admin admin = adminService.login(request);
		session.setAttribute("adminId", admin.getId());
		session.setAttribute("adminEmail", admin.getEmail());
		session.setAttribute("adminRole", admin.getRole());
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpSession session) {
		session.invalidate();
		return ResponseEntity.noContent().build();
	}
}
