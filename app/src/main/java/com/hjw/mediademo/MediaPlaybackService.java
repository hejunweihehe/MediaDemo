package com.hjw.mediademo;

import android.content.ContentResolver;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class MediaPlaybackService extends MediaBrowserServiceCompat {
    static TreeMap<String, MediaMetadataCompat> music = new TreeMap<>();
    private MediaSessionCompat mSession;

    @Override
    public void onCreate() {
        super.onCreate();

        mSession = new MediaSessionCompat(this, "MediaPlaybackService");
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS);
        //设置SessionToken，不设置的话客户端无法连接
        setSessionToken(mSession.getSessionToken());
        createMediaMetadataCompat(
                "Jazz_In_Paris",
                "Jazz in Paris",
                "Media Right Productions",
                "Jazz & Blues",
                "Jazz",
                103,
                TimeUnit.SECONDS,
                "jazz_in_paris.mp3",
                R.drawable.album_jazz_blues,
                "album_jazz_blues");
        createMediaMetadataCompat(
                "The_Coldest_Shoulder",
                "The Coldest Shoulder",
                "The 126ers",
                "Youtube Audio Library Rock 2",
                "Rock",
                160,
                TimeUnit.SECONDS,
                "the_coldest_shoulder.mp3",
                R.drawable.album_youtube_audio_library_rock_2,
                "album_youtube_audio_library_rock_2");
    }

    /**
     * 如果返回null，就代表拒绝连接
     *
     * @param s
     * @param i
     * @param bundle
     * @return
     */
    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String s, int i, @Nullable Bundle bundle) {
        Log.d("hjw_test", "onGetRoot");
        return new BrowserRoot("root", null);
    }

    @Override
    public void onLoadChildren(@NonNull String s, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        Log.d("hjw_test", "onLoadChildren");
        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();
        for (MediaMetadataCompat metadata : music.values()) {
            mediaItems.add(
                    new MediaBrowserCompat.MediaItem(
                            metadata.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
        }
        result.sendResult(mediaItems);
    }

    private static void createMediaMetadataCompat(
            String mediaId,
            String title,
            String artist,
            String album,
            String genre,
            long duration,
            TimeUnit durationUnit,
            String musicFilename,
            int albumArtResId,
            String albumArtResName) {
        music.put(
                mediaId,
                new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION,
                                TimeUnit.MILLISECONDS.convert(duration, durationUnit))
                        .putString(MediaMetadataCompat.METADATA_KEY_GENRE, genre)
                        .putString(
                                MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
                                getAlbumArtUri(albumArtResName))
                        .putString(
                                MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI,
                                getAlbumArtUri(albumArtResName))
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                        .build());
    }

    private static String getAlbumArtUri(String albumArtResName) {
        return ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                BuildConfig.APPLICATION_ID + "/drawable/" + albumArtResName;
    }
}
