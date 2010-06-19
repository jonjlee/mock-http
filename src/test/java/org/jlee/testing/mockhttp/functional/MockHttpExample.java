package org.jlee.testing.mockhttp.functional;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.jlee.testing.mockhttp.MockHttp;
import org.junit.Test;

public class MockHttpExample {

    static final String URL = "http://localhost:9000/foo";

    @Test public void standard_example() throws IOException {
        HttpClient client = new HttpClient();
        MockHttp mockHttp = new MockHttp(9000);
        
        MockHttp.when(mockHttp.put("/foo", "bar", "Content-Type: text/plain; charset=utf-8")).thenReturn(204);
        MockHttp.when(mockHttp.get("/foo"))
            .thenReturn("bar", "accept: text/plain")
            .thenReturn("baz", "x-header: value");
        
        GetMethod get = new GetMethod(URL);
        get.setRequestHeader("accept", "text/plain");
        client.executeMethod(get);
        assertEquals(200, get.getStatusCode());
        assertEquals("bar", get.getResponseBodyAsString());

        get = new GetMethod(URL);
        get.setRequestHeader("x-header", "value");
        client.executeMethod(get);
        assertEquals(200, get.getStatusCode());
        assertEquals("baz", get.getResponseBodyAsString());

        PutMethod put = new PutMethod(URL);
        put.setRequestEntity(new StringRequestEntity("bar", "text/plain", "utf-8"));
        client.executeMethod(put);
        assertEquals(204, put.getStatusCode());
        
        MockHttp.verify(mockHttp).get("/foo", "accept: text/plain");
        MockHttp.verify(mockHttp).get("/foo", "x-header: value");
        MockHttp.verify(mockHttp, 2).get("/foo");
        MockHttp.verify(mockHttp, 0).get("/foo", "x-another-header: value");
        MockHttp.verify(mockHttp).put("/foo", "bar", "content-type: text/plain; charset=utf-8");
        
        get.releaseConnection();
        put.releaseConnection();
    }
    
}
