package com.example.demo.order;

import com.example.demo.order.Order.Status;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {
	@GetMapping("orders/{orderId}")
	public Order getOrder(@PathVariable("orderId") Long id) {
		return new Order(id, Status.COMPLETED);
	}
}
