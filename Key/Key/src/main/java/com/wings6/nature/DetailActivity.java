package com.wings6.nature;

import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.wings6.nature.MusicLoader.MusicInfo;
import com.wings6.nature.NatureService.NatureBinder;

public class  DetailActivity extends Activity implements OnClickListener{

	private ImageView imageView;

	private static final String TAG = "com.example.natrue.DetailActivity";
	
	public static final String MUSIC_LENGTH = "com.wings6.nature.DetailActivity.MUSIC_LENGTH";
	public static final String CURRENT_POSITION = "com.wings6.nature.DetailActivity.CURRENT_POSITION";
	public static final String CURRENT_MUSIC = "com.wings6.nature.DetailActivity.CURRENT_MUSIC";

	private SeekBar pbDuration;
	private TextView tvTitle,tvTimeElapsed, tvDuration;
	private List<MusicInfo> musicList;
	private int currentMusic;
	
	private int currentPosition;
	
	private ProgressReceiver progressReceiver;	
	
	private  NatureBinder natureBinder;
	
	private int[] btnResIds = new int[] {
			R.id.btnMode,
			R.id.btnPrevious, 
			R.id.btnStartStop, 			
			R.id.btnNext,
			R.id.btnExit 
	};
	
	private ServiceConnection serviceConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			natureBinder = (NatureBinder) service;	
			if(natureBinder.isPlaying()){
				CustomAudioIcon btnStartStop = (CustomAudioIcon)findViewById(R.id.btnStartStop);
				btnStartStop.setFlagStart(false);		
			}
			CustomAudioIcon btnMode = (CustomAudioIcon)findViewById(R.id.btnMode);
			btnMode.setCurrentMode(natureBinder.getCurrentMode());
		}
	};
	
	private void connectToNatureService(){		
		Intent intent = new Intent(DetailActivity.this, NatureService.class);				
		bindService(intent, serviceConnection, BIND_AUTO_CREATE);				
	}
		
	
	@Override	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.push_right_in,R.anim.hold);
		MusicLoader musicLoader = MusicLoader.instance(getContentResolver());		
		musicList = musicLoader.getMusicList();	
		setContentView(R.layout.detail_layout);			
		connectToNatureService();
		initComponents();


		imageView= (ImageView) findViewById(R.id.tvLyric);
		Random(6);


	}

//随机数
	private void Random(int i) {
		int ran1 ;
		Random ran =new Random(System.currentTimeMillis());
		ran1 = ran.nextInt(i);
		if(ran1 == 1 ){
			Resources res = getResources();
			Drawable drawable = res.getDrawable(R.drawable.y1);
			imageView.setImageDrawable(drawable);
		}else if (ran1 == 2){
			Resources res = getResources();
			Drawable drawable = res.getDrawable(R.drawable.y2);
			imageView.setImageDrawable(drawable);
		}else if(ran1 ==3){
			Resources res = getResources();
			Drawable drawable = res.getDrawable(R.drawable.y3);
			imageView.setImageDrawable(drawable);
		}else if (ran1 ==4){
			Resources res = getResources();
			Drawable drawable = res.getDrawable(R.drawable.y4);
			imageView.setImageDrawable(drawable);
		}else if (ran1 == 5){
			Resources res = getResources();
			Drawable drawable = res.getDrawable(R.drawable.y5);
			imageView.setImageDrawable(drawable);

		}else if (ran1 == 6){
			Resources res = getResources();
			Drawable drawable = res.getDrawable(R.drawable.y11);
			imageView.setImageDrawable(drawable);
		}else {
			Resources res = getResources();
			Drawable drawable = res.getDrawable(R.drawable.y11);
			imageView.setImageDrawable(drawable);
		}

	}
	
	@Override
	public void onResume(){
		super.onResume();				
		initReceiver();		
	}
	
	@Override
	public void onPause(){
		super.onPause();		
		unregisterReceiver(progressReceiver);
		overridePendingTransition(R.anim.hold, R.anim.push_right_out);
	}
		
	public void onStop(){
		super.onStop();		
	}
	
	public void onDestroy(){
		super.onDestroy();
		if(natureBinder != null){
			unbindService(serviceConnection);
		}
	}	

	private void initComponents(){		
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		currentMusic = getIntent().getIntExtra(CURRENT_MUSIC,0);
		tvTitle.setText(musicList.get(currentMusic).getTitle());	
		
		tvDuration = (TextView) findViewById(R.id.tvDuration);
		int max = getIntent().getIntExtra(MUSIC_LENGTH, 0);
		tvDuration.setText(FormatHelper.formatDuration(max));
		
		pbDuration = (SeekBar) findViewById(R.id.pbDuration);
		pbDuration.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {		
				if(fromUser){
					natureBinder.changeProgress(progress);
				}
			}
		});
		pbDuration.setMax(max/1000);
					
		currentPosition = getIntent().getIntExtra(CURRENT_POSITION,0);
		pbDuration.setProgress(currentPosition / 1000);
		
		tvTimeElapsed = (TextView) findViewById(R.id.tvTimeElapsed);
		tvTimeElapsed.setText(FormatHelper.formatDuration(currentPosition));
				
		for(int resId : btnResIds){
			CustomAudioIcon icon = (CustomAudioIcon)findViewById(resId);
			icon.setOnClickListener(this);
		}				
	}	
	
	private void initReceiver(){
		progressReceiver = new ProgressReceiver();	
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(NatureService.ACTION_UPDATE_PROGRESS);
		intentFilter.addAction(NatureService.ACTION_UPDATE_DURATION);
		intentFilter.addAction(NatureService.ACTION_UPDATE_CURRENT_MUSIC);
		registerReceiver(progressReceiver, intentFilter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {		
		case R.id.btnStartStop:		
			play(currentMusic,R.id.btnStartStop);
			break;		
		case R.id.btnNext:
			natureBinder.toNext();
			break;
		case R.id.btnPrevious:
			natureBinder.toPrevious();
			break;
		case R.id.btnExit:	
			finish();
			break;
		case R.id.btnMode:						
			natureBinder.changeMode();
			break;
		default:
			break;
		}			
	}

	public void play(int currentMusic, int resId){
		CustomAudioIcon btnStartStop = (CustomAudioIcon) findViewById(resId);
		if(btnStartStop.isStartStatus()){
			natureBinder.stopPlay();
		}else{
			natureBinder.startPlay(currentMusic,currentPosition);
		}
	}

	
public 	class ProgressReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(NatureService.ACTION_UPDATE_PROGRESS.equals(action)){
				int progress = intent.getIntExtra(NatureService.ACTION_UPDATE_PROGRESS, currentPosition);				
				if(progress > 0){
					currentPosition = progress; // Remember the current position
					tvTimeElapsed.setText(FormatHelper.formatDuration(progress));
					pbDuration.setProgress(progress / 1000);
				}
			}else if(NatureService.ACTION_UPDATE_CURRENT_MUSIC.equals(action)){
				//Retrieve the current music and get the title to show on top of the screen.
				currentMusic = intent.getIntExtra(NatureService.ACTION_UPDATE_CURRENT_MUSIC, 0);					
				tvTitle.setText(musicList.get(currentMusic).getTitle());
			}else if(NatureService.ACTION_UPDATE_DURATION.equals(action)){
				//Receive the duration and show under the progress bar
				//Why do this ? because from the ContentResolver, the duration is zero.
				int duration = intent.getIntExtra(NatureService.ACTION_UPDATE_DURATION, 0);
				tvDuration.setText(FormatHelper.formatDuration(duration));
				pbDuration.setMax(duration / 1000);						
			}
		}
		
	}
}