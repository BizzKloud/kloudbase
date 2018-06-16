package com.example.smahadik.kloudbase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class FdSelectCatList extends AppCompatActivity {

    ListView catListView;
    ArrayList<String> catNamesArr;
    ArrayAdapter<String> catArrAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fd_select_cat_list);
        Intent venHome = getIntent();

        //Initializations
        catListView = findViewById(R.id.catListView);
        catNamesArr = new ArrayList<String>();
        catArrAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, catNamesArr);


        // Adding child data
        for (int i=0; i<VenHome.categoryArr.size(); i++) {
            catNamesArr.add(VenHome.categoryArr.get(i).get("name").toString() + " - " + String.valueOf(VenHome.foodItemArr.get(i).size()));
        }

        catListView.setAdapter(catArrAdapter);


        catListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent fdItemDetails = new Intent(FdSelectCatList.this, FdItemDetails.class);
                fdItemDetails.putExtra("position" , position);
                startActivity(fdItemDetails);
            }
        });

    }
}
