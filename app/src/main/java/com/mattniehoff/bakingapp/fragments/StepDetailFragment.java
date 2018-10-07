package com.mattniehoff.bakingapp.fragments;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mattniehoff.bakingapp.R;
import com.mattniehoff.bakingapp.activities.RecipeActivity;
import com.mattniehoff.bakingapp.activities.StepDetailActivity;
import com.mattniehoff.bakingapp.model.Step;

/**
 * A fragment representing a single Step detail screen.
 * This fragment is either contained in a {@link RecipeActivity}
 * in two-pane mode (on tablets) or a {@link StepDetailActivity}
 * on handsets.
 */
public class StepDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    //public static final String STEP_ID = "step_id";
    public static final String STEP_ARGUMENT = "step_argument";

    /**
     * The step this fragment is displaying information about.
     */
    private Step step;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StepDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(STEP_ARGUMENT)) {
            step = getArguments().getParcelable(STEP_ARGUMENT);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(step.getShortDescription());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.step_detail, container, false);

        // TODO: Add remaining recipe data: media/navigation
        // Show the step description as text in a TextView.
        if (step != null) {
            ((TextView) rootView.findViewById(R.id.step_detail)).setText(step.getDescription());
        }

        return rootView;
    }
}
