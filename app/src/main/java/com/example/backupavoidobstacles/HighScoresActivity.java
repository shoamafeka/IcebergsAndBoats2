package com.example.backupavoidobstacles;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import com.example.backupavoidobstacles.Fragments.ListFragment;
import com.example.backupavoidobstacles.Fragments.MapFragment;
import com.example.backupavoidobstacles.Models.HighScore;

public class HighScoresActivity extends AppCompatActivity implements ListFragment.HighScoreClickListener {

    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_and_map);

        mapFragment = new MapFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_FRAME_list, new ListFragment())
                .replace(R.id.main_FRAME_map, mapFragment)
                .commit();
    }

    @Override
    public void onHighScoreClicked(HighScore highScore) {
        if (mapFragment != null) {
            mapFragment.zoomToLocation(highScore.getLatitude(), highScore.getLongitude());
        }
    }
}