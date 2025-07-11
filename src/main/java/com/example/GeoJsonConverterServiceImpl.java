package com.example;
import org.json.JSONArray;
import org.json.JSONObject;

public class GeoJsonConverterServiceImpl extends GeoJsonConverterService {

    @Override
    public String convert(String geoJsonText) {
        // Парсим входной текст как JSON массив
        JSONArray geoJsonArray = new JSONArray(geoJsonText);
        JSONArray resultArray = new JSONArray();

        for (int i = 0; i < geoJsonArray.length(); i++) {
            JSONObject feature = geoJsonArray.getJSONObject(i);
            JSONObject properties = feature.getJSONObject("properties");
            JSONObject geometry = feature.getJSONObject("geometry");
            JSONArray coordinates = geometry.getJSONArray("coordinates");

            // Создаем новый объект для выходного формата
            JSONObject resultObject = new JSONObject();
            resultObject.put("name", properties.getString("name"));

            // Преобразование цвета, если он есть в свойствах
            if (properties.has("color")) {
                JSONObject color = properties.getJSONObject("color");
                resultObject.put("color", color);
            } else {
                // Значения по умолчанию, если цвет не указан
                JSONObject defaultColor = new JSONObject();
                defaultColor.put("red", 0);
                defaultColor.put("green", 0);
                defaultColor.put("blue", 0);
                defaultColor.put("alpha", 255);
                resultObject.put("color", defaultColor);
            }

            // Преобразование координат
            JSONArray points = new JSONArray();
            for (int j = 0; j < coordinates.length(); j++) {
                JSONArray coord = coordinates.getJSONArray(j);
                JSONObject point = new JSONObject();
                point.put("latitude", coord.getDouble(1));
                point.put("longitude", coord.getDouble(0));
                points.put(point);
            }
            resultObject.put("points", points);

            // Центр как среднее значение точек
            JSONObject center = calculateCenter(points);
            resultObject.put("center", center);

            resultArray.put(resultObject);
        }

        return resultArray.toString(2); // Преобразуем в строку с отступами для удобства чтения
    }

    private JSONObject calculateCenter(JSONArray points) {
        double sumLat = 0, sumLon = 0;
        for (int i = 0; i < points.length(); i++) {
            JSONObject point = points.getJSONObject(i);
            sumLat += point.getDouble("latitude");
            sumLon += point.getDouble("longitude");
        }

        JSONObject center = new JSONObject();
        center.put("latitude", sumLat / points.length());
        center.put("longitude", sumLon / points.length());
        return center;
    }
}
