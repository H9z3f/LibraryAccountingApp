package com.library.server.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.server.bodies.requests.UserRequestBody;
import com.library.server.bodies.responses.UserResponseBody;
import com.library.server.entities.Book;
import com.library.server.entities.Order;
import com.library.server.entities.Role;
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

public class UserRegisterHandler implements HttpHandler {
    private SessionFactory sessionFactory;
    private ObjectMapper objectMapper;
    private PasswordEncoder passwordEncoder;

    public UserRegisterHandler() {
        Configuration configuration = new Configuration().configure();
        sessionFactory = configuration.buildSessionFactory();
        objectMapper = new ObjectMapper();
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();

        switch (requestMethod) {
            case "POST" -> {
                registerUser(exchange);
            }
            default -> {
                sendResponse(exchange, HttpURLConnection.HTTP_BAD_METHOD, new UserResponseBody(false, "Invalid request method"));
            }
        }
    }

    private void registerUser(HttpExchange exchange) throws IOException {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            UserRequestBody userRequestBody = objectMapper.readValue(exchange.getRequestBody(), UserRequestBody.class);
            if (userRequestBody.getFullName().equals("") || userRequestBody.getEmail().equals("") || userRequestBody.getPassword().equals("")) {
                sendResponse(exchange, HttpURLConnection.HTTP_CONFLICT, new UserResponseBody(false, "Fields cannot be empty"));
                return;
            }

            Role role = session.get(Role.class, 2);

            User user = new User();
            user.setRole(role);
            user.setFullName(userRequestBody.getFullName());
            user.setEmail(userRequestBody.getEmail());
            user.setPassword(passwordEncoder.encode(userRequestBody.getPassword()));

            session.save(user);

            List<Order> orders = session.createQuery("from Order where user = :user order by id", Order.class)
                    .setParameter("user", user)
                    .list();
            List<Book> books = session.createQuery("from Book where isAvailable = :isAvailable order by id", Book.class)
                    .setParameter("isAvailable", true)
                    .list();

            session.getTransaction().commit();

            String jwt = JwtUtility.generate(user.getId());

            sendResponse(exchange, HttpURLConnection.HTTP_CREATED, new UserResponseBody(true, jwt, user, books, orders));
        } catch (Exception e) {
            session.getTransaction().rollback();

            sendResponse(exchange, HttpURLConnection.HTTP_CONFLICT, new UserResponseBody(false, e.getMessage()));
        } finally {
            session.close();
        }
    }

    private void sendResponse(HttpExchange exchange, int code, UserResponseBody userResponseBody) throws IOException {
        exchange.sendResponseHeaders(code, 0);
        objectMapper.writeValue(exchange.getResponseBody(), userResponseBody);
    }
}
