package com.manas.sunrisealarm.preferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.manas.sunrisealarm.R;
import com.manas.sunrisealarm.alarm.Alarm;
import com.manas.sunrisealarm.alarm.RegularPicker;
import com.manas.sunrisealarm.alarm.OffsetPicker.TimeWatcher;
import com.manas.sunrisealarm.alert.TonePicker;
import com.manas.sunrisealarm.database.Database;
import com.manas.sunrisealarm.preferences.AlarmPreference.Key;
import com.manas.sunrisealarm.preferences.AlarmPreference.Type;
import com.manas.sunrisealarm.service.AlarmServiceBroadcastReciever;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;

import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class AlarmPreferencesActivity extends Activity implements android.view.View.OnClickListener, OnSeekBarChangeListener, TimeWatcher{

	ImageButton saveButton;
	private TextView tv2,tv4,tv5,tv6,tv8,tv10,tv12,repeat;
	String hours,mins,sunriseTime,sunsetTime;
	public Alarm alarm;
	private MediaPlayer mediaPlayer;
	AlarmPreferenceListAdapter obj;
	private Dialog dialog,offset,NameDialog,alarmType;
	SeekBar sb,vb;
	MediaPlayer mp;
	AudioManager am;
	Button finish;
	Vibrator vibrator;
	CheckBox sun,mon,tue,wed,thu,fri,sat;
	TextView volume,myvibrator,adjust_time,afterDays;
	int height,wwidth;
	String buttonPressed=" ";
	com.manas.sunrisealarm.alarm.OffsetPicker t;
	Button set_tone;
      static int FLAG_FIRSTIME=0;
    public static Calendar origAtime;
     long diff;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.alarm_preferences);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		height = metrics.heightPixels;
		wwidth = metrics.widthPixels;
		int n= this.getResources().getConfiguration().orientation;

		


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


		tv2=(TextView) findViewById(R.id.tv2);
		tv4=(TextView) findViewById(R.id.tv4);
		tv6=(TextView) findViewById(R.id.tv6);
		tv8=(TextView) findViewById(R.id.tv8);
		tv10=(TextView) findViewById(R.id.tv10);
		tv12=(TextView) findViewById(R.id.tv12);
		tv5=(TextView) findViewById(R.id.tv5);
		repeat=(TextView) findViewById(R.id.repeat);
		sun=(CheckBox) findViewById(R.id.sunday);
		mon=(CheckBox) findViewById(R.id.monday);
		tue=(CheckBox) findViewById(R.id.tuesday);
		wed=(CheckBox) findViewById(R.id.wednesday);
		thu=(CheckBox) findViewById(R.id.thursday);
		fri=(CheckBox) findViewById(R.id.friday);
		sat=(CheckBox) findViewById(R.id.saturday);

		tv2.setOnClickListener(this);
		tv4.setOnClickListener(this);
		tv6.setOnClickListener(this);
		tv8.setOnClickListener(this);
		tv10.setOnClickListener(this);
		tv12.setOnClickListener(this);
		repeat.setOnClickListener(this);
		sun.setOnClickListener(this);
		mon.setOnClickListener(this);
		tue.setOnClickListener(this);
		wed.setOnClickListener(this);
		thu.setOnClickListener(this);
		fri.setOnClickListener(this);
		sat.setOnClickListener(this);



		saveButton = (ImageButton) findViewById(R.id.toolbar).findViewById(
				R.id.button_save);
		saveButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					saveButton.setBackgroundColor(getResources().getColor(
							R.color.holo_blue_light));
					break;
				case MotionEvent.ACTION_UP:
					v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
					Database.init(getApplicationContext());
					if (getMathAlarm().getId() < 1) {
						Database.create(getMathAlarm());
					} else {
						Database.update(getMathAlarm());
					}
					callMathAlarmScheduleService();
					Toast.makeText(AlarmPreferencesActivity.this,
							getMathAlarm().getTimeUntilNextAlarmMessage(),
							Toast.LENGTH_LONG).show();
					FLAG_FIRSTIME=0;
					finish();
				case MotionEvent.ACTION_MOVE:
				case MotionEvent.ACTION_CANCEL:
					saveButton.setBackgroundColor(getResources().getColor(
							android.R.color.transparent));								
					break;
				}
				return true;
			}
		});


		Bundle bundle = getIntent().getExtras();
		if (bundle != null && bundle.containsKey("alarm")) {

			setMathAlarm((Alarm) bundle.getSerializable("alarm"));
		}

	}

	private void callMathAlarmScheduleService() {
		Intent mathAlarmServiceIntent = new Intent(this,
				AlarmServiceBroadcastReciever.class);
		sendBroadcast(mathAlarmServiceIntent, null);
	}


	@Override
	public Object onRetainNonConfigurationInstance() {
		Object[] bundle = { getMathAlarm() };
		return bundle;
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			if (mediaPlayer != null)
				mediaPlayer.release();
			if(mp!=null)
				mp.release();
		} catch (Exception e) {
		}



	}

	@Override
	protected void onResume() {
		mp = MediaPlayer.create(this, Uri.parse(alarm.getAlarmTonePath()));

		// Restore data in event of case of orientation change
		@SuppressWarnings("deprecation")
		final Object data = getLastNonConfigurationInstance();
		if (data == null) {
			if (getMathAlarm() == null)
				setMathAlarm(new Alarm());

			obj=	new AlarmPreferenceListAdapter(this,
					getMathAlarm());
		} else {
			Object[] bundle = (Object[]) data;
			setMathAlarm((Alarm) bundle[0]);
			//setListAdapter((AlarmPreferenceListAdapter) bundle[1]);
		}

		AlarmPreference alarmPreferencerepeat= (AlarmPreference) obj.preferences
				.get(3);
		repeat.setText(alarmPreferencerepeat.getSummary());


		alarmPreferencerepeat= (AlarmPreference) obj.preferences
				.get(5);
		tv8.setText(alarmPreferencerepeat.getSummary());

		alarmPreferencerepeat= (AlarmPreference) obj.preferences
				.get(1);
		tv2.setText(alarmPreferencerepeat.getSummary());

		alarmPreferencerepeat= (AlarmPreference) obj.preferences
				.get(2);
		tv6.setText(alarmPreferencerepeat.getSummary());
		alarmPreferencerepeat= (AlarmPreference) obj.preferences
				.get(7);
		tv10.setText(alarmPreferencerepeat.getSummary());

		alarmPreferencerepeat= (AlarmPreference) obj.preferences
				.get(8);
		tv4.setText(alarmPreferencerepeat.getSummary());

		if(alarmPreferencerepeat.getSummary().contains("Sunrise"))
		{	tv5.setText("Offset:");
		AlarmPreference    offset= (AlarmPreference) obj.preferences
				.get(9);

		tv6.setText(" "+offset.getSummary().substring(0,3)+" HRS, "+offset.getSummary().substring(4,6)+" MINS "+alarmPreferencerepeat.getSummary());

		}
		else if(alarmPreferencerepeat.getSummary().contains("Sunset"))
		{
			tv5.setText("Offset:");
			AlarmPreference    offset= (AlarmPreference) obj.preferences
					.get(9);
			tv6.setText(" "+offset.getSummary().substring(0,3)+" HRS, "+offset.getSummary().substring(4,6)+" MINS "+alarmPreferencerepeat.getSummary());

		}	
		else
			tv5.setText("Time:");





		alarmPreferencerepeat= (AlarmPreference) obj.preferences
				.get(3);


		CharSequence[] multiListItems = new CharSequence[alarmPreferencerepeat
		                                                 .getOptions().length];
		for (int i = 0; i < multiListItems.length; i++)
		{	multiListItems[i] = alarmPreferencerepeat.getOptions()[i];


		}
		boolean[] checkedItems = new boolean[multiListItems.length];
		for (Alarm.Day day : getMathAlarm().getDays()) {
			checkedItems[day.ordinal()] = true;
			switch (day.ordinal()) {
			case 0:
				sun.setChecked(true);
				break;
			case 1:
				mon.setChecked(true);
				break;

			case 2:
				tue.setChecked(true);
				break;

			case 3:
				wed.setChecked(true);
				break;

			case 4:
				thu.setChecked(true);
				break;

			case 5:
				fri.setChecked(true);
				break;
			case 6:
				sat.setChecked(true);
				break;
			default:
				break;
			}

		}





		super.onResume();
	}

	public Alarm getMathAlarm() {
		return alarm;
	}

	public void setMathAlarm(Alarm alarm) {
		this.alarm = alarm;
	}





	public class AlarmPreferenceListAdapter {

		private Context context;
		private Alarm alarm;
		public List<AlarmPreference> preferences = new ArrayList<AlarmPreference>();
		private final String[] repeatDays = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};	
		private final String[] alarmDifficulties = {"Easy","Medium","Hard"};

		private String[] alarmTones;
		private String[] alarmTonePaths;

		public AlarmPreferenceListAdapter(Context context, Alarm alarm) {
			setContext(context);


			//			

			RingtoneManager ringtoneMgr = new RingtoneManager(getContext());

			ringtoneMgr.setType(RingtoneManager.TYPE_ALARM);

			Cursor alarmsCursor = ringtoneMgr.getCursor();

			alarmTones = new String[alarmsCursor.getCount()+1];
			alarmTones[0] = "Silent"; 
			alarmTonePaths = new String[alarmsCursor.getCount()+1];
			alarmTonePaths[0] = "";

			if (alarmsCursor.moveToFirst()) {		    			
				do {
					alarmTones[alarmsCursor.getPosition()+1] = ringtoneMgr.getRingtone(alarmsCursor.getPosition()).getTitle(getContext());
					alarmTonePaths[alarmsCursor.getPosition()+1] = ringtoneMgr.getRingtoneUri(alarmsCursor.getPosition()).toString();
				}while(alarmsCursor.moveToNext());					
			}

			alarmsCursor.close();
			setMathAlarm(alarm);		
		}


		public Alarm getMathAlarm() {		
			for(AlarmPreference preference : preferences){
				switch(preference.getKey()){
				case ALARM_ACTIVE:
					alarm.setAlarmActive((Boolean) preference.getValue());
					break;
				case ALARM_NAME:
					alarm.setAlarmName((String) preference.getValue());
					break;
				case ALARM_TIME:
					alarm.setAlarmTime((String) preference.getValue());
					break;
				case ALARM_DIFFICULTY:
					alarm.setDifficulty(Alarm.Difficulty.valueOf((String)preference.getValue()));
					break;
				case ALARM_TONE:
					alarm.setAlarmTonePath((String) preference.getValue());
					break;
				case ALARM_VIBRATE:
					alarm.setVibrate((Boolean) preference.getValue());
					break;
				case ALARM_REPEAT:
					alarm.setDays((Alarm.Day[]) preference.getValue());
					break;

				case ALARM_VOLUME:
					alarm.setVolume((String)preference.getValue());
					break;
				case ALARM_FADEIN:
					alarm.setfadein((String)preference.getValue());
					break;
				case ALARM_DA:
					alarm.setDA((String)preference.getValue());
				}
			}

			return alarm;
		}

		public void setMathAlarm(Alarm alarm) {

			this.alarm = alarm;
			preferences.clear();
			preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_ACTIVE,"Active", null, null, alarm.getAlarmActive(),Type.BOOLEAN));
			preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_NAME, "Label",alarm.getAlarmName(), null, alarm.getAlarmName(), Type.STRING));
			preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_TIME, "Set time",alarm.getAlarmTimeString(), null, alarm.getAlarmTime(), Type.TIME));
			preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_REPEAT, "Repeat",alarm.getRepeatDaysString(), repeatDays, alarm.getDays(),Type.MULTIPLE_LIST));
			preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_DIFFICULTY,"Difficulty", alarm.getDifficulty().toString(), alarmDifficulties, alarm.getDifficulty(), Type.LIST));

			Uri alarmToneUri = Uri.parse(alarm.getAlarmTonePath());
			Ringtone alarmTone = RingtoneManager.getRingtone(getContext(), alarmToneUri);

			if(alarmTone instanceof Ringtone && !alarm.getAlarmTonePath().equalsIgnoreCase("")){
				preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_TONE, "Ringtone", alarmTone.getTitle(getContext()),alarmTones, alarm.getAlarmTonePath(), Type.LIST));
			}else{
				preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_TONE, "Ringtone", getAlarmTones()[0],alarmTones, null, Type.LIST));
			}

			preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_VIBRATE, "Vibrate",null, null, alarm.getVibrate(), Type.BOOLEAN));
			preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_VOLUME, "Volume",alarm.getVolume(), null, alarm.getVolume(), Type.STRING));
			preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_TYPE, "NotDefined",alarm.getAlarmType(), null, alarm.getAlarmType(), Type.STRING));
			preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_OFSET, "NotDefined",alarm.getOffset(), null, alarm.getOffset(), Type.STRING));
			preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_FADEIN, "NotDefined",alarm.getfadein(), null, alarm.getfadein(), Type.STRING));
			preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_DA, "NotDefined",alarm.getDa(), null, alarm.getDa(), Type.STRING));
		}


		public Context getContext() {
			return context;
		}

		public void setContext(Context context) {
			this.context = context;
		}

		public String[] getRepeatDays() {
			return repeatDays;
		}

		public String[] getAlarmDifficulties() {
			return alarmDifficulties;
		}

		public String[] getAlarmTones() {
			return alarmTones;
		}

		public String[] getAlarmTonePaths() {
			return alarmTonePaths;
		}

	}
	@Override
	public void onClick(View v) {
		Alarm.Day thisDay;

		switch (v.getId()) {
		case R.id.tv2:

			final AlarmPreference alarmPreference = (AlarmPreference) obj.preferences.get(1);


			NameDialog = new Dialog(this,R.style.AppBaseTheme);
			NameDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			NameDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
			NameDialog.setContentView(R.layout.name_dialog);
			NameDialog.getWindow().setLayout((int)(wwidth*0.6f), (int)(height*0.45f));
			final EditText input = (EditText)NameDialog.findViewById(R.id.alarmName);
			Button Ok=(Button)NameDialog.findViewById(R.id.OKbutton1);
                   Ok.setVisibility(View.VISIBLE);
			LinearLayout buttons=(LinearLayout)NameDialog.findViewById(R.id.buttons);
                           buttons.setVisibility(View.GONE);
                 

			input.setText(alarmPreference.getValue().toString());

			Ok.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					alarmPreference.setValue(input.getText()
							.toString());

					if (alarmPreference.getKey() == Key.ALARM_NAME) {
						alarm.setAlarmName(alarmPreference
								.getValue().toString());
					}

					obj.setMathAlarm(getMathAlarm());

					//setMathAlarm(getMathAlarm());
					AlarmPreferencesActivity.this.onResume();
					NameDialog.dismiss();
				}



			});
			NameDialog.show();


			break;
		case R.id.tv4:
			setAlarmType();
			break;
		case R.id.tv6:
			buttonPressed="offset";
			obj.preferences.get(2);

			if(alarm.getAlarmType().contains("Sunrise")|| alarm.getAlarmType().contains("Sunset"))
			{
				dialog_setOffset();
			}


			else

			{	

			regularpicker();


			}
			break;
		case R.id.tv8:

			//dialog_melody();
			Intent i=new Intent(AlarmPreferencesActivity.this,TonePicker.class);
			startActivityForResult(i, 44);

			break;
		case R.id.tv10:

			dialog = new Dialog(AlarmPreferencesActivity.this,R.style.AppBaseTheme);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
			dialog.getWindow().setLayout((int)(wwidth*0.79f), (int)(height*0.6f));
			dialog.setContentView(R.layout.seekbarvolume);
			CheckBox vibrate=(CheckBox)dialog.findViewById(R.id.Vibrate);
			       
			if(alarm.getVibrate())
				vibrate.setChecked(true);
			else
				vibrate.setChecked(false);
			
			
			vibrate.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						
						@Override
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							// TODO Auto-generated method stub
						   if(isChecked)
							   alarm.setVibrate(true);
						   else
							   alarm.setVibrate(false);
							   
						}
					});
			
               LinearLayout Buttons=(LinearLayout)dialog.findViewById(R.id.buttons);
               Buttons.setVisibility(View.GONE);
			finish=(Button)dialog.findViewById(R.id.finish1);
			finish.setVisibility(View.VISIBLE);
			 set_tone=(Button)dialog.findViewById(R.id.alarmTone);
				AlarmPreference ToneName= (AlarmPreference) obj.preferences
						.get(5);
				set_tone.setText(ToneName.getSummary());
			set_tone.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(mp!=null)
					{if(mp.isPlaying())
						mp.stop();
					mp.reset();
					}
					Intent i=new Intent(AlarmPreferencesActivity.this,TonePicker.class);
					startActivityForResult(i, 44);
				}
			});

			volume=(TextView) dialog.findViewById(R.id.volume);
			myvibrator=(TextView) dialog.findViewById(R.id.vibrator);


			sb = (SeekBar) dialog.findViewById(R.id.sbVolume);
			vb= (SeekBar) dialog.findViewById(R.id.sbVibrate);
			mp = MediaPlayer.create(this, Uri.parse(alarm.getAlarmTonePath()));
			mp.reset();
			mp.setAudioStreamType(AudioManager.STREAM_ALARM);
			try {
				mp.setDataSource(alarm.getAlarmTonePath());
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

			
			sb.setProgress(Integer.valueOf(alarm.getVolume().substring(0, alarm.getVolume().length()-1))); 
		    vb.setProgress(Integer.valueOf(alarm.getfadein()));
			volume.setText(alarm.getVolume());
			myvibrator.setText(alarm.getfadein()+" min");
			sb.setOnSeekBarChangeListener(this);
			vb.setOnSeekBarChangeListener(this);
			dialog.show();

			finish.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(mp!=null)
					{	if(mp.isPlaying())
						mp.stop();
					mp.reset();
					}
					dialog.dismiss();
					AlarmPreferencesActivity.this.onResume();
				}
			});
			dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

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

			break;
		case R.id.tv12:
			Toast.makeText(this, R.string.ringtype, Toast.LENGTH_SHORT).show();
			break;
		case R.id.repeat:
			break;

		case R.id.sunday:

			thisDay = Alarm.Day.values()[0];

			if (sun.isChecked()) {
				alarm.addDay(thisDay);
			} else {
				alarm.removeDay(thisDay);
			}


			break;

		case R.id.monday:

			thisDay = Alarm.Day.values()[1];

			if (mon.isChecked()) {
				alarm.addDay(thisDay);
			} else {
				alarm.removeDay(thisDay);
			}


			break;


		case R.id.tuesday:

			thisDay = Alarm.Day.values()[2];

			if (tue.isChecked()) {
				alarm.addDay(thisDay);
			} else {
				alarm.removeDay(thisDay);
			}


			break;

		case R.id.wednesday:

			thisDay = Alarm.Day.values()[3];

			if (wed.isChecked()) {
				alarm.addDay(thisDay);
			} else {
				alarm.removeDay(thisDay);
			}


			break;
		case R.id.thursday:

			thisDay = Alarm.Day.values()[4];

			if (thu.isChecked()) {
				alarm.addDay(thisDay);
			} else {
				alarm.removeDay(thisDay);
			}


			break;

		case R.id.friday:

			thisDay = Alarm.Day.values()[5];

			if (fri.isChecked()) {
				alarm.addDay(thisDay);
			} else {
				alarm.removeDay(thisDay);
			}


			break;

		case R.id.saturday:

			thisDay = Alarm.Day.values()[6];

			if (sat.isChecked()) {
				alarm.addDay(thisDay);
			} else {
				alarm.removeDay(thisDay);
			}


			break;
		default:
			break;

		}
		obj.setMathAlarm(getMathAlarm());
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		// TODO Auto-generated method stub
		if(arg0.getId()==R.id.sbVolume)
		{    if(!mp.isPlaying())
			mp.start();
			double i=((double)arg1/(double)100);
				
			 float i1=(float)i;
			
			mp.setVolume(i1, i1);
		volume.setText(arg1+"%");
		alarm.setVolume(arg1+"%");
		}	
		else if(arg0.getId()==R.id.sTime)
		{
			 arg1 = ((int)Math.round(arg1/5))*5;
			 adjust_time.setText(arg1+" min");
			 if(arg1<9)
				 alarm.setDA("0"+arg1);
			 else
				 alarm.setDA(String.valueOf(arg1));
			 
			 if(arg1!=0)
			 afterDays.setText("After "+(int)TimeUnit.MILLISECONDS.toMinutes(diff)/arg1+" Days");
			 else
				 afterDays.setText(R.string.zero_days);
			
			
		      
		}
		else
		{
		myvibrator.setText(arg1+" min");
		alarm.setfadein(String.valueOf(arg1));
		}}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(mp!=null)
		{	mp.stop();
		mp.reset();
		}
		FLAG_FIRSTIME=0;
		super.onBackPressed();
	}





	private void dialog_setOffset()
	{   
		obj=	new AlarmPreferenceListAdapter(this,
				getMathAlarm());


		SharedPreferences pref = getSharedPreferences("sunrisesunset",MODE_PRIVATE);

		sunriseTime=pref.getString("sunrisetime", " N/A");
		sunsetTime=pref.getString("sunsettime", " N/A");
		if(sunriseTime.contains("N/A"))
			sunriseTime="06:42";

		if(sunsetTime.contains("N/A"))
			sunsetTime="06:43";


		if(alarm.getAlarmType().contains("Sunrise"))
		{ hours=sunriseTime.substring(0,2);
		mins=sunriseTime.substring(3, 5);
		}
		else
		{

			hours=sunsetTime.substring(0,2);
			mins=sunsetTime.substring(3, 5);
		}





		offset=new Dialog(AlarmPreferencesActivity.this,R.style.AppBaseTheme);
		offset.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		offset.getWindow().setLayout((int)(wwidth*0.7f), (int)(height*0.47f));
		offset.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		offset.setContentView(R.layout.offset_dialog);
		Button offset_button=(Button) offset.findViewById(R.id.dialog_next);
		offset_button.setText(R.string.Done);
   Button back=(Button)offset.findViewById(R.id.back);
   if(buttonPressed.contains("offset"))
	   back.setVisibility(View.GONE);
          buttonPressed=" ";
   back.setOnClickListener(new View.OnClickListener() {
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		         offset.dismiss();
		         setAlarmType();
	}
});



		t = (com.manas.sunrisealarm.alarm.OffsetPicker)offset.findViewById(R.id.timePicker1);
		t.setCurrentTimeFormate(com.manas.sunrisealarm.alarm.OffsetPicker.HOUR_24);


		t.hour_display.setText(alarm.getOffset().substring(1, 3));

		t.min_display.setText(alarm.getOffset().substring(4, 6));



		offset_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				time_update(Integer.valueOf(t.hour_display.getText().toString()),Integer.valueOf(t.min_display.getText().toString()));

				offset.dismiss();

				AlarmPreferencesActivity.this.onResume();
			}
		});





		offset.show();
		t.setTimeChangedListener(this);





	}

	@Override
	public void onTimeChanged(int h, int m, int am_pm) {
		// TODO Auto-generated method stub




		//	time_update(h,m);	




	}

	public void time_update(int h,int m)
	{     

		String offhour=String.valueOf(h);
		if(Integer.parseInt(offhour)<=9)
			offhour="0"+offhour;

		String offmin=String.valueOf(m);
		if(Integer.parseInt(offmin)<=9)
			offmin="0"+offmin;

		if(t.am_pm.getText().equals("+"))
		{   alarm.setOffset("+"+offhour+":"+offmin);



		Calendar newAlarmTime = Calendar.getInstance();
		newAlarmTime.set(Calendar.HOUR_OF_DAY, h);
		newAlarmTime.set(Calendar.MINUTE, m);
		newAlarmTime.set(Calendar.SECOND, 0);




		if(alarm.getAlarmType().contains("Sunrise"))
		{ newAlarmTime.add(Calendar.HOUR_OF_DAY,Integer.parseInt(sunriseTime.substring(0,2)));
		newAlarmTime.add(Calendar.MINUTE,Integer.parseInt(sunriseTime.substring(3, 5)));
		}
		else
		{ newAlarmTime.add(Calendar.HOUR_OF_DAY,Integer.parseInt(sunsetTime.substring(0,2)));
		newAlarmTime.add(Calendar.MINUTE,Integer.parseInt(sunsetTime.substring(3, 5)));
		}
		newAlarmTime.add(Calendar.SECOND, 0);
		alarm.setAlarmTime(newAlarmTime);

		obj.setMathAlarm(getMathAlarm());

		}

		else
		{

			alarm.setOffset("-"+offhour+":"+offmin);

			if(alarm.getAlarmType().contains("Sunrise"))
			{ Calendar newAlarmTime = Calendar.getInstance();
			newAlarmTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(sunriseTime.substring(0,2)));
			newAlarmTime.set(Calendar.MINUTE, Integer.parseInt(sunriseTime.substring(3, 5)));
			newAlarmTime.set(Calendar.SECOND, 0);

			newAlarmTime.add(Calendar.HOUR_OF_DAY,-h);
			newAlarmTime.add(Calendar.MINUTE,m);

			newAlarmTime.add(Calendar.SECOND, 0);
			alarm.setAlarmTime(newAlarmTime);

			obj.setMathAlarm(getMathAlarm());
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
				alarm.setAlarmTime(newAlarmTime);

				obj.setMathAlarm(getMathAlarm());

			}


		}





	}

	public void setAlarmType()
	{   obj=	new AlarmPreferenceListAdapter(this,
			getMathAlarm());

	DisplayMetrics metrics = new DisplayMetrics();
	getWindowManager().getDefaultDisplay().getMetrics(metrics);
	height = metrics.heightPixels;
	wwidth = metrics.widthPixels;

	alarmType = new Dialog(this,R.style.AppBaseTheme);
	alarmType.requestWindowFeature(Window.FEATURE_NO_TITLE); 
	alarmType.setContentView(R.layout.alarm_type_prompt);
	alarmType.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	alarmType.getWindow().setLayout((int)(wwidth*0.6f), (int)(height*0.49f));
	alarmType.show();




	ImageView sunrise=(ImageView)alarmType.findViewById(R.id.sunrise);
	ImageView sunset=(ImageView)alarmType.findViewById(R.id.sunset);
	ImageView regular=(ImageView)alarmType.findViewById(R.id.regular);

	sunrise.setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			alarmType.dismiss();
			dialog_setOffset();
			alarm.setAlarmType("Sunrise");
		}
	});



	sunset.setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			alarmType.dismiss();
			dialog_setOffset();
			alarm.setAlarmType("Sunset");
		}
	});


	regular.setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub





			alarmType.dismiss();


		
			

			
			regularpicker();
			
			


		}
	});



	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if (resultCode != RESULT_OK || data == null) return;

		Uri u = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
		Ringtone ringtone = RingtoneManager.getRingtone(this, u);
		
		//Settings.System.putString(getContentResolver(), Settings.System.ALARM_ALERT, u.toString());
		if(u!=null)    
		{	alarm.setAlarmTonePath(u.toString());  
		set_tone.setText(ringtone.getTitle(this));
		}
	}
	
	void regularpicker()
	{       if(FLAG_FIRSTIME==0)  
	{ origAtime=alarm.getAlarmTime();
	System.out.println("orig time======"+origAtime.getTime());
	    FLAG_FIRSTIME=1;
	}              
	
		final RegularPicker p=new RegularPicker(AlarmPreferencesActivity.this,wwidth,height,24,AlarmPreferencesActivity.this,obj);
	     
	if(buttonPressed.contains("offset"))
             p.dialog_button_back.setVisibility(View.GONE);	
	buttonPressed=" ";
	p.dialog_button_back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				p.dialog.dismiss();
				setAlarmType();
			}
		});
	
	p.dialog_button.setText(R.string.Done);
	p.dialog_button.setOnClickListener(new View.OnClickListener() {
            
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			p.dialog.dismiss();
			alarm.setAlarmType("Regular");
			
			AlarmPreferencesActivity.this.onResume();
			
				
		}
	});

	}
	
	/*public void intelligent_dialog()       to be used later for intelligent alarm functionality
	{
		final Dialog d=new Dialog(AlarmPreferencesActivity.this,R.style.AppBaseTheme);
		d.requestWindowFeature(Window.FEATURE_NO_TITLE); 
       d.getWindow().setLayout((int)(wwidth), (int)(height*0.47f));
		d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	       d.setContentView(R.layout.intelligentdialog);
	       afterDays=(TextView)d.findViewById(R.id.alarmText);
	       adjust_time=(TextView)d.findViewById(R.id.time);
	       SeekBar at=(SeekBar)d.findViewById(R.id.sTime);
	       at.setOnSeekBarChangeListener(this);
	       
	      Date firstTime=origAtime.getTime();
	      Date secondTime=alarm.getAlarmTime().getTime();
	       diff=firstTime.getTime()-secondTime.getTime();
	     
	       
	       Button enable=(Button)d.findViewById(R.id.enable);
	         enable.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
				 //   Calendar time=alarm.getAlarmTime();
				           origAtime.add(Calendar.MINUTE, -Integer.valueOf(alarm.getDa()));
					            alarm.setAlarmTime(origAtime);
					            
					            
				           d.dismiss();
				           AlarmPreferencesActivity.this.onResume();
				}
			});
	         
	         
	         Button disable=(Button)d.findViewById(R.id.cancel);
	         disable.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					alarm.setDA("00");
					d.dismiss();
				}
			});
	         
	       d.show();
	}*/
	
}
