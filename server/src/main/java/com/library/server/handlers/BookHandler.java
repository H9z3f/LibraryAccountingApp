package com.library.server.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.server.bodies.requests.BookRequestBody;
import com.library.server.bodies.responses.BookResponseBody;
import com.library.server.bodies.responses.MultipleBookResponseBody;
import com.library.server.entities.Book;
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

public class BookHandler implements HttpHandler {
    private SessionFactory sessionFactory;
    private ObjectMapper objectMapper;
    private PasswordEncoder passwordEncoder;

    public BookHandler() {
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
            sendResponse(exchange, HttpURLConnection.HTTP_UNAUTHORIZED, new BookResponseBody(false, "No authorization key"));
            return;
        }

        Long userId = JwtUtility.parse(jwt);
        if (userId == null) {
            sendResponse(exchange, HttpURLConnection.HTTP_UNAUTHORIZED, new BookResponseBody(false, "Invalid authorization key"));
            return;
        }

        switch (requestMethod) {
            case "GET" -> {
                findAllBooks(exchange, userId);
            }
            case "POST" -> {
                createBook(exchange, userId);
            }
            case "PUT" -> {
                updateBook(exchange, userId);
            }
            case "DELETE" -> {
                deleteBook(exchange, userId);
            }
            default -> {
                sendResponse(exchange, HttpURLConnection.HTTP_BAD_METHOD, new BookResponseBody(false, "Invalid request method"));
            }
        }
    }

    private void findAllBooks(HttpExchange exchange, Long userId) throws IOException {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            User user = session.get(User.class, userId);

            List<Book> books;

            if (user.getRole().getId() == 1) {
                books = session.createQuery("from Book", Book.class)
                        .list();
            } else {
                books = session.createQuery("from Book where isAvailable = :isAvailable", Book.class)
                        .setParameter("isAvailable", true)
                        .list();
            }

            sendResponse(exchange, HttpURLConnection.HTTP_OK, new MultipleBookResponseBody(true, books));
        } catch (Exception e) {
            session.getTransaction().rollback();

            sendResponse(exchange, HttpURLConnection.HTTP_CONFLICT, new MultipleBookResponseBody(false, e.getMessage()));
        } finally {
            session.close();
        }
    }

    private void createBook(HttpExchange exchange, Long userId) throws IOException {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            BookRequestBody bookRequestBody = objectMapper.readValue(exchange.getRequestBody(), BookRequestBody.class);
            if (bookRequestBody.getAuthor().equals("") || bookRequestBody.getBookName().equals("")) {
                sendResponse(exchange, HttpURLConnection.HTTP_CONFLICT, new BookResponseBody(false, "Fields cannot be empty"));
                return;
            }

            User user = session.get(User.class, userId);
            if (user.getRole().getId() != 1) {
                sendResponse(exchange, HttpURLConnection.HTTP_BAD_REQUEST, new BookResponseBody(false, "Not enough rights"));
                return;
            }

            Status status = session.get(Status.class, 1);

            Book book = new Book();
            book.setAuthor(bookRequestBody.getAuthor());
            book.setBookName(bookRequestBody.getBookName());
            book.setStatus(status);
            book.setAvailable(true);

            session.save(book);

            sendResponse(exchange, HttpURLConnection.HTTP_CREATED, new BookResponseBody(true, book));
        } catch (Exception e) {
            session.getTransaction().rollback();

            sendResponse(exchange, HttpURLConnection.HTTP_CONFLICT, new BookResponseBody(false, e.getMessage()));
        } finally {
            session.close();
        }
    }

    private void updateBook(HttpExchange exchange, Long userId) throws IOException {
        Long bookId = getParameterId(exchange);
        if (bookId == null) {
            sendResponse(exchange, HttpURLConnection.HTTP_BAD_REQUEST, new BookResponseBody(false, "Missing order id"));
            return;
        }

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            User user = session.get(User.class, userId);
            if (user.getRole().getId() != 1) {
                sendResponse(exchange, HttpURLConnection.HTTP_BAD_REQUEST, new BookResponseBody(false, "Not enough rights"));
                return;
            }

            Book book = session.get(Book.class, bookId);
            if (book == null) {
                sendResponse(exchange, HttpURLConnection.HTTP_BAD_REQUEST, new BookResponseBody(false, "This book does not exist"));
                return;
            }

            Status status = session.get(Status.class, 1);

            book.setStatus(status);
            book.setAvailable(true);

            session.update(book);

            sendResponse(exchange, HttpURLConnection.HTTP_OK, new BookResponseBody(true, book));
        } catch (Exception e) {
            session.getTransaction().rollback();

            sendResponse(exchange, HttpURLConnection.HTTP_CONFLICT, new BookResponseBody(false, e.getMessage()));
        } finally {
            session.close();
        }
    }

    private void deleteBook(HttpExchange exchange, Long userId) throws IOException {
        Long bookId = getParameterId(exchange);
        if (bookId == null) {
            sendResponse(exchange, HttpURLConnection.HTTP_BAD_REQUEST, new BookResponseBody(false, "Missing order id"));
            return;
        }

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            User user = session.get(User.class, userId);
            if (user.getRole().getId() != 1) {
                sendResponse(exchange, HttpURLConnection.HTTP_BAD_REQUEST, new BookResponseBody(false, "Not enough rights"));
                return;
            }

            Book book = session.get(Book.class, bookId);
            if (book == null) {
                sendResponse(exchange, HttpURLConnection.HTTP_BAD_REQUEST, new BookResponseBody(false, "This book does not exist"));
                return;
            }

            Status status = session.get(Status.class, 4);

            book.setStatus(status);
            book.setAvailable(false);

            session.update(book);

            sendResponse(exchange, HttpURLConnection.HTTP_OK, new BookResponseBody(true, book));
        } catch (Exception e) {
            session.getTransaction().rollback();

            sendResponse(exchange, HttpURLConnection.HTTP_CONFLICT, new BookResponseBody(false, e.getMessage()));
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
        if (path.split("/").length != 3 && !isNumber(path.split("/")[3])) {
            return null;
        }

        return Long.getLong(path.split("/")[3]);
    }

    private boolean isNumber(String parameterId) {
        for (char c : parameterId.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }

        return true;
    }

    private void sendResponse(HttpExchange exchange, int code, BookResponseBody bookResponseBody) throws IOException {
        exchange.sendResponseHeaders(code, 0);
        objectMapper.writeValue(exchange.getResponseBody(), bookResponseBody);
    }

    private void sendResponse(HttpExchange exchange, int code, MultipleBookResponseBody multipleBookResponseBody) throws IOException {
        exchange.sendResponseHeaders(code, 0);
        objectMapper.writeValue(exchange.getResponseBody(), multipleBookResponseBody);
    }
}
