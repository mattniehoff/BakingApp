package com.mattniehoff.bakingapp.fragments;

import android.app.Activity;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    public static final String STEP_ARGUMENT = "step_argument";

    /**
     * The step this fragment is displaying information about.
     */
    private Step step;

    private SimpleExoPlayer player;
    private SimpleExoPlayerView playerView;
    private AspectRatioFrameLayout playerFrameLayout;


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

//            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
//            if (appBarLayout != null) {
//                appBarLayout.setTitle(step.getShortDescription());
//            }
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

            if (step.getVideoURL().equals("")){
                playerView.setVisibility(View.GONE);
                playerFrameLayout.setVisibility(View.GONE);
            } else {
                playerFrameLayout.setVisibility(View.VISIBLE);
                playerView.setVisibility(View.VISIBLE);
                initializePlayer(Uri.parse(step.getVideoURL()));
            }
        }

        return rootView;
    }

    private void initializePlayer(Uri videoUri) {
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
        if (player != null){
            player.stop();
            player.release();
        }
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
