package com.example.lephleg.worldliving.data;

import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

public class PricesDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "living.db";

    public PricesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_PRICES_TABLE = "CREATE TABLE " + PricesContract.PricesEntry.TABLE_NAME + " (" +
                PricesContract.PricesEntry.COLUMN_ID + " INTEGER PRIMARY KEY," +
                PricesContract.PricesEntry.COLUMN_COUNTRY_CODE + " TEXT UNIQUE NOT NULL, " +
                PricesContract.PricesEntry.COLUMN_CURRENCY_CODE + " TEXT NOT NULL, " +
                PricesContract.PricesEntry.COLUMN_MEAL + " REAL, " +
                PricesContract.PricesEntry.COLUMN_WATER + " REAL, " +
                PricesContract.PricesEntry.COLUMN_COFFEE + " REAL, " +
                PricesContract.PricesEntry.COLUMN_COKE + " REAL, " +
                PricesContract.PricesEntry.COLUMN_MILK + " REAL, " +
                PricesContract.PricesEntry.COLUMN_BREAD + " REAL, " +
                PricesContract.PricesEntry.COLUMN_RICE + " REAL, " +
                PricesContract.PricesEntry.COLUMN_EGGS + " REAL, " +
                PricesContract.PricesEntry.COLUMN_CHICKEN + " REAL, " +
                PricesContract.PricesEntry.COLUMN_BEEF + " REAL, " +
                PricesContract.PricesEntry.COLUMN_WINE + " REAL, " +
                PricesContract.PricesEntry.COLUMN_TICKET + " REAL, " +
                PricesContract.PricesEntry.COLUMN_TAXI_START + " REAL, " +
                PricesContract.PricesEntry.COLUMN_TAXI_1KM + " REAL, " +
                PricesContract.PricesEntry.COLUMN_GAS + " REAL, " +
                PricesContract.PricesEntry.COLUMN_UTILITIES + " REAL, " +
                PricesContract.PricesEntry.COLUMN_INTERNET + " REAL, " +
                PricesContract.PricesEntry.COLUMN_GYM + " REAL, " +
                PricesContract.PricesEntry.COLUMN_CINEMA + " REAL, " +
                PricesContract.PricesEntry.COLUMN_RENT_SM_IN + " REAL, " +
                PricesContract.PricesEntry.COLUMN_RENT_SM_OUT + " REAL, " +
                PricesContract.PricesEntry.COLUMN_RENT_MD_IN + " REAL, " +
                PricesContract.PricesEntry.COLUMN_RENT_MD_OUT + " REAL, " +
                PricesContract.PricesEntry.COLUMN_SALARY + " REAL " +
                " UNIQUE (" + PricesContract.PricesEntry.COLUMN_COUNTRY_CODE + ", " +
                PricesContract.PricesEntry.COLUMN_CURRENCY_CODE + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_PRICES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PricesContract.PricesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
