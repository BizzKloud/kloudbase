package com.example.smahadik.kloudbase;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


public class TaxDetails extends AppCompatActivity {

    public static final String STATUS = "status";
    ExpandableListAdapter taxDetailsAdapter;
    ExpandableListView taxDetailsListView;
    Button edit;
    List<String> listDataHeader;
    LinkedHashMap<String, List<String>> listDataChildLabel;
    LinkedHashMap<String, List<String>> listDataChildValue;
    ArrayList<List<String>> dataListArr;
    ArrayList<List<String>> valueListArr;
    List<String> sample;
    List<String> sampleValue;
    int previousGroup = -1;
    Intent editTaxDetails;

    ProgressDialog progressDialog;

    FrameLayout progressBarHolder;
    ProgressBar progressBar;
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tax_details);

        //Initialization
        progressDialog = new ProgressDialog(this);

        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
        progressBar = findViewById(R.id.progressBar);
        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);



        // get the listview
        taxDetailsListView = (ExpandableListView) findViewById(R.id.taxDetailsView);


        taxDetailsListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                if(groupPosition != previousGroup)
                    taxDetailsListView.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });






    }



    @Override
    protected void onResume() {
        super.onResume();

        // preparing list data
        prepareListData();

        // setting list adapter
        taxDetailsAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChildLabel, listDataChildValue);
        Log.i("RESUMED" , listDataChildValue.toString());
        Log.i("RESUMED" , listDataHeader.toString());
        taxDetailsListView.setAdapter(taxDetailsAdapter);


    };

    public void edit (View view) {
        editTaxDetails = new Intent(this, EditTaxDetails.class);
        editTaxDetails.putExtra("editPosition", previousGroup);
        startActivity(editTaxDetails);

    }


    public void delete (View view) {


        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Delete " + listDataHeader.get(previousGroup))
                .setMessage("Are you sure you want to Delete " + listDataHeader.get(previousGroup) + " ?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

//                        Common.EnableProgressBar(progressBarHolder, inAnimation);
                        progressDialog.setMessage("Deleting " + listDataHeader.get(previousGroup));
                        progressDialog.show();

                        DocumentReference catDoc = VenHome.taxMRef.document(VenHome.taxArr.get(previousGroup).get("taxid").toString());
                        catDoc.update(STATUS, false).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
//                                Common.DisableProgressBar(progressBarHolder, outAnimation);
                                progressDialog.dismiss();
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
        for (int i=0; i<VenHome.taxArr.size(); i++) {
            listDataHeader.add(VenHome.taxArr.get(i).get("taxName").toString());
        }

        // Adding Header / child data with Label
        for (int i=0; i<VenHome.taxArr.size(); i++) {
            sample = new ArrayList<String>();
            sampleValue = new ArrayList<String>();

            sample.add("Description : ");
            sample.add("Name : " );
            sample.add("Percentage : ");
            sample.add("Edit");

            sampleValue.add(VenHome.taxArr.get(i).get("desp").toString());
            sampleValue.add(VenHome.taxArr.get(i).get("taxName").toString());
            sampleValue.add(VenHome.taxArr.get(i).get("taxPer").toString());
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



