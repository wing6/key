package com.wings6.nature;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Random;


public class MainActivity extends Activity {

    public MediaPlayer mediaPlayer;
    private boolean isStop = false;
    public MediaPlayer mediaPlayer1;

    private static final String TAG = "MainActivity";
    private SensorManager sm;
    private Sensor sensor;
    private Vibrator vibrator;

    private ImageView img ;

    private ImageView imageView;
    private AlphaAnimation alpha;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView img = (ImageView) findViewById(R.id.img);
        imageView = (ImageView) findViewById(R.id.imageView);


        music();
        shock();
        img(img);
    }

    private void img(ImageView img) {
        img.setVisibility(View.VISIBLE);
        AnimationDrawable drawable = (AnimationDrawable) img.getDrawable();
        drawable.start();
    }

    public void btnOnClick(View v) {
        switch (v.getId()) {
            case R.id.imageView:
                AlphaAnimation aa = (AlphaAnimation) AnimationUtils.loadAnimation(
                        MainActivity.this,
                        R.anim.anim_alpha);
                imageView.startAnimation(aa);
                aa.start();

                Random(10);
                break;
        }
    }
//随机数 参数内填入范围值
    private void Random(int i) {
        int ran1 ;
        Random ran =new Random(System.currentTimeMillis());
        ran1 = ran.nextInt(i);
        if(ran1 <= 2 ){
            Toast.makeText(this, "欢迎使用！" , Toast.LENGTH_SHORT).show();
        }else if (ran1 > 2 && ran1 <=5){
            Toast.makeText(this,"你好" , Toast.LENGTH_SHORT).show();
        }else if(ran1 > 5 && ran1 <=8){
            Toast.makeText(this,"=。=欢迎使用本播放器" , Toast.LENGTH_SHORT).show();
        }else if (ran1 > 8 && ran1 <=10){
            Toast.makeText(this,"反复点击有不通的对话", Toast.LENGTH_SHORT).show();
        }
    }

    public void music() {
        mediaPlayer = MediaPlayer.create(this, R.raw.click);

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setLooping(false);

        mediaPlayer1 = MediaPlayer.create(this, R.raw.xx);
        mediaPlayer1.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer1.setLooping(false);
        mediaPlayer1.start();

    }


    private void shock() {
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }


        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn1:
                   intent = new Intent();
                    intent.setClass(MainActivity.this, SecondActivity.class);
                    startActivity(intent);
                    overridePendingTransition(
                            R.anim.slide,
                            R.anim.slide);

                    break;

                case R.id.btn2:
                    intent = new Intent();
                  intent.setClass(MainActivity.this, musicActivity.class);
                    startActivity(intent);
                    overridePendingTransition(
                            R.anim.in_from_right,
                            R.anim.in_from_right);
                    Toast.makeText(this, "欢迎使用", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.btn3:
                    if (mediaPlayer1.isPlaying()) {
                        mediaPlayer1.pause();
                    } else if (!mediaPlayer1.isPlaying()) {
                        mediaPlayer1.start();
                    }

                    Toast.makeText(this, "请再次点击", Toast.LENGTH_SHORT).show();
                    break;


            }

        }



}


