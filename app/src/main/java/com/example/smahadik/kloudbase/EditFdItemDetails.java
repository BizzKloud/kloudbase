package com.example.smahadik.kloudbase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class EditFdItemDetails extends AppCompatActivity {

    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_fd_item_details);

        Intent fdItemDetails = getIntent();
        position = fdItemDetails.getIntExtra("editPosition" , -1);

        Toast.makeText(this, String.valueOf(position), Toast.LENGTH_SHORT).show();
    }
}
