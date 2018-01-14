package com.example.lephleg.worldliving;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lephleg.worldliving.model.Country;

import java.util.ArrayList;

public class CountryListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Country> {

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Country countrySelected);
    }

    final static String LOG_TAG = CountryListFragment.class.getSimpleName();
    CountriesAdapter listAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.country_list_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint(getResources().getString(R.string.search_hint));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                listAdapter.getFilter().filter(newText);

                return false;
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.country_list_fragment, container, false);

        ArrayList<Country> countryList = Country.getAllCountries(getActivity());

        listAdapter = new CountriesAdapter(getActivity(), countryList);

        ListView list = (ListView) rootView.findViewById(R.id.country_listview);
        list.setAdapter(listAdapter);

        // We'll call our MainActivity
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Country countrySelected = listAdapter.getItem(position);
                ((Callback) getActivity())
                        .onItemSelected(countrySelected);

                Toast toast = Toast.makeText(getActivity(), countrySelected.name + " has been selected!", Toast.LENGTH_SHORT);
                toast.show();

            }
        });

        return rootView;
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
