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

import java.util.Objects;

public class EditFdItemDetails extends AppCompatActivity {

    int position;
    int ratings;
    Double amount;
    EditText fdItemNameEditText;
    EditText fdItemAmountEditText;
    EditText fdItemcatEditText;
    EditText fdItemShortDespEditText;
    EditText fdItemLongDespEditText;
    EditText fdItemratingsEditText;
    DocumentReference fdItemCurrentDoc;

    FrameLayout progressBarHolder;
    ProgressBar progressBar;
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_fd_item_details);

        Intent fdItemDetails = getIntent();
        position = fdItemDetails.getIntExtra("editPosition" , -1);

        //Firebase
        fdItemCurrentDoc = VenHome.catPathRef.document(VenHome.categoryArr.get(FdItemDetails.position).get("catid").toString() + "/MenuM/" + VenHome.foodItemArr.get(FdItemDetails.position).get(position).get("fdid").toString() );

        // Initialization
        getSupportActionBar().setTitle(VenHome.foodItemArr.get(FdItemDetails.position).get(position).get("name").toString());

        fdItemNameEditText = findViewById(R.id.fdItemNameEditText);
        fdItemAmountEditText = findViewById(R.id.fdItemAmountEditText);
        fdItemcatEditText = findViewById(R.id.fdItemcatEditText);
        fdItemShortDespEditText = findViewById(R.id.fdItemShortDespEditText);
        fdItemLongDespEditText = findViewById(R.id.fdItemLongDespEditText);
        fdItemratingsEditText = findViewById(R.id.fdItemratingsEditText);

        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
        progressBar = findViewById(R.id.progressBar);
        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);

        fdItemNameEditText.setText(VenHome.foodItemArr.get(FdItemDetails.position).get(position).get("name").toString());
        fdItemShortDespEditText.setText(VenHome.foodItemArr.get(FdItemDetails.position).get(position).get("sdesp").toString());
        fdItemLongDespEditText.setText(VenHome.foodItemArr.get(FdItemDetails.position).get(position).get("ldesp").toString());
        fdItemcatEditText.setText(VenHome.foodItemArr.get(FdItemDetails.position).get(position).get("category").toString());
        fdItemcatEditText.setEnabled(false);
        fdItemAmountEditText.setText(VenHome.foodItemArr.get(FdItemDetails.position).get(position).get("amount").toString());
        fdItemratingsEditText.setText(VenHome.foodItemArr.get(FdItemDetails.position).get(position).get("rating").toString());
    }





    public void save(View view) {


        boolean flag = false;


        if (Objects.equals(fdItemNameEditText.getText().toString().trim(), "") || Objects.equals(fdItemShortDespEditText.getText().toString().trim(), "") || Objects.equals(fdItemLongDespEditText.getText().toString().trim(), "")) {
            flag = true;
            if (Objects.equals(fdItemNameEditText.getText().toString().trim(), "")) {
                Toast.makeText(this, "Name cannot be Empty", Toast.LENGTH_SHORT).show();
            } else if (Objects.equals(fdItemShortDespEditText.getText().toString().trim(), "")) {
                Toast.makeText(this, "Short Description cannot be Empty", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Long Description cannot be Empty", Toast.LENGTH_SHORT).show();
            }
        } else if (Objects.equals(fdItemratingsEditText.getText().toString().trim(), "")) {
            flag = true;
            Toast.makeText(this, "Ratings should be between 0-5", Toast.LENGTH_SHORT).show();
        } else if (Objects.equals(fdItemAmountEditText.getText().toString().trim(), "")) {
            flag = true;
            Toast.makeText(this, "Amount cannot be Empty", Toast.LENGTH_SHORT).show();
        } else {
            ratings = Integer.parseInt(fdItemratingsEditText.getText().toString().trim());
            amount = Double.parseDouble(fdItemAmountEditText.getText().toString().trim());

            if (ratings == 0 | ratings > 5) {
                flag = true;
                Toast.makeText(this, "Ratings should be between 0-5", Toast.LENGTH_SHORT).show();
            } else if (amount == 0) {
                flag = true;
                Toast.makeText(this, "Amount cannot be 0 (Zero)", Toast.LENGTH_SHORT).show();
            }
        }


        if (!flag) {
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
                            fdItemCurrentDoc = VenHome.catPathRef.document(VenHome.categoryArr.get(FdItemDetails.position).get("catid").toString() + "/MenuM/" + VenHome.foodItemArr.get(FdItemDetails.position).get(position).get("fdid").toString() );
                            fdItemCurrentDoc.update("name", fdItemNameEditText.getText().toString().trim());
                            fdItemCurrentDoc.update("sdesp", fdItemShortDespEditText.getText().toString().trim());
                            fdItemCurrentDoc.update("ldesp", fdItemLongDespEditText.getText().toString().trim());
                            fdItemCurrentDoc.update("amount", Double.parseDouble(fdItemAmountEditText.getText().toString().trim()));
                            fdItemCurrentDoc.update("rating", Integer.parseInt(fdItemratingsEditText.getText().toString().trim())).addOnCompleteListener(new OnCompleteListener<Void>() {
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
