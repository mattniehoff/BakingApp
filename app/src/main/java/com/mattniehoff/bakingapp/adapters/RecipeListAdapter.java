package com.mattniehoff.bakingapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mattniehoff.bakingapp.R;
import com.mattniehoff.bakingapp.model.Recipe;

import java.util.List;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.ViewHolder> {
    private Context context;
    private List<Recipe> data;
    private ListItemClickListener clickListener;

    public interface ListItemClickListener {
        void onListItemClick(Recipe recipe);
    }

    class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        // TODO: define more elements as added to the card view
        public TextView recipeName;


        public ViewHolder(View view) {
            super(view);
            recipeName = view.findViewById(R.id.recipe_card_name_textView);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            Recipe recipe = data.get(clickedPosition);
            clickListener.onListItemClick(recipe);
        }
    }

    public RecipeListAdapter(Context context, List<Recipe> data, ListItemClickListener listener) {
        this.context = context;
        this.data = data;
        this.clickListener = listener;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recipe_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Recipe recipe = data.get(position);
        viewHolder.recipeName.setText(recipe.getName());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    // Provides a way to update the data held in the Adapter post-construction
    public void updateData(List<Recipe> data) {
        this.data = data;
        notifyDataSetChanged();
    }
}
