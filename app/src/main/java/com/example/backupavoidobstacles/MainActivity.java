package com.example.backupavoidobstacles;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.backupavoidobstacles.GameManager.GameManager;
import com.example.backupavoidobstacles.Models.HighScore;
import com.example.backupavoidobstacles.utilities.MoveDetector;
import com.example.backupavoidobstacles.utilities.SharedPreferencesManager;
import com.example.backupavoidobstacles.utilities.SoundPlayer;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.example.backupavoidobstacles.GameManager.GameManager;
import com.example.backupavoidobstacles.utilities.MoveDetector;
import com.example.backupavoidobstacles.utilities.SoundPlayer;
import com.example.backupavoidobstacles.utilities.SharedPreferencesManager;
import com.example.backupavoidobstacles.Models.HighScore;


public class MainActivity extends AppCompatActivity {

    private final int ROWS = 6;
    private final int COLS = 5;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private LinearLayoutCompat gameArea;
    private ExtendedFloatingActionButton leftButton, rightButton;
    private TextView scoreTextView;
    private Handler handler = new Handler();
    private ImageView[][] obstacleGrid = new ImageView[ROWS][COLS];
    private ImageView[][] dollarGrid = new ImageView[ROWS][COLS];
    private ImageView[] boatImages = new ImageView[COLS];
    private AppCompatImageView[] hearts = new AppCompatImageView[3];
    private GameManager gameManager;
    private SoundPlayer soundPlayer;
    private MoveDetector moveDetector;
    private String mode;

    private LocationManager locationManager;
    private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;
    private boolean isFast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        mode = intent.getStringExtra("control_type");
        isFast = "buttons_fast".equals(mode);

        findViews();
        initGridAndBoats();

        soundPlayer = new SoundPlayer(this);
        gameManager = new GameManager(this, handler, new Runnable() {
            @Override
            public void run() {
                updateUI();
            }
        }, soundPlayer, R.raw.boom, isFast);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            requestLocationUpdates();
        }

        if ("buttons_slow".equals(mode) || "buttons_fast".equals(mode)) {
            leftButton.setVisibility(View.VISIBLE);
            rightButton.setVisibility(View.VISIBLE);
            leftButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gameManager.moveBoat(-1);
                    updateUI();
                }
            });

            rightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gameManager.moveBoat(1);
                    updateUI();
                }
            });
        } else if ("sensor".equals(mode)) {
            leftButton.setVisibility(View.INVISIBLE);
            rightButton.setVisibility(View.INVISIBLE);

            moveDetector = new MoveDetector(this, new MoveDetector.MoveCallback() {
                @Override
                public void onMoveLeft() {
                    gameManager.moveBoat(-1);
                    updateUI();
                }

                @Override
                public void onMoveRight() {
                    gameManager.moveBoat(1);
                    updateUI();
                }
            });
        }
    }

    private void requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}

                @Override
                public void onProviderEnabled(@NonNull String provider) {}

                @Override
                public void onProviderDisabled(@NonNull String provider) {}
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        gameManager.resetGame();
        gameManager.startGame();

        if (moveDetector != null) {
            moveDetector.register();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameManager.stopGame();
        soundPlayer.release();

        if (moveDetector != null) {
            moveDetector.unregister();
        }
    }

    private void initGridAndBoats() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int obstacleResId = getResources().getIdentifier("obstacle_" + row + col, "id", getPackageName());
                obstacleGrid[row][col] = findViewById(obstacleResId);
                int dollarResId = getResources().getIdentifier("coin_" + row + col, "id", getPackageName());
                dollarGrid[row][col] = findViewById(dollarResId);
            }
        }

        for (int col = 0; col < COLS; col++) {
            int resId = getResources().getIdentifier("boat_" + col, "id", getPackageName());
            boatImages[col] = findViewById(resId);
        }
    }

    private void findViews() {
        gameArea = findViewById(R.id.game_area);
        leftButton = findViewById(R.id.main_LBL_left);
        rightButton = findViewById(R.id.main_LBL_right);
        scoreTextView = findViewById(R.id.main_LBL_score);
        hearts = new AppCompatImageView[]{
                findViewById(R.id.main_IMG_heart1),
                findViewById(R.id.main_IMG_heart2),
                findViewById(R.id.main_IMG_heart3)
        };
    }

    private void updateUI() {
        int[][] obstacleMatrix = gameManager.getObstacleMatrix();
        int[][] dollarMatrix = gameManager.getDollarMatrix();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (obstacleMatrix[row][col] == 1) {
                    obstacleGrid[row][col].setVisibility(View.VISIBLE);
                } else {
                    obstacleGrid[row][col].setVisibility(View.INVISIBLE);
                }

                if (dollarMatrix[row][col] == 1) {
                    dollarGrid[row][col].setVisibility(View.VISIBLE);
                } else {
                    dollarGrid[row][col].setVisibility(View.INVISIBLE);
                }
            }
        }

        int[] boatLogicArray = gameManager.getBoatLogicArray();
        for (int col = 0; col < COLS; col++) {
            if (boatLogicArray[col] == 1) {
                boatImages[col].setVisibility(View.VISIBLE);
            } else {
                boatImages[col].setVisibility(View.INVISIBLE);
            }
        }

        int lives = gameManager.getLives();
        for (int i = 0; i < hearts.length; i++) {
            if (i < lives) {
                hearts[i].setVisibility(View.VISIBLE);
            } else {
                hearts[i].setVisibility(View.INVISIBLE);
            }
        }

        int score = gameManager.getScore();
        scoreTextView.setText(String.format("%03d", score));

        // If game is over, save score and navigate to high scores screen
        if (lives <= 0) {
            SharedPreferencesManager.getInstance(this).saveHighScore(new HighScore(score, currentLatitude, currentLongitude));
            Intent intent = new Intent(this, HighScoresActivity.class);
            startActivity(intent);
            finish();
        }
    }
}

