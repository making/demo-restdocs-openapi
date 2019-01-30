package com.example.demo.order;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Order {
	private final Long id;
	private final String email;
	private final long totalPrice;
	private final Status status;

	public Order(Long id, String email, long totalPrice, Status status) {
		this.id = id;
		this.email = email;
		this.totalPrice = totalPrice;
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public Status getStatus() {
		return status;
	}

	public String getEmail() {
		return email;
	}

	public long getTotalPrice() {
		return totalPrice;
	}

	public enum Status {
		SUBMITTED, COMPLETED;
	}

	public Order submitted(long id) {
		return new Order(id, this.email, this.totalPrice, Status.SUBMITTED);
	}
}
