package com.library.server;

import com.library.server.handlers.BookHandler;
import com.library.server.handlers.OrderHandler;
import com.library.server.handlers.UserLoginHandler;
import com.library.server.handlers.UserRegisterHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    private final int PORT = 8080;
    private final int THREADS = 0;
    private HttpServer httpServer;

    public Server() {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(PORT), THREADS);
            httpServer.createContext("/user/register", new UserRegisterHandler());
            httpServer.createContext("/user/login", new UserLoginHandler());
            httpServer.createContext("/book", new BookHandler());
            httpServer.createContext("/order", new OrderHandler());
            httpServer.setExecutor(null);
            httpServer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
