package com.example.m_hike.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.m_hike.DatabaseHelper;
import com.example.m_hike.R;
import com.example.m_hike.model.Hike;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AddHikeActivity extends AppCompatActivity {
    private EditText hikeName, hikeLocation, hikeLength, hikeDescription;
    private DatePicker hikeDate;
    private RadioGroup parkingStatusRadioGroup, difficultyLevelRadioGroup;
    private Button addHikeBtn;
    private DatabaseHelper databaseHelper;
    private Hike currentHike;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_hike);

        // Navigation menu
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                Intent home = new Intent(AddHikeActivity.this, MainActivity.class);
                startActivity(home);
                return true;
            } else if (item.getItemId() == R.id.navigation_add_hike) {
                return true;
            } else if (item.getItemId() == R.id.navigation_view_hikes){
                Intent view_hikes = new Intent(AddHikeActivity.this, ViewHikesActivity.class);
                startActivity(view_hikes);
                return true;
            }
            else {
                return false;
            }
        });

        currentHike = new Hike();
        databaseHelper = new DatabaseHelper(this);

        hikeName = findViewById(R.id.hikeName_input);
        hikeLocation = findViewById(R.id.hikeLocation_input);
        hikeLength = findViewById(R.id.hikeLength_input);
        hikeDescription = findViewById(R.id.hikeDescription_input);

        hikeDate = findViewById(R.id.hike_datePicker);
        hikeDate.init(2023, 0, 1, (view, year, month, day) -> {
            String selectedDate = year + "-" + (month + 1) + "-" + day;
            currentHike.setDate(selectedDate);
        });

        parkingStatusRadioGroup = findViewById(R.id.parking_status);
        parkingStatusRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.yes_status) {
                currentHike.setParking("Yes");
            } else if (checkedId == R.id.no_status) {
                currentHike.setParking("No");
            }
        });

        difficultyLevelRadioGroup = findViewById(R.id.hike_level);
        difficultyLevelRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.easy_level) {
                currentHike.setDifficulty("Easy");
            } else if (checkedId == R.id.normal_level) {
                currentHike.setDifficulty("Normal");
            } else if (checkedId == R.id.hard_level) {
                currentHike.setDifficulty("Hard");
            } else if (checkedId == R.id.insane_level) {
                currentHike.setDifficulty("Insane");
            }
        });

        addHikeBtn = findViewById(R.id.add_hike);
        addHikeBtn.setOnClickListener(view -> {
            String name = hikeName.getText().toString();
            String location = hikeLocation.getText().toString();
            String length = hikeLength.getText().toString();
            String description = hikeDescription.getText().toString();

            String parking = currentHike.getParking();
            String difficulty = currentHike.getParking();

            AlertDialog.Builder builder = new AlertDialog.Builder(AddHikeActivity.this);
            builder.setTitle("Adding confirmation");
            builder.setMessage("Are you sure you want to add this hike?");

            builder.setPositiveButton("Yes", (dialog, which) -> {
                if (name.isEmpty() || location.isEmpty() || length.isEmpty() || parking == null || difficulty == null || parking.isEmpty() || difficulty.isEmpty()){
                    Toast.makeText(AddHikeActivity.this, "Please enter the missing information", Toast.LENGTH_LONG).show();
                }
                else {
                    currentHike.setName(name);
                    currentHike.setLocation(location);
                    currentHike.setLength(length);
                    currentHike.setDescription(description);

                    databaseHelper.addNewHike(currentHike);
                    Toast.makeText(AddHikeActivity.this, "Hike added successfully", Toast.LENGTH_LONG).show();

                    // Clear the input fields and reset the currentHike object
                    hikeName.setText("");
                    hikeLocation.setText("");
                    hikeLength.setText("");
                    hikeDescription.setText("");
                    hikeDate.updateDate(2023, 0, 1); // Set a default date
                    parkingStatusRadioGroup.clearCheck();
                    currentHike = new Hike();
                }
            });
            builder.setNegativeButton("No", (dialog, which) -> {
                // User clicked "No," do nothing or provide feedback if needed
                Toast.makeText(AddHikeActivity.this, "Hike not added.", Toast.LENGTH_LONG).show();
            });
            builder.create().show();
        });
    }
}
