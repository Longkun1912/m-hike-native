package com.example.m_hike.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.m_hike.DatabaseHelper;
import com.example.m_hike.R;
import com.example.m_hike.model.Observation;

public class ObservationDetailsActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_observation_details);

        int hikeId = getIntent().getIntExtra("hike_id", -1);
        int observationId = getIntent().getIntExtra("observationId", -1);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Observation observation = databaseHelper.getObservationById(observationId);

        TextView observation_id = findViewById(R.id.observation_id);
        TextView observation_title = findViewById(R.id.observation_title);
        TextView observation_time = findViewById(R.id.observation_time);
        TextView observation_comment = findViewById(R.id.observation_comment);

        observation_id.setText("ID: " + String.valueOf(observation.getId()));
        observation_title.setText("Title: " + observation.getTitle());
        observation_time.setText("Time encounter: " + observation.getTime());
        observation_comment.setText("Comment: " + observation.getComment());

        // Button update current observation
        Button updateObservationBtn = findViewById(R.id.update_observation);
        updateObservationBtn.setOnClickListener(view -> {
            Intent updateObservation = new Intent(ObservationDetailsActivity.this, UpdateObservationActivity.class);
            updateObservation.putExtra("observation_id", observationId);
            updateObservation.putExtra("hikeId", hikeId);
            startActivity(updateObservation);
        });

        // Button remove current observation
        Button removeObservationBtn = findViewById(R.id.remove_observation);
        removeObservationBtn.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ObservationDetailsActivity.this);
            builder.setTitle("Deleting confirmation");
            builder.setMessage("Are you sure you want to delete this observation?");

            builder.setPositiveButton("Yes", (dialog, which) -> {
                databaseHelper.removeObservation(observationId);
                Intent observationList = new Intent(ObservationDetailsActivity.this, HikeDetailsActivity.class);
                observationList.putExtra("hikeId", hikeId);
                startActivity(observationList);
                Toast.makeText(ObservationDetailsActivity.this, "Observation deleted successfully.", Toast.LENGTH_LONG).show();
            });

            builder.setNegativeButton("No", (dialog, which) -> {
                Toast.makeText(ObservationDetailsActivity.this, "Delete request cancelled.", Toast.LENGTH_LONG).show();
            });
            builder.create().show();
        });


        // Button back to hike details
        Button backToObservationList = findViewById(R.id.return_hikeDetails);
        backToObservationList.setOnClickListener(view -> {
            Intent observationList = new Intent(ObservationDetailsActivity.this, HikeDetailsActivity.class);
            observationList.putExtra("hikeId", hikeId);
            startActivity(observationList);
        });
    }
}
