package com.example.lephleg.worldliving;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.example.lephleg.worldliving.data.Country;
import com.example.lephleg.worldliving.data.PriceItem;
import com.example.lephleg.worldliving.data.PricesContract;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

public class FetchCountryDataTask extends AsyncTask<Country, Void, Void> {

    private final String LOG_TAG = FetchCountryDataTask.class.getSimpleName();
    private WeakReference<Activity> mActivityRef;
    private ExpandableListAdapter mAdapter;
    private List<String> listDataHeader;
    private LinkedHashMap<String, List<PriceItem>> listDataChild = new LinkedHashMap<String, List<PriceItem>>();
    private ProgressDialog dialog;
    private Country mCountry;
    private String mCurrency;


    public FetchCountryDataTask(Activity activity) {
        mActivityRef = new WeakReference<>(activity);
        dialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        this.dialog.setMessage("Fetching data from API...");
        this.dialog.show();
    }

    @Override
    protected Void doInBackground(Country... params) {

        // verify size of params
        if (params.length == 0) {
            return null;
        }

        mCountry = params[0];
        Activity activity = mActivityRef.get();

        // check if data already exist in the database
        Cursor cursor = activity.getContentResolver().query(
                PricesContract.PricesEntry.buildPricesWithCountryAndCurrency(mCountry.code, Utilities.getPreferredCurrency(activity)),
                null,
                null,
                null,
                null);

        // if there are not, fetch them now
        if (cursor != null && cursor.getCount() == 0) {
            Log.d(LOG_TAG, "No data found in database for " + mCountry.name + ". Fetching from the API...");
            fetchDataFromApi();
        } else {
            Log.d(LOG_TAG, "Existing data found for " + mCountry.name + ". Fetching from the database...");
            fetchDataFromDatabase(cursor);
        }
        cursor.close();

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        Activity activity = mActivityRef.get();

        mAdapter = new PriceListAdapter(activity, listDataHeader, listDataChild);

        ExpandableListView expListView = (ExpandableListView) activity.findViewById(R.id.detail_country_exp_list);
        expListView.setAdapter(mAdapter);

        // dismiss the dialog
        if (dialog.isShowing()) {
            dialog.dismiss();
        }

    }

    private void fetchDataFromApi() {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String dataJsonStr = null;

        Activity activity = mActivityRef.get();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);

        String apiKey = BuildConfig.NUMBEO_API_KEY;
        mCurrency = prefs.getString(activity.getString(R.string.pref_currency_key),
                activity.getString(R.string.pref_currency_default_key));

        try {

            final String API_BASE_URL =
                    "http://www.numbeo.com:8008/api/country_prices?";
            final String API_KEY_PARAM = "api_key";
            final String COUNTRY_PARAM = "country";
            final String CURRENCY_PARAM = "currency";

            Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, apiKey)
                    .appendQueryParameter(COUNTRY_PARAM, mCountry.code)
                    .appendQueryParameter(CURRENCY_PARAM, mCurrency)
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
                return;
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
                return;
            }
            dataJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return;
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

            ArrayList<PriceItem> items = new ArrayList<PriceItem>();
            Log.d(LOG_TAG, "Parsing received values for " + mCountry.name + "...");

            for (int i = 0; i < priceItems.length(); i++) {

                JSONObject item = priceItems.getJSONObject(i);
                int itemId = item.getInt("item_id");

                PriceItem itemListed = PriceItem.getPriceItemById(itemId);

                if (itemListed != null) {
                    itemListed.avgPrice = Utilities.round(item.getDouble("average_price"), 2);
                    items.add(itemListed);
//                    Log.d(LOG_TAG, "Item found: " + itemListed.name + " - " + itemListed.avgPrice);
                }
            }
            Log.d(LOG_TAG, "Total items parsed: " + items.size());

            // format values to be injected to expandable list
            formatPriceItems(items);

            // cash prices in database
            storePrices(items);

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error parsing json object", e);
        }
    }


    private void fetchDataFromDatabase(Cursor cursor) {

        ArrayList<PriceItem> items = new ArrayList<PriceItem>();

        cursor.moveToFirst();
        for (int i = 0; i < cursor.getColumnCount(); i++)
        {
            // handle only price items
            if (!(cursor.getColumnName(i).equals(PricesContract.PricesEntry.COLUMN_ID)) &&
                    !(cursor.getColumnName(i).equals(PricesContract.PricesEntry.COLUMN_COUNTRY_CODE)) &&
                    !(cursor.getColumnName(i).equals(PricesContract.PricesEntry.COLUMN_CURRENCY_CODE))) {

                Log.d(LOG_TAG, "Analysing column " + cursor.getColumnName(i) + " ...");
                Log.d(LOG_TAG, "Column value: " + cursor.getDouble(i));

                // collect only non-null values
                if (!cursor.isNull(i)) {
                    PriceItem itemListed = PriceItem.getPriceItemByColumnName(cursor.getColumnName(i));
                    if (itemListed != null) {
                        itemListed.avgPrice = cursor.getDouble(i);
                        items.add(itemListed);
                    }
                }
            }
        }
        Log.d(LOG_TAG, "Total items found: " + items.size());

        formatPriceItems(items);

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

        Resources resources = mActivityRef.get().getResources();

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

    private void storePrices(List<PriceItem> items) {

        ContentValues priceValues = new ContentValues();

        priceValues.put(PricesContract.PricesEntry.COLUMN_COUNTRY_CODE, mCountry.code);
        priceValues.put(PricesContract.PricesEntry.COLUMN_CURRENCY_CODE, mCurrency);

        for (PriceItem i : items) {
            priceValues.put(i.dbColumn, i.avgPrice);
        }

        // add to database
        if ( priceValues.size() > 2 ) {
            mActivityRef.get().getContentResolver().insert(PricesContract.PricesEntry.buildPricesWithCountryAndCurrency(mCountry.code, mCurrency), priceValues);
            Log.d(LOG_TAG, "Data for " + mCountry.name + " stored in database successfully!.");

            // delete old data so we don't build up an endless history
            // TODO
        }

    }

}
