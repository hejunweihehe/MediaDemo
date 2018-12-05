package com.hjw.mediademo;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.btn_has)
    Button mPlayBtn;
    @BindView(R.id.btn_no)
    Button btn_no;
    @BindView(R.id.btn_image)
    Button mImageBtn;
    @BindView(R.id.txt_uri)
    TextView txt_uri;
    @BindView(R.id.btn_play)
    Button btn_play;
    Uri uri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.the_coldest_shoulder);
//                mediaPlayer.start(); // no need to call prepare(); create() does that for you

//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                Intent intent = new Intent();
                intent.setAction("test.com.test.act");
                intent.setType("image/*");
//                Intent intent = new Intent(Intent.ACTION_SEND);
                startActivityForResult(intent, 1);
//                startService(intent);
            }
        });

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("test.com.test.act");
                startActivityForResult(intent, 1);
            }
        });

        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                startActivityForResult(intent, 1);
            }
        });

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uri != null) {
                    try {
                        MediaPlayer mediaPlayer = new MediaPlayer();
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaPlayer.setDataSource(getApplicationContext(), uri);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            uri = data.getData();
            if (uri != null) {
                Log.d("test", "Uri = " + uri.toString());
                txt_uri.setText(uri.toString());
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
