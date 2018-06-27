package com.example.smahadik.kloudbase;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class AddNewFdItem extends AppCompatActivity {


    public static final String FCID = "fcid";
    public static final String BEGDA = "begda";
    public static final String ENDDA = "endda";
    public static final String VENID = "venid";
    public static final String NAME = "name";
    public static final String SDESP = "sdesp";
    public static final String LDESP = "ldesp";
    public static final String CATID = "catid";
    public static final String CATEGORY = "category";
    public static final String FDID = "fdid";
    public static final String STATUS = "status";
    public static final String FSPOS = "fspos";
    public static final String AMOUNT = "amount";
    public static final String PIC = "pic";
    public static final String PIC_L = "picL";
    public static final String RATING = "rating";
    EditText fdItemNameEditText;
    EditText fdItemAmountEditText;
    EditText fdItemcatEditText;
    EditText fdItemShortDespEditText;
    EditText fdItemLongDespEditText;
    EditText fdItemratingsEditText;
    ImageView fdimage;
    DocumentReference fdItemRef;
//    ArrayList<HashMap> fddItem;
    HashMap<String, String> newFdItem;
    String pic;
    String id = "";
    String fdid;
    String name;
    int amount;
    int ratings;
    String sdesp;
    String category;
    String ldesp;
    String venid;
    String venidno [];
    Integer REQUEST_CAMERA = 1, SELECT_FILE = 0;
    private Uri filePath;
    ProgressDialog progressDialog;

    FrameLayout progressBarHolder;
    ProgressBar progressBar;
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_fd_item);
        Intent DfItemDetails = getIntent();

        //FireStore



        // Initialization
        getSupportActionBar().setTitle("New Food Item");

        fdItemNameEditText = findViewById(R.id.fdItemNameEditText);
        fdItemAmountEditText = findViewById(R.id.fdItemAmountEditText);
        fdItemcatEditText = findViewById(R.id.fdItemcatEditText);
        fdItemShortDespEditText = findViewById(R.id.fdItemShortDespEditText);
        fdItemLongDespEditText = findViewById(R.id.fdItemLongDespEditText);
        fdItemratingsEditText = findViewById(R.id.fdItemratingsEditText);
        fdimage = findViewById(R.id.fdimage);

        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
        progressBar = findViewById(R.id.progressBar);
        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);
        progressDialog = new ProgressDialog(this);

        newFdItem = new HashMap<String, String>();

//        fddItem = VenHome.foodItemArr.get(FdItemDetails.position);

        int no = getFdItemId();

        if(no < 10) {
            id = "00" + no;
        }else if (no < 100 && no > 9) {
            id = "0" + no;
        }else {
            id = String.valueOf(no);
        }


        venid = VenHome.vendorDetails.get("venid").toString();

//        venidno = venid.split("_");

//        fdid = VenHome.fcDetails.get("fcid") + "_VEN_" + venidno[venidno.length-1] + "_MENU_" + id;
        fdid = venid + "_MENU_" + id;

        Toast.makeText(this, fdid, Toast.LENGTH_SHORT).show();

        fdItemcatEditText.setText(VenHome.categoryArr.get(FdItemDetails.position).get("name").toString());

        //Firebase
        fdItemRef = VenHome.catPathRef.document(VenHome.categoryArr.get(FdItemDetails.position).get("catid").toString() + "/MenuM/" + fdid);



    }


    public void galary (View view) {
        //Open Galary
        final CharSequence[] imageUploadOption = {"Camera" , "Mobile Galery", "My Cloud Galary" , "Common Galary"};

        AlertDialog.Builder uploadOption = new AlertDialog.Builder(this);
        uploadOption.setTitle("Add Image");
        uploadOption.setItems(imageUploadOption, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(imageUploadOption[which].equals("Camera")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);

                }else if(imageUploadOption[which].equals("Mobile Galery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent, "Select File"), SELECT_FILE);

                }else if(imageUploadOption[which].equals("My Cloud Galary")) {

                }else if(imageUploadOption[which].equals("Common Galary")) {

                }else {
                    dialog.dismiss();
                }
            }
        });
        uploadOption.show();

    }


    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK) {

            if(requestCode == REQUEST_CAMERA) {
                Bundle bundle = data.getExtras();
                final Bitmap bitmap = (Bitmap) bundle.get("data");
                fdimage.setImageBitmap(bitmap);

            }else if(requestCode == SELECT_FILE) {
                filePath = data.getData();
                Uri selectedImageUri = data.getData();
                fdimage.setImageURI(selectedImageUri);
            }

        }
    }

    private int getFdItemId() {

        if(VenHome.foodItemArr.get(FdItemDetails.position).isEmpty()) {
            return 1;
        }

        ArrayList<HashMap> fdItem = VenHome.foodItemArr.get(FdItemDetails.position);
        HashMap<String, String> temporary;
        for (int c = 0; c < (fdItem.size() - 1); c++) {
            for (int d = 0; d < (fdItem.size() - c - 1); d++) {

                String [] zero = fdItem.get(d).get("fdid").toString().split("_");
                String [] one = fdItem.get(d + 1).get("fdid").toString().split("_");
                int zeroint = Integer.parseInt(zero[zero.length-1]);
                int oneint = Integer.parseInt(one[one.length-1]);

                if (zeroint > oneint) {

                    temporary = fdItem.get(d);
                    fdItem.set(d, fdItem.get(d + 1));
                    fdItem.set(d + 1, temporary);

                }
            }
        }
        String [] no = fdItem.get(fdItem.size()-1).get("fdid").toString().split("_");
//        Log.i("NEW id" , String.valueOf(id + 1));

        return Integer.parseInt(no[no.length-1]) + 1;

    }


    public void add(View view) {

        boolean flag = false;

        if(Objects.equals(fdItemNameEditText.getText().toString().trim(), "") || Objects.equals(fdItemShortDespEditText.getText().toString().trim(), "") || Objects.equals(fdItemLongDespEditText.getText().toString().trim(), "")) {
            flag = true;
            if(Objects.equals(fdItemNameEditText.getText().toString().trim(), "")) {
                Toast.makeText(this, "Name cannot be Empty", Toast.LENGTH_SHORT).show();
            }else if (Objects.equals(fdItemShortDespEditText.getText().toString().trim(), "")) {
                Toast.makeText(this, "Short Description cannot be Empty", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Long Description cannot be Empty", Toast.LENGTH_SHORT).show();
            }
        } else if (Objects.equals(fdItemratingsEditText.getText().toString().trim(), "")) {
            flag = true;
            Toast.makeText(this, "Ratings should be between 0-5", Toast.LENGTH_SHORT).show();
        } else if (Objects.equals(fdItemAmountEditText.getText().toString().trim(), "")) {
            flag = true;
            Toast.makeText(this, "Amount cannot be Empty", Toast.LENGTH_SHORT).show();
        } else {
            name = fdItemNameEditText.getText().toString().trim();
            sdesp = fdItemShortDespEditText.getText().toString().trim();
            ldesp = fdItemLongDespEditText.getText().toString().trim();
            category = fdItemcatEditText.getText().toString().trim();
            ratings = Integer.parseInt(fdItemratingsEditText.getText().toString().trim());
            amount = Integer.parseInt(fdItemAmountEditText.getText().toString().trim());

            if (ratings == 0 || ratings > 5) {
                flag = true;
                Toast.makeText(this, "Ratings should be between 0-5", Toast.LENGTH_SHORT).show();
            }else if (amount == 0) {
                flag = true;
                Toast.makeText(this, "Amount cannot be 0 (Zero)", Toast.LENGTH_SHORT).show();
            } else if(filePath == null) {
                flag = true;
                Toast.makeText(this, "Food Item Image Cannot be Empty", Toast.LENGTH_SHORT).show();
            } else if (filePath == null) {
                flag = true;
                Toast.makeText(this, "Image cannot be empty", Toast.LENGTH_SHORT).show();
            }
        }


        if (!flag) {


            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(this);
            }

            builder.setTitle("Add New FoodItem")
                    .setMessage("Are you sure you want to Add this FoodItem ?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            progressDialog.setTitle("Adding Food Item");
                            progressDialog.show();
//                            Uploading Image
                            uploadFile();

//                            Create Food Item
//                            Common.EnableProgressBar(progressBarHolder, inAnimation);
                            newFdItem.put(FCID , VenHome.fcDetails.get("fcid").toString());
                            newFdItem.put(BEGDA , "1/1/2017");
                            newFdItem.put(ENDDA, "12/31/9999");
                            newFdItem.put(VENID, venid);
                            newFdItem.put(NAME, name);
                            newFdItem.put(SDESP, sdesp);
                            newFdItem.put(LDESP, ldesp);
                            newFdItem.put(CATID, VenHome.categoryArr.get(FdItemDetails.position).get("catid").toString());
                            newFdItem.put(CATEGORY, category);
                            newFdItem.put(FDID, fdid);
                            newFdItem.put(PIC, pic);
                            newFdItem.put(PIC_L, ".jpg");

                            fdItemRef.set(newFdItem);
                            fdItemRef.update(FSPOS, VenHome.foodItemArr.get(FdItemDetails.position).size()+1);
                            fdItemRef.update(AMOUNT, amount);
                            fdItemRef.update(RATING, ratings);
                            fdItemRef.update(STATUS, true ).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.i("Finish", "Updates Done");
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




    public void uploadFile() {
        //checking if file is available
        if (filePath != null) {

            //getting the storage reference
            pic = "Products/KloudKafe/"
                    + VenHome.fcDetails.get("fcid").toString() + "/"
                    + venid + "/"
                    + VenHome.categoryArr.get(FdItemDetails.position).get("catid").toString() + "/"
                    + fdid
                    + "." + getFileExtension(filePath);

            StorageReference sRef = VenHome.storageRef.child(pic);

            //adding the file to reference
            sRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //displaying the upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        } else {
            Toast.makeText(this, "Image cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }









}
