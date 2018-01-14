package com.example.lephleg.worldliving.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class PricesProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private PricesDbHelper mOpenHelper;

    static final int PRICES_WITH_COUNTRY_AND_CURRENCY = 100;

    private static final SQLiteQueryBuilder sPricesByCountryQueryBuilder;

    static {
        sPricesByCountryQueryBuilder = new SQLiteQueryBuilder();
        sPricesByCountryQueryBuilder.setTables(PricesContract.PricesEntry.TABLE_NAME);
    }

    //prices.country_code = ? AND currency_code = ?
    private static final String sCountryAndCurrencySelection =
            PricesContract.PricesEntry.TABLE_NAME +
                    "." + PricesContract.PricesEntry.COLUMN_COUNTRY_CODE + " = ? AND " +
                    PricesContract.PricesEntry.COLUMN_CURRENCY_CODE + " = ? ";

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PricesContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, PricesContract.PATH_PRICES + "/*/*", PRICES_WITH_COUNTRY_AND_CURRENCY);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new PricesDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "prices/*/*"
            case PRICES_WITH_COUNTRY_AND_CURRENCY:
            {
                retCursor = getPricesByCountryAndCurrency(uri, strings, s1);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor getPricesByCountryAndCurrency(Uri uri, String[] projection, String sortOrder) {
        String country = PricesContract.PricesEntry.getCountryFromUri(uri);
        String currency = PricesContract.PricesEntry.getCurrencyFromUri(uri);

        return sPricesByCountryQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sCountryAndCurrencySelection,
                new String[]{country, currency},
                null,
                null,
                sortOrder
        );
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRICES_WITH_COUNTRY_AND_CURRENCY:
                return PricesContract.PricesEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case PRICES_WITH_COUNTRY_AND_CURRENCY: {
                String country = contentValues.getAsString(PricesContract.PricesEntry.COLUMN_COUNTRY_CODE);
                String currency = contentValues.getAsString(PricesContract.PricesEntry.COLUMN_CURRENCY_CODE);

                long _id = db.insert(PricesContract.PricesEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = PricesContract.PricesEntry.buildPricesWithCountryAndCurrency(country, currency);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == s ) s = "1";
        switch (match) {
            case PRICES_WITH_COUNTRY_AND_CURRENCY:
                rowsDeleted = db.delete(
                        PricesContract.PricesEntry.TABLE_NAME, s, strings);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case PRICES_WITH_COUNTRY_AND_CURRENCY:
                rowsUpdated = db.update(PricesContract.PricesEntry.TABLE_NAME, contentValues, s, strings);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}