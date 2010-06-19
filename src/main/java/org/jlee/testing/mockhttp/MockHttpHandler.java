package org.jlee.testing.mockhttp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class MockHttpHandler implements HttpHandler {

    public static final int DEFAULT_STATUS_CODE = 500;
    public static final byte[] DEFAULT_BODY = "".getBytes();
    
    MockHttp owner;
    
    public MockHttpHandler(MockHttp owner) {
        this.owner = owner;
    }
    
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String requestPath = exchange.getRequestURI().getPath();
        String requestBody = Utils.readStream(exchange.getRequestBody());
        Map<String, String> requestHeaders = Utils.parseHeaders(exchange.getRequestHeaders());

        owner.logRequest(requestMethod, requestPath, requestBody, requestHeaders);
        
        ResponseDescription response = owner.nextMapping(requestMethod, requestPath, requestBody, requestHeaders);
        if (response == null) {
            sendDefaultResponse(exchange);
            return;
        }

        Map<String, String> responseHeaders = response.getHeaders();
        Headers exchangeResponseHeaders = exchange.getResponseHeaders();
        for (String header : responseHeaders.keySet()) {
            exchangeResponseHeaders.put(header, Arrays.asList(responseHeaders.get(header)));
        }
        
        long contentLength = response.getBody().length();
        if (contentLength == 0) {
            exchange.sendResponseHeaders(response.getStatusCode(), -1);
        } else {
            exchange.sendResponseHeaders(response.getStatusCode(), contentLength);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBody().getBytes());
            os.close();
        }
    }

    private void sendDefaultResponse(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(DEFAULT_STATUS_CODE, DEFAULT_BODY.length);
        OutputStream os = exchange.getResponseBody();
        os.write(DEFAULT_BODY);
        exchange.getResponseBody().close();
    }

}
