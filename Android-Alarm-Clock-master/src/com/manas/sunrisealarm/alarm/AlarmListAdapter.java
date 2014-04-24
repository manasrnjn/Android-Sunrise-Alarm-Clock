package com.manas.sunrisealarm.alarm;

import java.util.ArrayList;
import java.util.List;

import com.manas.sunrisealarm.R;
import com.manas.sunrisealarm.database.Database;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AlarmListAdapter extends BaseAdapter {

	private AlarmActivity alarmActivity;
	private List<Alarm> alarms = new ArrayList<Alarm>();
   
	public static final String ALARM_FIELDS[] = { Database.COLUMN_ALARM_NAME,
			Database.COLUMN_ALARM_TIME, Database.COLUMN_ALARM_DAYS };

	public AlarmListAdapter(AlarmActivity alarmActivity) {
		this.alarmActivity = alarmActivity;
		Database.init(alarmActivity);
		alarms = Database.getAll();
	}

	@Override
	public int getCount() {
		return alarms.size();
	}

	@Override
	public Object getItem(int position) {
		return alarms.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		if (null == view)
			view = LayoutInflater.from(alarmActivity).inflate(
					R.layout.alarm_list_element, null);

		
		Alarm alarm = (Alarm) getItem(position);

	
		
		TextView alarmName=(TextView) view.findViewById(R.id.textView_alarm_name);
		if(alarms.size()==0)
	     alarmName.setText("NO ALARMS AVAILABLE");
			
			else
			alarmName.setText(alarm.getAlarmName());
		TextView alarmTimeView = (TextView) view
				.findViewById(R.id.textView_alarm_time);
		alarmTimeView.setText(alarm.getAlarmTimeString());

		
			TextView alarmDaysView = (TextView) view
					.findViewById(R.id.textView_alarm_days);
			alarmDaysView.setText(alarm.getRepeatDaysString());
			
			
			ImageView alarmIcon= (ImageView) view.findViewById(R.id.alarmImage);
			
			if(alarm.getAlarmType().contains("Regular"))
				alarmIcon.setBackgroundResource(R.drawable.regular_small);
			else if(alarm.getAlarmType().contains("Sunrise"))
				alarmIcon.setBackgroundResource(R.drawable.sunrise_small);
			else
				alarmIcon.setBackgroundResource(R.drawable.sunset_small);

		return view;
	}

	public List<Alarm> getMathAlarms() {
		return alarms;
	}

	public void setMathAlarms(List<Alarm> alarms) {
		this.alarms = alarms;
	}

}
