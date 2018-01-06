package com.example.lephleg.worldliving;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.lephleg.worldliving.data.Country;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchCountryDataTask extends AsyncTask<Country, Void, String[]> {

    private final String LOG_TAG = FetchCountryDataTask.class.getSimpleName();

    @Override
    protected String[] doInBackground(Country... params) {

        // If there's no zip code, there's nothing to look up.  Verify size of params.
        if (params.length == 0) {
            return null;
        }

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String dataJsonStr = null;

        // TODO: Store key to gradle build
        String apiKey = "c2p5s2nxe3bulq";
        String country = params[0].code;
        // TODO: Fetch currency from preferences
        String currency = "USD";

        try {

            final String API_BASE_URL =
                    "http://www.numbeo.com:8008/api/country_prices?";
            final String API_KEY_PARAM = "api_key";
            final String COUNTRY_PARAM = "country";
            final String CURRENCY_PARAM = "currency";

            Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, apiKey)
                    .appendQueryParameter(COUNTRY_PARAM, country)
                    .appendQueryParameter(CURRENCY_PARAM, currency)
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            dataJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        Log.d(LOG_TAG, dataJsonStr);

        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }

}
