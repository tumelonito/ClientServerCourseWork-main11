package controllers.auth;

import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import controllers.BaseController;
import lombok.SneakyThrows;
import services.LoginService;

import java.net.HttpURLConnection;

public class LoginHandler extends BaseController {
    private final LoginService service = new LoginService();

    public LoginHandler() {
        super(false);
    }

    @Override
    @SneakyThrows
    protected void handlePost(HttpExchange exchange) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            LoginRequest loginRequest = mapper.readValue(exchange.getRequestBody(), LoginRequest.class);
            if (!service.signIn(loginRequest)) {
                exchange.sendResponseHeaders(403, 0);
                exchange.close();
                return;
            }
            exchange.getResponseHeaders().add("Token", JWTHandler.createToken(loginRequest.login()));
            exchange.sendResponseHeaders(200, 0);
            exchange.close();
        } catch (DatabindException e) {
            System.out.println("Error: " + e.getMessage());
            exchange.sendResponseHeaders(404, 0);
            exchange.close();
        }
    }

    @Override
    @SneakyThrows
    protected void handlePut(HttpExchange exchange) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            LoginRequest loginRequest = mapper.readValue(exchange.getRequestBody(), LoginRequest.class);
            if (!service.signUp(loginRequest)) {
                exchange.sendResponseHeaders(403, 0);
                exchange.close();
                return;
            }
            exchange.getResponseHeaders().add("Token", JWTHandler.createToken(loginRequest.login()));
            exchange.sendResponseHeaders(200, 0);
            exchange.close();
        } catch (DatabindException e) {
            System.out.println("Error: " + e.getMessage());
            exchange.sendResponseHeaders(404, 0);
            exchange.close();
        }
    }

    @Override
    @SneakyThrows
    protected void handleGet(HttpExchange httpExchange) {
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
        httpExchange.close();
    }

    @Override
    @SneakyThrows
    protected void handleDelete(HttpExchange httpExchange) {
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
        httpExchange.close();
    }
}
