package com.mattniehoff.bakingapp.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mattniehoff.bakingapp.R;
import com.mattniehoff.bakingapp.model.Ingredient;

import java.util.List;

public class IngredientListAdapter extends RecyclerView.Adapter<IngredientListAdapter.ViewHolder> {
    private List<Ingredient> data;

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView ingredientNameTextView;
        public TextView ingredientMeasureTextView;
        public TextView ingredientQuantityTextView;

        public ViewHolder(View view){
            super(view);
            ingredientNameTextView = view.findViewById(R.id.ingredient_list_name_textview);
            ingredientMeasureTextView = view.findViewById(R.id.ingredient_list_measure_textview);
            ingredientQuantityTextView = view.findViewById(R.id.ingredient_list_quantity_textview);
        }
    }

    public IngredientListAdapter(List<Ingredient> ingredients) {
        this.data = ingredients;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.ingredient_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Ingredient ingredient = data.get(position);
        viewHolder.ingredientNameTextView.setText(ingredient.getIngredient());
        viewHolder.ingredientMeasureTextView.setText(ingredient.getMeasure());
        viewHolder.ingredientQuantityTextView.setText(String.valueOf(ingredient.getQuantity()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateData(List<Ingredient> data) {
        this.data = data;
        notifyDataSetChanged();
    }
}
