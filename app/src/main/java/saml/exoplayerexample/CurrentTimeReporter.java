package saml.exoplayerexample;

import android.os.Handler;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class CurrentTimeReporter implements Runnable {
    final static private Timeline.Window WINDOW = new Timeline.Window();
    final static private Timeline.Period PERIOD = new Timeline.Period();

    private final TextView textView;
    private final SimpleExoPlayer player;
    private final Handler handler;
    private final long intervalMs;
    private String prevPosition = "";

    public CurrentTimeReporter(
            final TextView textView,
            final SimpleExoPlayer player,
            final Handler handler,
            final long intervalMs) {
        this.textView = textView;
        this.player = player;
        this.handler = handler;
        this.intervalMs = intervalMs;
    }

    public void start() {
        handler.postDelayed(this, this.intervalMs);
    }

    public void stop() {
        handler.removeCallbacks(this);
    }

    private static String toDurationString(long millis) {
        return millis != C.TIME_UNSET ? Duration.ofMillis(millis).toString() : "";
    }

    @Override
    public void run() {
        final int windowIndex = player.getCurrentWindowIndex();
        final int periodIndex = player.getCurrentPeriodIndex();
        final Timeline timeline = player.getCurrentTimeline();
        final Timeline.Window window = timeline.getWindow(windowIndex, new Timeline.Window());
        final Timeline.Period period = timeline.getPeriod(periodIndex, new Timeline.Period());

        // All are in milliseconds
        final long positionInWindow = player.getCurrentPosition();
        final long windowDuration = window.getDurationMs();
        final long windowOffset = window.getPositionInFirstPeriodMs();
        final long windowPTS = window.presentationStartTimeMs;
        final long windowStart = window.windowStartTimeMs;
        final long periodDuration = period.getDurationMs();
        final long periodOffset = period.getPositionInWindowMs();

        String position = "";
        if (windowStart != C.TIME_UNSET) {
            // EXT-X-PROGRAM-DATE-TIME exists for the first segment in the manifest.
            position = ZonedDateTime.ofInstant(Instant.ofEpochMilli(windowStart + positionInWindow), ZoneOffset.UTC).toString();
        } else if (windowPTS != C.TIME_UNSET) {
            // Could not come up with HLS manifest that would set presentationStartTimeMs.
            position = "(pts)" + Duration.ofMillis(windowPTS + positionInWindow).toString();
        } else {
            // No start time is given for the window.
            // But, for HLS, it looks like window is sliding while period stays still.
            // window.getPositionInFirstPeriodMs() increases and period.getPositionInWindowMs() decreases the same amount.
            // So, current position is just elapsed time from when the player joins the livestream.
            position = "(elapsed)" + Duration.ofMillis(windowOffset + positionInWindow).toString();
        }

        if (!prevPosition.equals(position)) {
            this.textView.append(
                    String.format("pos=%s pdur=%s wdur=%s poff=%s woff=%s\n",
                            position,
                            toDurationString(periodDuration),
                            toDurationString(windowDuration),
                            toDurationString(periodOffset),
                            toDurationString(windowOffset)));
        }
        prevPosition = position;

        // recurse
        this.handler.postDelayed(this, this.intervalMs);
    }
}
