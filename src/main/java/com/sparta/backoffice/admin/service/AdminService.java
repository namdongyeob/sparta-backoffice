package com.sparta.backoffice.admin.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.backoffice.admin.dto.AdminLoginRequest;
import com.sparta.backoffice.admin.dto.AdminSignupResponse;
import com.sparta.backoffice.admin.dto.AdminSignupRequest;
import com.sparta.backoffice.admin.entity.Admin;
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
		if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())){
			throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
		}
		switch (admin.getStatus()){
			case PENDING -> throw new IllegalArgumentException("승인 대기 중인 계정입니다.");
			case REJECTED -> throw new IllegalArgumentException("거부된 계정입니다.");
			case SUSPENDED -> throw new IllegalArgumentException("정지된 계정입니다.");
			case INACTIVE -> throw new IllegalArgumentException("비활성화된 계정입니다.");
		}
		return admin;

	}
}
