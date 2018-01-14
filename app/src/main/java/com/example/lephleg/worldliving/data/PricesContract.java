package com.example.lephleg.worldliving.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class PricesContract {

    public static final String CONTENT_AUTHORITY = "com.example.lephleg.worldliving.data";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PRICES = "prices";

    public static final class PricesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PRICES).build();

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRICES;

        static final String TABLE_NAME = "prices";

        static final String COLUMN_ID = "id";
        static final String COLUMN_COUNTRY_CODE = "country_code";
        static final String COLUMN_CURRENCY_CODE = "currency_code";

        static final String COLUMN_MEAL = "meal";
        static final String COLUMN_WATER = "water";
        static final String COLUMN_COFFEE = "coffee";
        static final String COLUMN_COKE = "coke";
        static final String COLUMN_MILK = "milk";
        static final String COLUMN_BREAD = "bread";
        static final String COLUMN_RICE = "rice";
        static final String COLUMN_EGGS = "eggs";
        static final String COLUMN_CHICKEN = "chicken";
        static final String COLUMN_BEEF = "beef";
        static final String COLUMN_WINE = "wine";

        static final String COLUMN_TICKET = "ticket";
        static final String COLUMN_TAXI_START = "taxi_start";
        static final String COLUMN_TAXI_1KM = "taxi_1km";
        static final String COLUMN_GAS = "gas";

        static final String COLUMN_UTILITIES = "utilities";
        static final String COLUMN_INTERNET = "internet";

        static final String COLUMN_GYM = "gym";
        static final String COLUMN_CINEMA = "cinema";

        static final String COLUMN_RENT_SM_IN = "rent_sm_in";
        static final String COLUMN_RENT_SM_OUT = "rent_sm_out";
        static final String COLUMN_RENT_MD_IN = "rent_md_in";
        static final String COLUMN_RENT_MD_OUT = "rent_md_out";

        static final String COLUMN_SALARY = "salary";

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
