package at.shockbytes.corey.fragment.dialog;

import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.List;

import at.shockbytes.corey.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author Martin Macheiner
 *         Date: 24.02.2017.
 */

public class WearMusicControlDialogFragment extends BottomSheetDialogFragment {


    public static WearMusicControlDialogFragment newInstance() {
        WearMusicControlDialogFragment fragment = new WearMusicControlDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @BindView(R.id.fragment_music_control_btn_play_pause)
    protected ImageButton imgbtnPlayPause;

    @BindView(R.id.fragment_music_control_btn_previous)
    protected ImageButton imgbtnPrevious;

    @BindView(R.id.fragment_music_control_btn_next)
    protected ImageButton imgbtnNext;

    private Unbinder unbinder;

    private BottomSheetBehavior.BottomSheetCallback behaviorCallback
            = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    private MediaController mediaController;

    private AudioManager audioManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //((WearCoreyApp) getActivity().getApplication()).getAppComponent().inject(this);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.dialogfragment_music_control, null);
        unbinder = ButterKnife.bind(this, contentView);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams layoutParams =
                (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(behaviorCallback);
            ((BottomSheetBehavior) behavior).setHideable(false);
        }

        setup();
    }

    @OnClick(R.id.fragment_music_control_btn_previous)
    protected void onClickMediaPrevious() {
        mediaController.getTransportControls().skipToPrevious();
    }

    @OnClick(R.id.fragment_music_control_btn_next)
    protected void onClickMediaNext() {
        mediaController.getTransportControls().skipToNext();
    }

    @OnClick(R.id.fragment_music_control_btn_play_pause)
    protected void onClickMediaStartPause() {

        int icon;
        // Deactivate music
        if (isMusicActive()) {
            icon = R.drawable.ic_music_play;
            mediaController.getTransportControls().pause();
        } else {
            // Activate music
            icon = R.drawable.ic_music_pause;
            mediaController.getTransportControls().play();
        }
        imgbtnPlayPause.setImageResource(icon);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    private void setup() {

        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);

        int icon = isMusicActive()
                ? R.drawable.ic_music_pause
                : R.drawable.ic_music_play;
        imgbtnPlayPause.setImageResource(icon);

        setupMediaController();
    }

    private void setupMediaController() {

        MediaSessionManager msm = (MediaSessionManager) getContext()
                .getSystemService(Context.MEDIA_SESSION_SERVICE);

        List<MediaController> controllers = msm.getActiveSessions(null);
        if (controllers.size() > 0) {
            mediaController = controllers.get(0);

        } else {
            Toast.makeText(getContext(), "Controller not available...",Toast.LENGTH_LONG).show();
            dismiss();
        }

    }

    private boolean isMusicActive() {
        return audioManager.isMusicActive();
    }


}
