package de.gzockoll.solar.piko;

import java.util.HashMap;
import java.util.Map;

public class Aggregator {
    public Map<String,Number> aggregate(Map<String,Number> data, Map headers, String val, Map nextHeaders) {
        String content= (String) headers.get("content");
        if (data==null)
            data=new HashMap<>();
        data.put(content,Double.parseDouble(val));
        return data;
    }
}
