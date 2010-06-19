package org.jlee.testing.mockhttp;

import java.io.IOException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestMockHttpVerify {
    
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
    
    @Test(expected=AssertionError.class) public void verifyNever_asserts_when_url_was_retrieved() throws IOException {
        method = new GetMethod(URL);
        client.executeMethod(method);
        
        MockHttp.verify(mockHttp, 0).get(PATH);
    }

    @Test public void verifyNever_doesnt_asserts_when_url_not_retrieved() {
        MockHttp.when(mockHttp.get(PATH)).thenReturn(200);
        MockHttp.verify(mockHttp, 0).get(PATH);
    }

    @Test public void verifyNever_doesnt_asserts_when_different_method_used() throws IOException {
        MockHttp.when(mockHttp.get(PATH)).thenReturn(200);
        method = new PutMethod(URL);
        
        client.executeMethod(method);
        
        MockHttp.verify(mockHttp, 0).get(PATH);
    }

    @Test public void verifyNever_doesnt_asserts_when_different_url_used() throws IOException {
        MockHttp.when(mockHttp.get(PATH)).thenReturn(200);
        method = new GetMethod(URL + "/foo");
        
        client.executeMethod(method);
        
        MockHttp.verify(mockHttp, 0).get(PATH);
    }

    @Test public void verifyOnce_doesnt_assert_when_url_retrieved() throws IOException {
        MockHttp.when(mockHttp.get(PATH)).thenReturn(200);
        method = new GetMethod(URL);
        
        client.executeMethod(method);
        
        MockHttp.verify(mockHttp).get(PATH);
    }

    @Test(expected=AssertionError.class) public void verifyOnce_asserts_when_url_not_retrieved() {
        MockHttp.when(mockHttp.get(PATH)).thenReturn(200);
        MockHttp.verify(mockHttp).get(PATH);
    }

    @Test(expected=AssertionError.class) public void verifyOnce_asserts_when_url_retrieved_too_many_times() throws IOException {
        MockHttp.when(mockHttp.get(PATH)).thenReturn(200);
        method = new GetMethod(URL);
        
        client.executeMethod(method);
        client.executeMethod(method);
        
        MockHttp.verify(mockHttp).get(PATH);
    }

}
