package com.sparta.backoffice.order.service;

import org.springframework.stereotype.Service;

import com.sparta.backoffice.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
	private final OrderRepository orderRepository;
}
