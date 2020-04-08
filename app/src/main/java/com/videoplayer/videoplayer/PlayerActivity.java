package com.videoplayer.videoplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Player.EventListener;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.videoplayer.videoplayer.data.VideoInfo;
import com.videoplayer.videoplayer.data.VideoInfoDAO;
import com.videoplayer.videoplayer.data.VideoInfoDatabase;
import com.videoplayer.videoplayer.data.model.VideoResponseModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.videoplayer.videoplayer.VideoHomeAdapter.SELECTED_VIDEO_ID;
import static com.videoplayer.videoplayer.VideoHomeAdapter.TRACK_LIST;
import static com.videoplayer.videoplayer.VideoHomeAdapter.VIDEO_LIST_MASTER;

public class PlayerActivity extends AppCompatActivity implements Player.EventListener {
    private PlayerView playerView;
    private ImageView fullscreenButton;
    private SimpleExoPlayer player;
    private ArrayList<VideoResponseModel> videoResponseModelArrayList;
    private RecyclerView rvRelated;
    private TextView tvTitle, tvDesc, tvClearHistory;
    private HashMap<String, Integer> trackAndPositionMap;
    private String strSelectedVideoId;
    private NestedScrollView nvScroller;
    private VideoInfoDAO videoInfoDAO;
    boolean fullscreen = false;
    private static final int VIDEO_ENDED = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        VideoInfoDatabase videoInfoDatabase = VideoInfoDatabase.getVideoInfoDatabase(this);
        videoInfoDAO= videoInfoDatabase.videoInfoModel();
        init();
        initPlayerAndSetUI();
        onClicks();
    }
    @SuppressWarnings("unchecked")
    private void init() {
        rvRelated = findViewById(R.id.rvRelated);
        rvRelated.setLayoutManager(new LinearLayoutManager(this));
        playerView = findViewById(R.id.exo_player);
        tvTitle = findViewById(R.id.tvTitle);
        tvDesc = findViewById(R.id.tvDesc);
        nvScroller = findViewById(R.id.nvScroller);
        

        fullscreenButton = playerView.findViewById(R.id.exo_fullscreen_icon);

        Intent intent = getIntent();
        if (intent != null
                && intent.getSerializableExtra(VIDEO_LIST_MASTER) != null
                && intent.getSerializableExtra(SELECTED_VIDEO_ID) != null
                && intent.getSerializableExtra(TRACK_LIST) != null) {

            trackAndPositionMap = (HashMap<String, Integer>) intent.getSerializableExtra(TRACK_LIST);
            strSelectedVideoId = intent.getStringExtra(SELECTED_VIDEO_ID);
            videoResponseModelArrayList = (ArrayList<VideoResponseModel>) intent.getSerializableExtra(VIDEO_LIST_MASTER);
            player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
            playerView.setPlayer(player);
        } else {
            Toast.makeText(this, "Something went wrong, please try again.", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void initPlayerAndSetUI() {
        DefaultDataSourceFactory defaultDataSourceFactory =
                new DefaultDataSourceFactory(this, Util.getUserAgent(this, "video player"));

        ArrayList<ExtractorMediaSource> mediaSourceArrayList = new ArrayList<>();
        for (int i = 0; i < videoResponseModelArrayList.size(); i++) {

            VideoResponseModel videoResponseModel = videoResponseModelArrayList.get(i);

            ExtractorMediaSource extractorMediaSource = new ExtractorMediaSource.Factory(defaultDataSourceFactory)
                    .createMediaSource(Uri.parse(videoResponseModel.getUrl()));
            mediaSourceArrayList.add(extractorMediaSource);
        }
        int playlistSize = mediaSourceArrayList.size();
        ConcatenatingMediaSource concatenatingMediaSource = new
                ConcatenatingMediaSource(mediaSourceArrayList.toArray(new MediaSource[playlistSize]));
        VideoInfo videoInfo = videoInfoDAO.getVideoInfo(strSelectedVideoId);

        boolean haveStartPosition = false;
        if (videoInfo != null)
            haveStartPosition = !videoInfo.getIsEnded();
        if (haveStartPosition) {
            player.seekTo(0, videoInfo.getLastPositionMillis());
        }
        player.prepare(concatenatingMediaSource, !haveStartPosition, false);

        player.setPlayWhenReady(false);
        player.addListener(this);
        pos_setUI(strSelectedVideoId);

       // setUI(videoResponseModelSelected, tempArrayList);
    }

    private void setUI(VideoResponseModel playingVideoModel, ArrayList<VideoResponseModel> remainingArrModels) {
        tvTitle.setText(playingVideoModel.getTitle());
        tvDesc.setText(playingVideoModel.getDescription());
        rvRelated.setAdapter(new RecentListAdapter(this, remainingArrModels));
    }

    public  void  onItemSelected(String videoId) {
        VideoResponseModel videoResponseModelSelected = null;
        ArrayList<VideoResponseModel> tempArrayList = new ArrayList<>();
        for (int i = 0; i < videoResponseModelArrayList.size(); i++) {
            VideoResponseModel videoResponseModel = videoResponseModelArrayList.get(i);
            if (videoResponseModel.getId().equals(videoId)) {
                videoResponseModelSelected = videoResponseModel;
            } else {
                tempArrayList.add(videoResponseModel);
            }
        }

        VideoInfo videoInfo = videoInfoDAO.getVideoInfo(videoId);

        if (videoInfo != null)
            changeTrackOnSelect(trackAndPositionMap.get(videoId)
                    , videoResponseModelSelected
                    , tempArrayList
                    , videoInfo.getIsEnded()
                    , videoInfo.getLastPositionMillis());
        else changeTrackOnSelect(trackAndPositionMap.get(videoId)
                , videoResponseModelSelected
                , tempArrayList
                , false
                , -1);
    }

    private void changeTrackOnSelect(int position
            , VideoResponseModel videoResponseModel
            , ArrayList<VideoResponseModel> remainingArrModels
            , boolean isEnded
            , long seekTo) {
        int pos = player.getCurrentWindowIndex();
        long currentMillis = player.getCurrentPosition();
        tvTitle.setText(videoResponseModel.getTitle());
        tvDesc.setText(videoResponseModel.getDescription());
        player.seekTo(position, isEnded && seekTo < 0 ? C.TIME_UNSET : seekTo);

        player.setPlayWhenReady(true);
        rvRelated.setAdapter(new RecentListAdapter(this, remainingArrModels));

        rvRelated.scrollToPosition(0);
        nvScroller.fullScroll(View.FOCUS_UP);
        nvScroller.smoothScrollTo(0, 0);
        insertVideoInfo(pos, currentMillis, false);
    }

    private void insertVideoInfo(int position, long currentMillis, boolean isEnded) {
        String videoId = getKeyByValue(trackAndPositionMap, position);
        if (videoId!=null)
            videoInfoDAO.insertVideoInfo(new VideoInfo(videoId, currentMillis, isEnded));
    }
    private static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void onClicks() {
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandOnclick(tvDesc.getMaxLines());
            }
        });

        //fullscreen

        fullscreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fullscreen) {
                    fullscreenButton.setImageDrawable(ContextCompat.getDrawable(PlayerActivity.this, R.drawable.ic_fullscreen_open));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    if(getSupportActionBar() != null){
                        getSupportActionBar().show();
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) playerView.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = (int) ( 200 * getApplicationContext().getResources().getDisplayMetrics().density);
                    playerView.setLayoutParams(params);
                    fullscreen = false;
                }else{
                    fullscreenButton.setImageDrawable(ContextCompat.getDrawable(PlayerActivity.this, R.drawable.ic_fullscreen_close));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                            |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                    if(getSupportActionBar() != null){
                        getSupportActionBar().hide();
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) playerView.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = params.MATCH_PARENT;
                    playerView.setLayoutParams(params);
                    fullscreen = true;
                }
            }
        });


    }

    private void expandOnclick(int MAX_LINES) {

        if (MAX_LINES == 3)
            expandInfo();
        else collapseInfo();

    }

    private void collapseInfo() {
        tvDesc.setMaxLines(3);
    }

    private void expandInfo() {
        tvDesc.setMaxLines(Integer.MAX_VALUE);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {
        int trackPosition = player.getCurrentWindowIndex();
        if (reason == VIDEO_ENDED) {
           pos_setUI(getKeyByValue(trackAndPositionMap, trackPosition));
            insertVideoInfo(trackPosition, player.getCurrentPosition(), true);
        }

    }

    private void pos_setUI(String firstVideoId) {
        VideoResponseModel videoResponseModelSelected = null;
        ArrayList<VideoResponseModel> tempArrayList = new ArrayList<>();
        for (int i = 0; i < videoResponseModelArrayList.size(); i++) {
            VideoResponseModel videoResponseModel = videoResponseModelArrayList.get(i);
            if (videoResponseModel.getId().equals(firstVideoId)) {
                videoResponseModelSelected = videoResponseModel;
            } else {
                tempArrayList.add(videoResponseModel);
            }
        }

        setUI(videoResponseModelSelected, tempArrayList);
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }
    @Override
    protected void onPause() {
        super.onPause();
        player.getCurrentPosition();
        insertVideoInfo(player.getCurrentWindowIndex(), player.getCurrentPosition(), false);
        player.setPlayWhenReady(false);
    }

   /* @Override
    protected void onStop() {
        super.onStop();
        //playerView.setPlayer(null);
        player.release();
        player = null;
    }*/


   /* @Override
    protected void onDestroy() {
        super.onDestroy();

    }*/


    @Override
    protected void onStart() {
        super.onStart();
        player.getCurrentPosition();
        player.seekTo(0);
        insertVideoInfo(player.getCurrentWindowIndex(), player.getCurrentPosition(), false);
        player.setPlayWhenReady(false);
    }



}
