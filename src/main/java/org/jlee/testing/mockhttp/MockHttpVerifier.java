package org.jlee.testing.mockhttp;
import static org.junit.Assert.*;

public class MockHttpVerifier {

    int numberOfTimes;
    MockHttp mockHttp;
    
    public MockHttpVerifier(MockHttp mockHttp) {
        this(mockHttp, 1);
    }

    public MockHttpVerifier(MockHttp mockHttp, int numberOfTimes) {
        this.mockHttp = mockHttp;
        this.numberOfTimes = numberOfTimes;
    }

    public void get(String path, String... headers) {
        assertEquals(numberOfTimes, mockHttp.getRequestCount("GET", path, null, Utils.parseHeaders(headers)));
    }

    public void put(String path, String body, String... headers) {
        assertEquals(numberOfTimes, mockHttp.getRequestCount("PUT", path, body, Utils.parseHeaders(headers)));
    }
}
