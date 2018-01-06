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

import com.example.lephleg.worldliving.data.Country;

import java.util.ArrayList;

public class CountriesAdapter extends ArrayAdapter<Country> {

    private Context mContext;
    private ArrayList<Country> mCountries;

    // View lookup cache
    private static class ViewHolder {
        TextView name;
        ImageView flag;
    }

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

        ViewHolder viewHolder; // view lookup cache stored in tag

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {

            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.country_item, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.country_name);
            viewHolder.flag = (ImageView) convertView.findViewById(R.id.country_icon);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the data object
        viewHolder.name.setText(country.name);
        viewHolder.flag.setImageResource(country.flag);

        // Return the completed view to render on screen
        return convertView;
    }
}
