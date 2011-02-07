package org.jlee.testing.mockhttp;

import java.util.HashMap;
import java.util.Map;

public class UrlParts {

    Map<String, String> queryParams;
    String path;

    public static UrlParts parse(String url) {
        Map<String, String> qpMap = new HashMap<String, String>();
        String[] urlParts = url.split("\\?", 2);

        if (urlParts.length > 1) {
            String[] qps = urlParts[1].split("&");
            for (String qp : qps) {
                String[] qpParts = qp.split("=");
                if (qpParts.length == 1) { 
                    qpMap.put(qpParts[0].toLowerCase(), null);
                } else {
                    qpMap.put(qpParts[0].toLowerCase(), qpParts[1]);
                }
            }
        }
        
        return new UrlParts(urlParts[0], qpMap);
    }

    public UrlParts(String path, Map<String, String> queryParams) {
        this.path = path;
        this.queryParams = queryParams;
    }
    
}
