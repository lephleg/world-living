package com.example.lephleg.worldliving.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.example.lephleg.worldliving.BuildConfig;
import com.example.lephleg.worldliving.FetchCountryDataTask;
import com.example.lephleg.worldliving.R;
import com.example.lephleg.worldliving.Utilities;
import com.example.lephleg.worldliving.data.PricesContract;
import com.example.lephleg.worldliving.model.Country;
import com.example.lephleg.worldliving.model.PriceItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String LOG_TAG = SyncAdapter.class.getSimpleName();

    // 60 seconds (1 minute) * 60 minutes (1 hour) * 24 * 3= 3 days
    public static final int SYNC_INTERVAL = 60 * 60 * 24 * 3;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/6;

    private Context mContext;

    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        Log.d(LOG_TAG, "Starting sync...");
        Map<Entry<Country, String>, ArrayList<PriceItem>> map = new HashMap<>();

        try {
            // query the database for stored existing data
            Cursor cursor = provider.query(PricesContract.PricesEntry.CONTENT_URI, null, null, null, null);

            // iterate through database records and fetch fresh data for each one of them
            // store data into composite map
            while (cursor != null && cursor.moveToNext()) {
                String countryCode = cursor.getString(1);
                Country country = Country.getByCode(mContext, countryCode);
                String currency = cursor.getString(2);
                if (country != null) {
                    Log.d(LOG_TAG, "Entry for " + country.name + " in " + currency + " found in database! Fetching fresh data...");
                    ArrayList<PriceItem> items = SyncAdapter.fetchDataFromApi(mContext, country, currency);
                    map.put(new SimpleEntry<>(country, currency), items);

                }

            }

            // store data to database with a bulk insert
            SyncAdapter.storeBulkPrices(mContext, map);


        } catch (RemoteException e) {
            Log.e(LOG_TAG, "Error querying content provider from sync service. " + e.getMessage());
        }

    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        SyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
    }

    public static ArrayList<PriceItem> fetchDataFromApi(Context context, Country country, String currency) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String dataJsonStr = null;

        String apiKey = BuildConfig.NUMBEO_API_KEY;

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
            Log.e(FetchCountryDataTask.LOG_TAG, "Error ", e);
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
                    Log.e(FetchCountryDataTask.LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            // Instantiate a JSON object from the request response
            JSONObject jsonObject = new JSONObject(dataJsonStr);
            JSONArray priceItems = jsonObject.getJSONArray("prices");

            ArrayList<PriceItem> items = new ArrayList<PriceItem>();
            Log.d(FetchCountryDataTask.LOG_TAG, "Parsing received values for " + country.name + "...");

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
            Log.d(SyncAdapter.LOG_TAG, "Total items parsed: " + items.size());

            // cache prices in database
            storePrices(context, items, country, currency);

            return items;

        } catch (Exception e) {
            Log.e(SyncAdapter.LOG_TAG, "Error parsing json object", e);
        }
        return null;
    }

    private static void storePrices(Context context, List<PriceItem> items, Country country, String currency) {

        ContentValues priceValues = new ContentValues();

        priceValues.put(PricesContract.PricesEntry.COLUMN_COUNTRY_CODE, country.code);
        priceValues.put(PricesContract.PricesEntry.COLUMN_CURRENCY_CODE, currency);

        for (PriceItem i : items) {
            priceValues.put(i.dbColumn, i.avgPrice);
        }

        // add to database
        if ( priceValues.size() > 2 ) {
            context.getContentResolver().insert(PricesContract.PricesEntry.buildPricesWithCountryAndCurrency(country.code, currency), priceValues);
            Log.d(SyncAdapter.LOG_TAG, "Data for " + country.name + " stored in database successfully!");

            // no need to delete old data as they'll always be replaced due to country-currency unique constraint
        }

    }

    private static void storeBulkPrices(Context context, Map<Entry<Country, String>, ArrayList<PriceItem>> map) {

        ArrayList<ContentValues> priceValuesList = new ArrayList<>();

        for (Entry<Entry<Country, String>, ArrayList<PriceItem>> entry : map.entrySet()) {
            ContentValues values = new ContentValues();

            Entry<Country, String> compKey = entry.getKey();
            Country country = compKey.getKey();
            String currency = compKey.getValue();

            values.put(PricesContract.PricesEntry.COLUMN_COUNTRY_CODE, country.code);
            values.put(PricesContract.PricesEntry.COLUMN_CURRENCY_CODE, currency);

            for (PriceItem i : entry.getValue()) {
                values.put(i.dbColumn, i.avgPrice);
            }
            priceValuesList.add(values);
        }

        int count = priceValuesList.size();

        ContentValues[] contentValues = priceValuesList.toArray(new ContentValues[count]);
        context.getContentResolver().bulkInsert(PricesContract.PricesEntry.CONTENT_URI, contentValues);

        Log.d(SyncAdapter.LOG_TAG, "Data for " + count + " entries stored in database successfully!");

    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
