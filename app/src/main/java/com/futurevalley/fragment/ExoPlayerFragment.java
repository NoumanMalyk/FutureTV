package com.futurevalley.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.futurevalley.adapter.SubTitleAdapter;
import com.futurevalley.item.ItemPlayer;
import com.futurevalley.item.ItemSubTitle;
import com.futurevalley.futurestudio.R;
import com.futurevalley.util.Events;
import com.futurevalley.util.GlobalBus;
import com.futurevalley.util.RvOnClickListener;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.text.CaptionStyleCompat;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SubtitleView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import org.greenrobot.eventbus.Subscribe;

public class ExoPlayerFragment extends Fragment {
    private static final String TAG = "StreamPlayerActivity";
    private SimpleExoPlayer player;
    private DefaultBandwidthMeter BANDWIDTH_METER;
    private DataSource.Factory mediaDataSourceFactory;
    private ProgressBar progressBar;
    ImageView imgFull, imgSetting;
    public boolean isFullScr = false;
    Button btnTryAgain;
    SubtitleView subtitleView;
    private static final String streamUrl = "streamUrl";
    private static final String playerData = "playerData";
    ItemPlayer itemPlayer;
    int mSubPosition = 0, mQualityPosition = 0, mSubPreviousPosition = 0, mQualityPreviousPosition = 0;
    private long playerPosition;

    public static ExoPlayerFragment newInstance(String SId) {
        ExoPlayerFragment f = new ExoPlayerFragment();
        Bundle args = new Bundle();
        args.putString(streamUrl, SId);
        f.setArguments(args);
        return f;
    }

    public static ExoPlayerFragment newInstance(ItemPlayer itemPlayer) {
        ExoPlayerFragment f = new ExoPlayerFragment();
        Bundle args = new Bundle();
        args.putParcelable(playerData, itemPlayer);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_exo_player, container, false);
        GlobalBus.getBus().register(this);

        if (getArguments() != null) {
            itemPlayer = (ItemPlayer) getArguments().getParcelable(playerData);
        }
        progressBar = rootView.findViewById(R.id.progressBar);
        imgFull = rootView.findViewById(R.id.img_full_scr);
        imgSetting = rootView.findViewById(R.id.img_setting);
        btnTryAgain = rootView.findViewById(R.id.btn_try_again);
        BANDWIDTH_METER = new DefaultBandwidthMeter.Builder(requireActivity()).build();

        mediaDataSourceFactory = buildDataSourceFactory(true);
        player = new SimpleExoPlayer.Builder(requireActivity()).build();
        PlayerView playerView = rootView.findViewById(R.id.exoPlayerView);
        subtitleView = rootView.findViewById(R.id.exo_subtitles);
        playerView.setPlayer(player);
        playerView.setUseController(true);
        playerView.requestFocus();


        Uri uri = Uri.parse(getSelectedQuantityStreamUrl(mQualityPosition));

        MediaSource mediaSource = buildMediaSource(uri);
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);

        Typeface subtitleTypeface = Typeface.createFromAsset(requireActivity().getAssets(), "fonts/custom.ttf");
        subtitleView.setStyle(new CaptionStyleCompat(Color.WHITE, Color.TRANSPARENT, Color.TRANSPARENT, CaptionStyleCompat.EDGE_TYPE_NONE, Color.WHITE, subtitleTypeface));
        subtitleView.setFixedTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, int reason) {
                Log.d(TAG, "onTimelineChanged: ");
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.d(TAG, "onTracksChanged: " + trackGroups.length);
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                Log.d(TAG, "onLoadingChanged: " + isLoading);
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.d(TAG, "onPlayerStateChanged: " + playWhenReady);
                if (playbackState == PlaybackStateCompat.STATE_PLAYING) {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.e(TAG, "onPlayerError: ", error);
                player.stop();
                btnTryAgain.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                //    errorDialog();
            }

            @Override
            public void onPositionDiscontinuity(int reason) {
                Log.d(TAG, "onPositionDiscontinuity: true");
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });

        imgFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFullScr) {
                    isFullScr = false;
                    Events.FullScreen fullScreen = new Events.FullScreen();
                    fullScreen.setFullScreen(false);
                    GlobalBus.getBus().post(fullScreen);
                } else {
                    isFullScr = true;
                    Events.FullScreen fullScreen = new Events.FullScreen();
                    fullScreen.setFullScreen(true);
                    GlobalBus.getBus().post(fullScreen);
                }
            }
        });

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTryAgain.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                retryLoad();
            }
        });

        if (!itemPlayer.isQuality() && !itemPlayer.isSubTitle()) {
            imgSetting.setVisibility(View.GONE);
        }
        imgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingOpen();
            }
        });

        return rootView;
    }

    public void retryLoad() {
        mSubPosition = 0;
        mQualityPosition = 0;
        Uri uri = Uri.parse(getSelectedQuantityStreamUrl(mQualityPosition));
        MediaSource mediaSource = buildMediaSource(uri);
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);
    }

    private MediaSource buildMediaSource(Uri uri) {
        int type = Util.inferContentType(uri);
        switch (type) {
            case C.TYPE_SS:
                return new SsMediaSource.Factory(new DefaultSsChunkSource.Factory(mediaDataSourceFactory), buildDataSourceFactory(false)).createMediaSource(uri);
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(new DefaultDashChunkSource.Factory(mediaDataSourceFactory), buildDataSourceFactory(false)).createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ProgressiveMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    private MediaSource buildSubtitleMediaSource(Uri subtitleUri) {
        return new SingleSampleMediaSource.Factory(mediaDataSourceFactory).createMediaSource(subtitleUri, Format.createTextSampleFormat(null, MimeTypes.APPLICATION_SUBRIP, Format.NO_VALUE, "en", null),
                C.TIME_UNSET);
    }

    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    public DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(requireActivity(), bandwidthMeter,
                buildHttpDataSourceFactory(bandwidthMeter));
    }

    public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSourceFactory(Util.getUserAgent(requireActivity(), "ExoPlayerDemo"), bandwidthMeter);
    }

    @Subscribe
    public void getFullScreen(Events.FullScreen fullScreen) {
        isFullScr = fullScreen.isFullScreen();
        if (fullScreen.isFullScreen()) {
            imgFull.setImageResource(R.drawable.ic_fullscreen_exit);
        } else {
            imgFull.setImageResource(R.drawable.ic_fullscreen);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (player != null && player.getPlayWhenReady()) {
            player.setPlayWhenReady(false);
            player.getPlaybackState();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null && player.getPlayWhenReady()) {
            player.setPlayWhenReady(false);
            player.getPlaybackState();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player != null) {
            player.setPlayWhenReady(true);
            player.getPlaybackState();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GlobalBus.getBus().unregister(this);
        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop();
            player.release();
        }
    }

    private void settingOpen() {
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(requireActivity());
        View sheetView = requireActivity().getLayoutInflater().inflate(R.layout.layout_player_setting, null);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();

        LinearLayout lytQuality = sheetView.findViewById(R.id.lytQuality);
        LinearLayout lytSubTitles = sheetView.findViewById(R.id.lytSubTitles);
        MaterialButton btn480 = sheetView.findViewById(R.id.btn420p);
        MaterialButton btn720 = sheetView.findViewById(R.id.btn720p);
        MaterialButton btn1080 = sheetView.findViewById(R.id.btn1080p);
        MaterialButton btnOk = sheetView.findViewById(R.id.btnOk);
        MaterialButton btnCancel = sheetView.findViewById(R.id.btnCancel);
        RecyclerView rcSubtitle = sheetView.findViewById(R.id.rcSubtitle);

        MaterialButtonToggleGroup toggleGroup = sheetView.findViewById(R.id.toggleButton);
        toggleGroup.check(getSelectedQuantity());

        if (!itemPlayer.isQuality()) {
            lytQuality.setVisibility(View.GONE);
        }

        if (!itemPlayer.isSubTitle()) {
            lytSubTitles.setVisibility(View.GONE);
        }

        if (itemPlayer.getQuality480().isEmpty()) {
            btn480.setVisibility(View.GONE);
        }

        if (itemPlayer.getQuality720().isEmpty()) {
            btn720.setVisibility(View.GONE);
        }

        if (itemPlayer.getQuality1080().isEmpty()) {
            btn1080.setVisibility(View.GONE);
        }

        SubTitleAdapter subTitleAdapter = new SubTitleAdapter(requireActivity(), itemPlayer.getSubTitles());
        rcSubtitle.setAdapter(subTitleAdapter);
        subTitleAdapter.select(mSubPosition);

        subTitleAdapter.setOnItemClickListener(new RvOnClickListener() {
            @Override
            public void onItemClick(int position) {
                subTitleAdapter.select(position);
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.dismiss();
                playerPosition = player.getCurrentPosition();
                mSubPosition = subTitleAdapter.getSelect();
                mQualityPosition = setSelectedQuantity(toggleGroup.getCheckedButtonId());
                ItemSubTitle itemSubTitle = itemPlayer.getSubTitles().get(mSubPosition);
                boolean isSubTitleOff = itemSubTitle.getSubTitleId().equals("0");
                if (mSubPosition != mSubPreviousPosition || mQualityPosition != mQualityPreviousPosition) {
                    playWithSubtitleQuality(getSelectedQuantityStreamUrl(mQualityPosition), itemSubTitle.getSubTitleUrl(), isSubTitleOff);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.dismiss();
            }
        });
    }

    private void playWithSubtitleQuality(String qtyUrl, String subTitleUrl, boolean isSubTitleOff) {
        mSubPreviousPosition = mSubPosition;
        mQualityPreviousPosition = mQualityPosition;

        player.stop();
        progressBar.setVisibility(View.VISIBLE);
        Uri uri = Uri.parse(qtyUrl);
        Uri subTitleUri = Uri.parse(subTitleUrl);
        MediaSource mediaSource;
        if (isSubTitleOff) {
            mediaSource = buildMediaSource(uri);
        } else {
            mediaSource = new MergingMediaSource(buildMediaSource(uri), buildSubtitleMediaSource(subTitleUri));
        }
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);
        player.seekTo(playerPosition);
    }

    private int getSelectedQuantity() {
        int btnId;
        switch (mQualityPosition) {
            case 1:
                btnId = R.id.btn420p;
                break;
            case 2:
                btnId = R.id.btn720p;
                break;
            case 3:
                btnId = R.id.btn1080p;
                break;
            case 0:
            default:
                btnId = R.id.btnDefault;
                break;
        }
        return btnId;
    }

    private int setSelectedQuantity(int btnId) {
        int selectedId;
        switch (btnId) {
            case R.id.btn420p:
                selectedId = 1;
                break;
            case R.id.btn720p:
                selectedId = 2;
                break;
            case R.id.btn1080p:
                selectedId = 3;
                break;
            case R.id.btnDefault:
            default:
                selectedId = 0;
                break;
        }
        return selectedId;
    }

    private String getSelectedQuantityStreamUrl(int mQPosition) {
        String videoUrl;
        switch (mQPosition) {
            case 1:
                videoUrl = itemPlayer.getQuality480();
                break;
            case 2:
                videoUrl = itemPlayer.getQuality720();
                break;
            case 3:
                videoUrl = itemPlayer.getQuality1080();
                break;
            case 0:
            default:
                videoUrl = itemPlayer.getDefaultUrl();
                break;
        }
        return videoUrl;
    }
}
