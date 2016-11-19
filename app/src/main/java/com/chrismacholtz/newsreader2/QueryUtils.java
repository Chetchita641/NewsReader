package com.chrismacholtz.newsreader2;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SWS Customer on 11/12/2016.
 */

public final class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
    }

    //Fetch the pertinent news using the Guardian API
    public static List<News> fetchNews(String urlString) {
        //Make a url out of the string
        URL url = makeUrl(urlString);

        List<News> listOfNews = new ArrayList<>();
        String JSONResponse;

        try {
            //Establish the Http connection
            JSONResponse = makeHttpResponse(url);
            //Sort through the response to find the pertinent JSON Objects
            JSONObject root = new JSONObject(JSONResponse);
            JSONObject response = root.getJSONObject("response");

            //If there's no articles, abort
            if (response.getInt("total") == 0)
                return null;

            //Collects results. Default limit is 20 articles.
            JSONArray results = response.getJSONArray("results");
            for (int i = 0; i < results.length() || i < 20; i++) {
                JSONObject currentResult = results.getJSONObject(i);

                String webTitle = currentResult.getString("webTitle");
                String pubDate = currentResult.getString("webPublicationDate");
                String sectionName = currentResult.getString("sectionName");
                String webUrl = currentResult.optString("webUrl");

                listOfNews.add(new News(webTitle, pubDate, sectionName, webUrl));
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error receiving information", e);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing JSON information", e);
        }

        return listOfNews;
    }

    //Create a URL from a String
    private static URL makeUrl(String urlString) {
        URL url = null;

        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Malformed URL Exception", e);
        }

        return url;
    }

    //Establish an Http connection and run it through an InputStream
    private static String makeHttpResponse(URL url) throws IOException {
        String response = "";

        if (url == null)
            return response;

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                response = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error: Http Response Code:" + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error receiving information", e);

        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
            if (inputStream != null)
                inputStream.close();
        }

        return response;
    }

    //Receive an InputStream and convert it into a String
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }

        return output.toString();
    }
}
