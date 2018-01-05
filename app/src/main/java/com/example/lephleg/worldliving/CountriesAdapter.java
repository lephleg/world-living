package com.example.lephleg.worldliving;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CountriesAdapter extends ArrayAdapter<Country> {

    private Context mContext;
    private ArrayList<Country> mCountries;

    public CountriesAdapter(Context context, ArrayList<Country> countries) {
        super(context,0, countries);
        this.mContext = context;
        this.mCountries = countries;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Country country = mCountries.get(position);
        country.loadFlagByCode(mContext);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.country_item, parent, false);
        }

        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.country_name);
        ImageView ivFlag = (ImageView) convertView.findViewById(R.id.country_icon);

        // Populate the data into the template view using the data object
        tvName.setText(country.name);
        ivFlag.setImageResource(country.flag);

        // Return the completed view to render on screen
        return convertView;
    }
}
