package com.library.server.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.server.bodies.requests.OrderRequestBody;
import com.library.server.bodies.responses.MultipleOrderResponseBody;
import com.library.server.bodies.responses.OrderResponseBody;
import com.library.server.entities.Book;
import com.library.server.entities.Order;
import com.library.server.entities.Status;
import com.library.server.entities.User;
import com.library.server.utilities.JwtUtility;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

public class OrderHandler implements HttpHandler {
    private SessionFactory sessionFactory;
    private ObjectMapper objectMapper;
    private PasswordEncoder passwordEncoder;

    public OrderHandler() {
        Configuration configuration = new Configuration().configure();
        sessionFactory = configuration.buildSessionFactory();
        objectMapper = new ObjectMapper();
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();

        String jwt = getJwt(exchange);
        if (jwt == null) {
            sendResponse(exchange, HttpURLConnection.HTTP_UNAUTHORIZED, new OrderResponseBody(false, "No authorization key"));
            return;
        }

        Long userId = JwtUtility.parse(jwt);
        if (userId == null) {
            sendResponse(exchange, HttpURLConnection.HTTP_UNAUTHORIZED, new OrderResponseBody(false, "Invalid authorization key"));
            return;
        }

        switch (requestMethod) {
            case "GET" -> {
                findAllOrders(exchange, userId);
            }
            case "POST" -> {
                createOrder(exchange, userId);
            }
            case "PUT" -> {
                updateOrder(exchange, userId);
            }
            case "DELETE" -> {
                deleteOrder(exchange, userId);
            }
            default -> {
                sendResponse(exchange, HttpURLConnection.HTTP_BAD_METHOD, new OrderResponseBody(false, "Invalid request method"));
            }
        }
    }

    private void findAllOrders(HttpExchange exchange, Long userId) throws IOException {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            User user = session.get(User.class, userId);

            List<Order> orders;

            if (user.getRole().getId() == 1) {
                orders = session.createQuery("from Order order by id", Order.class)
                        .list();
            } else {
                orders = session.createQuery("from Order where user = :user order by id", Order.class)
                        .setParameter("user", user)
                        .list();
            }

            sendResponse(exchange, HttpURLConnection.HTTP_OK, new MultipleOrderResponseBody(true, orders));
        } catch (Exception e) {
            session.getTransaction().rollback();

            sendResponse(exchange, HttpURLConnection.HTTP_CONFLICT, new MultipleOrderResponseBody(false, e.getMessage()));
        } finally {
            session.close();
        }
    }

    private void createOrder(HttpExchange exchange, Long userId) throws IOException {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            OrderRequestBody orderRequestBody = objectMapper.readValue(exchange.getRequestBody(), OrderRequestBody.class);
            if (orderRequestBody.getBookId() < 1) {
                sendResponse(exchange, HttpURLConnection.HTTP_CONFLICT, new OrderResponseBody(false, "Fields cannot be empty"));
                return;
            }

            User user = session.get(User.class, userId);

            Book book = session.get(Book.class, orderRequestBody.getBookId());
            if (book == null) {
                sendResponse(exchange, HttpURLConnection.HTTP_BAD_REQUEST, new OrderResponseBody(false, "This book does not exist"));
                return;
            } else if (!book.getAvailable()) {
                sendResponse(exchange, HttpURLConnection.HTTP_CONFLICT, new OrderResponseBody(false, "Book is not available for booking"));
                return;
            }

            Status status = session.get(Status.class, 3);

            book.setStatus(status);
            book.setAvailable(false);

            session.update(book);

            Order order = new Order();
            order.setUser(user);
            order.setBook(book);
            order.setReady(false);
            order.setBeenIssued(false);

            session.save(order);
            session.getTransaction().commit();

            sendResponse(exchange, HttpURLConnection.HTTP_CREATED, new OrderResponseBody(true, order));
        } catch (Exception e) {
            session.getTransaction().rollback();

            sendResponse(exchange, HttpURLConnection.HTTP_CONFLICT, new OrderResponseBody(false, e.getMessage()));
        } finally {
            session.close();
        }
    }

    private void updateOrder(HttpExchange exchange, Long userId) throws IOException {
        Long orderId = getParameterId(exchange);
        if (orderId == null) {
            sendResponse(exchange, HttpURLConnection.HTTP_BAD_REQUEST, new OrderResponseBody(false, "Missing order id"));
            return;
        }

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            User user = session.get(User.class, userId);
            if (user.getRole().getId() != 1) {
                sendResponse(exchange, HttpURLConnection.HTTP_BAD_REQUEST, new OrderResponseBody(false, "Not enough rights"));
                return;
            }

            Order order = session.get(Order.class, orderId);
            if (order == null) {
                sendResponse(exchange, HttpURLConnection.HTTP_CONFLICT, new OrderResponseBody(false, "This reservation does not exist"));
                return;
            }

            order.setReady(true);

            session.update(order);
            session.getTransaction().commit();

            sendResponse(exchange, HttpURLConnection.HTTP_OK, new OrderResponseBody(true, order));
        } catch (Exception e) {
            session.getTransaction().rollback();

            sendResponse(exchange, HttpURLConnection.HTTP_CONFLICT, new OrderResponseBody(false, e.getMessage()));
        } finally {
            session.close();
        }
    }

    private void deleteOrder(HttpExchange exchange, Long userId) throws IOException {
        Long orderId = getParameterId(exchange);
        if (orderId == null) {
            sendResponse(exchange, HttpURLConnection.HTTP_BAD_REQUEST, new OrderResponseBody(false, "Missing order id"));
            return;
        }

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            User user = session.get(User.class, userId);
            if (user.getRole().getId() != 1) {
                sendResponse(exchange, HttpURLConnection.HTTP_BAD_REQUEST, new OrderResponseBody(false, "Not enough rights"));
                return;
            }

            Order order = session.get(Order.class, orderId);
            if (order == null) {
                sendResponse(exchange, HttpURLConnection.HTTP_CONFLICT, new OrderResponseBody(false, "This reservation does not exist"));
                return;
            }

            Status status = session.get(Status.class, 2);

            Book book = session.get(Book.class, order.getBook().getId());
            book.setStatus(status);

            session.update(book);

            order.setBook(book);
            order.setReady(true);
            order.setBeenIssued(true);

            session.update(order);
            session.getTransaction().commit();

            sendResponse(exchange, HttpURLConnection.HTTP_OK, new OrderResponseBody(true, order));
        } catch (Exception e) {
            session.getTransaction().rollback();

            sendResponse(exchange, HttpURLConnection.HTTP_CONFLICT, new OrderResponseBody(false, e.getMessage()));
        } finally {
            session.close();
        }
    }

    private String getJwt(HttpExchange exchange) {
        String authorizationHeader = exchange.getRequestHeaders().getFirst("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }

        return authorizationHeader.substring("Bearer ".length());
    }

    private Long getParameterId(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        if (path.split("/").length != 3 && !isNumber(path.split("/")[2])) {
            return null;
        }

        return Long.parseLong(path.split("/")[2]);
    }

    private boolean isNumber(String parameterId) {
        for (char c : parameterId.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }

        return true;
    }

    private void sendResponse(HttpExchange exchange, int code, OrderResponseBody orderResponseBody) throws IOException {
        exchange.sendResponseHeaders(code, 0);
        objectMapper.writeValue(exchange.getResponseBody(), orderResponseBody);
    }

    private void sendResponse(HttpExchange exchange, int code, MultipleOrderResponseBody multipleOrderResponseBody) throws IOException {
        exchange.sendResponseHeaders(code, 0);
        objectMapper.writeValue(exchange.getResponseBody(), multipleOrderResponseBody);
    }
}
