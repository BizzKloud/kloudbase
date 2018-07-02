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
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.lang.annotation.Documented;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CatOrganize extends AppCompatActivity {

    ArrayList<String> catList;
    DocumentReference category;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cat_organize);

        catList = new ArrayList<String>();
        progressDialog = new ProgressDialog(this);

        catList = (ArrayList<String>) CategoryDetails.listDataHeader;

        OrganiseArrayAdapter adapter = new OrganiseArrayAdapter(this, R.layout.text_view, catList);
        DynamicListView listView = (DynamicListView) findViewById(R.id.listview);

        listView.setList(catList);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater saveMenu = getMenuInflater();
        saveMenu.inflate(R.menu.save_menu, menu );
        return true;
//        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {


        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }

        builder.setTitle("Save Category Sequence")
                .setMessage("Are you sure you want to Update Category Sequence?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(item.getItemId() == R.id.save) {

                            progressDialog.setMessage("Saving Changes ...");
                            progressDialog.show();

                            for(int i=0; i<catList.size(); i++) {
                                for (HashMap entry  : VenHome.categoryArr)
                                {
                                    if(entry.get("name").equals(catList.get(i))) {

                                        if(i+1 != Integer.parseInt(entry.get("catpos").toString()) ) {
                                            category = VenHome.catPathRef.document(entry.get("catid").toString());
                                            category.update("catpos" , i+1 );
                                        }
                                    }
                                }

                            }
                            progressDialog.dismiss();
                            finish();
                        }
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

        return super.onOptionsItemSelected(item);
    }





}

