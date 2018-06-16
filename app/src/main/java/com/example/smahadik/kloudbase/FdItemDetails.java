package com.example.smahadik.kloudbase;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class FdItemDetails extends AppCompatActivity {

    public static int position;

    public static final String STATUS = "status";
    ExpandableListAdapterFdItem fdItemDetailsAdapter;
    ExpandableListView fdItemDetailsListView;
    Button edit;
    public static List<String> listDataHeader;
    LinkedHashMap<String, List<String>> listDataChildLabel;
    LinkedHashMap<String, List<String>> listDataChildValue;
    ArrayList<List<String>> dataListArr;
    ArrayList<List<String>> valueListArr;
    List<String> sample;
    List<String> sampleValue;
    public static int previousGroup = -1;
    Intent editfdItemDetails;

    FrameLayout progressBarHolder;
    ProgressBar progressBar;
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fd_item_details);


        Intent FdSelectCatList = getIntent();
        position = FdSelectCatList.getIntExtra("position" , -1);
        getSupportActionBar().setTitle(VenHome.categoryArr.get(position).get("name").toString());


        //Initialization
        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
        progressBar = findViewById(R.id.progressBar);
        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);


        // get the listview
        fdItemDetailsListView= (ExpandableListView) findViewById(R.id.fdDetailsView);


        fdItemDetailsListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

                if(groupPosition != previousGroup)
                    fdItemDetailsListView.collapseGroup(previousGroup);

                previousGroup = groupPosition;
            }
        });


    }

//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        MenuInflater catMenu = getMenuInflater();
//        catMenu.inflate(R.menu.cat_menu, menu );
//        return true;
////        return super.onCreateOptionsMenu(menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        if(item.getItemId() == R.id.addNewCategory) {
//            Intent addNewCat = new Intent(CategoryDetails.this, AddNewCategory.class);
//            startActivity(addNewCat);
//        }else if(item.getItemId() == R.id.catPositioning) {
//            Intent catreorder = new Intent(CategoryDetails.this, CatOrganize.class);
//            startActivity(catreorder);
//        }
//
//
//        return super.onOptionsItemSelected(item);
//    }



    @Override
    protected void onResume() {
        super.onResume();

        // preparing list data
//        Log.i("Header" , String.valueOf(VenHome.foodItemArr.get(position).size()) );
//        Log.i("Header" , VenHome.foodItemArr.get(position).toString() );
        prepareListData();

        // setting list adapter
        fdItemDetailsAdapter = new ExpandableListAdapterFdItem(this, listDataHeader, listDataChildLabel, listDataChildValue);
        Log.i("RESUMED" , listDataChildValue.toString());
        Log.i("RESUMED" , listDataHeader.toString());
        fdItemDetailsListView.setAdapter(fdItemDetailsAdapter);


    };



    public void edit (View view) {
        editfdItemDetails = new Intent(this, EditFdItemDetails.class);
        editfdItemDetails.putExtra("editPosition", previousGroup);
        startActivity(editfdItemDetails);
    }


    public void delete (View view) {


        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Delete FoodItem")
                .setMessage("Are you sure you want to Delete this FoodItem ?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Common.EnableProgressBar(progressBarHolder, inAnimation);

                        DocumentReference fdItemDoc = VenHome.catPathRef.document(VenHome.categoryArr.get(position).get("catid").toString() + "/MenuM/" + VenHome.foodItemArr.get(position).get(previousGroup).get("fdid").toString() );
                        fdItemDoc.update(STATUS , false).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Common.DisableProgressBar(progressBarHolder, outAnimation);
                                onResume();
                            }
                        });

                        // continue with delete
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }






    //    Preparing the list data
    public void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChildLabel = new LinkedHashMap<String, List<String>>();
        listDataChildValue = new LinkedHashMap<String, List<String>>();
        dataListArr = new ArrayList<List<String>>();
        valueListArr = new ArrayList<List<String>>();

        // Adding child data
        for (int i=0; i<VenHome.foodItemArr.get(position).size(); i++) {
//            Log.i("Header" , VenHome.foodItemArr.get(position).get(i).get("name").toString() );
            listDataHeader.add(VenHome.foodItemArr.get(position).get(i).get("name").toString());
        }

        // Adding Header / child data with Label
        for (int i=0; i<VenHome.foodItemArr.get(position).size(); i++) {
            sample = new ArrayList<String>();
            sampleValue = new ArrayList<String>();

            sample.add("Name : " );
            sample.add("Amount : " );
            sample.add("Short Desp : ");
            sample.add("Long Desp : ");
            sample.add("Category  : ");
            sample.add("Ratings : ");
            sample.add("Position : ");
            sample.add("Edit");

            sampleValue.add(VenHome.foodItemArr.get(position).get(i).get("name").toString());
            sampleValue.add(VenHome.foodItemArr.get(position).get(i).get("amount").toString());
            sampleValue.add(VenHome.foodItemArr.get(position).get(i).get("sdesp").toString());
            sampleValue.add(VenHome.foodItemArr.get(position).get(i).get("ldesp").toString());
            sampleValue.add(VenHome.foodItemArr.get(position).get(i).get("category").toString());
            sampleValue.add(VenHome.foodItemArr.get(position).get(i).get("rating").toString());
            sampleValue.add(VenHome.foodItemArr.get(position).get(i).get("fspos").toString());
            sampleValue.add("Edit");

            dataListArr.add(sample);
            valueListArr.add(sampleValue);

        }

        for (int i=0; i<listDataHeader.size(); i++) {
            listDataChildLabel.put(listDataHeader.get(i), dataListArr.get(i));
            listDataChildValue.put(listDataHeader.get(i), valueListArr.get(i));
        }



    }







}
