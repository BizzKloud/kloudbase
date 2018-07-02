package com.example.smahadik.kloudbase;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ListAdapter extends BaseAdapter {


    private Context _context;
    private ArrayList<String> _listDataHeader; // header titles
    private ArrayList<String> _listDataHeaderCount; // header titles
    LayoutInflater infalInflater;

    public ListAdapter(Context context, ArrayList<String> listDataHeader, ArrayList<String> listDataHeaderCount) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataHeaderCount = listDataHeaderCount;
    }

    public Object getData(int position) {
        String [] string = new String[2];
        string[0] = _listDataHeader.get(position);
        string[1] = _listDataHeaderCount.get(position);
        return string;
    }

    @Override
    public int getCount() {
        return _listDataHeader.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.i("Child" , String.valueOf(position));
        final String[] childText = (String[]) getData(position);

        if (convertView == null) {
            infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (infalInflater != null) {
            convertView = infalInflater.inflate(R.layout.list_catwithcount, parent, false);
            TextView txtListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
            txtListHeader.setText(childText[0]);
            TextView txtListHeaderCount = (TextView) convertView.findViewById(R.id.lblListHeaderCount);
            txtListHeaderCount.setText(childText[1]);
        }


        return convertView;
    }
}
