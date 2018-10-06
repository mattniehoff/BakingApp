package com.mattniehoff.bakingapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.mattniehoff.bakingapp.R;
import com.mattniehoff.bakingapp.adapters.RecipeListAdapter;
import com.mattniehoff.bakingapp.model.RecipesResponse;
import com.mattniehoff.bakingapp.network.NetworkUtils;
import com.mattniehoff.bakingapp.network.RecipesClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
        implements RecipeListAdapter.ListItemClickListener {

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
        adapter = new RecipeListAdapter(this, new ArrayList<RecipesResponse>(0), this);
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
        Call<List<RecipesResponse>> call = client.getRecipesList();
        call.enqueue(new Callback<List<RecipesResponse>>() {
            @Override
            public void onResponse(Call<List<RecipesResponse>> call, Response<List<RecipesResponse>> response) {
                if (response.isSuccessful()) {
                    adapter.updateData(response.body());
                } else {
                    // TODO: Add logging
                    //   Log.e(TAG, response.message());
                }
            }

            @Override
            public void onFailure(Call<List<RecipesResponse>> call, Throwable t) {
                //TODO; Log here and probably display something Log.i("flag", "failure");
            }
        });
    }

    @Override
    public void onListItemClick(RecipesResponse recipesResponse) {
        // TODO: Open Start RecipeActivity

    }
}
