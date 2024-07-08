package com.example.backupavoidobstacles.utilities;

import android.content.Context;
import android.media.MediaPlayer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SoundPlayer {

    private Context context;
    private MediaPlayer mediaPlayer;
    private ExecutorService executorService;

    public SoundPlayer(Context context) {
        this.context = context;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void playSound(int soundResId) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                stopSound();
                mediaPlayer = MediaPlayer.create(context, soundResId);
                if (mediaPlayer != null) {
                    mediaPlayer.start();
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            stopSound();
                        }
                    });
                }
            }
        });
    }

    public void stopSound() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void release() {
        stopSound();
        executorService.shutdown();
    }
}
