package com.sparta.backoffice.order.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sparta.backoffice.order.service.OrderService;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
	private final OrderService orderService;
}
