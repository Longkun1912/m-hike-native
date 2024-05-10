package com.example.m_hike.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.m_hike.DatabaseHelper;
import com.example.m_hike.R;
import com.example.m_hike.model.Observation;

import java.util.GregorianCalendar;
import java.util.Locale;

public class UpdateObservationActivity extends AppCompatActivity {
    private EditText title, comment;
    private CalendarView observationDate;
    private TimePicker observationTime;
    private Observation observation;
    private String selectedDate, selectedTime;
    private Button updateObservation;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_observation);

        int observationId = getIntent().getIntExtra("observation_id", -1);
        int hikeId = getIntent().getIntExtra("hikeId", -1);


        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        observation = databaseHelper.getObservationById(observationId);

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

        // Set the current details for viewing
        title.setText(observation.getTitle());
        comment.setText(observation.getComment());

        String[] parts = observation.getTime().split(" at ");
        selectedDate = parts[0];
        selectedTime = parts[1];

        String[] dateComponents = selectedDate.split("/");
        int day = Integer.parseInt(dateComponents[0]);
        int month = Integer.parseInt(dateComponents[1]);
        int year = Integer.parseInt(dateComponents[2]);
        System.out.println("Year: " + year + " ;Month: " + month + " ;Day: " + day);
        observationDate.setDate(new GregorianCalendar(year, month, day).getTimeInMillis(), false, true);

        String[] timeComponents = selectedTime.split(":| ");
        int hour = Integer.parseInt(timeComponents[0]);
        int minute = Integer.parseInt(timeComponents[1]);
        String amPm = timeComponents[2];

        observationTime.setHour((amPm.equals("PM") && hour < 12) ? hour + 12 : hour);
        observationTime.setMinute(minute);
        observationTime.setIs24HourView(false);

        // Confirm update button
        updateObservation = findViewById(R.id.confirmUpdate_observation);
        updateObservation.setOnClickListener(view -> {
            String observation_title = title.getText().toString();
            String observation_comment = comment.getText().toString();

            AlertDialog.Builder builder = new AlertDialog.Builder(UpdateObservationActivity.this);
            builder.setTitle("Updating confirmation");
            builder.setMessage("Are you sure you want to update this observation?");

            builder.setPositiveButton("Yes", (dialog, which) -> {
                if (observation_title.isEmpty() || selectedDate == null || selectedTime == null){
                    Toast.makeText(UpdateObservationActivity.this, "Please enter the missing information", Toast.LENGTH_LONG).show();
                }
                else {
                    String observation_dateTime = selectedDate + " at " + selectedTime;
                    observation.setTitle(observation_title);
                    observation.setComment(observation_comment);
                    observation.setTime(observation_dateTime);

                    databaseHelper.updateObservation(observationId, observation);
                    Toast.makeText(UpdateObservationActivity.this, "Observation updated successfully", Toast.LENGTH_LONG).show();
                }
            });
            builder.setNegativeButton("No", (dialog, which) -> {
                Toast.makeText(UpdateObservationActivity.this, "Update request cancelled.", Toast.LENGTH_LONG).show();
            });
            builder.create().show();
        });

        // Cancel update button and back to observation details
        Button cancelUpdateObservation = findViewById(R.id.back_to_observationDetails);
        cancelUpdateObservation.setOnClickListener(view -> {
            Intent observationDetails = new Intent(UpdateObservationActivity.this, ObservationDetailsActivity.class);
            observationDetails.putExtra("observationId", observationId);
            observationDetails.putExtra("hike_id", hikeId);
            startActivity(observationDetails);
        });
    }
}
