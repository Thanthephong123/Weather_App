import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class WeatherApp {
    //Fetch weather data for given location
    public static JSONObject getWeatherData(String locationName) {
        // get location using API
        JSONArray locationData = getLocationData(locationName);
        JSONObject location =(JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        //Build API for getting weather data using latitude and longitude
        String urlString = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude + "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m";
        try{
            //API weather call
            HttpURLConnection conm = fetchAPIResponse(urlString);
            if (conm.getResponseCode() != 200) {
                System.out.println("Could not connect to API");
                return null;
            }

            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conm.getInputStream());
            while (scanner.hasNext()) {
                resultJson.append(scanner.nextLine());

            }
            scanner.close();
            conm.disconnect();

            // parse into JSON
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            //Hourly data
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            //Temperature
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            //Weather Condition
            JSONArray weathercode = (JSONArray) hourly.get("weather_code");
            String weatherCondition = convertWeatherCode((long) weathercode.get(index));
            System.out.println(weathercode.get(index));

            //Humidity
            JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            //Windspeed
            JSONArray windspeedData = (JSONArray) hourly.get("wind_speed_10m");
            double windspeed = (double) windspeedData.get(index);

            //Build JSON Object
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);

            return weatherData;

        }catch(Exception e){
            e.printStackTrace();
        }
        return null;

    }
    // get coordinates from a name
    private static JSONArray getLocationData(String locationName) {
        //Follow API request format
        locationName = locationName.replaceAll(" ", "+");

        //Building API call
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" + locationName + "&count=10&language=en&format=json";
        try{
            //call API and get response
            HttpURLConnection conm = fetchAPIResponse(urlString);

            //check response status
            if (conm.getResponseCode() != 200) {
                System.out.println("Could not connect to API");
                return null;
            }
            else {
                //store API results
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conm.getInputStream());
                //Read and store
                while (scanner.hasNext()) {
                    resultJson.append(scanner.nextLine());

                }
                scanner.close();
                conm.disconnect();

                //Parse JSON string into JSON obj
                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                //Get list of location data
                JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
                return locationData;
            }


        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    private static HttpURLConnection fetchAPIResponse(String urlString) {
        try{
            //create connection
            URL url = new URL(urlString);
            HttpURLConnection conm = (HttpURLConnection) url.openConnection();

            //set request method to get
            conm.setRequestMethod("GET");

            //Connect to API
            conm.connect();
            return conm;
        }catch(IOException e){
            e.printStackTrace();
        }
        return null; // Connection error
    }

    private static int findIndexOfCurrentTime(JSONArray timeList) {
        String currentTime = getCurrentTime();
        for(int i=0; i<timeList.size(); i++) {
            String time = (String) timeList.get(i);
            if (time.equalsIgnoreCase(currentTime)) {
                return i;
            }
        }
        return 0;

    }

    // Get time and date, format
    private static String getCurrentTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");
        String formattedDateTime = currentDateTime.format(formatter);
        return formattedDateTime;

    }

    private static String convertWeatherCode(long weathercode) {
        String weatherCondition = "";
        if (weathercode == 0L){
            weatherCondition = "Clear";
        }
        else if (weathercode <= 3L && weathercode > 0L){
            weatherCondition = "Cloudy";
        }
        else if ((weathercode >= 51L && weathercode <= 67L) || (weathercode >=80L && weathercode <= 99L)){
            weatherCondition = "Rain";

        }
        else if (weathercode >= 71L && weathercode <= 77L) {
            weatherCondition = "Snow";
        }

        return weatherCondition;
    }



}
