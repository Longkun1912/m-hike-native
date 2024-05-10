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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UpdateHikeActivity extends AppCompatActivity {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private EditText hikeName, hikeLocation, hikeLength, hikeDescription;
    private DatePicker hikeDate;
    private RadioGroup parkingStatusRadioGroup, difficultyLevelRadioGroup;
    private Button updateHikeBtn, cancelUpdateHikeBtn;
    private DatabaseHelper databaseHelper;
    private Hike currentHike;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_hike);

        int hikeId = getIntent().getIntExtra("hike_id", -1);

        databaseHelper = new DatabaseHelper(this);
        currentHike = databaseHelper.getHikeById(hikeId);

        hikeName = findViewById(R.id.hikeName_input);
        hikeLocation = findViewById(R.id.hikeLocation_input);
        hikeLength = findViewById(R.id.hikeLength_input);
        hikeDescription = findViewById(R.id.hikeDescription_input);

        hikeDate = findViewById(R.id.hike_datePicker);

        // Get the year, month, day of the selected hike respectively
        try {
            Date date = sdf.parse(currentHike.getDate());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int hikeYear = calendar.get(Calendar.YEAR);
            int hikeMonth = calendar.get(Calendar.MONTH);
            int hikeDay = calendar.get(Calendar.DAY_OF_MONTH);

            // Default selection of current hike date
            hikeDate.init(hikeYear, hikeMonth, hikeDay, (view, year, month, day) -> {
                String selectedDate = year + "-" + (month + 1) + "-" + day;
                currentHike.setDate(selectedDate);
            });
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }

        parkingStatusRadioGroup = findViewById(R.id.parking_status);

        // Set the default value of parking status for current hike
        if (currentHike.getParking().equals("Yes")){
            parkingStatusRadioGroup.check(R.id.yes_status);
        } else if (currentHike.getParking().equals("No")){
            parkingStatusRadioGroup.check(R.id.no_status);
        }

        parkingStatusRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.yes_status) {
                currentHike.setParking("Yes");
            } else if (checkedId == R.id.no_status) {
                currentHike.setParking("No");
            }
        });

        difficultyLevelRadioGroup = findViewById(R.id.hike_level);

        // Set the default value of difficulty for current hike
        if (currentHike.getDifficulty().equals("Easy")){
            difficultyLevelRadioGroup.check(R.id.easy_level);
        } else if (currentHike.getDifficulty().equals("Normal")){
            difficultyLevelRadioGroup.check(R.id.normal_level);
        } else if (currentHike.getDifficulty().equals("Hard")){
            difficultyLevelRadioGroup.check(R.id.hard_level);
        } else if (currentHike.getDifficulty().equals("Insane")){
            difficultyLevelRadioGroup.check(R.id.insane_level);
        }

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

        // Set the current details for viewing
        hikeName.setText(currentHike.getName());
        hikeLocation.setText(currentHike.getLocation());
        hikeLength.setText(currentHike.getLength());
        hikeDescription.setText(currentHike.getDescription());

        // Update selected hike
        updateHikeBtn = findViewById(R.id.confirm_update);
        updateHikeBtn.setOnClickListener(view -> {
            String name = hikeName.getText().toString();
            String location = hikeLocation.getText().toString();
            String length = hikeLength.getText().toString();
            String description = hikeDescription.getText().toString();

            String parking = currentHike.getParking();
            String difficulty = currentHike.getDifficulty();

            AlertDialog.Builder builder = new AlertDialog.Builder(UpdateHikeActivity.this);
            builder.setTitle("Updating confirmation");
            builder.setMessage("Are you sure you want to update this hike?");

            builder.setPositiveButton("Yes", (dialog, which) -> {
                if (name.isEmpty() || location.isEmpty() || length.isEmpty() || parking.isEmpty() || difficulty.isEmpty()){
                    Toast.makeText(UpdateHikeActivity.this, "Please enter the missing information", Toast.LENGTH_LONG).show();
                }
                else {
                    currentHike.setName(name);
                    currentHike.setLocation(location);
                    currentHike.setLength(length);
                    currentHike.setDifficulty(difficulty);
                    currentHike.setDescription(description);

                    databaseHelper.updateHike(hikeId, currentHike);
                    Toast.makeText(UpdateHikeActivity.this, "Hike updated successfully", Toast.LENGTH_LONG).show();
                }
            });
            builder.setNegativeButton("No", (dialog, which) -> {
                Toast.makeText(UpdateHikeActivity.this, "Updating request cancelled.", Toast.LENGTH_LONG).show();
            });
            builder.create().show();
        });

        cancelUpdateHikeBtn = findViewById(R.id.cancel_update);
        cancelUpdateHikeBtn.setOnClickListener(view -> {
            Intent hikeDetails = new Intent(UpdateHikeActivity.this, HikeDetailsActivity.class);
            hikeDetails.putExtra("hikeId", hikeId);
            startActivity(hikeDetails);
        });
    }
}
