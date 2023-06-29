package il.co.rtcohen.rt.service.cities;

import il.co.rtcohen.rt.dal.dao.City;
import il.co.rtcohen.rt.dal.repositories.CityRepository;
import il.co.rtcohen.rt.utils.Logger;
import il.co.rtcohen.rt.utils.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class IsraelCities {
    @Autowired
    CityRepository cityRepository;

    private static final String cityListUrl = "https://data.gov.il/api/3/action/datastore_search?resource_id=5c78e9fa-c2e2-4771-93ff-7f400a12f7ba&limit=100000";
    private static List<String> citiesInIsrael = null;

    public City findCityNameInAddress(@NotNull String address) {
        City city = null;
        List<String> citiesFound = new ArrayList<>();
        for (String cityName : getCitiesList()) {
            if (address.contains(cityName)) {
                citiesFound.add(cityName);
            }
        }
        if (1 == citiesFound.size()) {
            String cityName = citiesFound.get(0);
            city = cityRepository.getItemByName(cityName);
            if (null == city) {
                city = new City();
                city.setName(cityName);
                cityRepository.insertItem(city);
            }
        }
        return city;
    }

    private List<String> getCitiesList() {
        if (null == citiesInIsrael) {
            try {
                Logger.getLogger(this).info("Trying to retrieve israel cities list from " + cityListUrl);
                JSONObject json = new JSONObject(sendHttpRequest(cityListUrl));
                JSONObject result = json.getJSONObject("result");
                JSONArray array = result.getJSONArray("records");
                citiesInIsrael = jsonArrayToList(array, "שם_ישוב");
                Logger.getLogger(this).info("Israel cities list was retrieved");
            } catch (Exception e) {
                Logger.getLogger(this).info("Couldn't retrieve israel cities list: " + e);
            }
        }
        return citiesInIsrael;
    }

    private static String sendHttpRequest(String url) throws IOException {
        URL apiUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } else {
            throw new IOException("HTTP request failed. Response Code: " + responseCode);
        }
    }

    private static List<String> jsonArrayToList(JSONArray jsonArray, String column) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                String value = jsonArray.getJSONObject(i).getString(column);
                list.add(StringUtils.reduceSpaces(value));
            } catch (JSONException e) {
                Logger.info(IsraelCities.class, "The following exception casued by the following json object: " + jsonArray.getJSONObject(i));
                Logger.exception(IsraelCities.class, e);
            }
        }
        return list;
    }
}
