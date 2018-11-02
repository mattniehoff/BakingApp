package com.mattniehoff.bakingapp.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

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

    private TextView instructionTextView;

    private SimpleExoPlayer player;
    private SimpleExoPlayerView playerView;
    private AspectRatioFrameLayout playerFrameLayout;
    private ImageView thumbnailImageView;

    // Boolean to track if our View exists.
    private Boolean isViewInitialized = false;

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

        if (savedInstanceState == null) {
            if (getArguments().containsKey(RECIPE_ARGUMENT)) {
                recipe = getArguments().getParcelable(RECIPE_ARGUMENT);
            }

            if (getArguments().containsKey(STEP_ARGUMENT)) {
                stepIndex = getArguments().getInt(STEP_ARGUMENT, 0);
                setStep(stepIndex);
            }
        } else {
            if (savedInstanceState.containsKey(RECIPE_ARGUMENT)) {
                recipe = savedInstanceState.getParcelable(RECIPE_ARGUMENT);
            }

            if (savedInstanceState.containsKey(STEP_ARGUMENT)) {
                stepIndex = savedInstanceState.getInt(STEP_ARGUMENT, 0);
                setStep(stepIndex);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.step_detail, container, false);

        if (currentStep != null) {
            instructionTextView = rootView.findViewById(R.id.step_detail);
            initializeButtons(rootView);
            initializeMedia(rootView);
            isViewInitialized = true;
        }

        updateLayout();

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(this.RECIPE_ARGUMENT, recipe);
        outState.putInt(this.STEP_ARGUMENT, stepIndex);
    }

    private void initializeButtons(View rootView) {
        previousButton = rootView.findViewById(R.id.step_previous_button);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Decrement step and update layout
                if (stepIndex != 0) {
                    stepIndex--;
                    setStep(stepIndex);
                }
            }
        });


        nextButton = rootView.findViewById(R.id.step_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stepIndex < recipe.getSteps().size()) {
                    stepIndex++;
                    setStep(stepIndex);
                }
            }
        });
    }

    private void initializeMedia(View rootView) {
        playerView = rootView.findViewById(R.id.step_player_view);
        thumbnailImageView = rootView.findViewById(R.id.step_thumbnail_image_view);
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
    }

    private void initializePlayer(String videoUrl) {
        Uri videoUri = Uri.parse(videoUrl);

        // Release the player so it is null and recreated.
        releasePlayer();
        if (player == null) {
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
            player = null;
        }
    }

    private void setStep(Integer stepIndex) {
        if (stepIndex < recipe.getSteps().size()) {
            currentStep = recipe.getSteps().get(stepIndex);
            onStepChanged();
        } else {
            Log.e(TAG, stepIndex + " is out of bounds. Setting to first step.");
            currentStep = recipe.getSteps().get(0);
        }
    }

    private void onStepChanged() {
        // If view isn't initialized, setting media, text, etc. will be on null objects
        if (isViewInitialized) {
            updateLayout();
        }
    }

    private void updateLayout() {
        // Populate the step description.
        instructionTextView.setText(currentStep.getDescription());

        // Populate media - Show video, else, show thumbnail, else hide all
        releasePlayer();
        if (!currentStep.getVideoURL().equals("")) {
            playerView.setVisibility(View.VISIBLE);
            initializePlayer(currentStep.getVideoURL());
            thumbnailImageView.setVisibility(View.GONE);
        } else if (!currentStep.getThumbnailURL().equals("") &&
                !isMp4Extension(currentStep.getThumbnailURL())) {
            // Remove player view
            playerView.setVisibility(View.GONE);

            // Set thumbnail
            Picasso.get().load(currentStep.getThumbnailURL()).into(thumbnailImageView);
            thumbnailImageView.setVisibility(View.VISIBLE);
        } else {
            // Default to placeholder
            playerView.setVisibility(View.GONE);
            thumbnailImageView.setImageResource(R.drawable.ic_bread_image);
            thumbnailImageView.setVisibility(View.VISIBLE);
        }
    }

    // https://stackoverflow.com/a/8955087/2107568
    private boolean isMp4Extension(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
        return extension.equals("mp4");
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
