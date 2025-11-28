package com.example.nutrisnap.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.nutrisnap.data.model.ChatMessage;
import com.example.nutrisnap.data.model.Exercise;
import com.example.nutrisnap.databinding.ItemChatMessageBinding;
import com.example.nutrisnap.databinding.ItemExerciseSelectableBinding;
import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_MESSAGE = 0;
    private static final int VIEW_TYPE_EXERCISES = 1;

    private final List<ChatMessage> messages;
    private List<Exercise> currentExercises;
    private final List<Exercise> selectedExercises = new ArrayList<>();
    private final OnExercisesSelectedListener listener;

    public interface OnExercisesSelectedListener {
        void onExercisesSelected(List<Exercise> exercises);
    }

    public ChatAdapter(List<ChatMessage> messages, OnExercisesSelectedListener listener) {
        this.messages = messages;
        this.listener = listener;
    }

    public void setCurrentExercises(List<Exercise> exercises) {
        this.currentExercises = exercises;
        selectedExercises.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == messages.size() - 1 && currentExercises != null && !currentExercises.isEmpty()) {
            return VIEW_TYPE_EXERCISES;
        }
        return VIEW_TYPE_MESSAGE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_EXERCISES) {
            ItemExerciseSelectableBinding binding = ItemExerciseSelectableBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new ExerciseViewHolder(binding);
        } else {
            ItemChatMessageBinding binding = ItemChatMessageBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new MessageViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MessageViewHolder) {
            ((MessageViewHolder) holder).bind(messages.get(position));
        } else if (holder instanceof ExerciseViewHolder) {
            ((ExerciseViewHolder) holder).bind(currentExercises);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemChatMessageBinding binding;

        MessageViewHolder(ItemChatMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ChatMessage message) {
            binding.tvMessage.setText(message.message);

            if (message.isUser) {
                binding.cardMessage.setCardBackgroundColor(0xFF4CAF50);
                binding.tvMessage.setTextColor(0xFFFFFFFF);
            } else {
                binding.cardMessage.setCardBackgroundColor(0xFFEEEEEE);
                binding.tvMessage.setTextColor(0xFF000000);
            }
        }
    }

    class ExerciseViewHolder extends RecyclerView.ViewHolder {
        private final ItemExerciseSelectableBinding binding;

        ExerciseViewHolder(ItemExerciseSelectableBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(List<Exercise> exercises) {
            binding.llExercises.removeAllViews();
            selectedExercises.clear();

            for (Exercise exercise : exercises) {
                View exerciseView = LayoutInflater.from(binding.getRoot().getContext())
                        .inflate(com.example.nutrisnap.R.layout.item_exercise_card, binding.llExercises, false);

                // Find and populate the TextViews
                android.widget.TextView tvExerciseName = exerciseView
                        .findViewById(com.example.nutrisnap.R.id.tvExerciseName);
                android.widget.TextView tvExerciseDetails = exerciseView
                        .findViewById(com.example.nutrisnap.R.id.tvExerciseDetails);

                if (tvExerciseName != null && exercise.exerciseName != null) {
                    tvExerciseName.setText(exercise.exerciseName);
                }

                if (tvExerciseDetails != null) {
                    String details = String.format(java.util.Locale.US, "%d sets × %d reps",
                            exercise.sets != null ? exercise.sets : 0,
                            exercise.reps != null ? exercise.reps : 0);
                    if (exercise.duration != null && !exercise.duration.isEmpty()) {
                        details += " • " + exercise.duration;
                    }
                    tvExerciseDetails.setText(details);
                }

                // Set initial background to white
                exerciseView.setBackgroundColor(0xFFFFFFFF);

                exerciseView.setOnClickListener(v -> {
                    boolean isSelected = !v.isSelected();
                    v.setSelected(isSelected);

                    if (isSelected) {
                        selectedExercises.add(exercise);
                        v.setBackgroundColor(0xFFBBDEFB); // Light blue when selected
                    } else {
                        selectedExercises.remove(exercise);
                        v.setBackgroundColor(0xFFFFFFFF); // White when not selected
                    }
                    binding.btnSaveWorkout.setVisibility(selectedExercises.isEmpty() ? View.GONE : View.VISIBLE);
                });

                binding.llExercises.addView(exerciseView);
            }

            binding.btnSaveWorkout.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onExercisesSelected(new ArrayList<>(selectedExercises));
                }
            });
        }
    }
}
