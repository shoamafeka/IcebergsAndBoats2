package com.example.backupavoidobstacles;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class MenuActivity extends AppCompatActivity {

    private MaterialButton btnPlayWithButtonsSlow;
    private MaterialButton btnPlayWithButtonsFast;
    private MaterialButton btnPlayWithSensor;
    private MaterialButton btnHighScores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnPlayWithButtonsSlow = findViewById(R.id.btn_play_with_buttons_slow);
        btnPlayWithButtonsFast = findViewById(R.id.btn_play_with_buttons_fast);
        btnPlayWithSensor = findViewById(R.id.btn_play_with_sensor);
        btnHighScores = findViewById(R.id.btn_high_scores);

        btnPlayWithButtonsSlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                intent.putExtra("control_type", "buttons_slow");
                startActivity(intent);
            }
        });

        btnPlayWithButtonsFast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                intent.putExtra("control_type", "buttons_fast");
                startActivity(intent);
            }
        });

        btnPlayWithSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                intent.putExtra("control_type", "sensor");
                startActivity(intent);
            }
        });

        btnHighScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, HighScoresActivity.class);
                startActivity(intent);
            }
        });
    }
}
