package com.mattniehoff.bakingapp;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientListWidget extends AppWidgetProvider {

    public static final String sharedPrefFile = "com.mattniehoff.bakingapp";
    public static final String RECIPE_KEY = "recipe";
    public static final String INGREDIENTS_KEY = "ingredients";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Get ingredients from SharedPreferences and update.
        SharedPreferences preferences = context.getSharedPreferences(sharedPrefFile, 0);
        String recipeName = preferences.getString(RECIPE_KEY, context.getString(R.string.recipe_name_default));
        String ingredients = preferences.getString(INGREDIENTS_KEY, context.getString(R.string.ingredients_list_default));

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredient_list_widget);
        views.setTextViewText(R.id.appwidget_recipe_label, recipeName);
        views.setTextViewText(R.id.appwidget_ingredients_list, ingredients);


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
}

