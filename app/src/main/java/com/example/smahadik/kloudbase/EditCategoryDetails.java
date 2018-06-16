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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

public class EditCategoryDetails extends AppCompatActivity {

    EditText catNameEditText;
    EditText catShortDespEditText;
    EditText catLongDespEditText;
    DocumentReference catCurrentDoc;

    FrameLayout progressBarHolder;
    ProgressBar progressBar;
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category_details);

        Intent catDetails = getIntent();
        int position = catDetails.getIntExtra("editPosition", -1);

        //Firebase
        catCurrentDoc = VenHome.catPathRef.document(VenHome.categoryArr.get(position).get("catid").toString());

        // Initialization
        getSupportActionBar().setTitle(VenHome.categoryArr.get(position).get("name").toString());

        catNameEditText = findViewById(R.id.catNameEditText);
        catShortDespEditText = findViewById(R.id.catShortDespEditText);
        catLongDespEditText = findViewById(R.id.catLongDespEditText);

        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
        progressBar = findViewById(R.id.progressBar);
        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);


        catNameEditText.setText(VenHome.categoryArr.get(position).get("name").toString());
        catShortDespEditText.setText(VenHome.categoryArr.get(position).get("sdesp").toString());
        catLongDespEditText.setText(VenHome.categoryArr.get(position).get("ldesp").toString());



    }

    public void save(View view) {

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Save entry")
                .setMessage("Are you sure you want to Save the Changes ?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Common.EnableProgressBar(progressBarHolder, inAnimation);
                        catCurrentDoc.update("name", catNameEditText.getText().toString().trim());
                        catCurrentDoc.update("sdesp", catShortDespEditText.getText().toString().trim());
                        catCurrentDoc.update("ldesp", catLongDespEditText.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
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
