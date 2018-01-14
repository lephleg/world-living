package com.example.lephleg.worldliving;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
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

import com.example.lephleg.worldliving.model.Country;

public class CountryDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Country> {

    private static final String LOG_TAG = CountryDetailFragment.class.getSimpleName();
    private ShareActionProvider mShareActionProvider;
    private Country mCountry;
    private PriceListAdapter mExpListAdapter;

    public CountryDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.country_detail_fragment, container, false);

        ImageView flag = (ImageView) rootView.findViewById(R.id.detail_country_flag);
        TextView name = (TextView) rootView.findViewById(R.id.detail_country_name);

        Bundle arguments = getArguments();

        if (arguments != null) {
            mCountry = arguments.getParcelable("country");
            flag.setImageResource(mCountry.flag);
            name.setText(mCountry.name);
            (new FetchCountryDataTask(getActivity())).execute(mCountry);
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.country_detail_fragment, menu);

        if (mCountry != null) {
            // Locate MenuItem with ShareActionProvider
            MenuItem menuItem = menu.findItem(R.id.action_share);

            // Fetch and store ShareActionProvider
            mShareActionProvider =  (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
            mShareActionProvider.setShareIntent(createShareIntent());
        }
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, String.format(getContext().getResources().getString(R.string.share_text), mCountry.name));
        return shareIntent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_map) {
            openCountryInMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openCountryInMap() {

        Uri geoLocation = Uri.parse("geo:0,0?q=" + mCountry.name);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(LOG_TAG, "Couldn't call " + geoLocation.toString() + ", no receiving apps installed!");
        }

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
