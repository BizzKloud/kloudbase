package com.example.smahadik.kloudbase;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class AddNewCategory extends AppCompatActivity {


    public static final String CATID = "catid";
    public static final String BEGDA = "begda";
    public static final String ENDDA = "endda";
    public static final String VENID = "venid";
    public static final String NAME = "name";
    public static final String SDESP = "sdesp";
    public static final String LDESP = "ldesp";
    public static final String CATPOS = "catpos";
    public static final String STATUS = "status";
    EditText catNameEditText;
    EditText catShortDespEditText;
    EditText catLongDespEditText;
    DocumentReference catRef;
    ArrayList<HashMap> category;
    HashMap<String, String> newcat;
    String fcid;
    String name;
    String sdesp;
    String ldesp;

    FrameLayout progressBarHolder;
    ProgressBar progressBar;
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_category);


        Intent catDetails = getIntent();


        // Initialization
        getSupportActionBar().setTitle("Add New Category");

        catNameEditText = findViewById(R.id.catNameEditText);
        catShortDespEditText = findViewById(R.id.catShortDespEditText);
        catLongDespEditText = findViewById(R.id.catLongDespEditText);

        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
        progressBar = findViewById(R.id.progressBar);
        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);
        newcat = new HashMap<String, String>();

        category = VenHome.categoryArr;

        int id = getCategoryId();

        fcid = VenHome.fcDetails.get("fcid") + "_CAT_" + id;

        Toast.makeText(this, fcid, Toast.LENGTH_SHORT).show();

        //Firebase
        catRef = VenHome.vendorRef.collection("CategoryM").document(fcid);













    }



    private int getCategoryId() {

        if(VenHome.categoryArr.isEmpty()) {
            return 0;
        }
        ArrayList<HashMap> category = VenHome.categoryArr;
        HashMap<String, String> temporary;
        for (int c = 0; c < (category.size() - 1); c++) {
            for (int d = 0; d < (category.size() - c - 1); d++) {

                String [] zero = category.get(d).get("catid").toString().split("_");
                String [] one = category.get(d + 1).get("catid").toString().split("_");
                int zeroint = Integer.parseInt(zero[zero.length-1]);
                int oneint = Integer.parseInt(one[one.length-1]);

                if (zeroint > oneint) {

                    temporary = category.get(d);
                    category.set(d, category.get(d + 1));
                    category.set(d + 1, temporary);

                }
            }
        }

        String [] no = category.get(category.size()-1).get("catid").toString().split("_");
//        Log.i("NEW id" , String.valueOf(id + 1));

        return Integer.parseInt(no[no.length-1]) + 1;

    }




    public void add(View view) {

        name = catNameEditText.getText().toString().trim();
        sdesp = catShortDespEditText.getText().toString().trim();
        ldesp = catLongDespEditText.getText().toString().trim();

        if(name != "" && sdesp != "" && ldesp != "") {

            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(this);
            }

            builder.setTitle("Add New Category")
                    .setMessage("Are you sure you want to Add this Category ?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            Common.EnableProgressBar(progressBarHolder, inAnimation);
                            newcat.put(CATID , fcid);
                            newcat.put(BEGDA , "1/1/2017");
                            newcat.put(ENDDA , "12/31/9999");
                            newcat.put(VENID , VenHome.vendorDetails.get(VENID).toString());
                            newcat.put(NAME , name);
                            newcat.put(SDESP , sdesp);
                            newcat.put(LDESP, ldesp);

                            catRef.set(newcat);
                            catRef.update(CATPOS , VenHome.categoryArr.size()+1);
                            catRef.update(STATUS , true ).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.i("Finish", "Updates Done");
                                    Common.DisableProgressBar(progressBarHolder, outAnimation);
                                    finish();
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


        } else {
            Toast.makeText(this, "Something is Empty", Toast.LENGTH_SHORT).show();
        }

    }




    public void cancel(View view) {

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Cancel")
                .setMessage("Are you sure you want to Cancel ?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        finish();
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
}
