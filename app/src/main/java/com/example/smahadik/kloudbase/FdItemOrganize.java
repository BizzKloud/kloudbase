package com.example.smahadik.kloudbase;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.HashMap;

public class FdItemOrganize extends AppCompatActivity {

    ArrayList<String> fdList;
    DocumentReference foodItem;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fd_item_organize);


        fdList = new ArrayList<String>();
        progressDialog = new ProgressDialog(this);

        fdList = (ArrayList<String>) FdItemDetails.listDataHeader;

        OrganiseArrayAdapter adapter = new OrganiseArrayAdapter(this, R.layout.text_view, fdList);
        DynamicListView listView = (DynamicListView) findViewById(R.id.listview);

        listView.setList(fdList);
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

        builder.setTitle("Save Food Item Sequence")
                .setMessage("Are you sure you want to Update Food Item Sequence?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(item.getItemId() == R.id.save) {

                            progressDialog.setMessage("Saving Changes ...");
                            progressDialog.show();

                            for(int i=0; i<fdList.size(); i++) {
                                for (HashMap entry  : VenHome.foodItemArr.get(FdItemDetails.position))
                                {
                                    if(entry.get("name").equals(fdList.get(i))) {

                                        if(i+1 != Integer.parseInt(entry.get("fspos").toString()) ) {
                                            foodItem = VenHome.catPathRef.document( VenHome.categoryArr.get(FdItemDetails.position).get("catid").toString() + "/MenuM/" + entry.get("fdid").toString());
                                            foodItem.update("fspos" , i+1 );
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
