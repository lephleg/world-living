package com.example.lephleg.worldliving;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.lephleg.worldliving.model.PriceItem;

import java.util.HashMap;
import java.util.List;

public class PriceListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataHeader; // header titles

    // child data in format of header title, child title
    private HashMap<String, List<PriceItem>> listDataChild;

    public PriceListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<PriceItem>> listChildData) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.price_list_group, null);

        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.price_group_header);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final PriceItem child = (PriceItem) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.price_list_item, null);
        }

        TextView nameListChild = (TextView) convertView
                .findViewById(R.id.price_item_name);
        nameListChild.setText(child.name);

        TextView priceListChild = (TextView) convertView
                .findViewById(R.id.price_item_price);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String currencyKey = prefs.getString(context.getString(R.string.pref_currency_key),
                context.getString(R.string.usd_key));

        int priceSymbol = R.string.usd_price;

        if (currencyKey.equals(context.getString(R.string.eur_key))) {
            priceSymbol = R.string.eur_price;
        }
        if (currencyKey.equals(context.getString(R.string.gbp_key))) {
            priceSymbol = R.string.gbp_price;
        }

        priceListChild.setText(String.format(context.getResources().getString(priceSymbol), child.avgPrice));

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
    @Override
    public PriceItem getChild(int groupPosition, int childPosititon) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
