package saml.exoplayerexample;

import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.source.MediaSourceEventListener;

public class AnalyticsHandler implements AnalyticsListener {
    final private TextView textView;
    final private SimpleExoPlayer player;
    final static private Timeline.Window WINDOW = new Timeline.Window();
    final static private Timeline.Period PERIOD = new Timeline.Period();

    public AnalyticsHandler(final TextView textView, final SimpleExoPlayer player) {
        this.textView = textView;
        this.player = player;
    }

    //    @Override
    public void _onLoadCompleted(
            EventTime eventTime,
            MediaSourceEventListener.LoadEventInfo loadEventInfo,
            MediaSourceEventListener.MediaLoadData mediaLoadData) {
        String metadata = "";
        if (mediaLoadData.trackFormat != null && mediaLoadData.trackFormat.metadata != null) {
            metadata = mediaLoadData.trackFormat.metadata.toString();
        }
        long start = 0;
        long end = 0;
        if (mediaLoadData.mediaStartTimeMs != C.TIME_UNSET) {
            start = mediaLoadData.mediaStartTimeMs / 1000;
            end = mediaLoadData.mediaEndTimeMs / 1000;
        }
        textView.append(String.format("dataType=%s trackType=%s format=%s start=%s end=%s\n",
                mediaLoadData.dataType,
                mediaLoadData.trackType,
                metadata,
                start,
                end));

    }

    //    @Override
    public void yoloonTimelineChanged(EventTime eventTime, int reason) {
        final int windowIndex = player.getCurrentWindowIndex();
        final int periodIndex = player.getCurrentPeriodIndex();
        final Timeline timeline = player.getCurrentTimeline();
        final Timeline.Window window = timeline.getWindow(windowIndex, WINDOW);
        final Timeline.Period period = timeline.getPeriod(periodIndex, PERIOD);

        long start = 0;
        if (window.presentationStartTimeMs != C.TIME_UNSET) {
            start = window.presentationStartTimeMs;
        } else if (window.windowStartTimeMs != C.TIME_UNSET) {
            start = window.windowStartTimeMs;
        } else {
            start = -period.getPositionInWindowMs() / 1000;
        }

        long duration = 0;
        if (period.getDurationMs() != C.TIME_UNSET) {
            duration = period.getDurationMs() / 1000;
        }

        final long position = (player.getCurrentPosition() - period.getPositionInWindowMs()) / 1000;

        textView.append(String.format("start=%s duration=%s position=%s window=%s period=%s reason=%s\n",
                start,
                duration,
                position,
                windowIndex,
                periodIndex,
                reason));
    }
}
