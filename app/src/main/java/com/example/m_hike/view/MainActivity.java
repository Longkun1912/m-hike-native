package com.example.m_hike.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.m_hike.DatabaseHelper;
import com.example.m_hike.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {
    private Button deleteDataBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        deleteDataBtn = findViewById(R.id.deleteData);
        deleteDataBtn.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Reset database confirmation");
            builder.setMessage("Are you sure you want to delete all details of the database?");

            builder.setPositiveButton("Yes", (dialog, which) -> {
                databaseHelper.deleteAllData();
                Toast.makeText(MainActivity.this, "Database reset successfully.", Toast.LENGTH_LONG).show();
            });
            builder.setNegativeButton("No", (dialog, which) -> {
                Toast.makeText(MainActivity.this, "Request cancelled.", Toast.LENGTH_LONG).show();
            });
            builder.create().show();
        });



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                return true;
            } else if (item.getItemId() == R.id.navigation_add_hike) {
                Intent add_hike = new Intent(MainActivity.this, AddHikeActivity.class);
                startActivity(add_hike);
                return true;
            } else if (item.getItemId() == R.id.navigation_view_hikes){
                Intent view_hikes = new Intent(MainActivity.this, ViewHikesActivity.class);
                startActivity(view_hikes);
                return true;
            }
            else {
                return false;
            }
        });

    }
}

