package com.example.nutrisnap.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.nutrisnap.R;
import com.example.nutrisnap.data.model.Meal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private List<Meal> meals = new ArrayList<>();

    public void setMeals(List<Meal> meals) {
        this.meals = meals;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meal, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Meal meal = meals.get(position);
        holder.tvFoodName.setText(meal.foodName);
        holder.tvCalories.setText(meal.calories + " cal");
        holder.tvProtein.setText(String.format(Locale.getDefault(), "P: %.1fg", meal.protein));
        holder.tvCarbs.setText(String.format(Locale.getDefault(), "C: %.1fg", meal.carbs));
        holder.tvFat.setText(String.format(Locale.getDefault(), "F: %.1fg", meal.fat));

        // Format time
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
            Date date = inputFormat.parse(meal.consumedAt);
            if (date != null) {
                holder.tvMealTime.setText(outputFormat.format(date));
            }
        } catch (Exception e) {
            holder.tvMealTime.setText(meal.consumedAt);
        }
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    static class MealViewHolder extends RecyclerView.ViewHolder {
        TextView tvFoodName, tvCalories, tvMealTime, tvProtein, tvCarbs, tvFat;

        MealViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvCalories = itemView.findViewById(R.id.tvCalories);
            tvMealTime = itemView.findViewById(R.id.tvMealTime);
            tvProtein = itemView.findViewById(R.id.tvProtein);
            tvCarbs = itemView.findViewById(R.id.tvCarbs);
            tvFat = itemView.findViewById(R.id.tvFat);
        }
    }
}
