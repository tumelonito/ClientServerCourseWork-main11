package org.example;

import com.sun.net.httpserver.HttpServer;
import controllers.AccountController;
import controllers.AssetController;
import controllers.DashboardController;
import controllers.TransactionController;
import controllers.auth.LoginHandler;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 8080), 0);

        server.createContext("/login", new LoginHandler());
        server.createContext("/api/accounts", new AccountController());
        server.createContext("/api/assets", new AssetController());
        server.createContext("/api/transactions", new TransactionController());
        server.createContext("/api/dashboard", new DashboardController());

        server.setExecutor(null);
        server.start();
        System.out.println("Server is listening on port 8080");
    }
}