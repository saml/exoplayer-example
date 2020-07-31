package saml.exoplayerexample;

import android.widget.TextView;

import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataOutput;

public class TimedMetadataHandler implements MetadataOutput {
    final private TextView textView;
    public TimedMetadataHandler(final TextView textView) {
        this.textView = textView;
    }
    @Override
    public void onMetadata(Metadata metadata) {
        textView.setText(metadata.toString());
    }
}
