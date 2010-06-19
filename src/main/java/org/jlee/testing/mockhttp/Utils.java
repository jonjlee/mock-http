package org.jlee.testing.mockhttp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

public class Utils {

    public static Map<String, String> parseHeaders(String[] headers) {
        if (headers == null)
            return new HashMap<String, String>();
        
        Map<String, String> ret = new HashMap<String, String>();
        for (String header : headers) {
        
            String[] parts = header.split(":", 2);
            
            if (parts.length == 1) 
                ret.put(parts[0].trim().toLowerCase(), "");
            ret.put(parts[0].trim().toLowerCase(), parts[1].trim().toLowerCase());
        }
        
        return ret;
    }
    
    public static Map<String, String> parseHeaders(Map<String, List<String>> headers) {
        Map<String, String> ret = new HashMap<String, String>();
        for (String header : headers.keySet()) {
            ret.put(header.trim().toLowerCase(), joinList(headers.get(header), ","));
        }
        return ret;
    }

    private static String joinList(List<String> list, String delimiter) {
        StringBuilder ret = new StringBuilder("");
        for (String item : list) {
            if (ret.length() > 0) {
                ret.append(delimiter);
            }
            ret.append(item);
        }
        return ret.toString();
    }

    public static String readStream(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        while (true) {
            final int readCount = in.read(buffer);
            if (readCount == -1) {
                break;
            }
            out.write(buffer, 0, readCount);
        }
        return new String(out.toByteArray());
    }
    
}
