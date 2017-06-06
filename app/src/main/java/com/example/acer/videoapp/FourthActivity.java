package com.example.acer.videoapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

public class FourthActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    public static final String API_KEY = "AIzaSyCjf4x-6Tpkz4gGS2Of1xVXD0lIt7u1ERI";
    public static String VIDEO_ID = "";
    Toolbar toolbar3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth);

        //setting title to toolbar
        toolbar3 = (Toolbar) findViewById(R.id.toolbar3);
        toolbar3.setTitleTextColor(getColor(R.color.white));

        //initializing youtube player view
        YouTubePlayerView youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player);
        youTubePlayerView.initialize(API_KEY, this);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            TextView textView = (TextView) findViewById(R.id.text);
            textView.setText(bundle.getString("name"));
            TextView textView1 = (TextView) findViewById(R.id.text1);
            textView1.setText(bundle.getString("engname"));
            TextView textView2 = (TextView) findViewById(R.id.text2);
            textView2.setText(bundle.getString("desc"));
            VIDEO_ID=bundle.getString("link");
        }

        //set back button on toolbar
        toolbar3.setNavigationIcon(R.drawable.back);
        toolbar3.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult result){
        Toast.makeText(this,"Failed to Initialize",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored){
        //add listeners to YouTubePlayer instance
        player.setPlayerStateChangeListener(playerStateChangeListener);
        player.setPlaybackEventListener(playbackEventListner);

        //start buffering
        if (!wasRestored){
            player.cueVideo(VIDEO_ID);
        }
    }

    private PlaybackEventListener playbackEventListner = new PlaybackEventListener() {
        @Override
        public void onPlaying() {

        }

        @Override
        public void onPaused() {

        }

        @Override
        public void onStopped() {

        }

        @Override
        public void onBuffering(boolean arg0) {

        }

        @Override
        public void onSeekTo(int arg0) {

        }
    };

    private PlayerStateChangeListener playerStateChangeListener = new PlayerStateChangeListener() {
        @Override
        public void onLoading() {

        }

        @Override
        public void onLoaded(String arg0) {

        }

        @Override
        public void onAdStarted() {

        }

        @Override
        public void onVideoStarted() {

        }

        @Override
        public void onVideoEnded() {

        }

        @Override
        public void onError(ErrorReason arg0) {

        }
    };

}
