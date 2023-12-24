package com.library.server.bodies.requests;

public class OrderRequestBody {
    private long bookId;

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }
}
