package com.example.smahadik.kloudbase;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
    ImageView fdimage;
    StorageReference storageRefimage;
    String pic;
    int REQUEST_CAMERA = 1, SELECT_FILE = 0;
    private Uri filePath;
    ProgressDialog progressDialog;
    ProgressBar progressBar;
    Bitmap bitmap;

//
//    FrameLayout progressBarHolder;
//    ProgressBar progressBar;
//    AlphaAnimation inAnimation;
//    AlphaAnimation outAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_fd_item_details);

        Intent fdItemDetails = getIntent();
        position = fdItemDetails.getIntExtra("editPosition" , -1);
        pic = VenHome.foodItemArr.get(FdItemDetails.position).get(position).get("pic").toString();

        //Firebase
        fdItemCurrentDoc = VenHome.catPathRef.document(VenHome.categoryArr.get(FdItemDetails.position).get("catid").toString() + "/MenuM/" + VenHome.foodItemArr.get(FdItemDetails.position).get(position).get("fdid").toString() );
        storageRefimage = VenHome.storageRef.child(pic);


        // Initialization
        getSupportActionBar().setTitle(VenHome.foodItemArr.get(FdItemDetails.position).get(position).get("name").toString());

        fdimage = findViewById(R.id.fdimage);
        fdItemNameEditText = findViewById(R.id.fdItemNameEditText);
        fdItemAmountEditText = findViewById(R.id.fdItemAmountEditText);
        fdItemcatEditText = findViewById(R.id.fdItemcatEditText);
        fdItemShortDespEditText = findViewById(R.id.fdItemShortDespEditText);
        fdItemLongDespEditText = findViewById(R.id.fdItemLongDespEditText);
        fdItemratingsEditText = findViewById(R.id.fdItemratingsEditText);

        progressDialog = new ProgressDialog(this);
        progressBar = findViewById(R.id.progressBar);

//        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
//        progressBar = findViewById(R.id.progressBar);
//        inAnimation = new AlphaAnimation(0f, 1f);
//        inAnimation.setDuration(200);
//        outAnimation = new AlphaAnimation(1f, 0f);
//        outAnimation.setDuration(200);

        Glide.with(fdimage.getContext()).using(new FirebaseImageLoader()).load(storageRefimage)
//                .diskCacheStrategy(DiskCacheStrategy.NONE ).skipMemoryCache(true)
                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .listener(new RequestListener<StorageReference, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(fdimage);
        fdItemNameEditText.setText(VenHome.foodItemArr.get(FdItemDetails.position).get(position).get("name").toString());
        fdItemShortDespEditText.setText(VenHome.foodItemArr.get(FdItemDetails.position).get(position).get("sdesp").toString());
        fdItemLongDespEditText.setText(VenHome.foodItemArr.get(FdItemDetails.position).get(position).get("ldesp").toString());
        fdItemcatEditText.setText(VenHome.foodItemArr.get(FdItemDetails.position).get(position).get("category").toString());
        fdItemcatEditText.setEnabled(false);
        fdItemAmountEditText.setText(VenHome.foodItemArr.get(FdItemDetails.position).get(position).get("amount").toString());
        fdItemratingsEditText.setText(VenHome.foodItemArr.get(FdItemDetails.position).get(position).get("rating").toString());
    }




    public void galary (View view) {


            final CharSequence[] imageUploadOption = {"Camera", "Mobile Galery", "My Cloud Galary", "Common Galary"};

            AlertDialog.Builder uploadOption = new AlertDialog.Builder(this);
            uploadOption.setTitle("Add Image");
            uploadOption.setItems(imageUploadOption, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (imageUploadOption[which].equals("Camera")) {

                        if (ContextCompat.checkSelfPermission(EditFdItemDetails.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(EditFdItemDetails.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);

                        } else {
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, REQUEST_CAMERA);
                        }

                    } else if (imageUploadOption[which].equals("Mobile Galery")) {

                        if (ContextCompat.checkSelfPermission(EditFdItemDetails.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(EditFdItemDetails.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, SELECT_FILE);

                        } else {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                        }

                    } else if (imageUploadOption[which].equals("My Cloud Galary")) {

                    } else if (imageUploadOption[which].equals("Common Galary")) {

                    } else {
                        dialog.dismiss();
                    }
                }
            });
            uploadOption.show();



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_CAMERA);
           } else {
                Toast.makeText(this, "Camera Permission is needed to select Image from Galary", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == SELECT_FILE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Galary permission granted", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
            } else {
                Toast.makeText(this, "Media Permission is needed to select Image from Galary", Toast.LENGTH_LONG).show();
            }
        }
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
                bitmap = (Bitmap) bundle.get("data");
                fdimage.setImageBitmap(bitmap);
                filePath = null;

            }else if(requestCode == SELECT_FILE) {
                filePath = data.getData();
                Log.i("SELECT FILE FilePath" , filePath.toString());
                Uri selectedImageUri = data.getData();
                fdimage.setImageURI(selectedImageUri);
                bitmap = null;
            }

        }
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
//            else if (filePath == null && bitmap == null) {
////                flag = true;
////                Toast.makeText(this, "Image cannot be empty", Toast.LENGTH_SHORT).show();
////            }
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
                            progressDialog.setMessage("Updating FoodItem Details");
                            progressDialog.show();
//                            Uploading Image
                            if (filePath != null || bitmap != null) {
                                uploadFile();
                            }


                            fdItemCurrentDoc = VenHome.catPathRef.document(VenHome.categoryArr.get(FdItemDetails.position).get("catid").toString() + "/MenuM/" + VenHome.foodItemArr.get(FdItemDetails.position).get(position).get("fdid").toString() );
                            fdItemCurrentDoc.update("name", fdItemNameEditText.getText().toString().trim());
                            fdItemCurrentDoc.update("sdesp", fdItemShortDespEditText.getText().toString().trim());
                            fdItemCurrentDoc.update("ldesp", fdItemLongDespEditText.getText().toString().trim());
                            fdItemCurrentDoc.update("amount", Double.parseDouble(fdItemAmountEditText.getText().toString().trim()));
                            fdItemCurrentDoc.update("rating", Integer.parseInt(fdItemratingsEditText.getText().toString().trim())).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                    + VenHome.vendorDetails.get("venid").toString() + "/"
                    + VenHome.categoryArr.get(FdItemDetails.position).get("catid").toString() + "/"
                    + VenHome.foodItemArr.get(FdItemDetails.position).get(position).get("fdid").toString()
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

        }else if(bitmap != null) {
            //getting the storage reference
            pic = "Products/KloudKafe/"
                    + VenHome.fcDetails.get("fcid").toString() + "/"
                    + VenHome.vendorDetails.get("venid").toString() + "/"
                    + VenHome.categoryArr.get(FdItemDetails.position).get("catid").toString() + "/"
                    + VenHome.foodItemArr.get(FdItemDetails.position).get(position).get("fdid").toString()
                    + ".jpg";

            StorageReference sRef = VenHome.storageRef.child(pic);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            //adding the file to reference
            sRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    //displaying the upload progress
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                    }
            });

        } else {
            Toast.makeText(this, "Image cannot be empty", Toast.LENGTH_SHORT).show();
            //display an error if no file is selected
        }
    }



}
