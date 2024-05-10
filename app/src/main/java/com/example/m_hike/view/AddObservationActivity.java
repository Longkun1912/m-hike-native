package com.example.m_hike.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.m_hike.DatabaseHelper;
import com.example.m_hike.R;
import com.example.m_hike.model.Observation;

import java.util.GregorianCalendar;
import java.util.Locale;

public class AddObservationActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private EditText title, comment;
    private CalendarView observationDate;
    private TimePicker observationTime;
    private Observation observation;
    private String selectedDate, selectedTime;
    private Button addObservationBtn;

    @Override
    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_observation);

        int hikeId = getIntent().getIntExtra("hike_id", -1);

        observation = new Observation();
        databaseHelper = new DatabaseHelper(this);

        title = findViewById(R.id.input_observationName);
        comment = findViewById(R.id.input_observationComment);

        observationDate = findViewById(R.id.observation_dateInput);
        observationDate.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.US, "%02d/%02d/%02d", dayOfMonth, month + 1, year % 100);
        });

        observationTime = findViewById(R.id.observation_timeInput);
        observationTime.setOnTimeChangedListener((view, hourOfDay, minute) -> {
            String amPm;
            if (hourOfDay >= 12) {
                amPm = "PM";
                if (hourOfDay > 12) {
                    hourOfDay -= 12;
                }
            } else {
                amPm = "AM";
                if (hourOfDay == 0) {
                    hourOfDay = 12;
                }
            }
            selectedTime = String.format(Locale.US, "%02d:%02d %s", hourOfDay, minute, amPm);
        });

        // Cancel adding button and back to hike details
        Button cancelAddObservation = findViewById(R.id.back_to_hikeDetails);
        cancelAddObservation.setOnClickListener(view -> {
            Intent hikeDetails = new Intent(AddObservationActivity.this, HikeDetailsActivity.class);
            hikeDetails.putExtra("hikeId", hikeId);
            startActivity(hikeDetails);
        });

        // Confirm adding button
        addObservationBtn = findViewById(R.id.confirmAdd_observation);
        addObservationBtn.setOnClickListener(view -> {
            String observation_title = title.getText().toString();
            String observation_comment = comment.getText().toString();

            AlertDialog.Builder builder = new AlertDialog.Builder(AddObservationActivity.this);
            builder.setTitle("Adding confirmation");
            builder.setMessage("Are you sure you want to add this observation?");

            builder.setPositiveButton("Yes", (dialog, which) -> {
                if (observation_title.isEmpty() || selectedDate == null || selectedTime == null){
                    Toast.makeText(AddObservationActivity.this, "Please enter the missing information", Toast.LENGTH_LONG).show();
                }
                else {
                    String observation_dateTime = selectedDate + " at " + selectedTime;
                    observation.setTitle(observation_title);
                    observation.setComment(observation_comment);
                    observation.setTime(observation_dateTime);

                    databaseHelper.addNewObservation(observation, hikeId);
                    Toast.makeText(AddObservationActivity.this, "Observation added successfully", Toast.LENGTH_LONG).show();

                    // Clear the input fields and reset the observation object
                    observation = new Observation();
                    title.setText("");
                    comment.setText("");
                    observationDate.setDate(new GregorianCalendar(2023, 0, 1).getTimeInMillis(), false, true);
                    observationTime.setIs24HourView(false); // Enable AM/PM mode
                    observationTime.setHour(12);
                    observationTime.setMinute(0);
                }
            });
            builder.setNegativeButton("No", (dialog, which) -> {
                Toast.makeText(AddObservationActivity.this, "Adding request cancelled.", Toast.LENGTH_LONG).show();
            });
            builder.create().show();
        });
    }
}
