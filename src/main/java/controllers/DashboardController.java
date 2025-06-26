package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import controllers.auth.JWTHandler;
import dto.PortfolioSummary;
import lombok.SneakyThrows;
import services.DashboardService;

import java.io.IOException;

public class DashboardController extends BaseController {

    private final DashboardService dashboardService = new DashboardService();
    private final ObjectMapper mapper = new ObjectMapper();

    public DashboardController() {
        super(true);
    }

    @Override
    @SneakyThrows
    protected void handleGet(HttpExchange exchange) {
        String userLogin = JWTHandler.decode(exchange.getRequestHeaders().getFirst("Token"));
        String[] pathSegments = exchange.getRequestURI().getPath().split("/");

        if (pathSegments.length == 5 && pathSegments[3].equals("account")) {
            long accountId = Long.parseLong(pathSegments[4]);
            PortfolioSummary summary = dashboardService.getPortfolioSummaryForAccount(accountId, userLogin);
            sendResponse(exchange, 200, mapper.writeValueAsString(summary));
        } else {
            sendResponse(exchange, 400, "{\"error\":\"Invalid path. Use /api/dashboard/account/{id}\"}");
        }
    }

    @Override protected void handlePost(HttpExchange e) { sendMethodNotAllowed(e); }
    @Override protected void handlePut(HttpExchange e) { sendMethodNotAllowed(e); }
    @Override protected void handleDelete(HttpExchange e) { sendMethodNotAllowed(e); }

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