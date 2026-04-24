package com.sparta.backoffice.admin.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.backoffice.admin.dto.AdminResponseDto;
import com.sparta.backoffice.admin.dto.AdminSignupRequestDto;
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
	public AdminResponseDto signup(AdminSignupRequestDto requestDto) {
		if (adminRepository.existsByEmail(requestDto.getEmail())) {
			throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
		}
		String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
		Admin admin = requestDto.toEntity(encodedPassword);
		Admin savedAdmin = adminRepository.save(admin);
		return AdminResponseDto.from(savedAdmin);
	}
}
