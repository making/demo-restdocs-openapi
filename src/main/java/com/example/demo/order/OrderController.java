package com.example.demo.order;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class OrderController {
	private AtomicLong counter = new AtomicLong(1);

	@GetMapping("orders/{orderId}")
	public Order getOrder(@PathVariable("orderId") Long id) {
		return new Order(id, "demo@example.com", 10000, Order.Status.COMPLETED);
	}

	@PostMapping("orders")
	public ResponseEntity<Order> order(@RequestBody Order order,
			UriComponentsBuilder builder) {
		Order submitted = order.submitted(counter.getAndIncrement());
		return ResponseEntity.created(builder
				.pathSegment("orders", String.valueOf(submitted.getId())).build().toUri())
				.body(submitted);
	}
}
