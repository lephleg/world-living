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

import com.example.lephleg.worldliving.model.Country;
import com.example.lephleg.worldliving.model.PriceItem;
import com.example.lephleg.worldliving.data.PricesContract;
import com.example.lephleg.worldliving.sync.SyncAdapter;

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
import java.util.LinkedHashMap;
import java.util.List;

public class FetchCountryDataTask extends AsyncTask<Country, Void, Void> {

    public final static String LOG_TAG = FetchCountryDataTask.class.getSimpleName();
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

        Activity activity = mActivityRef.get();
        mCountry = params[0];
        mCurrency = Utilities.getPreferredCurrency(activity);

        // check if data already exist in the database
        Cursor cursor = activity.getContentResolver().query(
                PricesContract.PricesEntry.buildPricesWithCountryAndCurrency(mCountry.code, mCurrency),
                null,
                null,
                null,
                null);

        // if there are not, fetch them now
        if (cursor != null && cursor.getCount() == 0) {
            Log.d(LOG_TAG, "No data found in database for " + mCountry.name + " in " + mCurrency + ". Fetching from the API...");
            formatPriceItems(SyncAdapter.fetchDataFromApi(activity, mCountry, mCurrency));
        } else {
            Log.d(LOG_TAG, "Existing data found for " + mCountry.name + " in " + mCurrency + ". Fetching from the database...");
            formatPriceItems(fetchDataFromDatabase(cursor));
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

    private ArrayList<PriceItem> fetchDataFromDatabase(Cursor cursor) {

        ArrayList<PriceItem> items = new ArrayList<PriceItem>();

        cursor.moveToFirst();
        for (int i = 0; i < cursor.getColumnCount(); i++)
        {
            // handle only price items
            if (!(cursor.getColumnName(i).equals(PricesContract.PricesEntry.COLUMN_ID)) &&
                    !(cursor.getColumnName(i).equals(PricesContract.PricesEntry.COLUMN_COUNTRY_CODE)) &&
                    !(cursor.getColumnName(i).equals(PricesContract.PricesEntry.COLUMN_CURRENCY_CODE))) {

                Log.d(LOG_TAG, "Column: " + cursor.getColumnName(i) + " -- Value: " + cursor.getDouble(i)); // shows 0.0 for NULL

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
        Log.d(LOG_TAG, "Total items collected: " + items.size());

        return items;

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

}
