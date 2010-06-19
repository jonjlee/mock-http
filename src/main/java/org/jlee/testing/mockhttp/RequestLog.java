package org.jlee.testing.mockhttp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequestLog {

    List<RequestDescription> requestLog = new ArrayList<RequestDescription>();
    
    public void log(String methodName, String path, String body, Map<String, String> headers) {
        requestLog.add(new RequestDescription(null, methodName, path, body, headers));
    }

    public int count(String methodName, String path, String body, Map<String, String> headers) {
        int count = 0;
        RequestDescription template = new RequestDescription(null, methodName, path, body, headers);
        for (RequestDescription request : requestLog) {
            if (template.matches(request))
                count++;
        }
        return count;
    }
}
