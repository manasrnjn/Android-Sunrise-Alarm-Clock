package com.manas.sunrisealarm.alarm;



import java.io.IOException;
import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;



import com.manas.sunrisealarm.R;
import com.manas.sunrisealarm.alarm.OffsetPicker.TimeWatcher;
import com.manas.sunrisealarm.alert.TonePicker;
import com.manas.sunrisealarm.database.Database;
import com.manas.sunrisealarm.preferences.AlarmPreference;
import com.manas.sunrisealarm.preferences.AlarmPreferencesActivity;
import com.manas.sunrisealarm.preferences.AlarmPreference.Key;
import com.manas.sunrisealarm.preferences.AlarmPreferencesActivity.AlarmPreferenceListAdapter;
import com.manas.sunrisealarm.service.AlarmServiceBroadcastReciever;
import com.manas.sunrisealarm.service.DatabaseUpdateReceiver;


import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;


public class AlarmActivity extends Activity implements
android.view.View.OnClickListener, OnSeekBarChangeListener, TimeWatcher {
	AlarmPreferenceListAdapter obj;
	private Button next_d;
	CheckBox sun,mon,tue,wed,thu,fri,sat;
	ImageButton newButton;
	SeekBar sb,vb;
	MediaPlayer mp;
	AudioManager am;
	Vibrator vibrator;
	AlarmPreferencesActivity watObj;
	int height,wwidth;
	ListView lv;
	private TextView showdate,addAlarm;
	com.manas.sunrisealarm.alarm.OffsetPicker t;
	Button set_tone;
	private ImageView sunrise,sunset,regular;
	double i1;
	int i;
	private Dialog dialog,dialog2,dialog3,offset;
	private String alarmType=null;
	TextView volume,myvibrator,sunrisetime,sunsettime,noAlarms;
	private List<Alarm> alarms = new ArrayList<Alarm>();
	String sunriseTime,sunsetTime;
	Alarm.Day thisDay;
	String hours,mins;
	AlarmListAdapter alarmListAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.alarm_activity);
		noAlarms=(TextView)findViewById(R.id.noAlarms);
		addAlarm=(TextView)findViewById(R.id.textView_title_bar1);
		lv = (ListView) findViewById(R.id.listview);
		
		int n= this.getResources().getConfiguration().orientation;
		
		
	
             

		watObj=new AlarmPreferencesActivity();   
		watObj.setMathAlarm(new Alarm());
		obj=	watObj.new AlarmPreferenceListAdapter(this,
				watObj.getMathAlarm());
      
	
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int heights = metrics.heightPixels;
		int widths = metrics.widthPixels;
		if(n==1)
		{	this.height=heights;
		   this.wwidth=widths;
		}    else
		{
			this.height=widths;
			this.wwidth=heights;
		}

		

		

		
		sunrisetime=(TextView) findViewById(R.id.sunrisetime);
		sunsettime=(TextView) findViewById(R.id.sunsettime);	
	
		

		
	

		SharedPreferences pref = getSharedPreferences("sunrisesunset",MODE_PRIVATE);
		sunrisetime.setText(pref.getString("sunrisetime", " N/A"));
		sunsettime.setText(pref.getString("sunsettime", " N/A"));

		
		
		showdate=(TextView)findViewById(R.id.Date);
		displayTime();



		newButton = (ImageButton) findViewById(com.manas.sunrisealarm.R.id.button_new);
		newButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					newButton.setBackgroundColor(getResources().getColor(
							R.color.holo_blue_light));
					break;
				case MotionEvent.ACTION_UP:
					v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

					dialog_alarmType();




				case MotionEvent.ACTION_MOVE:
				case MotionEvent.ACTION_CANCEL:
					newButton.setBackgroundColor(getResources().getColor(
							android.R.color.transparent));
					break;
				}
				return true;
			}
		});

addAlarm.setOnClickListener(new View.OnClickListener() {
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		

			dialog_alarmType();




	
		}
		
	
	
});

	
              lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					
					
					final Alarm alarm = (Alarm) alarmListAdapter
							.getItem(position);
						
					Builder dialog = new AlertDialog.Builder(
							AlarmActivity.this);
					dialog.setTitle("Delete");
					dialog.setMessage("Delete this alarm?");
					dialog.setPositiveButton("Ok", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {




							Database.init(AlarmActivity.this);
							Database.deleteEntry(alarm);

							AlarmActivity.this
							.callMathAlarmScheduleService();
                                AlarmActivity.this.onResume();
							//Intent refresh= new Intent(AlarmActivity.this,AlarmActivity.class);
							//startActivity(refresh);

						}
					});
					dialog.show();
					
					
					
					
					// TODO Auto-generated method stub
					return true;
				}
			});

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					// TODO Auto-generated method stub
					
					Alarm alarm = (Alarm) alarmListAdapter.getItem(position);
					Intent intent = new Intent(AlarmActivity.this,
							AlarmPreferencesActivity.class);
					intent.putExtra("alarm", alarm);
					startActivity(intent);
					
					
				}
			});

		callMathAlarmScheduleService();

	



		
	}

	private void callMathAlarmScheduleService() {
		Intent mathAlarmServiceIntent = new Intent(AlarmActivity.this,
				AlarmServiceBroadcastReciever.class);
		sendBroadcast(mathAlarmServiceIntent, null);
	}

	@Override
	protected void onPause() {
		// setListAdapter(null);
		Database.deactivate();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mp = MediaPlayer.create(this, Uri.parse(watObj.alarm.getAlarmTonePath()));
		
		Database.init(this);
		alarms = Database.getAll();
		if(alarms.size()>0)
            noAlarms.setVisibility(View.GONE);

		
		
		
		final Object data = getLastNonConfigurationInstance();
		if (data == null) {
			alarmListAdapter = new AlarmListAdapter(this);
		} else {
			alarmListAdapter = (AlarmListAdapter) data;
		}

		lv.setAdapter(alarmListAdapter);

		
		SharedPreferences pref = getSharedPreferences("sunrisesunset",MODE_PRIVATE);
		sunrisetime.setText(pref.getString("sunrisetime", " N/A"));
		sunsettime.setText(pref.getString("sunsettime", " N/A"));

		
		super.onResume();






	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		return alarmListAdapter;
	}

	@Override
	public void onClick(View v) {


				
	

		


		if(v.getId()==R.id.regular)
		{    
			






			


			
			alarmType="Regular";
			watObj.alarm.setAlarmType(alarmType);

			dialog.dismiss();
			
			RegularPicker();
			
			


		}

		if(v.getId()==R.id.sunrise)
		{     

			sunriseTime=new SunriseCalculator(AlarmActivity.this,"SUNRISE").onPreExecute();
			if(sunriseTime==null)
				sunriseTime="06:42";

			

			alarmType="Sunrise";
			watObj.alarm.setAlarmType(alarmType);
			dialog.dismiss();
			dialog_setOffset();
		}

		if(v.getId()==R.id.sunset)
		{     

			sunsetTime=new SunriseCalculator(AlarmActivity.this,"SUNSET").onPreExecute();
			if(sunsetTime==null)
				sunsetTime="06:43";

			

			alarmType="Sunset";
			watObj.alarm.setAlarmType(alarmType);
			dialog.dismiss();
			dialog_setOffset();
		}


	

		if(v.getId()==R.id.dialog_next)

		{

			time_update(Integer.valueOf(t.hour_display.getText().toString()),Integer.valueOf(t.min_display.getText().toString()));
			offset.dismiss();
			dialog_setDays();
		}	


		switch (v.getId()) {


		
		case R.id.sunday:
			
			thisDay = Alarm.Day.values()[0];

			if (sun.isChecked()) {
				
				watObj.alarm.addDay(thisDay);
			} else {
				
				watObj.alarm.removeDay(thisDay);
			}
			obj.setMathAlarm(watObj.getMathAlarm());
			break;

		case R.id.monday:

			thisDay = Alarm.Day.values()[1];

			if (mon.isChecked()) {
				watObj.alarm.addDay(thisDay);
			} else {
				watObj.alarm.removeDay(thisDay);
			}
			obj.setMathAlarm(watObj.getMathAlarm());

			break;


		case R.id.tuesday:

			thisDay = Alarm.Day.values()[2];

			if (tue.isChecked()) {
				watObj.alarm.addDay(thisDay);
			} else {
				watObj.alarm.removeDay(thisDay);
			}

			obj.setMathAlarm(watObj.getMathAlarm());
			break;

		case R.id.wednesday:

			thisDay = Alarm.Day.values()[3];

			if (wed.isChecked()) {
				watObj.alarm.addDay(thisDay);
			} else {
				watObj.alarm.removeDay(thisDay);
			}

			obj.setMathAlarm(watObj.getMathAlarm());
			break;
		case R.id.thursday:

			thisDay = Alarm.Day.values()[4];

			if (thu.isChecked()) {
				watObj.alarm.addDay(thisDay);
			} else {
				watObj.alarm.removeDay(thisDay);
			}

			obj.setMathAlarm(watObj.getMathAlarm());
			break;

		case R.id.friday:

			thisDay = Alarm.Day.values()[5];

			if (fri.isChecked()) {
				watObj.alarm.addDay(thisDay);
			} else {
				watObj.alarm.removeDay(thisDay);
			}

			obj.setMathAlarm(watObj.getMathAlarm());
			break;

		case R.id.saturday:

			thisDay = Alarm.Day.values()[6];

			if (sat.isChecked()) {
				watObj.alarm.addDay(thisDay);
			} else {
				watObj.alarm.removeDay(thisDay);
			}

			obj.setMathAlarm(watObj.getMathAlarm());
			break;


		default:
			break;
		}



	}



	private void displayTime()
	{
		DateFormat df = DateFormat.getDateInstance();

		
		Date today = Calendar.getInstance().getTime();        
		
		String reportDate = df.format(today);



		showdate.setText(reportDate);


		
	}

	

	public void dialog_setDays()
	{ 	

		final Dialog dialog1;
		dialog1 = new Dialog(this,R.style.AppBaseTheme);
		dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog1.getWindow().setLayout((int)(wwidth*0.7f), (int)(height*0.65));
		dialog1.setContentView(R.layout.repeatdays);
		Button dialog_btn_back=(Button)dialog1.findViewById(R.id.back);
		dialog_btn_back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog1.dismiss();
				if(watObj.alarm.getAlarmType().contains("Regular"))
				{
					dialog1.dismiss();
					RegularPicker();
				}
				else
				dialog_setOffset();
			}
		});
		
		
		
		sun=(CheckBox) dialog1.findViewById(R.id.sunday);
		mon=(CheckBox) dialog1.findViewById(R.id.monday);
		tue=(CheckBox) dialog1.findViewById(R.id.tuesday);
		wed=(CheckBox) dialog1.findViewById(R.id.wednesday);
		thu=(CheckBox) dialog1.findViewById(R.id.thursday);
		fri=(CheckBox) dialog1.findViewById(R.id.friday);
		sat=(CheckBox) dialog1.findViewById(R.id.saturday);
		next_d=(Button) dialog1.findViewById(R.id.next);
		next_d.setVisibility(View.VISIBLE);

		dialog1.show();
		sun.setOnClickListener(this);
		sun.setChecked(true);
		mon.setOnClickListener(this);
		mon.setChecked(true);
		tue.setOnClickListener(this);
		tue.setChecked(true);
		wed.setOnClickListener(this);
		wed.setChecked(true);
		thu.setOnClickListener(this);
		thu.setChecked(true);
		fri.setOnClickListener(this);
		fri.setChecked(true);
		sat.setOnClickListener(this);
		sat.setChecked(true);
		next_d.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				dialog1.dismiss();
				dialog_volume();
				
			}
		});

		
	}

	

	private void dialog_volume()

	{   

	dialog2 = new Dialog(this,R.style.AppBaseTheme);
	dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
	dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	dialog2.getWindow().setLayout((int)(wwidth*0.85f), (int)(height*0.6f));


	dialog2.setContentView(R.layout.seekbarvolume);
	
	
	CheckBox vibrate=(CheckBox)dialog2.findViewById(R.id.Vibrate);
    vibrate.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
		   if(isChecked)
			   watObj.alarm.setVibrate(true);
		   else
			   watObj.alarm.setVibrate(false);
			   
		}
	});
	
	Button button_back=(Button)dialog2.findViewById(R.id.back);
	button_back.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(mp!=null)
			{if(mp.isPlaying())
			mp.stop();
			mp.reset();
			}
			
			dialog2.dismiss();
			dialog_setDays();
		}
	});

	Button finish=(Button)dialog2.findViewById(R.id.finish);
	finish.setText("Next");
	 set_tone=(Button)dialog2.findViewById(R.id.alarmTone);
	sb = (SeekBar) dialog2.findViewById(R.id.sbVolume);
	
	
	
	
	
	vb= (SeekBar) dialog2.findViewById(R.id.sbVibrate);

	volume=(TextView) dialog2.findViewById(R.id.volume);
	myvibrator=(TextView) dialog2.findViewById(R.id.vibrator);


	
	mp = MediaPlayer.create(this, Uri.parse((watObj.alarm.getAlarmTonePath()
			)));
	
	mp.reset();
	mp.setAudioStreamType(AudioManager.STREAM_ALARM);
	try {
		mp.setDataSource(watObj.alarm.getAlarmTonePath());
		mp.prepare();
	} catch (IllegalArgumentException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SecurityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IllegalStateException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	//mp.start();
	mp.setLooping(true);
	am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
     

	sb.setProgress(am.getStreamVolume(AudioManager.STREAM_ALARM));
	
	
	sb.setOnSeekBarChangeListener(this);
	vb.setOnSeekBarChangeListener(this);
	

	finish.setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(mp!=null)
			{if(mp.isPlaying())
			mp.stop();
			mp.reset();
			}
			dialog2.dismiss();
			dialog_name();
			obj.setMathAlarm(watObj.getMathAlarm());
		}
	});
	
	Uri ringtoneUri = Uri.parse(watObj.alarm.getAlarmTonePath());
	Ringtone ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
	 set_tone.setText(ringtone.getTitle(this));
       set_tone.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//dialog_melody();
			if(mp!=null)
			{	if(mp.isPlaying())
			mp.stop();
			mp.reset();
			}
			
			Intent i=new Intent(AlarmActivity.this,TonePicker.class);
			startActivityForResult(i, 44);
			
		}
	});
       
       dialog2.setOnDismissListener(new DialogInterface.OnDismissListener() {
		
		@Override
		public void onDismiss(DialogInterface dialog) {
			// TODO Auto-generated method stub
			if(mp!=null)
				if(mp.isPlaying())
				{
					mp.stop();
					mp.reset();
				}
		}
	});
       dialog2.show();
	}
	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		// TODO Auto-generated method stub
		if(arg0.getId()==R.id.sbVolume)
		{	
			if(!mp.isPlaying())
				mp.start();
		
		 i1=((double)arg1/(double)100);
		
		 float i1=(float)this.i1;
		
		mp.setVolume(i1, i1);
		volume.setText(arg1+"%");
		watObj.alarm.setVolume(arg1+"%");
		}
		else
		{
		myvibrator.setText(arg1+" min");
		watObj.alarm.setfadein(String.valueOf(arg1));
		}



	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub



	}
	private void dialog_name()
	{
		final AlarmPreference alarmPreference = (AlarmPreference) obj.preferences.get(1);





		dialog3 = new Dialog(this,R.style.AppBaseTheme);
		dialog3.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog3.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog3.setContentView(R.layout.name_dialog);
		Button dialog_back=(Button)dialog3.findViewById(R.id.back);
		dialog_back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog3.dismiss();
				dialog_volume();
			}
		});
		dialog3.getWindow().setLayout((int)(wwidth*0.6f), (int)(height*0.5f));
		final EditText input = (EditText)dialog3.findViewById(R.id.alarmName);
		Button Ok=(Button)dialog3.findViewById(R.id.OKbutton);


		input.setText(alarmPreference.getValue().toString());

		Ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alarmPreference.setValue(input.getText()
						.toString());

				if (alarmPreference.getKey() == Key.ALARM_NAME) {
					watObj.alarm.setAlarmName(alarmPreference
							.getValue().toString());
				}

				obj.setMathAlarm(watObj.getMathAlarm());

				Database.init(getApplicationContext());
				Database.create(watObj.getMathAlarm());
				callMathAlarmScheduleService();
				Toast.makeText(AlarmActivity.this,
						watObj.getMathAlarm().getTimeUntilNextAlarmMessage(),
						Toast.LENGTH_LONG).show();

				if(watObj.alarm.getAlarmType().contains("Sunrise") || watObj.alarm.getAlarmType().contains("Sunset"))
				{ 	 Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DAY_OF_MONTH, 1);

				calendar.set(Calendar.MINUTE,05);
				calendar.set(Calendar.HOUR_OF_DAY,00);
				calendar.set(Calendar.SECOND,0);
				Intent intent = new Intent(AlarmActivity.this, DatabaseUpdateReceiver.class);
				intent.setAction("com.manas.sunrisealarm.alarm");	

				PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

				AlarmManager alarmManager = (AlarmManager)AlarmActivity.this.getSystemService(Context.ALARM_SERVICE);

				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY , pendingIntent);						  



				}  


				
				dialog3.dismiss();
				AlarmActivity.this.onResume();
				
			}

		});
		dialog3.show();



	}




	private void dialog_setOffset()
	{   
		

	
		if(alarmType.contains("Sunrise"))
		{ hours=sunriseTime.substring(0,2);
		mins=sunriseTime.substring(3, 5);
		}
		else
		{

			hours=sunsetTime.substring(0,2);
			mins=sunsetTime.substring(3, 5);
		}





		offset=new Dialog(AlarmActivity.this,R.style.AppBaseTheme);

		offset.requestWindowFeature(Window.FEATURE_NO_TITLE);
		offset.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		offset.setContentView(R.layout.offset_dialog);
		Button offset_button=(Button) offset.findViewById(R.id.dialog_next);
		Button offset_button_back=(Button) offset.findViewById(R.id.back);
		offset_button_back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				offset.dismiss();
				dialog_alarmType();
			}
		});
		offset.getWindow().setLayout((int)(wwidth*0.7f), (int)(height*0.47f));
		
		 t = (com.manas.sunrisealarm.alarm.OffsetPicker)offset.findViewById(R.id.timePicker1);
		 t.setCurrentTimeFormate(com.manas.sunrisealarm.alarm.OffsetPicker.HOUR_24);
		 t.setTimeChangedListener(this);
		offset_button.setOnClickListener(this);
		

		offset.show();






	}

	@Override
	public void onTimeChanged(int h, int m, int am_pm) {
		// TODO Auto-generated method stub
		








	}

	public void time_update(int h, int m)
	
	{
		String offhour=String.valueOf(h);
		if(Integer.parseInt(offhour)<=9)
			offhour="0"+offhour;

		String offmin=String.valueOf(m);
		if(Integer.parseInt(offmin)<=9)
			offmin="0"+offmin;

		if(t.am_pm.getText().equals("+"))
		
		{   watObj.alarm.setOffset("+"+offhour+":"+offmin);
		


		Calendar newAlarmTime = Calendar.getInstance();
		newAlarmTime.set(Calendar.HOUR_OF_DAY, h);
		newAlarmTime.set(Calendar.MINUTE, m);
		newAlarmTime.set(Calendar.SECOND, 0);




		if(alarmType.contains("Sunrise"))
		{ newAlarmTime.add(Calendar.HOUR_OF_DAY,Integer.parseInt(sunriseTime.substring(0,2)));
		newAlarmTime.add(Calendar.MINUTE,Integer.parseInt(sunriseTime.substring(3, 5)));
		}
		else
		{ newAlarmTime.add(Calendar.HOUR_OF_DAY,Integer.parseInt(sunsetTime.substring(0,2)));
		newAlarmTime.add(Calendar.MINUTE,Integer.parseInt(sunsetTime.substring(3, 5)));
		}
		newAlarmTime.add(Calendar.SECOND, 0);
		watObj.alarm.setAlarmTime(newAlarmTime);

		obj.setMathAlarm(watObj.getMathAlarm());

		}

		else
		{

			watObj.alarm.setOffset("-"+offhour+":"+offmin);
			
			if(alarmType.contains("Sunrise"))
			{ Calendar newAlarmTime = Calendar.getInstance();
			newAlarmTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(sunriseTime.substring(0,2)));
			newAlarmTime.set(Calendar.MINUTE, Integer.parseInt(sunriseTime.substring(3, 5)));
			newAlarmTime.set(Calendar.SECOND, 0);

			newAlarmTime.add(Calendar.HOUR_OF_DAY,-h);
			newAlarmTime.add(Calendar.MINUTE,m);

			newAlarmTime.add(Calendar.SECOND, 0);
			watObj.alarm.setAlarmTime(newAlarmTime);

			obj.setMathAlarm(watObj.getMathAlarm());
			}

			else
			{

				Calendar newAlarmTime = Calendar.getInstance();
				newAlarmTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(sunsetTime.substring(0,2)));
				newAlarmTime.set(Calendar.MINUTE, Integer.parseInt(sunsetTime.substring(3, 5)));
				newAlarmTime.set(Calendar.SECOND, 0);

				newAlarmTime.add(Calendar.HOUR_OF_DAY,-h);
				newAlarmTime.add(Calendar.MINUTE,m);
				newAlarmTime.add(Calendar.SECOND, 0);
				watObj.alarm.setAlarmTime(newAlarmTime);

				obj.setMathAlarm(watObj.getMathAlarm());

			}


		}





		
	}

	 @Override
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	  {
	    if (resultCode != RESULT_OK || data == null) return;
		
	

	    Uri u = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
		Ringtone ringtone = RingtoneManager.getRingtone(this, u);
	               if(u!=null)
	               {    watObj.alarm.setAlarmTonePath(u.toString());  
	               set_tone.setText(ringtone.getTitle(this));
	  }}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
		if(mp!=null)
		if(mp.isPlaying())
		{
			mp.stop();
			mp.reset();
		}
		super.onBackPressed();
	}
	  
void dialog_alarmType()
{
	dialog = new Dialog(this,R.style.AppBaseTheme);
	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
	dialog.setContentView(R.layout.alarm_type_prompt);
	dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	
	dialog.getWindow().setLayout((int)(wwidth*0.6f), (int)(height*0.49f));
	
		
		
	dialog.setTitle("ADD ALARM");



	sunrise=(ImageView)dialog.findViewById(R.id.sunrise);
	sunset=(ImageView)dialog.findViewById(R.id.sunset);
	regular=(ImageView)dialog.findViewById(R.id.regular);
	sunrise.setOnClickListener(this);
	sunset.setOnClickListener(this);
	regular.setOnClickListener(this);
	dialog.show();
}
void RegularPicker()
{
	final RegularPicker p=new RegularPicker(this,wwidth,height,24,watObj,obj);
	p.dialog_button.setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			p.dialog.dismiss();
			dialog_setDays();
		}
	});
	
	p.dialog_button_back.setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			p.dialog.dismiss();
			dialog_alarmType();
		}
	});
}

/*public void performmanualupdate(View v)
{ 
	sendBroadcast(new Intent(this,DatabaseUpdateReceiver.class));
}
*/

}






















