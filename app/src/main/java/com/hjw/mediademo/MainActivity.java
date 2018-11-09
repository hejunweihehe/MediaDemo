package com.hjw.mediademo;

import android.content.ComponentName;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.list)
    protected ListView list;
    private MediaBrowserCompat mMediaBrowser;
    private MediaBrowserConnectionCallback mMediaBrowserConnectionCallback;
    private MediaControllerCompat mMediaController;
    private MediaBrowserSubscriptionCallback mMediaBrowserSubscriptionCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mMediaBrowserConnectionCallback = new MediaBrowserConnectionCallback();
        mMediaBrowserSubscriptionCallback = new MediaBrowserSubscriptionCallback();
    }

    @Override
    protected void onStart() {
        super.onStart();
        connectToService();
    }

    private void connectToService() {
        if (mMediaBrowser == null) {
            mMediaBrowser =
                    new MediaBrowserCompat(
                            this,
                            new ComponentName(this, MediaPlaybackService.class),
                            mMediaBrowserConnectionCallback,
                            null);
            mMediaBrowser.connect();
        }
        Log.d("hjw_test", "onStart: Creating MediaBrowser, and connecting");
    }

    private class MediaBrowserConnectionCallback extends MediaBrowserCompat.ConnectionCallback {

        // Happens as a result of onStart().
        @Override
        public void onConnected() {
            Log.d("hjw_test", "onConnected");
            try {
                // Get a MediaController for the MediaSession.
                mMediaController =
                        new MediaControllerCompat(MainActivity.this, mMediaBrowser.getSessionToken());
            } catch (RemoteException e) {
                Log.d("hjw_test", String.format("onConnected: Problem: %s", e.toString()));
                throw new RuntimeException(e);
            }

            mMediaBrowser.subscribe(mMediaBrowser.getRoot(), mMediaBrowserSubscriptionCallback);
        }
    }

    // Receives callbacks from the MediaBrowser when the MediaBrowserService has loaded new media
    // that is ready for playback.
    public class MediaBrowserSubscriptionCallback extends MediaBrowserCompat.SubscriptionCallback {

        @Override
        public void onChildrenLoaded(@NonNull String parentId,
                                     @NonNull List<MediaBrowserCompat.MediaItem> children) {
            Log.d("hjw_test", "onChildrenLoaded");
            //children是从onLoadChildren发送过来的音频数据
            // Queue up all media items for this simple sample.
            for (final MediaBrowserCompat.MediaItem mediaItem : children) {
                mMediaController.addQueueItem(mediaItem.getDescription());
            }

            // Call prepare now so pressing play just works.
            //TODO 不调用该方法会不会无法播放？
            mMediaController.getTransportControls().prepare();
        }
    }
}
