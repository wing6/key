package com.wings6.nature;

import java.util.List;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.wings6.nature.MusicLoader.MusicInfo;
import com.wings6.nature.NatureService.NatureBinder;
import com.wings6.update.UpdateManager;

public class musicActivity extends Activity implements OnClickListener {

    public static final String TAG = "com.wings6.nature.MAIN_ACTIVITY";

    protected NatureBinder natureBinder;

    private ListView lvSongs;
    private SeekBar pbDuration;

    private TextView tvCurrentMusic;

    private List<MusicInfo> musicList;
    private int currentMusic; // The music that is playing.
    private int currentPosition; //The position of the music is playing.
    private int currentMax;

    private Button btnStartStop;
    private Button btnNext;
    private Button btnDetail;


    private ProgressReceiver progressReceiver;


    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            natureBinder = (NatureBinder) service;
        }
    };

    private void connectToNatureService() {
        Intent intent = new Intent(musicActivity.this, NatureService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_main);

        MusicLoader musicLoader = MusicLoader.instance(getContentResolver());
        musicList = musicLoader.getMusicList();
        connectToNatureService();
        initComponents();

        //				 调用更新方法 进行操作   记得添加权限
        UpdateManager manager = new UpdateManager(musicActivity.this);
        // 检查软件更新
        manager.parsing();
//        测试有没有监测到手机广播 然后进行操作
        cs();
    }

    //接受来电去电广播 然后进行 音乐操作  暂停 或者继续播放
    public void cs() {
        TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE); // 获取系统服务
        telManager.listen(new MobliePhoneStateListener(),
                PhoneStateListener.LISTEN_CALL_STATE);
    }

    private class MobliePhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE: // 挂机状态
//                    Toast.makeText(musicActivity.this, "挂机状态", Toast.LENGTH_SHORT).show();
                    if (natureBinder != null) {
                        if (natureBinder.isPlaying()) {
                            natureBinder.stopPlay();
                        } else {
                            natureBinder.startPlay(currentMusic, 0);
                        }
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:    //通话状态
//                    Toast.makeText(musicActivity.this, "通话状态", Toast.LENGTH_SHORT).show();
                    natureBinder.stopPlay();
                    break;
                case TelephonyManager.CALL_STATE_RINGING:    //响铃状态

                    if (natureBinder != null) {
                        if (natureBinder.isPlaying()) {
                            natureBinder.stopPlay();
                        } else {
                            natureBinder.startPlay(currentMusic, 0);
                        }
                    }
//                    Toast.makeText(musicActivity.this, "响铃状态", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }


    public void onResume() {
        super.onResume();
        registerReceiver();
        if (natureBinder != null) {
            if (natureBinder.isPlaying()) {
                btnStartStop.setBackgroundResource(R.drawable.pause);
            } else {
                btnStartStop.setBackgroundResource(R.drawable.play);
            }
            natureBinder.notifyActivity();
        }
    }

    //	后台时回调
    public void onPause() {
        super.onPause();
        unregisterReceiver(progressReceiver);


    }

    public void tz() {
        natureBinder.stopPlay();
    }

    //	停止时回调
    public void onStop() {
        super.onStop();
    }

    public void onDestroy() {
        super.onDestroy();
        if (natureBinder != null) {
            unbindService(serviceConnection);
        }
    }

    private void initComponents() {
        pbDuration = (SeekBar) findViewById(R.id.pbDuration);
        pbDuration.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                if (fromUser) {
                    natureBinder.changeProgress(progress);
                }
            }
        });

        tvCurrentMusic = (TextView) findViewById(R.id.tvCurrentMusic);

        btnStartStop = (Button) findViewById(R.id.btnStartStop);
        btnStartStop.setOnClickListener(this);

        btnNext = (Button) findViewById(R.id.btnNext);
        btnNext.setOnClickListener(this);

        btnDetail = (Button) findViewById(R.id.btnDetail);
        btnDetail.setOnClickListener(this);

        MusicAdapter adapter = new MusicAdapter();
        lvSongs = (ListView) findViewById(R.id.lvSongs);
        lvSongs.setAdapter(adapter);
        lvSongs.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                currentMusic = position;
                natureBinder.startPlay(currentMusic, 0);
                if (natureBinder.isPlaying()) {
                    btnStartStop.setBackgroundResource(R.drawable.pause);
                }
            }
        });
    }

    private void registerReceiver() {
        progressReceiver = new ProgressReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NatureService.ACTION_UPDATE_PROGRESS);
        intentFilter.addAction(NatureService.ACTION_UPDATE_DURATION);
        intentFilter.addAction(NatureService.ACTION_UPDATE_CURRENT_MUSIC);
        registerReceiver(progressReceiver, intentFilter);
    }

    class MusicAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return musicList.size();
        }

        @Override
        public Object getItem(int position) {
            return musicList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return musicList.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(musicActivity.this).inflate(R.layout.music_item, null);
                ImageView pImageView = (ImageView) convertView.findViewById(R.id.albumPhoto);
                TextView pTitle = (TextView) convertView.findViewById(R.id.title);
                TextView pDuration = (TextView) convertView.findViewById(R.id.duration);
                TextView pArtist = (TextView) convertView.findViewById(R.id.artist);
                viewHolder = new ViewHolder(pImageView, pTitle, pDuration, pArtist);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.imageView.setImageResource(R.drawable.audio);
            viewHolder.title.setText(musicList.get(position).getTitle());
            viewHolder.duration.setText(FormatHelper.formatDuration(musicList.get(position).getDuration()));
            viewHolder.artist.setText(musicList.get(position).getArtist());

            return convertView;
        }
    }

    class ViewHolder {
        public ViewHolder(ImageView pImageView, TextView pTitle, TextView pDuration, TextView pArtist) {
            imageView = pImageView;
            title = pTitle;
            duration = pDuration;
            artist = pArtist;
        }

        ImageView imageView;
        TextView title;
        TextView duration;
        TextView artist;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStartStop:
                play(currentMusic, R.id.btnStartStop);
                break;
            case R.id.btnNext:
                natureBinder.toNext();
                break;

            case R.id.btnDetail:
                Intent intent = new Intent(musicActivity.this, DetailActivity.class);
                intent.putExtra(DetailActivity.MUSIC_LENGTH, currentMax);
                intent.putExtra(DetailActivity.CURRENT_MUSIC, currentMusic);
                intent.putExtra(DetailActivity.CURRENT_POSITION, currentPosition);
                startActivity(intent);
                break;
        }
    }

    public void TvcurrentMusic(View v) {
        switch (v.getId()) {
            case R.id.tvCurrentMusic1:
                Intent intent1 = new Intent(musicActivity.this, DetailActivity.class);
                intent1.putExtra(DetailActivity.MUSIC_LENGTH, currentMax);
                intent1.putExtra(DetailActivity.CURRENT_MUSIC, currentMusic);
                intent1.putExtra(DetailActivity.CURRENT_POSITION, currentPosition);
                startActivity(intent1);
                break;
        }
    }


    private void play(int position, int resId) {
        if (natureBinder.isPlaying()) {
            natureBinder.stopPlay();
            btnStartStop.setBackgroundResource(R.drawable.play);
        } else {
            natureBinder.startPlay(position, currentPosition);
            btnStartStop.setBackgroundResource(R.drawable.pause);
        }
    }


    public class ProgressReceiver extends BroadcastReceiver {

        final String TAG1 = "PhoneStatReceiver";
        boolean incomingFlag = false;
        String incoming_number = null;


        @Override
        public void onReceive(Context context, Intent intent) {


            String action = intent.getAction();
            if (NatureService.ACTION_UPDATE_PROGRESS.equals(action)) {
                int progress = intent.getIntExtra(NatureService.ACTION_UPDATE_PROGRESS, 0);
                if (progress > 0) {
                    currentPosition = progress; // Remember the current position
                    pbDuration.setProgress(progress / 1000);
                }
            } else if (NatureService.ACTION_UPDATE_CURRENT_MUSIC.equals(action)) {
                //Retrive the current music and get the title to show on top of the screen.
                currentMusic = intent.getIntExtra(NatureService.ACTION_UPDATE_CURRENT_MUSIC, 0);
                tvCurrentMusic.setText(musicList.get(currentMusic).getTitle());
            } else if (NatureService.ACTION_UPDATE_DURATION.equals(action)) {
                //Receive the duration and show under the progress bar
                //Why do this ? because from the ContentResolver, the duration is zero.
                currentMax = intent.getIntExtra(NatureService.ACTION_UPDATE_DURATION, 0);
                int max = currentMax / 1000;
                pbDuration.setMax(currentMax / 1000);
            }


        }


    }

}
