package com.example.smahadik.kloudbase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class FdSelectCatList extends AppCompatActivity {

    ListView catListView;
    ListAdapter listAdapter;
//    ArrayList<String> catNamesArr;
//    ArrayAdapter<String> catArrAdapter;

    ArrayList<String> listDataHeader;
    ArrayList<String> listDataHeaderCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fd_select_cat_list);
        Intent venHome = getIntent();

        //Initializations
        catListView = findViewById(R.id.catListView);
//        catNamesArr = new ArrayList<String>();
        listDataHeader = new ArrayList<String>();
        listDataHeaderCount = new ArrayList<String>();
//        catArrAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listDataHeader);


        catListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent fdItemDetails = new Intent(FdSelectCatList.this, FdItemDetails.class);
                fdItemDetails.putExtra("position" , position);
                startActivity(fdItemDetails);
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

        Log.i("On Resume" , "At FD Select CAt List" );
        // Adding child data
        listDataHeader.clear();
        listDataHeaderCount.clear();

        for (int i=0; i<VenHome.categoryArr.size(); i++) {
            listDataHeader.add(VenHome.categoryArr.get(i).get("name").toString());
            listDataHeaderCount.add(String.valueOf(VenHome.foodItemArr.get(i).size()));
        }

        listAdapter = new ListAdapter(this, listDataHeader , listDataHeaderCount);

        catListView.setAdapter(listAdapter);
    }
}
