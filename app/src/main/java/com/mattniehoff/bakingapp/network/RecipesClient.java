package com.mattniehoff.bakingapp.network;

import com.mattniehoff.bakingapp.model.RecipesResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RecipesClient {
    @GET("baking.json")
    Call<List<RecipesResponse>> getRecipesList();
}
