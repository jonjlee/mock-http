package org.jlee.testing.mockhttp;

import java.util.HashMap;
import java.util.Map;

public class ResponseDescription {
    
    public int getStatusCode() { return statusCode; }
    int statusCode = 500;

    String body = "";
    public String getBody() { return body; }

    Map<String, String> headers = new HashMap<String, String>();
    public Map<String, String> getHeaders() { return headers; }
    
    public ResponseDescription(int statusCode, String body, String[] headers) {
        if (body == null) 
            body = "";
        
        this.statusCode = statusCode;
        this.body = body;
        this.headers = Utils.parseHeaders(headers);
    }
}
