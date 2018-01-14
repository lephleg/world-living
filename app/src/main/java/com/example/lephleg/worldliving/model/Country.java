package com.example.lephleg.worldliving.model;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.lephleg.worldliving.R;

import java.util.ArrayList;
import java.util.Locale;

public class Country implements Parcelable{

    public String name;
    public String code;
    public int flag;

        public Country(String name, String code) {
            this.name = name;
            this.code = code;
        }

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Country(Parcel in) {
        this.name = in.readString();
        this.code = in.readString();
        this.flag = in.readInt();
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

    public static ArrayList<Country> getAllCountries(Context context) {
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

    public static Country getByCode(Context context, String code) {
        ArrayList<Country> countries = Country.getAllCountries(context);

        for (Country c : countries) {
            if (c.code.equals(code)) {
                return c;
            }
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(this.name);
            parcel.writeString(this.code);
            parcel.writeInt(this.flag);
    }

    public static final Parcelable.Creator<Country> CREATOR = new Parcelable.Creator<Country>() {
        public Country createFromParcel(Parcel in) {
            return new Country(in);
        }

        public Country[] newArray(int size) {
            return new Country[size];
        }
    };

}
