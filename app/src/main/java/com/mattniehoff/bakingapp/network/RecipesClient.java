package com.mattniehoff.bakingapp.network;

import com.mattniehoff.bakingapp.model.RecipesResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RecipesClient {
    @GET()
    Call<RecipesResponse> getRecipes();
}
