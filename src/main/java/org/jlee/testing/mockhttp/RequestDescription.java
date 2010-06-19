package org.jlee.testing.mockhttp;

import java.util.Map;

public class RequestDescription {
    
    MockHttp http;
    public MockHttp getHttp() { return http; }

    String method;
    public String getMethod() { return method; }

    String path;
    public String getPath() { return path; }

    String body;
    public String getBody() { return body; }

    Map<String, String> headers;
    public Map<String, String> getHeaders() { return headers; }

    public RequestDescription(MockHttp http, String method, String path, String body, Map<String, String> headers) {
        this.http = http;
        this.method = method.toLowerCase();
        this.path = path.toLowerCase();
        this.body = body;
        this.headers = headers;
    }

    public boolean matches(String methodName, String path, String body, Map<String, String> headers) {
        return method.equals(methodName.toLowerCase()) 
                    && pathsMatch(this.path, path) 
                    && bodysMatch(this.body, body) 
                    && headersMatch(this.headers, headers);
    }

    public boolean matches(Object obj) {
        if (obj != null && !(obj instanceof RequestDescription))
            return false;

        RequestDescription r = (RequestDescription) obj;
        return matches(r.getMethod(), r.getPath(), r.getBody(), r.getHeaders());
    }

    private boolean headersMatch(Map<String, String> headersTemplate, Map<String, String> headers) {
        if (headersTemplate != null && headers != null) {
            for (String header : headersTemplate.keySet()) {
                String templateValue = headersTemplate.get(header);
                String value = headers.get(header);
                if ((value == null && templateValue != null) 
                        || (value != null && templateValue == null) 
                        || !templateValue.equals(value))
                    return false;
            }
        }
        
        return true;
    }

    private boolean bodysMatch(String bodyTemplate, String body) {
        return bodyTemplate == null || body == null || bodyTemplate.equals(body);
    }

    private boolean pathsMatch(String pathTemplate, String path) {
        return pathTemplate == null || path == null || pathTemplate.equals(path.toLowerCase());
    }
}
