package com.mattniehoff.bakingapp.activities;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.mattniehoff.bakingapp.IngredientListWidget;
import com.mattniehoff.bakingapp.R;
import com.mattniehoff.bakingapp.adapters.RecipeListAdapter;
import com.mattniehoff.bakingapp.model.Recipe;
import com.mattniehoff.bakingapp.network.NetworkUtils;
import com.mattniehoff.bakingapp.network.RecipesClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.mattniehoff.bakingapp.IngredientListWidget.sharedPrefFile;

public class MainActivity extends AppCompatActivity
        implements RecipeListAdapter.ListItemClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private RecipeListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Recycler View
        RecyclerView recyclerView = findViewById(R.id.recipe_list_recyclerview);
        recyclerView.setHasFixedSize(true);

        // Layout Manager
        GridLayoutManager layoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.recipe_list_grid_columns));
        recyclerView.setLayoutManager(layoutManager);

        // Adapter
        adapter = new RecipeListAdapter(this, new ArrayList<Recipe>(0), this);
        recyclerView.setAdapter(adapter);

        // Populate RecyclerView
        populateRecipes();
    }

    private void populateRecipes() {
        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NetworkUtils.RECIPE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create client
        RecipesClient client = retrofit.create(RecipesClient.class);

        // Make the call -
        Call<List<Recipe>> call = client.getRecipesList();
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful()) {
                    adapter.updateData(response.body());
                } else {
                    Log.e(TAG, response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Log.e(TAG, "Failed in call to recipe list.");
            }
        });
    }

    @Override
    public void onListItemClick(Recipe recipe) {
        // when we choose a new recipe, update the widget text.
        updateWidgetText(recipe);

        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra(RecipeActivity.RECIPE_EXTRA, recipe);
        startActivity(intent);
    }

    private void updateWidgetText(Recipe recipe) {

        SharedPreferences preferences = this.getSharedPreferences(sharedPrefFile, 0);

        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putString(IngredientListWidget.RECIPE_KEY, recipe.getName());
        prefEditor.putString(IngredientListWidget.INGREDIENTS_KEY, recipe.getIngredientsString());
        prefEditor.apply();

        // See https://stackoverflow.com/a/7738687/2107568 for triggering update
        Intent intent = new Intent(this, IngredientListWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        int[] ids = AppWidgetManager.getInstance(getApplication())
                .getAppWidgetIds(new ComponentName(getApplication(), IngredientListWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }
}
