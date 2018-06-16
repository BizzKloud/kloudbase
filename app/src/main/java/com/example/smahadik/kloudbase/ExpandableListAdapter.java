package com.example.smahadik.kloudbase;

/**
 * Created by smahadik on 6/5/18.
 */

import android.graphics.Color;
import android.util.Log;
import android.widget.BaseExpandableListAdapter;
//package info.androidhive.expandablelistview;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;


public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private LinkedHashMap<String, List<String>> _listDataChildLable;
    private LinkedHashMap<String, List<String>> _listDataChildValue;
    LayoutInflater infalInflater;

    public ExpandableListAdapter(Context context, List<String> listDataHeader, LinkedHashMap<String, List<String>> listChildDataLable, LinkedHashMap<String, List<String>> listChildDataValue) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChildLable = listChildDataLable;
        this._listDataChildValue = listChildDataValue;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String [] string = new String[2];
        string[0] = _listDataChildLable.get(this._listDataHeader.get(groupPosition)).get(childPosition);
        string[1] = _listDataChildValue.get(this._listDataHeader.get(groupPosition)).get(childPosition);
        return string;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        Log.i("Child" , String.valueOf(groupPosition));
        final String[] childText = (String[]) getChild(groupPosition, childPosition);

        if (convertView == null) {
            infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if(isLastChild){
            if (infalInflater != null) {
                convertView = infalInflater.inflate(R.layout.list_item_with_button,parent, false);
            }
        } else {
            if (infalInflater != null) {
                convertView = infalInflater.inflate(R.layout.list_item, parent, false);
                TextView txtListChildLable = (TextView) convertView.findViewById(R.id.lblListItemLable);
                txtListChildLable.setText(childText[0]);
                TextView txtListChildValue = (TextView) convertView.findViewById(R.id.lblListItemValue);
                txtListChildValue.setText(childText[1]);
            }
        }


        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChildLable.get(this._listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (infalInflater != null) {
                convertView = infalInflater.inflate(R.layout.list_group, parent, false);
            }
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
//        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);


        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
