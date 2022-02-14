package com.hakai.irc.callback;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class CallbackServer implements HttpHandler {
    private final HttpServer httpServer;
    private final Consumer<String> callback;

    public CallbackServer(Consumer<String> callback) throws IOException {
        this.callback = callback;

        httpServer = HttpServer.create(new InetSocketAddress("localhost", 5756), 0);
        httpServer.createContext("/", this);
        httpServer.start();

        Thread t = new Thread(() -> {
            try {
                Thread.sleep(120000);
            } catch (InterruptedException e) {}
            close();
        });
        t.start();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "failed";
        String query = exchange.getRequestURI().getQuery();

        if (query != null && query.startsWith("code=")){
            response = "redirect...";
            exchange.getResponseHeaders().add("Location", "https://yellowsnow.xyz/");
            callback.accept(query.substring(5));
        }

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.getResponseBody().close();
        new Thread(this::close).start();
    }

    public void close(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignore) { }
        httpServer.stop(0);
    }
}
