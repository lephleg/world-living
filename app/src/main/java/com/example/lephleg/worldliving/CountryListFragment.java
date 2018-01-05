package com.example.lephleg.worldliving;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class CountryListFragment extends Fragment {

    final static String LOG_TAG = CountryListFragment.class.getSimpleName();
    CountriesAdapter listAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.country_list_fragment, container, false);

        ArrayList<Country> countryList = Country.getAllCountries(getActivity());

        listAdapter = new CountriesAdapter(getActivity(), countryList);

        ListView list = (ListView) rootView.findViewById(R.id.country_listview);
        list.setAdapter(listAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Country countrySelected = (Country) adapterView.getItemAtPosition(position);
                updateData(countrySelected);

                Toast toast = Toast.makeText(getActivity(), countrySelected.name + " has been selected!", Toast.LENGTH_SHORT);
                toast.show();

            }
        });

        return rootView;
    }

    private void updateData(Country country) {
        FetchCountryDataTask dataTask = new FetchCountryDataTask();
        dataTask.execute(country);
    }

}
