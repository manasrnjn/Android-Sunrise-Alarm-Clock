package com.manas.sunrisealarm.alert;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import com.manas.sunrisealarm.R;
import com.manas.sunrisealarm.alarm.Alarm;
import com.manas.sunrisealarm.database.Database;
import com.manas.sunrisealarm.service.AlarmServiceBroadcastReciever;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class AlarmFirstAlertActivity extends Activity implements OnClickListener {

	private Alarm alarm;
	private TextView title,time;
	private ImageView snooze_10,snooze_5,snooze_3,stop;
	private MediaPlayer mediaPlayer;
	private Vibrator vibrator;
	private boolean alarmActive;
	private static String origtime=null;
	MusicManager m;
	Timer timer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		
		final Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		     
	
		
		
		
		
		setContentView(R.layout.firstalert);
		
		Bundle bundle = this.getIntent().getExtras();
		alarm = (Alarm) bundle.getSerializable("alarm");
		m=new MusicManager();
		m.maxVolume=(Integer.valueOf(alarm.getVolume().substring(0, alarm.getVolume().length()-1)))/100f;
		 
		m.fadeSpeed=m.maxVolume/(Integer.valueOf(alarm.getfadein())*2);
		
		
		if(origtime==null)
		origtime=alarm.getAlarmTimeString();
		

		
		title=(TextView)findViewById(R.id.title);
		time=(TextView)findViewById(R.id.time);
		
		snooze_10=(ImageView) findViewById(R.id.snooze_10);
		snooze_5=(ImageView) findViewById(R.id.snooze_5);
		snooze_3=(ImageView) findViewById(R.id.snooze_3);
		stop=(ImageView) findViewById(R.id.stop);
		
		
	title.setText(alarm.getAlarmName());
      time.setText(alarm.getAlarmTimeString());
      
      
      snooze_10.setOnClickListener(this);
      snooze_5.setOnClickListener(this);
      snooze_3.setOnClickListener(this);
      stop.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			alarmActive = false;
			if (vibrator != null)
				vibrator.cancel();
			try {
				mediaPlayer.stop();
			} catch (IllegalStateException ise) {

			}
			try {
				mediaPlayer.release();
			} catch (Exception e) {

			}
			
			if(origtime!=null)
			{	Database.init(getApplicationContext());
				Database.snooze(alarm, origtime);
			Intent mathAlarmServiceIntent = new Intent(AlarmFirstAlertActivity.this,
					AlarmServiceBroadcastReciever.class);
			sendBroadcast(mathAlarmServiceIntent, null);
			origtime=null;
			}
			timer.cancel();
			AlarmFirstAlertActivity.this.finish();
		}
	});
	
      TelephonyManager telephonyManager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);

		PhoneStateListener phoneStateListener = new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				switch (state) {
				case TelephonyManager.CALL_STATE_RINGING:
					
					try {
						mediaPlayer.pause();
					} catch (IllegalStateException e) {

					}
					break;
				case TelephonyManager.CALL_STATE_IDLE:
					
					try {
						mediaPlayer.start();
					} catch (IllegalStateException e) {

					}
					break;
				}
				super.onCallStateChanged(state, incomingNumber);
			}
		};

		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_CALL_STATE);

		// Toast.makeText(this, answerString, Toast.LENGTH_LONG).show();

		startAlarm();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch (v.getId()) {
	
			
			
		case R.id.snooze_5 :
			snooze(5);
			
			

                  break;
                  
		case R.id.snooze_3 :
			snooze(3);
		
                  break;
                  
		case R.id.snooze_10 :
			snooze(10);
		
                  break;
		default:
			break;
			
		}
		
				
		
	}
	private void startAlarm() {
  
		if (alarm.getAlarmTonePath() != "") {
			mediaPlayer = new MediaPlayer();
			if (alarm.getVibrate()) {
				vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
				long[] pattern = { 1000, 200, 200, 200 };
				vibrator.vibrate(pattern, 0);
			}
			try {
		
				timer = new Timer();
			
				TimerTask task = new TimerTask() {
		            public void run() {
		            	
		                if (m.currentVolume<m.maxVolume) {
		                	m.update(mediaPlayer);
		                } else {
		                	
		                    timer.cancel();
		                    m.currentVolume=0.0f;
		                    m.maxVolume=0.0f;
		                    m.fadeSpeed=0.0f;
		                }
		            }
		        };
		         
		        timer.schedule(task, 0, 30000);
			
			
			mediaPlayer.setDataSource(this,
						Uri.parse(alarm.getAlarmTonePath()));
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
				mediaPlayer.setLooping(true);
				mediaPlayer.prepare();
				mediaPlayer.start();

			} catch (Exception e) {
				mediaPlayer.release();
				alarmActive = false;
			}
		}

	}
	@Override
	protected void onResume() {
		super.onResume();
		alarmActive = true;
	}
	
	@Override
	public void onBackPressed() {
		if (!alarmActive)
			super.onBackPressed();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		StaticWakeLock.lockOff(this);
	}
	@Override
	protected void onDestroy() {
		try {
			if (vibrator != null)
				vibrator.cancel();
		} catch (Exception e) {

		}
		try {
			mediaPlayer.stop();
		} catch (Exception e) {

		}
		try {
			mediaPlayer.release();
		} catch (Exception e) {

		}
		super.onDestroy();
	}
	
	private void snooze(int offset) 
	{  	alarmActive = false;
	if (vibrator != null)
		vibrator.cancel();
	try {
		mediaPlayer.stop();
	} catch (IllegalStateException ise) {

	}
	try {
		mediaPlayer.release();
	} catch (Exception e) {

	}
	 Calendar calendar = Calendar.getInstance();
        //int snoozeTime = mMinute + SNOOZE_MIN;
        calendar.add(Calendar.MINUTE, offset); //SNOOZE_MIN = 1;
       
        
        
        String time = "";
		if (calendar.get(Calendar.HOUR_OF_DAY) <= 9)
			time += "0";
		time += String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
		time += ":";

		if (calendar.get(Calendar.MINUTE) <= 9)
			time += "0";
		time += String.valueOf(calendar.get(Calendar.MINUTE));

        
	
	Database.init(getApplicationContext());
	Database.snooze(alarm, time);
	
	Intent mathAlarmServiceIntent = new Intent(this,
			AlarmServiceBroadcastReciever.class);
	sendBroadcast(mathAlarmServiceIntent, null);
	timer.cancel();
	AlarmFirstAlertActivity.this.finish();
	 
	}
	
	}
	

