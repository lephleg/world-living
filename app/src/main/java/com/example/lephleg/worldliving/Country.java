package com.example.lephleg.worldliving;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;

import java.util.ArrayList;
import java.util.Locale;

public class Country {

    public String name;
    public String code;
    public int flag;

        public Country(String name, String code) {
            this.name = name;
            this.code = code;
        }

    public void loadFlagByCode(Context context) {

        try {
            this.flag = context.getResources()
                    .getIdentifier(this.code.toLowerCase(Locale.ENGLISH) + "_flag", "drawable",
                            context.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
            this.flag = -1;
        }
    }

    public static ArrayList<Country> getAllCountries(Context context)
    {
        Resources resources = context.getResources();

        TypedArray countryNames = resources.obtainTypedArray(R.array.country_names);
        TypedArray countryCodes = resources.obtainTypedArray(R.array.country_codes);

        int count = countryNames.length();

        ArrayList<Country> countries = new ArrayList<Country>();

        for (int i = 0; i < count; i++) {
            Country country = new Country(countryNames.getText(i).toString(), countryCodes.getText(i).toString());
            countries.add(country);
        }

        countryNames.recycle();
        countryCodes.recycle();

        return countries;
    }

}
