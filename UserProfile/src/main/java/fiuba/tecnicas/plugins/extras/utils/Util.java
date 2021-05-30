package fiuba.tecnicas.plugins.extras.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class Util {
    public static Map<String, Object> makeMap(String string, Object object) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(string, object);
        return map;
    }
}
