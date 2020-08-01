package saml.exoplayerexample;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.DebugTextViewHelper;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.util.Util;

import androidx.appcompat.app.AppCompatActivity;

public class PlayerActivity extends AppCompatActivity {

    private PlayerView playerView;
    private TextView debugTextView;
    private TextView customTextView;
    private DebugTextViewHelper debugTextViewHelper;
    private SimpleExoPlayer player;
    private CurrentTimeReporter currentTimeReporter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        debugTextView = findViewById(R.id.debug_text_view);
        debugTextView.setVisibility(View.VISIBLE);

        customTextView = findViewById(R.id.custom_text_view);
        customTextView.append("\n");
        customTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        customTextView.setVisibility(View.VISIBLE);

        playerView = findViewById(R.id.video_view);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void initializePlayer() {
        if (player == null) {
            final DefaultTrackSelector trackSelector = new DefaultTrackSelector(this);
            trackSelector.setParameters(trackSelector.buildUponParameters().setMaxVideoSizeSd());

            player = new SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector).build();
            player.addAnalyticsListener(new EventLogger(trackSelector));
            player.setPlayWhenReady(true);
            player.addMetadataOutput(new TimedMetadataHandler(customTextView));
            player.addAnalyticsListener(new AnalyticsHandler(customTextView, player));

            currentTimeReporter = new CurrentTimeReporter(customTextView, player, new Handler(), 4000);
            currentTimeReporter.start();

            debugTextViewHelper = new DebugTextViewHelper(player, debugTextView);
            debugTextViewHelper.start();

            playerView.setPlayer(player);

        }

        final DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "app-name"));
        final Uri uri = Uri.parse(getString(R.string.media_url_hls));
        final HlsMediaSource hlsMediaSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);

        player.prepare(hlsMediaSource);
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            debugTextViewHelper.stop();
            currentTimeReporter.stop();
            currentTimeReporter = null;
            debugTextViewHelper = null;
            player = null;
        }
    }
}
