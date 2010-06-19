package org.jlee.testing.mockhttp;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestMockHttpBasic {
    
    static final String PATH = "/path";
    static final String URL = "http://localhost:9000" + PATH;
    static final String BODY = "body";
    
    MockHttp mockHttp;
    HttpClient client;
    HttpMethod method;
    
    @Before public void setup() {
        mockHttp = new MockHttp(9000);

        client = new HttpClient();
        client.getParams().setParameter(HttpClientParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));
    }
    
    @After public void teardown() {
        if (method != null)
            method.releaseConnection();
        mockHttp.shutdown();
    }

    @Test public void construction_starts_listening_server() throws IOException {
        method = new GetMethod(URL);
        client.executeMethod(method);
        assertTrue(method.getStatusCode() >= 200);
    }

    @Test(expected=IOException.class) public void shutdown_stops_server() throws IOException {
        // Use a different port than all the other tests to verify that
        // a previous server instance isn't responding
        MockHttp mockHttp = new MockHttp(9001);
        mockHttp.shutdown();

        method = new GetMethod("http://localhost:9001");
        client.executeMethod(method);
    }
    
    @Test public void get_returns_specified_status_code() throws IOException {
        method = new GetMethod(URL);
        MockHttp.when(mockHttp.get(PATH)).thenReturn(200, null);
        client.executeMethod(method);
        
        assertEquals(200, method.getStatusCode());
        assertEquals("0", method.getResponseHeader("Content-Length").getValue());
        assertEquals(0, method.getResponseBody().length);
    }
    
    @Test public void get_returns_specified_body() throws IOException {
        method = new GetMethod(URL);
        MockHttp.when(mockHttp.get(PATH)).thenReturn(BODY);
        client.executeMethod(method);
        
        assertEquals(200, method.getStatusCode());
        assertEquals(BODY, method.getResponseBodyAsString());
    }

    @Test public void put_returns_specified_status_code() throws IOException {
        method = new PutMethod(URL);
        MockHttp.when(mockHttp.put(PATH)).thenReturn(200);
        client.executeMethod(method);
        
        assertEquals(200, method.getStatusCode());
        assertEquals("0", method.getResponseHeader("Content-Length").getValue());
        assertEquals(0, method.getResponseBody().length);
    }

    @Test public void put_returns_specified_body() throws IOException {
        method = new PutMethod(URL);
        MockHttp.when(mockHttp.put(PATH)).thenReturn(BODY);
        client.executeMethod(method);
        
        assertEquals(200, method.getStatusCode());
        assertEquals(BODY, method.getResponseBodyAsString());
    }

    @Test public void post_returns_specified_status_code() throws IOException {
        method = new PostMethod(URL);
        MockHttp.when(mockHttp.post(PATH)).thenReturn(200);
        client.executeMethod(method);
        
        assertEquals(200, method.getStatusCode());
        assertEquals("0", method.getResponseHeader("Content-Length").getValue());
        assertEquals(0, method.getResponseBody().length);
    }

    @Test public void post_returns_specified_body() throws IOException {
        method = new PostMethod(URL);
        MockHttp.when(mockHttp.post(PATH)).thenReturn(BODY);
        client.executeMethod(method);
        
        assertEquals(200, method.getStatusCode());
        assertEquals(BODY, method.getResponseBodyAsString());
    }

    @Test public void delete_returns_specified_status_code() throws IOException {
        method = new DeleteMethod(URL);
        MockHttp.when(mockHttp.delete(PATH)).thenReturn(200);
        client.executeMethod(method);
        
        assertEquals(200, method.getStatusCode());
        assertEquals("0", method.getResponseHeader("Content-Length").getValue());
        assertEquals(0, method.getResponseBody().length);
    }

    @Test public void wrong_method_returns_500() throws IOException {
        method = new DeleteMethod(URL);
        MockHttp.when(mockHttp.get(PATH)).thenReturn(200);
        client.executeMethod(method);
        
        assertEquals(500, method.getStatusCode());
    }
    
    @Test public void Test_Chained_Returns() throws IOException {
        method = new GetMethod(URL);
        MockHttp.when(mockHttp.get(PATH))
            .thenReturn(200, "foo")
            .thenReturn(200, "bar")
            .thenReturn(404);
        
        client.executeMethod(method);
        assertEquals(200, method.getStatusCode());
        assertEquals("foo", method.getResponseBodyAsString());

        client.executeMethod(method);
        assertEquals(200, method.getStatusCode());
        assertEquals("bar", method.getResponseBodyAsString());

        client.executeMethod(method);
        assertEquals(404, method.getStatusCode());

        client.executeMethod(method);
        assertEquals(404, method.getStatusCode());
    }

    @Test public void Last_When_Call_Wins() throws IOException {
        method = new GetMethod(URL);
        MockHttp.when(mockHttp.get(PATH)).thenReturn(200, "foo");
        MockHttp.when(mockHttp.get(PATH)).thenReturn(404);
        
        client.executeMethod(method);
        assertEquals(404, method.getStatusCode());
    }
}
