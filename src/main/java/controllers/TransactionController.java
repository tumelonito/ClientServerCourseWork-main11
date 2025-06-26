package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpExchange;
import controllers.auth.JWTHandler;
import lombok.SneakyThrows;
import models.Transaction;
import services.TransactionService;

import java.io.IOException;

public class TransactionController extends BaseController {

    private final TransactionService transactionService = new TransactionService();
    private final ObjectMapper mapper = new ObjectMapper();

    public TransactionController() {
        super(true);
        mapper.registerModule(new JavaTimeModule());
    }

    @Override
    @SneakyThrows
    protected void handlePost(HttpExchange exchange) {
        String userLogin = JWTHandler.decode(exchange.getRequestHeaders().getFirst("Token"));
        Transaction transaction = mapper.readValue(exchange.getRequestBody(), Transaction.class);
        long newTransactionId = transactionService.createTransaction(transaction, userLogin);
        if (newTransactionId > 0) {
            sendResponse(exchange, 201, "{\"id\":" + newTransactionId + "}");
        } else {
            sendResponse(exchange, 500, "{\"error\":\"Failed to create transaction\"}");
        }
    }

    @Override
    protected void handleGet(HttpExchange httpExchange) { sendMethodNotAllowed(httpExchange); }
    @Override
    protected void handlePut(HttpExchange httpExchange) { sendMethodNotAllowed(httpExchange); }
    @Override
    protected void handleDelete(HttpExchange httpExchange) { sendMethodNotAllowed(httpExchange); }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.getResponseBody().close();
    }

    @SneakyThrows
    private void sendMethodNotAllowed(HttpExchange exchange) {
        sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
    }
}