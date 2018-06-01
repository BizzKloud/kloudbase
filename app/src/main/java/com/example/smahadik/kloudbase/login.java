package com.example.smahadik.kloudbase;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class login extends AppCompatActivity {

    // Firestore
    FirebaseFirestore firestore;
    CollectionReference db;
    FirebaseAuth mAuth;

    // Initialization
    ArrayList<HashMap> foodCourtArr = new ArrayList<HashMap>();
    ArrayList<HashMap> usersArr = new ArrayList<HashMap>();
    ArrayList<String> fcNames = new ArrayList<String>();
    ArrayList<String> userNames = new ArrayList<String>();
    String foodcourtsPath = "foodcourts";
    String usersPath;
    String fcid;
    String venid;
    String passcode;
    Spinner foodcourtSpnr;
    Spinner usernameSpr;
    EditText passEditText;
    Button login;
    Switch aswitch;
    TextView admin;
    TextView vendor;
    FrameLayout progressBarHolder;
    ProgressBar progressBar;
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;
    int pos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // FireStore Settings
        firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);
        db = FirebaseFirestore.getInstance().collection("foodcourts");




        //Initialization
        passEditText = (EditText) findViewById(R.id.passEditText);
        foodcourtSpnr = (Spinner) findViewById(R.id.foodcourt);
        usernameSpr = (Spinner) findViewById(R.id.username);
        login = (Button) findViewById(R.id.login);
        aswitch = (Switch) findViewById(R.id.aswitch);
        admin = (TextView) findViewById(R.id.admin);
        vendor = (TextView) findViewById(R.id.vendor);
        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        // getting data from firestore
        new AsysncTask().execute(foodcourtsPath);



        //disable fields
        passEditText.setEnabled(false);
        usernameSpr.setEnabled(false);


        //Setting a Switch
        aswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    initialRestart(admin, vendor);
                }else {

                    initialRestart(vendor, admin);
                }
            }
        });





        // Spinner
        fcNames.add("Select Food Court");
        ArrayAdapter<String> adapterfc = new ArrayAdapter<String>(this, R.layout.spinner_item, fcNames);
        foodcourtSpnr.setAdapter(adapterfc);

        foodcourtSpnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                foodcourtSpnr.setSelection(position);

                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }

                if(position > 0) {
                    passEditText.setEnabled(false);
                    passEditText.setText("");
                    usernameSpr.setEnabled(true);
                    userNames.clear();
                    userNames.add("Select User Name");
                    usersArr.clear();
                    fcid = foodCourtArr.get(position-1).get("fcid").toString();
                    usersPath = foodcourtsPath + "/" + fcid +"/VendorM";
                    new AsysncTask().execute(usersPath);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        userNames.add("Select User Name");
        ArrayAdapter<String> adapterven = new ArrayAdapter<String>(this, R.layout.spinner_item, userNames);
        usernameSpr.setAdapter(adapterven);

        usernameSpr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                usernameSpr.setSelection(position);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                if(position > 0) {
                    passEditText.setText("");
                    pos = position-1;
                    venid = usersArr.get(position-1).get("venid").toString();
                    passcode = usersArr.get(position-1).get("pwd").toString();
                    passEditText.setEnabled(true);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    } //ON Create Done






    // Set FoodCourts/Vendor Names
    public ArrayList<String> setnames(ArrayList<String> names, ArrayList<HashMap> hashmapArr) {
        for (int i = 0; i < hashmapArr.size(); i++) {
            names.add(hashmapArr.get(i).get("name").toString());
        }
        return (names);
    }








    // Spinner Initializer
    public ArrayAdapter<String> initSpinner(ArrayList<String> list) {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, list) {

            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;

    }





    // Initial Restart For Switch
    public void initialRestart (TextView checked, TextView unChecked) {
        checked.setTextColor(Color.parseColor("#ffff8800"));
        checked.setTextSize(22);
        unChecked.setTextColor(Color.parseColor("#aaaaaa"));
        unChecked.setTextSize(20);

        passEditText.setEnabled(false);
        usernameSpr.setEnabled(false);
        usernameSpr.setSelection(0);
        passEditText.setText("");
        foodcourtSpnr.setSelection(0);
    }






    // AsyncTask FireStore
    private class AsysncTask extends AsyncTask<String , Void, Void> implements com.example.smahadik.kloudbase.GetFcAsysncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            inAnimation = new AlphaAnimation(0f, 1f);
            inAnimation.setDuration(200);
            progressBarHolder.setAnimation(inAnimation);
            progressBarHolder.setVisibility(View.VISIBLE);
            login.setEnabled(false);
        }

//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            outAnimation = new AlphaAnimation(1f, 0f);
//            outAnimation.setDuration(200);
//            progressBarHolder.setAnimation(outAnimation);
//            progressBarHolder.setVisibility(View.GONE);
//        }


        @Override
        protected Void doInBackground(String... strings) {

            if(strings[0].contains("VendorM")) {

                firestore.collection(strings[0]).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                usersArr.add((HashMap) document.getData() );
                            }
                            //Initiliaze
                            userNames = setnames(userNames, usersArr);
                            usernameSpr.setAdapter(initSpinner(userNames));
                        }
                    }
                });

            }else {
                db.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                foodCourtArr.add((HashMap) document.getData());
                            }

                            //Initialize
                            fcNames = setnames(fcNames, foodCourtArr);
                            foodcourtSpnr.setAdapter(initSpinner(fcNames));
                        }
                    }
                });
            }
            return null;
        }
    }


    //ONclick LOGIN
    public void login (View view) {

//        inAnimation = new AlphaAnimation(0f, 1f);
//        inAnimation.setDuration(200);
//        progressBarHolder.setAnimation(inAnimation);
//        progressBar.setVisibility(View.VISIBLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        String password = passEditText.getText().toString().trim();
        if(foodcourtSpnr.getSelectedItem() == "Food Court") {
            Toast.makeText(this, "Select 'FoodCourt' ", Toast.LENGTH_LONG).show();
        }else if (usernameSpr.getSelectedItem() == "User Name") {
            Toast.makeText(this, "Select 'User Name' ", Toast.LENGTH_LONG).show();
        }else if(password.equals("")) {
            Toast.makeText(this, "Enter Password", Toast.LENGTH_LONG).show();
        }else {
            if(password.equals(passcode)) {
                Toast.makeText(this, "Success", Toast.LENGTH_LONG).show();
                Intent venHome = new Intent(this, VenHome.class);
                venHome.putExtra("fdName", foodcourtSpnr.getSelectedItem().toString());
                venHome.putExtra("venName", usernameSpr.getSelectedItem().toString());
                venHome.putExtra("db" , usersPath + "/" + venid );
                venHome.putExtra("vendorDetails" , usersArr.get(pos));
                startActivity(venHome);
                finish();

            }else {
                Toast.makeText(this, "Invalid Login Credentials", Toast.LENGTH_LONG).show();
            }
        }
    }





}





