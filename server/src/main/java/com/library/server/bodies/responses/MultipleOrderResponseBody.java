package com.library.server.bodies.responses;

import com.library.server.entities.Order;

import java.util.List;

public class MultipleOrderResponseBody {
    private boolean success;
    private String message;
    private List<Order> orders;

    public MultipleOrderResponseBody(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public MultipleOrderResponseBody(boolean success, List<Order> orders) {
        this.success = success;
        this.orders = orders;
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

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
