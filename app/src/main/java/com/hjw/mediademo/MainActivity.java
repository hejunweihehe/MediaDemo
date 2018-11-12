package com.hjw.mediademo;

import android.content.ComponentName;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    private boolean mIsPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mMediaBrowserConnectionCallback = new MediaBrowserConnectionCallback();
        mMediaBrowserSubscriptionCallback = new MediaBrowserSubscriptionCallback();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("hjw_test", "onItemClick position = " + position);
                mMediaController.getTransportControls().skipToQueueItem(position);
                if (mIsPlaying) {
                    mMediaController.getTransportControls().pause();
                } else {
                    mMediaController.getTransportControls().play();
                }
            }
        });
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
                            new ComponentName("com.example.android.mediasession", "com.example.android.mediasession.service.MusicService"),
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
                mMediaController.registerCallback(new MediaBrowserListener());
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
            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1);
            //children是从onLoadChildren发送过来的音频数据
            for (final MediaBrowserCompat.MediaItem mediaItem : children) {
                //MediaSession.Callback对应的addQueueItem方法会被调用
                mMediaController.addQueueItem(mediaItem.getDescription());
                adapter.add(mediaItem.toString());
            }
            list.setAdapter(adapter);

            // Call prepare now so pressing play just works.
            //MediaSession.Callback对应的prepare方法会被调用
            mMediaController.getTransportControls().prepare();
        }
    }

    /**
     * Implementation of the {@link MediaControllerCompat.Callback} methods we're interested in.
     * <p>
     * Here would also be where one could override
     * {@code onQueueChanged(List<MediaSessionCompat.QueueItem> queue)} to get informed when items
     * are added or removed from the queue. We don't do this here in order to keep the UI
     * simple.
     */
    private class MediaBrowserListener extends MediaControllerCompat.Callback {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat playbackState) {
            Log.d("hjw_test", "onPlaybackStateChanged");
            mIsPlaying = playbackState != null &&
                    playbackState.getState() == PlaybackStateCompat.STATE_PLAYING;
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat mediaMetadata) {
            Log.d("hjw_test", "onMetadataChanged");
        }

        @Override
        public void onSessionDestroyed() {
            Log.d("hjw_test", "onSessionDestroyed");
            super.onSessionDestroyed();
        }

        @Override
        public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
            Log.d("hjw_test", "onQueueChanged");
            super.onQueueChanged(queue);
        }
    }
}
