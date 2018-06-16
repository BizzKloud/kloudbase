
package com.example.smahadik.kloudbase;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class VenHome extends AppCompatActivity {

    // Firestore
    public static FirebaseStorage storage;
    public static StorageReference storageRef;
    StorageReference storageRefLogo;
    FirebaseFirestore firestoreRef;
    public static DocumentReference vendorRef;
    public static CollectionReference taxMRef;
    public static CollectionReference catPathRef;


    //Initilization
//    TextView textViewVenName;
    Intent intent;
    public static HashMap fcDetails;
    public static HashMap vendorDetails;
    //    TextView textViewfdname;
    TextView textViewTaxCount;
    TextView textViewCatCount;
    TextView textViewFdItemCount;
    ImageView venLogo;
    public static ArrayList<HashMap> categoryArr;
    public static ArrayList<ArrayList <HashMap> > foodItemArr;
    public static ArrayList<HashMap> taxArr;
    GridLayout grid;
    int countCat = 0;
    int countFdItem = 0;
    boolean getCatIdVar = false;
    int index = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ven_home);


        Intent login = getIntent();
        String venName = login.getStringExtra("venName");
        String path = login.getStringExtra("db");
        String catPath = path + "/CategoryM";
        vendorDetails = (HashMap) login.getSerializableExtra("vendorDetails");
        fcDetails = (HashMap) login.getSerializableExtra("fc");
        vendorDetails.get("pic");

//        getSupportActionBar().setTitle(venName);


        // FireStore Settings
        firestoreRef = FirebaseFirestore.getInstance();
        catPathRef = firestoreRef.collection(catPath);
        vendorRef =  firestoreRef.document(path);
        taxMRef = firestoreRef.collection(path + "/TaxM");



        //Getting data from firestore
        new AsysncTask().execute(catPath,path);



        //Initializations
        grid = findViewById(R.id.grid);
//        textViewfdname = (TextView) findViewById(R.id.textViewfdname);
//        textViewVenName = (TextView) findViewById(R.id.textViewVenName);
        textViewTaxCount = (TextView) findViewById(R.id.textViewTaxCount);
        textViewCatCount = (TextView) findViewById(R.id.textViewCatCount);
        textViewFdItemCount = (TextView) findViewById(R.id.textViewFdItemCount);
        venLogo = (ImageView) findViewById(R.id.venLogo);
        categoryArr = new ArrayList<HashMap>();
        taxArr = new ArrayList<HashMap>();
        foodItemArr = new ArrayList<ArrayList<HashMap>>();



        //FireStorage Setting
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        String pic = vendorDetails.get("pic").toString();
        storageRefLogo = storageRef.child(pic);
        Glide.with(this).using(new FirebaseImageLoader()).load(storageRefLogo).into(venLogo);



        //Setting basic terms
//        textViewVenName.setText(venName);
//        textViewfdname.setText(fdName);


        //Setting Card click listner
        setCardEvent();


    } //OnCreate Done

    private void setCardEvent() {
        for(int i=0; i<grid.getChildCount(); i++) {
            CardView cardView = (CardView) grid.getChildAt(i);
            final int finalI = i;

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch(finalI) {
                        case 0:
                            intent = new Intent(VenHome.this, TaxDetails.class);
                            startActivity(intent);
                            break;
                        case 1:
                            intent = new Intent(VenHome.this, CategoryDetails.class);
                            startActivity(intent);
                            break;
                        case 2:
                            intent = new Intent(VenHome.this, FdSelectCatList.class);
                            startActivity(intent);
                            break;
                    }
                }
            });
        }
    }


    private class AsysncTask extends AsyncTask<String , Void, Void> implements com.example.smahadik.kloudbase.GetFcAsysncTask {

        @Override
        protected Void doInBackground(final String... strings) {

            catPathRef.whereEqualTo("status" , true).addSnapshotListener(new EventListener<QuerySnapshot>() {

                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    categoryArr.clear();
                    foodItemArr.clear();
                    Log.i("Asysnc Task" , "Running AsysnscTask Category");
                    countCat = 0;
                    countFdItem = 0;

                    if (e != null) {
                        Log.i("Connection Failed", "Connection to FireBase Failed", e);
                        return;
                    }

                    for(QueryDocumentSnapshot cat : queryDocumentSnapshots) {
                        countCat++;
                        categoryArr.add((HashMap) cat.getData());
                        final String catid = cat.getString("catid");
                        String menuPath = strings[0] + "/" +  catid + "/MenuM";
                        final CollectionReference fditem = firestoreRef.collection(menuPath);
//                        final ArrayList<HashMap> sample = new ArrayList<HashMap>();


                        fditem.whereEqualTo("status" , true).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

//                                Log.i("cat count fditem" , String.valueOf(countCat));
//                                Log.i("Cat Array count fditem" , String.valueOf(categoryArr.size()));
//                                Log.i("Asysnc Task" , "Running AsysnscTask FooDITE");
                                getCatIdVar = false;
                                final ArrayList<HashMap> sample = new ArrayList<HashMap>();
                                for(QueryDocumentSnapshot fdItem : queryDocumentSnapshots) {
//                                        Log.i("CatID " , String.valueOf(fdItem.get("catid")));

                                    if (!getCatIdVar) {
                                        for(HashMap cat : categoryArr) {
                                            if (cat.get("catid").toString().equals((String)fdItem.get("catid")) & !getCatIdVar ) {
//                                                Log.i("CAtegory ID" , "FOUND .....");
                                                index = categoryArr.indexOf(cat);
//                                                Log.i("index" , String.valueOf(index));
                                                getCatIdVar = true;
                                                break;
                                            }
                                        }
                                    }
                                    sample.add((HashMap) fdItem.getData());
                                }

                                if(foodItemArr.size() > index) {
                                    countFdItem = countFdItem + (sample.size() - foodItemArr.get(index).size());
                                    foodItemArr.set(index, sample);

                                }else {
                                    Log.i("First" , "True");
                                    countFdItem = countFdItem + sample.size();
                                    foodItemArr.add(sample);
                                }

//                                    Log.i("items count" , String.valueOf(countFdItem));
//                                    Log.i("items Array size" , String.valueOf(foodItemArr.size()));
                                    Log.i("items Array" , foodItemArr.toString());
                                textViewFdItemCount.setText(String.valueOf(countFdItem));

                            }
                        });

                    }

                    textViewCatCount.setText(String.valueOf(countCat));
//                    Log.i("cat count" , String.valueOf(countCat));
//                    Log.i("Cat Array count" , String.valueOf(categoryArr.size()));
                    Log.i("cat Array" , categoryArr.toString());
                    Log.i("fd Array" , foodItemArr.toString());
                }
            });



            taxMRef.whereEqualTo("status" , true).addSnapshotListener(new EventListener<QuerySnapshot>() {

                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot taxqueryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    taxArr.clear();
                    for(QueryDocumentSnapshot taxItem : taxqueryDocumentSnapshots) {
                        taxArr.add((HashMap) taxItem.getData());
                    }

                    textViewTaxCount.setText(String.valueOf(taxArr.size()));
                    Log.i("TaxArray" , taxArr.toString());
                }
            });


            return null;
        }
    }





}


