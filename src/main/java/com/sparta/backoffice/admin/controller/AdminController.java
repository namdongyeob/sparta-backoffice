package com.sparta.backoffice.admin.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.backoffice.admin.dto.AdminGetAllRequest;
import com.sparta.backoffice.admin.dto.AdminGetMeResponse;
import com.sparta.backoffice.admin.dto.AdminGetResponse;
import com.sparta.backoffice.admin.dto.AdminSignupResponse;
import com.sparta.backoffice.admin.dto.AdminSignupRequest;
import com.sparta.backoffice.admin.service.AdminService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admins")
public class AdminController {
	private final AdminService adminService;

	@PostMapping("/signup")
	public ResponseEntity<AdminSignupResponse> signup(@Valid @RequestBody AdminSignupRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(adminService.signup(request));
	}

	@GetMapping("/{id}")
	public ResponseEntity<AdminGetResponse> getAdmin(
		@PathVariable Long id
	) {
		return ResponseEntity.status(HttpStatus.OK).body(adminService.getAdmin(id));
	}

	@GetMapping
	public ResponseEntity<Page<AdminGetResponse>> getAdmins(
		@ModelAttribute AdminGetAllRequest request
	) {
		return ResponseEntity.status(HttpStatus.OK).body(adminService.getAdmins(request));
	}
	@GetMapping("/me")
	public ResponseEntity<AdminGetMeResponse> getMe(HttpSession session) {
		Long adminId = (Long) session.getAttribute("adminId");
		return ResponseEntity.status(HttpStatus.OK).body(adminService.getMe(adminId));
	}
}
