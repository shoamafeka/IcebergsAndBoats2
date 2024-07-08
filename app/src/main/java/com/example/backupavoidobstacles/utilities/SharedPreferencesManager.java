package com.example.backupavoidobstacles.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.backupavoidobstacles.Models.HighScore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class SharedPreferencesManager {

    private static final String PREFS_NAME = "high_scores_prefs";
    private static final String HIGH_SCORES_KEY = "high_scores";

    private static SharedPreferencesManager instance;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    private SharedPreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static SharedPreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesManager(context);
        }
        return instance;
    }

    public List<HighScore> getHighScores() {
        String json = sharedPreferences.getString(HIGH_SCORES_KEY, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<HighScore>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void saveHighScore(HighScore highScore) {
        List<HighScore> highScores = getHighScores();
        highScores.add(highScore);
        Collections.sort(highScores, (o1, o2) -> Integer.compare(o2.getScore(), o1.getScore()));

        if (highScores.size() > 10) {
            highScores = highScores.subList(0, 10);
        }

        String json = gson.toJson(highScores);
        sharedPreferences.edit().putString(HIGH_SCORES_KEY, json).apply();
    }
}
