package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import controllers.auth.JWTHandler;
import lombok.SneakyThrows;
import models.Account;
import services.AccountService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class AccountController extends BaseController {
    private final AccountService accountService = new AccountService();
    private final ObjectMapper mapper = new ObjectMapper();

    public AccountController() {
        super(true);
    }

    @Override
    @SneakyThrows
    protected void handleGet(HttpExchange exchange) {
        String userLogin = JWTHandler.decode(exchange.getRequestHeaders().getFirst("Token"));
        String path = exchange.getRequestURI().getPath();
        String[] pathSegments = path.split("/");

        if (pathSegments.length == 3) {
            List<Account> accounts = accountService.getAllAccounts(userLogin);
            sendResponse(exchange, 200, mapper.writeValueAsString(accounts));
        }
        else if (pathSegments.length == 4) {
            long id = Long.parseLong(pathSegments[3]);
            Optional<Account> account = accountService.getAccountById(id, userLogin);
            if (account.isPresent()) {
                sendResponse(exchange, 200, mapper.writeValueAsString(account.get()));
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Account not found\"}");
            }
        } else {
            sendResponse(exchange, 400, "{\"error\":\"Invalid path\"}");
        }
    }

    @Override
    @SneakyThrows
    protected void handlePost(HttpExchange exchange) {
        String userLogin = JWTHandler.decode(exchange.getRequestHeaders().getFirst("Token"));
        Account account = mapper.readValue(exchange.getRequestBody(), Account.class);
        long newAccountId = accountService.createAccount(account, userLogin);
        if (newAccountId > 0) {
            sendResponse(exchange, 201, "{\"id\":" + newAccountId + "}");
        } else {
            sendResponse(exchange, 500, "{\"error\":\"Failed to create account\"}");
        }
    }

    @Override
    @SneakyThrows
    protected void handlePut(HttpExchange exchange) {
        String userLogin = JWTHandler.decode(exchange.getRequestHeaders().getFirst("Token"));
        long accountId = Long.parseLong(exchange.getRequestURI().getPath().split("/")[3]);
        Account account = mapper.readValue(exchange.getRequestBody(), Account.class);
        int updatedRows = accountService.updateAccount(accountId, account, userLogin);
        if (updatedRows > 0) {
            sendResponse(exchange, 200, "{\"message\":\"Account updated\"}");
        } else {
            sendResponse(exchange, 404, "{\"error\":\"Account not found or no permission\"}");
        }
    }

    @Override
    @SneakyThrows
    protected void handleDelete(HttpExchange exchange) {
        String userLogin = JWTHandler.decode(exchange.getRequestHeaders().getFirst("Token"));
        long accountId = Long.parseLong(exchange.getRequestURI().getPath().split("/")[3]);
        if (accountService.deleteAccount(accountId, userLogin)) {
            sendResponse(exchange, 200, "{\"message\":\"Account deleted\"}");
        } else {
            sendResponse(exchange, 404, "{\"error\":\"Account not found or no permission\"}");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.getResponseBody().close();
    }
}