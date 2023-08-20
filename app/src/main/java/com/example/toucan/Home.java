package com.example.toucan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {
    private BottomNavigationView bottomNavigation;
    private DatabaseReference reference;
    private RecyclerView recyclerView;
    private AudioListAdapter adapter;
    public String cnt;
    private List<MusicClass> audioUrls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigation = findViewById(R.id.bottomNavigation);
        reference = FirebaseDatabase.getInstance().getReference("audio");
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new AudioListAdapter(audioUrls, this);
        recyclerView.setAdapter(adapter);

        bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottomHome) {
                return true;
            } else if (item.getItemId() == R.id.bottomMusic) {
                startActivity(new Intent(Home.this, Music.class));
                finish();
                return true;
            } else if (item.getItemId() == R.id.bottomPerson) {
                startActivity(new Intent(Home.this, Profile.class));
                finish();
                return true;
            }
            return false;
        });

        readCountAndFetchData();
    }

    private void readCountAndFetchData() {
        DatabaseReference fruitReference = FirebaseDatabase.getInstance().getReference("cnt");
        fruitReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cnt = dataSnapshot.getValue().toString();
                int value = Integer.parseInt(cnt);
                for (int i = 0; i < value; i++) {
                    fetchDownloadLink(String.valueOf(i));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled event
            }
        });
    }

    private void fetchDownloadLink(String index) {
        reference.child(index).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String thumbUri = dataSnapshot.child("thumbUri").getValue().toString();
                String uri = dataSnapshot.child("uri").getValue().toString();
                audioUrls.add(new MusicClass(uri, thumbUri, name));
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
