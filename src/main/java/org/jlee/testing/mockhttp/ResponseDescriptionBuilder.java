package org.jlee.testing.mockhttp;

public class ResponseDescriptionBuilder {

    RequestDescription request;
    
    public ResponseDescriptionBuilder(RequestDescription request) {
        this.request = request;
    }
    
    public ResponseDescriptionBuilder thenReturn(int statusCode) {
        return thenReturn(statusCode, null);
    }

    public ResponseDescriptionBuilder thenReturn(String body, String... headers) {
        return thenReturn(200, body, headers);
    }

    public ResponseDescriptionBuilder thenReturn(int statusCode, String body, String... headers) {
        this.request.getHttp().addMapping(this.request, new ResponseDescription(statusCode, body, headers));
        return this;
    }
}
