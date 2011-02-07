package org.jlee.testing.mockhttp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.sun.net.httpserver.HttpServer;

public class MockHttp {

    HttpServer http;
    Map<RequestDescription, Queue<ResponseDescription>> mappings = new LinkedHashMap<RequestDescription, Queue<ResponseDescription>>();
    RequestLog requestLog = new RequestLog();
    
    public static ResponseDescriptionBuilder when(RequestDescription request) {
        request.getHttp().removeMapping(request);
        return new ResponseDescriptionBuilder(request);
    }

    public static MockHttpVerifier verify(MockHttp mockHttp) {
        return new MockHttpVerifier(mockHttp);
    }

    public static MockHttpVerifier verify(MockHttp mockHttp, int numberOfTimes) {
        return new MockHttpVerifier(mockHttp, numberOfTimes);
    }

    public MockHttp(int port) {
        InetSocketAddress addr = new InetSocketAddress(port);
        
        try {
            http = HttpServer.create(addr, 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        http.createContext("/", new MockHttpHandler(this));
        http.start();
    }
    
    public RequestDescription get(String url, String... headers) {
        return onMethod("GET", url, null, headers);
    }

    public RequestDescription put(String url) {
        return put(url, null);
    }

    public RequestDescription put(String url, String body, String... headers) {
        return onMethod("PUT", url, body, headers);
    }

    public RequestDescription post(String url) {
        return post(url, null);
    }

    public RequestDescription post(String url, String body, String... headers) {
        return onMethod("POST", url, body, headers);
    }

    public RequestDescription delete(String url, String... headers) {
        return onMethod("DELETE", url, null, headers);
    }

    public RequestDescription onMethod(String methodName, String url, String body, String[] headers) {
        UrlParts urlParts = UrlParts.parse(url);
        return new RequestDescription(this, methodName, url.getPath(), url.getQueryParams(), body, Utils.parseHeaders(headers));
    }
    
    
    public void addMapping(RequestDescription request, ResponseDescription response) {
        Queue<ResponseDescription> responses = getMapping(request);
        if (responses == null) {
            responses = new ConcurrentLinkedQueue<ResponseDescription>();
            mappings.put(request, responses);
        }
        
        responses.add(response);
    }
    
    public Queue<ResponseDescription> getMapping(RequestDescription request) {
        for (RequestDescription k : mappings.keySet()) {
            if (k.matches(request)) {
                return mappings.get(k);
            }
        }
        return null;
    }

    public ResponseDescription nextMapping(String methodName, String url, String body, Map<String, String> headers) {
        Queue<ResponseDescription> responses = getMapping(new RequestDescription(null, methodName, url, body, headers));
        if (responses == null)
            return null;
        if (responses.size() == 1)
            return responses.peek();
        return responses.poll();
    }
    
    public void removeMapping(RequestDescription request) {
        for (RequestDescription k : mappings.keySet()) {
            if (k.matches(request)) {
                mappings.remove(k);
            }
        }
    }

    public void logRequest(String methodName, String url, String body, Map<String, String> headers) {
        requestLog.log(methodName, url, body, headers);
    }

    public int getRequestCount(String methodName, String url, String body, Map<String, String> headers) {
        return requestLog.count(methodName, url, body, headers);
    }

    public void shutdown() {
        http.stop(0);
    }
}