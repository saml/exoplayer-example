package saml.exoplayerexample;

import android.net.Uri;
import android.os.Bundle;
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
    private DebugTextViewHelper debugTextViewHelper;
    private SimpleExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        debugTextView = findViewById(R.id.debug_text_view);
        debugTextView.append("\n");
        debugTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        debugTextView.setVisibility(View.VISIBLE);
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
            player.addMetadataOutput(new TimedMetadataHandler(debugTextView));
            player.addAnalyticsListener(new AnalyticsHandler(debugTextView, player));

            playerView.setPlayer(player);;
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
            debugTextViewHelper = null;
            player = null;
        }
    }
}
