package com.example.lephleg.worldliving;

import android.support.v4.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lephleg.worldliving.data.Country;

public class CountryDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Country> {

    private static final String LOG_TAG = CountryDetailFragment.class.getSimpleName();
    private ShareActionProvider mShareActionProvider;
    private Country mCountry;

    public CountryDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mCountry = arguments.getParcelable("country");
        }

        View rootView = inflater.inflate(R.layout.country_detail_fragment, container, false);

        ImageView flag = (ImageView) rootView.findViewById(R.id.detail_country_flag);
        flag.setImageResource(mCountry.flag);
        TextView name = (TextView) rootView.findViewById(R.id.detail_country_name);
        name.setText(mCountry.name);

        FetchCountryDataTask dataTask = new FetchCountryDataTask();
        dataTask.execute(mCountry);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.country_detail_fragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // TODO: share functionality

    }

    @Override
    public Loader<Country> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Country> loader, Country data) {

    }

    @Override
    public void onLoaderReset(Loader<Country> loader) {

    }
}
