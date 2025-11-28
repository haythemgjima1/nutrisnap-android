package com.example.nutrisnap.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.nutrisnap.data.model.Exercise;
import com.example.nutrisnap.databinding.ItemExerciseBinding;
import java.util.List;
import java.util.Locale;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private final List<Exercise> exercises;
    private final OnExerciseCheckedListener listener;

    public interface OnExerciseCheckedListener {
        void onExerciseChecked(Exercise exercise, boolean isChecked);
    }

    public ExerciseAdapter(List<Exercise> exercises, OnExerciseCheckedListener listener) {
        this.exercises = exercises;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemExerciseBinding binding = ItemExerciseBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ExerciseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        holder.bind(exercises.get(position));
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    class ExerciseViewHolder extends RecyclerView.ViewHolder {
        private final ItemExerciseBinding binding;

        ExerciseViewHolder(ItemExerciseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Exercise exercise) {
            binding.tvExerciseName.setText(exercise.exerciseName);

            String details = String.format(Locale.US, "%d sets × %d reps",
                    exercise.sets != null ? exercise.sets : 0,
                    exercise.reps != null ? exercise.reps : 0);

            if (exercise.duration != null && !exercise.duration.isEmpty()) {
                details += " • " + exercise.duration;
            }

            binding.tvExerciseDetails.setText(details);
            binding.checkbox.setChecked(exercise.isCompleted != null && exercise.isCompleted);

            binding.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onExerciseChecked(exercise, isChecked);
                }
            });

            binding.getRoot().setOnClickListener(v -> {
                binding.checkbox.setChecked(!binding.checkbox.isChecked());
            });
        }
    }
}
