package com.example.lephleg.worldliving;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.example.lephleg.worldliving.data.Country;
import com.example.lephleg.worldliving.data.PriceItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FetchCountryDataTask extends AsyncTask<Country, Void, String[]> {

    private final String LOG_TAG = FetchCountryDataTask.class.getSimpleName();
    private WeakReference<Activity> mActivity;
    private ExpandableListAdapter mAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<PriceItem>> listDataChild = new HashMap<String, List<PriceItem>>();
    private ProgressDialog dialog;


    public FetchCountryDataTask(Activity activity) {
        mActivity = new WeakReference<>(activity);
        dialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        this.dialog.setMessage("Loading data...");
        this.dialog.show();
    }

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

        Activity activity = mActivity.get();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);

        String apiKey = BuildConfig.NUMBEO_API_KEY;
        Country country = params[0];
        String currency = prefs.getString(activity.getString(R.string.pref_currency_key),
                activity.getString(R.string.pref_currency_default_key));

        try {

            final String API_BASE_URL =
                    "http://www.numbeo.com:8008/api/country_prices?";
            final String API_KEY_PARAM = "api_key";
            final String COUNTRY_PARAM = "country";
            final String CURRENCY_PARAM = "currency";

            Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, apiKey)
                    .appendQueryParameter(COUNTRY_PARAM, country.code)
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

        try {
            // Instantiate a JSON object from the request response
            JSONObject jsonObject = new JSONObject(dataJsonStr);
            JSONArray priceItems = jsonObject.getJSONArray("prices");

            List<PriceItem> items = new ArrayList<PriceItem>();
            Log.d(LOG_TAG, "Parsing values for " + country.name + "...");

            for (int i = 0; i < priceItems.length(); i++) {

                JSONObject item = priceItems.getJSONObject(i);
                int itemId = item.getInt("item_id");

                PriceItem itemListed = PriceItem.getPriceItemById(itemId);

                if (itemListed != null) {
                    itemListed.avgPrice = item.getDouble("average_price");
                    items.add(itemListed);
//                    Log.d(LOG_TAG, "Item found: " + itemListed.name + " - " + itemListed.avgPrice);
                }
            }
            Log.d(LOG_TAG, "Total items found: " + items.size());

            formatPriceItems(items);

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error parsing json object", e);
        }

        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }

    @Override
    protected void onPostExecute(String[] strings) {

        mAdapter = new PriceListAdapter(mActivity.get(), listDataHeader, listDataChild);

        ExpandableListView expListView = (ExpandableListView) mActivity.get().findViewById(R.id.detail_country_exp_list);
        expListView.setAdapter(mAdapter);

        // dismiss the dialog
        if (dialog.isShowing()) {
            dialog.dismiss();
        }

    }

    private void formatPriceItems(List<PriceItem> items) {

        List<PriceItem> restaurants = new ArrayList<PriceItem>();
        List<PriceItem> markets = new ArrayList<PriceItem>();
        List<PriceItem> transportation = new ArrayList<PriceItem>();
        List<PriceItem> utilities = new ArrayList<PriceItem>();
        List<PriceItem> leisure = new ArrayList<PriceItem>();
        List<PriceItem> rent = new ArrayList<PriceItem>();
        List<PriceItem> earnings = new ArrayList<PriceItem>();

        for (PriceItem i : items) {
            switch (i.groupId) {
                case 1:
                    restaurants.add(i);
                    break;
                case 2:
                    markets.add(i);
                    break;
                case 3:
                    transportation.add(i);
                    break;
                case 4:
                    utilities.add(i);
                    break;
                case 5:
                    leisure.add(i);
                    break;
                case 6:
                    rent.add(i);
                    break;
                case 7:
                    earnings.add(i);
                    break;
            }
        }

        Resources resources = mActivity.get().getResources();

        if (restaurants.size() > 0) {
            listDataChild.put(resources.getString(R.string.restaurants_item_group_label), restaurants);
        }
        if (markets.size() > 0) {
            listDataChild.put(resources.getString(R.string.markets_item_group_label), markets);
        }
        if (transportation.size() > 0) {
            listDataChild.put(resources.getString(R.string.transportation_item_group_label), transportation);
        }
        if (utilities.size() > 0) {
            listDataChild.put(resources.getString(R.string.utilities_item_group_label), utilities);
        }
        if (leisure.size() > 0) {
            listDataChild.put(resources.getString(R.string.leisure_item_group_label), leisure);
        }
        if (rent.size() > 0) {
            listDataChild.put(resources.getString(R.string.rent_item_group_label), rent);
        }
        if (earnings.size() > 0) {
            listDataChild.put(resources.getString(R.string.earnings_item_group_label), earnings);
        }

        listDataHeader = new ArrayList<String>(listDataChild.keySet());
    }
}
