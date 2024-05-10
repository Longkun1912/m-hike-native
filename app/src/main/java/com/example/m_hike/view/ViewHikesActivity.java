package com.example.m_hike.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.m_hike.DatabaseHelper;
import com.example.m_hike.R;
import com.example.m_hike.model.Hike;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class ViewHikesActivity extends AppCompatActivity {
    ArrayAdapter<Hike> adapter;
    List<Hike> hikes;
    private DatabaseHelper databaseHelper;
    private ListView hike_list;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_hikes);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                Intent home = new Intent(ViewHikesActivity.this, MainActivity.class);
                startActivity(home);
                return true;
            } else if (item.getItemId() == R.id.navigation_add_hike) {
                Intent add_hike = new Intent(ViewHikesActivity.this, AddHikeActivity.class);
                startActivity(add_hike);
                return true;
            } else if (item.getItemId() == R.id.navigation_view_hikes) {
                return true;
            } else {
                return false;
            }
        });

        hike_list = findViewById(R.id.hike_list);
        databaseHelper = new DatabaseHelper(this);

        hikes = databaseHelper.getHikes(null);

        SearchView searchView = findViewById(R.id.search_view);
        searchView.setIconifiedByDefault(false); // Show the search icon within the SearchView

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterHikes(newText, hikes, databaseHelper);
                return true;
            }
        });

        adapter = new ArrayAdapter<Hike>(this, R.layout.list_hikes, hikes) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.list_hikes, null);
                }

                TextView hikeName = convertView.findViewById(R.id.hike_item_name);
                TextView hikeDifficulty = convertView.findViewById(R.id.hike_item_difficulty);

                Hike hike = getItem(position);
                if (hike != null) {
                    hikeName.setText(hike.getName());
                    hikeDifficulty.setText(hike.getDifficulty());
                }
                return convertView;
            }
        };

        hike_list.setAdapter(adapter);

        hike_list.setOnItemClickListener((parent, view, position, id) -> {
            Hike selectedHike = hikes.get(position); // Get the selected hike from the adapter

            // Display the details of the selected hike
            openHikeDetailsActivity(selectedHike.getId());
        });
    }

    // View details of selected hike
    private void openHikeDetailsActivity(int hikeId) {
        Intent intent = new Intent(this, HikeDetailsActivity.class);
        intent.putExtra("hikeId", hikeId);
        startActivity(intent);
    }

    // Filter hikes by search name
    private void filterHikes(String query, List<Hike> hikes, DatabaseHelper databaseHelper) {
        hikes = databaseHelper.getHikes(query);

        adapter.clear(); // Clear the current list
        adapter.addAll(hikes); // Add filtered hikes to the adapter
        adapter.notifyDataSetChanged(); // Notify the adapter of the data change
    }
}
