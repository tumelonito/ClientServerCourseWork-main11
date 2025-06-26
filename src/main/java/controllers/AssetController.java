package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import lombok.SneakyThrows;
import models.Asset;
import services.AssetService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class AssetController extends BaseController {
    private final AssetService assetService = new AssetService();
    private final ObjectMapper mapper = new ObjectMapper();

    public AssetController() {
        super(true);
    }

    @Override
    @SneakyThrows
    protected void handleGet(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String[] pathSegments = path.split("/");

        if (pathSegments.length == 3) {
            List<Asset> assets = assetService.getAllAssets();
            sendResponse(exchange, 200, mapper.writeValueAsString(assets));
        }
        else if (pathSegments.length == 4) {
            long id = Long.parseLong(pathSegments[3]);
            Optional<Asset> asset = assetService.getAssetById(id);
            if (asset.isPresent()) {
                sendResponse(exchange, 200, mapper.writeValueAsString(asset.get()));
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Asset not found\"}");
            }
        } else {
            sendResponse(exchange, 400, "{\"error\":\"Invalid path\"}");
        }
    }

    @Override
    @SneakyThrows
    protected void handlePost(HttpExchange exchange) {
        Asset asset = mapper.readValue(exchange.getRequestBody(), Asset.class);
        long newAssetId = assetService.createAsset(asset);
        if (newAssetId > 0) {
            sendResponse(exchange, 201, "{\"id\":" + newAssetId + "}");
        } else {
            sendResponse(exchange, 500, "{\"error\":\"Failed to create asset\"}");
        }
    }

    @Override
    @SneakyThrows
    protected void handlePut(HttpExchange exchange) {
        long assetId = Long.parseLong(exchange.getRequestURI().getPath().split("/")[3]);
        Asset asset = mapper.readValue(exchange.getRequestBody(), Asset.class);
        int updatedRows = assetService.updateAsset(assetId, asset);
        if (updatedRows > 0) {
            sendResponse(exchange, 200, "{\"message\":\"Asset updated\"}");
        } else {
            sendResponse(exchange, 404, "{\"error\":\"Asset not found\"}");
        }
    }

    @Override
    @SneakyThrows
    protected void handleDelete(HttpExchange exchange) {
        long assetId = Long.parseLong(exchange.getRequestURI().getPath().split("/")[3]);
        if (assetService.deleteAsset(assetId)) {
            sendResponse(exchange, 200, "{\"message\":\"Asset deleted\"}");
        } else {
            sendResponse(exchange, 404, "{\"error\":\"Asset not found\"}");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.getResponseBody().close();
    }
}