package com.example.backupavoidobstacles.GameManager;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;
import com.example.backupavoidobstacles.utilities.SoundPlayer;
import java.util.Random;

public class GameManager {

    private final int ROWS = 6;
    private final int COLS = 5;
    private int updateInterval = 2000; // default to 2 seconds

    private Context context;
    private Handler handler;
    private Random random;
    private int[][] obstacleMatrix;
    private int[][] dollarMatrix;
    private int[] boatLogicArray;
    private int boatPosition;
    private int lives;
    private int score;
    private int iteration;

    private boolean isGameRunning;
    private Runnable updateCallback;
    private SoundPlayer soundPlayer;
    private int collisionSoundResId;

    public GameManager(Context context, Handler handler, Runnable updateCallback, SoundPlayer soundPlayer, int collisionSoundResId, boolean isFast) {
        this.context = context;
        this.handler = handler;
        this.random = new Random();
        this.obstacleMatrix = new int[ROWS][COLS];
        this.dollarMatrix = new int[ROWS][COLS];
        this.boatLogicArray = new int[COLS];
        this.updateCallback = updateCallback;
        this.soundPlayer = soundPlayer;
        this.collisionSoundResId = collisionSoundResId;
        this.updateInterval = isFast ? 1000 : 2000; // fast mode updates every 1 second
        resetGame();
    }

    public void startGame() {
        if (!isGameRunning) {
            isGameRunning = true;
            handler.post(updateObstaclePosition);
        }
    }

    public void stopGame() {
        if (isGameRunning) {
            isGameRunning = false;
            handler.removeCallbacks(updateObstaclePosition);
        }
    }

    public void resetGame() {
        boatPosition = COLS / 2;
        lives = 3;
        score = 0;
        iteration = 0;
        isGameRunning = false;

        // Clear the logic matrix
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                obstacleMatrix[row][col] = 0;
                dollarMatrix[row][col] = 0;
            }
        }

        // Clear boat logic array
        for (int col = 0; col < COLS; col++) {
            boatLogicArray[col] = 0;
        }

        boatLogicArray[boatPosition] = 1;
    }

    public void moveBoat(int direction) {
        boatLogicArray[boatPosition] = 0;
        boatPosition += direction;
        if (boatPosition < 0) boatPosition = 0;
        if (boatPosition >= COLS) boatPosition = COLS - 1;
        boatLogicArray[boatPosition] = 1;
    }

    public int[][] getObstacleMatrix() {
        return obstacleMatrix;
    }

    public int[][] getDollarMatrix() {
        return dollarMatrix;
    }

    public int[] getBoatLogicArray() {
        return boatLogicArray;
    }

    public int getLives() {
        return lives;
    }

    public int getScore() {
        return score;
    }

    private Runnable updateObstaclePosition = new Runnable() {
        @Override
        public void run() {
            if (!isGameRunning) return;

            moveItemsDown();
            clearTopRow();
            if (iteration % 2 == 0) {
                addNewItems();
            }
            iteration++;
            checkCollisions();

            // Trigger the UI update in MainActivity
            updateCallback.run();

            handler.postDelayed(this, updateInterval);
        }
    };

    private void moveItemsDown() {
        for (int row = ROWS - 1; row > 0; row--) {
            for (int col = 0; col < COLS; col++) {
                obstacleMatrix[row][col] = obstacleMatrix[row - 1][col];
                dollarMatrix[row][col] = dollarMatrix[row - 1][col];
            }
        }
    }

    private void clearTopRow() {
        for (int col = 0; col < COLS; col++) {
            obstacleMatrix[0][col] = 0;
            dollarMatrix[0][col] = 0;
        }
    }

    private void addNewItems() {
        for (int col = 0; col < COLS; col++) {
            if (random.nextBoolean()) {
                obstacleMatrix[0][col] = random.nextInt(2); // 50% chance to add obstacle
            } else {
                dollarMatrix[0][col] = random.nextInt(2); // 50% chance to add dollar
            }
        }
    }

    private void checkCollisions() {
        for (int col = 0; col < COLS; col++) {
            if (obstacleMatrix[ROWS - 1][col] == 1 && boatLogicArray[col] == 1) {
                lives--;
                soundPlayer.playSound(collisionSoundResId);
                toastAndVibrate("Hit!");
                if (lives <= 0) {
                    stopGame();
                }
            }
            if (dollarMatrix[ROWS - 1][col] == 1 && boatLogicArray[col] == 1) {
                score++;
                toastAndVibrate("Dollar collected!");
            }
        }
    }

    private void toastAndVibrate(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        vibrate();
    }

    private void vibrate() {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(500);
        }
    }
}
