package com.library.client.responses;

import com.library.client.dtos.Order;

import java.util.List;

public class MultipleOrderResponseBody {
    private boolean success;
    private String message;
    private List<Order> orders;

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
