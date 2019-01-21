package com.example.demo.order;

public class Order {
	private final Long id;
	private final Status status;

	public Order(Long id, Status status) {
		this.id = id;
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public Status getStatus() {
		return status;
	}

	public enum Status {
		SUBMITTED, COMPLETED;
	}
}
