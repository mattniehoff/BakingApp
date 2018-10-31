package com.mattniehoff.bakingapp.activities;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.rule.ActivityTestRule;

import com.mattniehoff.bakingapp.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    private IdlingResource idlingResource;

    @Before
    public void registerIdlingResource() {
        idlingResource = activityRule.getActivity().getIdlingResource();
        Espresso.registerIdlingResources(idlingResource);
    }

    @Test
    public void clickRecipeStep() {
        //
        onView(withId(R.id.recipe_list_recyclerview))
            .check(matches(isDisplayed()));

        // Scroll to position
        onView(withId(R.id.recipe_list_recyclerview))
                .perform(RecyclerViewActions.scrollToPosition(1));

        // Press the recipe in our main activity
        onView(withId(R.id.recipe_list_recyclerview))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        // Test that we've gone into an activity that inflates a layout with our
        // ingredients list recyclerview
        onView(withId(R.id.step_list))
                .check(matches(isDisplayed()));

        // Scroll to second element
        onView(withId(R.id.step_list))
                .perform(RecyclerViewActions.scrollToPosition(1));

        // Click
        onView(withId(R.id.step_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        // Verify we're showing stepdetail
        onView(withId(R.id.step_detail))
                .check(matches(isDisplayed()));
    }

    // https://stackoverflow.com/a/44344572/2107568
    @After
    public void unregisterIdlingResource() {
        if (idlingResource != null) {
            Espresso.unregisterIdlingResources(idlingResource);
        }
    }

}