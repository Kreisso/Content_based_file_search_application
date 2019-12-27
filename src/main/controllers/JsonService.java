package main.controllers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class JsonService {
    public static final String PATH_TO_HISTORY = "../../resources/json/history.json";
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

    public static void clearHistory() {
        wordsToCount = new HashMap<>();
    }


    public void saveJson() {
        try {
            FileWriter writer = new FileWriter(
                    new File(this.getClass().getResource(PATH_TO_HISTORY).getPath()));
            writer.write(JsonService.prepareJsonToSave().toJSONString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadJson() {
        InputStream inputStream = getClass().getResourceAsStream(PATH_TO_HISTORY);
        JSONParser jsonParser = new JSONParser();
        try {
            JSONArray jsonArray = (JSONArray) jsonParser.parse(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            JsonService.setWordsToCount(jsonArray);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

}
