package com.library.server.bodies.responses;

import com.library.server.entities.Order;

public class OrderResponseBody {
    private boolean success;
    private String message;
    private Order order;

    public OrderResponseBody(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public OrderResponseBody(boolean success, Order order) {
        this.success = success;
        this.order = order;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
