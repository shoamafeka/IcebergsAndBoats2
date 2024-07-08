package com.example.backupavoidobstacles.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.backupavoidobstacles.R;
import com.example.backupavoidobstacles.Models.HighScore;
import java.util.List;



public class HighScoreAdapter extends RecyclerView.Adapter<HighScoreAdapter.HighScoreViewHolder> {

    private List<HighScore> highScores;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(HighScore highScore);
    }

    public HighScoreAdapter(List<HighScore> highScores, OnItemClickListener listener) {
        this.highScores = highScores;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HighScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_high_score, parent, false);
        return new HighScoreViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HighScoreViewHolder holder, int position) {
        HighScore highScore = highScores.get(position);
        holder.scoreTextView.setText(String.valueOf(highScore.getScore()));
        holder.itemView.setOnClickListener(v -> listener.onItemClick(highScore));
    }

    @Override
    public int getItemCount() {
        return highScores.size();
    }

    static class HighScoreViewHolder extends RecyclerView.ViewHolder {
        TextView scoreTextView;

        HighScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            scoreTextView = itemView.findViewById(R.id.high_score_text);
        }
    }
}
