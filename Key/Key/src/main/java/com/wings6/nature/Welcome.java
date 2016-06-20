package com.wings6.nature;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;


public class Welcome extends Activity {

    private static final String SHARE_APP_TAG = "首次启动 启动";
    public MediaPlayer mediaPlayer, mediaPlayer1;

    private ImageView img;

    /**
     * 定义消息处理与发送对象
     */
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            // 处理消息
            if (msg.what == 110) {
                // 创建Intent意图对象
                Intent intent = new Intent();
                // 设置需要启动窗口
                intent.setClass(Welcome.this, musicActivity.class);
                // 跳转到主窗口
                startActivity(intent);
            }
            return true;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        SharedPreferences setting = getSharedPreferences(SHARE_APP_TAG, 0);
        Boolean user_first = setting.getBoolean("FIRST", true);
        if (user_first) {//第一次
            setting.edit().putBoolean("FIRST", false).commit();
            Log.d("Welcome", "第一次");
            img = (ImageView) findViewById(R.id.img);
            img(img);
//        设定动画播放时长
            handler.sendEmptyMessageDelayed(110, 3000);
            musics();
        } else {
            Log.d("Welcome", "不是第一次");
            Intent intent;
            intent = new Intent();
            intent.setClass(Welcome.this, musicActivity.class);
            startActivity(intent);
        }


    }


    //    设定动画
    private void img(ImageView img) {
        AnimationDrawable drawable = (AnimationDrawable) img.getDrawable();
        drawable.start();
    }


    //    动画播放完毕后 自动finish掉窗口
    @Override
    protected void onPause() {
        super.onPause();
        // 关闭该窗口
        this.finish();
    }


    protected void musics() {
        mediaPlayer = MediaPlayer.create(this, R.raw.click);

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setLooping(false);

        mediaPlayer1 = MediaPlayer.create(this, R.raw.xx);
        mediaPlayer1.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer1.setLooping(false);
        mediaPlayer1.start();

    }


}

