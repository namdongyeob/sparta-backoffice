package com.sparta.backoffice.admin.service;

import org.springframework.stereotype.Service;

import com.sparta.backoffice.admin.repository.AdminRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
	private final AdminRepository adminRepository;
}
