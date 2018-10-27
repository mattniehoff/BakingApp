package com.mattniehoff.bakingapp.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.mattniehoff.bakingapp.R;
import com.mattniehoff.bakingapp.activities.RecipeActivity;
import com.mattniehoff.bakingapp.activities.StepDetailActivity;
import com.mattniehoff.bakingapp.model.Recipe;
import com.mattniehoff.bakingapp.model.Step;

/**
 * A fragment representing a single Step detail screen.
 * This fragment is either contained in a {@link RecipeActivity}
 * in two-pane mode (on tablets) or a {@link StepDetailActivity}
 * on handsets.
 */
public class StepDetailFragment extends Fragment {
    private static final String TAG = StepDetailFragment.class.getSimpleName();

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String STEP_ARGUMENT = "step_argument";
    public static final String RECIPE_ARGUMENT = "recipe_argument";

    /**
     * The currentStep this fragment is displaying information about.
     */
    private Recipe recipe;
    private Step currentStep;
    private Integer stepIndex;

    private SimpleExoPlayer player;
    private SimpleExoPlayerView playerView;
    private AspectRatioFrameLayout playerFrameLayout;

    // Navigation
    Button previousButton;
    Button nextButton;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StepDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments().containsKey(RECIPE_ARGUMENT)) {
            recipe = getArguments().getParcelable(RECIPE_ARGUMENT);
        }

        if (getArguments().containsKey(STEP_ARGUMENT)) {
            stepIndex = getArguments().getInt(STEP_ARGUMENT, 0);
            setStep(stepIndex);

            Activity activity = this.getActivity();

//            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
//            if (appBarLayout != null) {
//                appBarLayout.setTitle(currentStep.getShortDescription());
//            }
        }
    }

    private void setStep(Integer stepIndex){
        if (stepIndex < recipe.getSteps().size()) {
            currentStep = recipe.getSteps().get(stepIndex);
        } else {
            Log.e(TAG, stepIndex + " is out of bounds. Setting to first step.");
            currentStep = recipe.getSteps().get(0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.step_detail, container, false);

        // TODO: Add remaining recipe data: media/navigation
        // Show the currentStep description as text in a TextView.
        if (currentStep != null) {
            ((TextView) rootView.findViewById(R.id.step_detail)).setText(currentStep.getDescription());

            previousButton = rootView.findViewById(R.id.step_previous_button);
            nextButton = rootView.findViewById(R.id.step_next_button);

            playerView = rootView.findViewById(R.id.step_player_view);

            playerFrameLayout = rootView.findViewById(R.id.step_media_frame_layout);
            playerFrameLayout.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);

            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            float aspectRatio;
            if (displayMetrics.widthPixels > displayMetrics.heightPixels) {
                aspectRatio = ((float) displayMetrics.widthPixels / (float) displayMetrics.heightPixels);
            } else {
                aspectRatio = ((float) displayMetrics.heightPixels / (float) displayMetrics.widthPixels);
            }

            playerFrameLayout.setAspectRatio(aspectRatio);

            setMedia();
        }

        return rootView;
    }

    private void setMedia() {
        if (currentStep.getVideoURL().equals("")) {
            playerView.setVisibility(View.GONE);
            playerFrameLayout.setVisibility(View.GONE);
        } else {
            playerFrameLayout.setVisibility(View.VISIBLE);
            playerView.setVisibility(View.VISIBLE);
            initializePlayer(currentStep.getVideoURL());
        }
    }

    private void initializePlayer(String videoUrl) {
        if (player == null) {
            Uri videoUri = Uri.parse(videoUrl);
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            player = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
            playerView.setPlayer(player);

            // Prepare MediaSource
            MediaSource mediaSource = new ExtractorMediaSource(videoUri, new DefaultDataSourceFactory(
                    getActivity(), getString(R.string.app_name)), new DefaultExtractorsFactory(), null, null);


            player.prepare(mediaSource, true, false);
            player.setPlayWhenReady(true);
        }
    }

    private void releasePlayer() {
        if (player != null) {
            player.stop();
            player.release();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initializePlayer(currentStep.getVideoURL());
    }

    @Override
    public void onDestroy() {
        releasePlayer();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        releasePlayer();
        super.onStop();
    }
}
