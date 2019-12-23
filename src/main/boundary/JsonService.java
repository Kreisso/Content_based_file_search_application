package main.boundary;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JsonService {
    private static Map<String, Long> wordsToCount = new HashMap<>();

    public static void setWordsToCount(JSONArray arrayOfWords) {
        for (Object o : arrayOfWords) {
            JSONObject word = (JSONObject) o;
            String value = (String) word.get("value");
            Long count = (Long) word.get("count");
            wordsToCount.put(value, count);
        }
    }

    public static Map<String, Long> getWordsToCount() {
        return wordsToCount;
    }

    public static void addWord(String sample) {
        if (sample.length() <= 0) {
            return;
        }
        if (wordsToCount.containsKey(sample)) {
            Long count = wordsToCount.get(sample);
            wordsToCount.replace(sample, count + 1);
        } else {
            wordsToCount.put(sample, 1L);
        }
    }

    public static JSONArray prepareJsonToSave() {
        JSONArray jsonArray = new JSONArray();
        wordsToCount.entrySet().forEach(element -> jsonArray.add(createJsonObject(element)));
        return jsonArray;
    }

    private static Object createJsonObject(Map.Entry<String, Long> element) {
        JSONObject word = new JSONObject();
        word.put("value", element.getKey());
        word.put("count", element.getValue());
        return word;
    }

}
