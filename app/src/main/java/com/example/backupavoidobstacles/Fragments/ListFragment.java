package com.example.backupavoidobstacles.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.backupavoidobstacles.R;
import com.example.backupavoidobstacles.Adapters.HighScoreAdapter;
import com.example.backupavoidobstacles.Models.HighScore;
import com.example.backupavoidobstacles.utilities.SharedPreferencesManager;
import java.util.List;

public class ListFragment extends Fragment {

    private RecyclerView recyclerView;
    private HighScoreClickListener listener;

    public interface HighScoreClickListener {
        void onHighScoreClicked(HighScore highScore);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_high_scores);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getActivity() instanceof HighScoreClickListener) {
            listener = (HighScoreClickListener) getActivity();
        }

        loadHighScores();
        return view;
    }

    private void loadHighScores() {
        List<HighScore> highScores = SharedPreferencesManager.getInstance(getContext()).getHighScores();
        HighScoreAdapter adapter = new HighScoreAdapter(highScores, new HighScoreAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(HighScore highScore) {
                if (listener != null) {
                    listener.onHighScoreClicked(highScore);
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }
}
