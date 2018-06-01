package com.example.smahadik.kloudbase;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import junit.framework.Test;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VenHome extends Activity {

    // Firestore
    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference storageRefLogo;
    FirebaseFirestore firestoreRef;
    CollectionReference catPathRef;
    DocumentReference vendorRef;
    CollectionReference taxMRef;

    //Initilization
    TextView textViewVenName;
    TextView textViewfdname;
    TextView textViewTaxCount;
    TextView textViewCatCount;
    TextView textViewFdItemCount;
    ImageView venLogo;
    ArrayList<HashMap> categoryArr;
    ArrayList<ArrayList <HashMap> > foodItemArr;
    ArrayList<HashMap> taxArr;
    int countCat = 0;
    int countFdItem = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ven_home);

        Intent login = getIntent();
        String fdName = login.getStringExtra("fdName");
        String venName = login.getStringExtra("venName");
        String path = login.getStringExtra("db");
        String catPath = path + "/CategoryM";
        HashMap vendorDetails = (HashMap) login.getSerializableExtra("vendorDetails");
        vendorDetails.get("pic");


        // FireStore Settings
        firestoreRef = FirebaseFirestore.getInstance();
        catPathRef = firestoreRef.collection(catPath);
        vendorRef =  firestoreRef.document(path);
        taxMRef = firestoreRef.collection(path + "/TaxM");



        //Getting data from firestore
        new AsysncTask().execute(catPath,path);



        //Initializations
        textViewfdname = (TextView) findViewById(R.id.textViewfdname);
        textViewVenName = (TextView) findViewById(R.id.textViewVenName);
        textViewTaxCount = (TextView) findViewById(R.id.textViewTaxCount);
        textViewCatCount = (TextView) findViewById(R.id.textViewCatCount);
        textViewFdItemCount = (TextView) findViewById(R.id.textViewFdItemCount);
        venLogo = (ImageView) findViewById(R.id.venLogo);
        categoryArr = new ArrayList<HashMap>();
        taxArr = new ArrayList<HashMap>();
        foodItemArr = new ArrayList<ArrayList<HashMap>>();
        final Integer count = 0;







        //FireStorage Setting
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        String pic = vendorDetails.get("pic").toString();
        storageRefLogo = storageRef.child(pic);
        Glide.with(this).using(new FirebaseImageLoader()).load(storageRefLogo).into(venLogo);




        //Setting basic terms
        textViewVenName.setText(venName);
        textViewfdname.setText(fdName);




    } //OnCreate Done



    private class AsysncTask extends AsyncTask<String , Void, Void> implements com.example.smahadik.kloudbase.GetFcAsysncTask {

        @Override
        protected Void doInBackground(final String... strings) {

            catPathRef.orderBy("catpos").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    countCat = 0;
                    countFdItem = 0;

                    if (e != null) {
                        Log.i("Connection Failed", "Connection to FireBase Failed", e);
                        return;
                    }

                    for(QueryDocumentSnapshot cat : queryDocumentSnapshots) {
                        countCat++;

                        categoryArr.add((HashMap) cat.getData());
                        String catid = cat.getString("catid");
                        String menuPath = strings[0] + "/" +  catid + "/MenuM";
                        final CollectionReference fditem = firestoreRef.collection(menuPath);
                        final ArrayList<HashMap> sample = new ArrayList<HashMap>();


                        fditem.orderBy("fspos").addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                countCat++;
                                for(QueryDocumentSnapshot fdItem : queryDocumentSnapshots) {
                                    countFdItem++;
                                    sample.add((HashMap) fdItem.getData());
                                }
//                                sample.size()
                                foodItemArr.add(sample);
//                                Log.i("items count" , String.valueOf(countFdItem));
                                textViewFdItemCount.setText("Count : " + countFdItem);

                            }
                        });

                    }

                    textViewCatCount.setText("Count : " + countCat);
//                    Log.i("cat count" , String.valueOf(countCat));
//                    Log.i("cat Array" , categoryArr.toString());
//                    Log.i("fd Array" , foodItemArr.toString());
                }
            });


            taxMRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot taxqueryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    for(QueryDocumentSnapshot taxItem : taxqueryDocumentSnapshots) {
                        taxArr.add((HashMap) taxItem.getData());
                    }

                    textViewTaxCount.setText("Count : " + taxArr.size());
//                    Log.i("TaxArray" , taxArr.toString());
                }
            });


            return null;
        }
    }





}
