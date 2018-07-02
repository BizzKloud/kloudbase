package com.example.smahadik.kloudbase;

import android.app.ProgressDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.util.Objects;

public class EditTaxDetails extends AppCompatActivity {

    EditText taxNameEditText;
    EditText taxDespEditText;
    EditText taxPerEditText;
    DocumentReference taxCurrentDoc;

    ProgressDialog progressDialog;

    FrameLayout progressBarHolder;
    ProgressBar progressBar;
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tax_details);

        Intent taxDetails = getIntent();
        int position = taxDetails.getIntExtra("editPosition", -1);

        //Firebase
        taxCurrentDoc = VenHome.taxMRef.document(VenHome.taxArr.get(position).get("taxid").toString());

        // Initialization
        getSupportActionBar().setTitle(VenHome.taxArr.get(position).get("taxName").toString());

        taxNameEditText = findViewById(R.id.taxNameEditText);
        taxDespEditText = findViewById(R.id.taxDespEditText);
        taxPerEditText = findViewById(R.id.taxPerEditText);

        progressDialog = new ProgressDialog(this);

        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
        progressBar = findViewById(R.id.progressBar);
        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);


        taxNameEditText.setText(VenHome.taxArr.get(position).get("taxName").toString());
        taxDespEditText.setText(VenHome.taxArr.get(position).get("desp").toString());
        taxPerEditText.setText(VenHome.taxArr.get(position).get("taxPer").toString());




    }

    public void save(View view) {


        boolean flag = false;
        final Double tax;

        if (Objects.equals(taxNameEditText.getText().toString().trim(), "")) {
            flag = true;
            Toast.makeText(this, "Name cannot be Empty", Toast.LENGTH_SHORT).show();
        } else if (Objects.equals(taxDespEditText.getText().toString().trim(), "")) {
            flag = true;
            Toast.makeText(this, "Description cannot be Empty", Toast.LENGTH_SHORT).show();
        } else if (Objects.equals(taxPerEditText.getText().toString().trim(), "")) {
            flag = true;
            Toast.makeText(this, "Tax Percentage cannot be Empty", Toast.LENGTH_SHORT).show();
        } else {
            tax = Double.parseDouble(taxPerEditText.getText().toString().trim());
            if (tax == 0) {
                flag = true;
                Toast.makeText(this, "Tax Percentage cannot be 0 (Zero) ", Toast.LENGTH_SHORT).show();
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
//                            Common.EnableProgressBar(progressBarHolder, inAnimation);
                            progressDialog.setMessage("Updating Tax Details ");
                            progressDialog.show();
                            taxCurrentDoc.update("taxName", taxNameEditText.getText().toString().trim());
                            taxCurrentDoc.update("desp", taxDespEditText.getText().toString().trim());
                            taxCurrentDoc.update("taxPer", Double.parseDouble(taxPerEditText.getText().toString().trim())).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
//                                    Common.DisableProgressBar(progressBarHolder, outAnimation);
                                    progressDialog.dismiss();
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
        builder.setTitle("Save entry")
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
