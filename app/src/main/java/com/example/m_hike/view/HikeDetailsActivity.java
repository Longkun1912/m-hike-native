package com.example.m_hike.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.m_hike.DatabaseHelper;
import com.example.m_hike.R;
import com.example.m_hike.model.Hike;
import com.example.m_hike.model.Observation;

import java.util.List;

public class HikeDetailsActivity extends AppCompatActivity {
    private ListView observation_list;

    @Override
    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_hike_details);

        int hikeId = getIntent().getIntExtra("hikeId", -1);

        // Query the database to get the details of the selected hike using hikeId
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Hike hike = databaseHelper.getHikeById(hikeId);

        TextView hikeIdTextView = findViewById(R.id.hike_id_detail);
        TextView hikeNameTextView = findViewById(R.id.hike_name_detail);
        TextView hikeLocationTextView = findViewById(R.id.hike_location_detail);
        TextView hikeDateTextView = findViewById(R.id.hike_date_detail);
        TextView hikeParkingTextView = findViewById(R.id.hike_parking_detail);
        TextView hikeLengthTextView = findViewById(R.id.hike_length_detail);
        TextView hikeDifficultyTextView = findViewById(R.id.hike_difficulty_detail);
        TextView hikeDescriptionTextView = findViewById(R.id.hike_description_detail);


        hikeIdTextView.setText("ID: " + hike.getId());
        hikeNameTextView.setText("Name: " + hike.getName());
        hikeLocationTextView.setText("Location: " + hike.getLocation());
        hikeDateTextView.setText("Date: " + hike.getDate());
        hikeParkingTextView.setText("Parking status: " + hike.getParking());
        hikeLengthTextView.setText("Length: " + hike.getLength());
        hikeDifficultyTextView.setText("Difficulty level: " + hike.getDifficulty());
        hikeDescriptionTextView.setText("Description: " + hike.getDescription());

        // Back to hike list button
        Button backToHikeList = findViewById(R.id.backToHikeList);
        backToHikeList.setOnClickListener(view -> {
            Intent hikeList = new Intent(HikeDetailsActivity.this, ViewHikesActivity.class);
            startActivity(hikeList);
        });

        // Update hike button
        Button updateHike = findViewById(R.id.updateHike);
        updateHike.setOnClickListener(view -> {
            Intent updateHikeForm = new Intent(HikeDetailsActivity.this, UpdateHikeActivity.class);
            updateHikeForm.putExtra("hike_id", hikeId);
            startActivity(updateHikeForm);
        });

        // Delete hike button
        Button deleteHike = findViewById(R.id.deleteHike);
        deleteHike.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(HikeDetailsActivity.this);
            builder.setTitle("Deleting confirmation");
            builder.setMessage("Are you sure you want to delete this hike?");

            builder.setPositiveButton("Yes", (dialog, which) -> {
                databaseHelper.deleteHike(hikeId);
                Intent hikeList = new Intent(HikeDetailsActivity.this, ViewHikesActivity.class);
                startActivity(hikeList);
                Toast.makeText(HikeDetailsActivity.this, "Hike deleted successfully.", Toast.LENGTH_LONG).show();
            });
            builder.setNegativeButton("No", (dialog, which) -> {
                Toast.makeText(HikeDetailsActivity.this, "Delete request cancelled.", Toast.LENGTH_LONG).show();
            });
            builder.create().show();
        });

        // List of observations
        observation_list = findViewById(R.id.observation_list);

        List<Observation> observations = databaseHelper.getObservationsForHike(hikeId);

        ArrayAdapter<Observation> adapter = new ArrayAdapter<Observation>(this, R.layout.list_observations, observations) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.list_observations, null);
                }

                TextView observationTitle = convertView.findViewById(R.id.observation_item_title);
                Button viewObservation = convertView.findViewById(R.id.observation_item_action);

                Observation observation = getItem(position);
                if (observation != null) {
                    observationTitle.setText(observation.getTitle());
                    viewObservation.setText("View");

                    // View observation details
                    viewObservation.setOnClickListener(view -> {
                        openObservationDetailsActivity(observation.getId(), hikeId);
                    });
                }
                return convertView;
            }
        };
        observation_list.setAdapter(adapter);

        // Add new observation button
        Button addObservation = findViewById(R.id.addObservation_btn);
        addObservation.setOnClickListener(view -> {
            Intent add_observation = new Intent(HikeDetailsActivity.this, AddObservationActivity.class);
            add_observation.putExtra("hike_id", hikeId);
            startActivity(add_observation);
        });
    }

    private void openObservationDetailsActivity(int observation_id, int hike_id){
        Intent intent = new Intent(this, ObservationDetailsActivity.class);
        intent.putExtra("hike_id", hike_id);
        intent.putExtra("observationId", observation_id);
        startActivity(intent);
    }
}
