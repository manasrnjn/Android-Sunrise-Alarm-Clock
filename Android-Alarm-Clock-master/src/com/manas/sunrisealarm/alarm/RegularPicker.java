package com.manas.sunrisealarm.alarm;

import java.util.Calendar;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.Button;
import com.manas.sunrisealarm.R;
import com.manas.sunrisealarm.alarm.TimePicker.TimeWatcher;
import com.manas.sunrisealarm.preferences.AlarmPreferencesActivity;
import com.manas.sunrisealarm.preferences.AlarmPreferencesActivity.AlarmPreferenceListAdapter;

public class RegularPicker implements TimeWatcher{
	public Dialog dialog;
	AlarmPreferenceListAdapter obj;
	AlarmPreferencesActivity watObj;
	public Button dialog_button,dialog_button_back;
	
	public RegularPicker(Context context,int w,int h,int format,AlarmPreferencesActivity watObj, AlarmPreferenceListAdapter obj)

	{         
		
		this.obj=obj;
		this.watObj=watObj;


		dialog=new Dialog(context,R.style.AppBaseTheme);

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

		dialog.getWindow().setLayout((int)(w*0.7f), (int)(h*0.47f));
		dialog.setContentView(R.layout.time_dialog);

		dialog_button_back=(Button) dialog.findViewById(R.id.back);   
		dialog_button=(Button) dialog.findViewById(R.id.dialog_next);
      
		com.manas.sunrisealarm.alarm.TimePicker t = (com.manas.sunrisealarm.alarm.TimePicker)dialog.findViewById(R.id.timePicker1);
		t.setCurrentTimeFormate(24);
		
		t.hour_display.setText(watObj.alarm.getAlarmTimeString().substring(0, 2));
		
		
		
		
		
		t.min_display.setText(watObj.alarm.getAlarmTimeString().substring(3, 5));
		t.setTimeChangedListener(this);


		

		dialog.show();
	}




	@Override
	public void onTimeChanged(int h, int m, int am_pm) {
		// TODO Auto-generated method stub
	



		Calendar newAlarmTime = Calendar.getInstance();
		newAlarmTime.set(Calendar.HOUR_OF_DAY, h);
		newAlarmTime.set(Calendar.MINUTE, m);
		newAlarmTime.set(Calendar.SECOND, 0);
		watObj.alarm.setAlarmTime(newAlarmTime);
        
		
		
		obj.setMathAlarm(watObj.getMathAlarm());
	}

}
