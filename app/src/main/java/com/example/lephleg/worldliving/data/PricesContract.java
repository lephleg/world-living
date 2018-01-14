package com.example.lephleg.worldliving.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class PricesContract {

    public static final String CONTENT_AUTHORITY = "com.example.lephleg.worldliving";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PRICES = "prices";

    public static final class PricesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PRICES).build();

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRICES;

        public static final String TABLE_NAME = "prices";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_COUNTRY_CODE = "country_code";
        public static final String COLUMN_CURRENCY_CODE = "currency_code";

        public static final String COLUMN_MEAL = "meal";
        public static final String COLUMN_WATER = "water";
        public static final String COLUMN_COFFEE = "coffee";
        public static final String COLUMN_COKE = "coke";
        public static final String COLUMN_MILK = "milk";
        public static final String COLUMN_BREAD = "bread";
        public static final String COLUMN_RICE = "rice";
        public static final String COLUMN_EGGS = "eggs";
        public static final String COLUMN_CHICKEN = "chicken";
        public static final String COLUMN_BEEF = "beef";
        public static final String COLUMN_WINE = "wine";

        public static final String COLUMN_TICKET = "ticket";
        public static final String COLUMN_TAXI_START = "taxi_start";
        public static final String COLUMN_TAXI_1KM = "taxi_1km";
        public static final String COLUMN_GAS = "gas";

        public static final String COLUMN_UTILITIES = "utilities";
        public static final String COLUMN_INTERNET = "internet";

        public static final String COLUMN_GYM = "gym";
        public static final String COLUMN_CINEMA = "cinema";

        public static final String COLUMN_RENT_SM_IN = "rent_sm_in";
        public static final String COLUMN_RENT_SM_OUT = "rent_sm_out";
        public static final String COLUMN_RENT_MD_IN = "rent_md_in";
        public static final String COLUMN_RENT_MD_OUT = "rent_md_out";

        public static final String COLUMN_SALARY = "salary";

        public static Uri buildPricesWithCountryAndCurrency(String country, String currency) {
            return CONTENT_URI.buildUpon().appendPath(country)
                    .appendPath(currency).build();
        }

        public static String getCountryFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getCurrencyFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

    }
}
