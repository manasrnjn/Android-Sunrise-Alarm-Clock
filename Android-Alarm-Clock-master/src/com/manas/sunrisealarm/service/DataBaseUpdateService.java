package com.manas.sunrisealarm.service;

import java.util.Calendar;
import java.util.List;

import com.manas.sunrisealarm.alarm.Alarm;
import com.manas.sunrisealarm.alarm.SunriseCalculator;
import com.manas.sunrisealarm.database.Database;
import com.manas.sunrisealarm.preferences.AlarmPreferencesActivity;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

public class DataBaseUpdateService extends Service{

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		Database.init(getApplicationContext());
		
		AlarmPreferencesActivity watObj=new AlarmPreferencesActivity();   
	
		
		String sunriseTime=new SunriseCalculator(getApplicationContext(),"SUNRISE").onPreExecute();
         if(sunriseTime==null)
            sunriseTime="06:42";
         
         String sunsetTime=new SunriseCalculator(getApplicationContext(),"SUNSET").onPreExecute();
         if(sunsetTime==null)
            sunsetTime="06:43";
         
         SharedPreferences.Editor editor = this.getSharedPreferences("sunrisesunset",Context.MODE_PRIVATE).edit();
			
		 editor.putString("sunrisetime", sunriseTime );
		 editor.putString("sunsettime", sunsetTime);
		 editor.commit();
		
		Database.init(getApplicationContext());
		List<Alarm> alarms = Database.getAll();
		
		Calendar newAlarmTime = Calendar.getInstance();
		
		
		for(Alarm alarm : alarms){
		
			if(alarm.getAlarmType().contains("Sunrise"))
			{ 
			    String hours=alarm.getOffset().substring(1, 3);
			    String mins=alarm.getOffset().substring(4, 6);
				String sign =alarm.getOffset().substring(0, 1);
				
				
			
			
				
				newAlarmTime.set(Calendar.HOUR_OF_DAY,Integer.valueOf(sunriseTime.substring(0, 2)));
				newAlarmTime.set(Calendar.MINUTE, Integer.valueOf(sunriseTime.substring(3, 5)));
				newAlarmTime.set(Calendar.SECOND, 0);
				
				if(sign.contains("+"))
					newAlarmTime.add(Calendar.MINUTE,+(Integer.valueOf(hours)*60+Integer.valueOf(mins)));
				else
					newAlarmTime.add(Calendar.MINUTE,-(Integer.valueOf(hours)*60+Integer.valueOf(mins)));
				
				
				
				alarm.setAlarmTime(newAlarmTime);
				Database.init(getApplicationContext());
				Database.update(alarm);
				callMathAlarmScheduleService();
				
			}	
			
			if(alarm.getAlarmType().contains("Sunset"))
			{  
			    
				String hours=alarm.getOffset().substring(1, 3);
			    String mins=alarm.getOffset().substring(4, 6);
				String sign =alarm.getOffset().substring(0, 1);
				
				
			
			
				
				newAlarmTime.set(Calendar.HOUR_OF_DAY,Integer.valueOf(sunsetTime.substring(0, 2)));
				newAlarmTime.set(Calendar.MINUTE, Integer.valueOf(sunsetTime.substring(3, 5)));
				newAlarmTime.set(Calendar.SECOND, 0);
				
				if(sign.contains("+"))
					newAlarmTime.add(Calendar.MINUTE,+(Integer.valueOf(hours)*60+Integer.valueOf(mins)));
				else
					newAlarmTime.add(Calendar.MINUTE,-(Integer.valueOf(hours)*60+Integer.valueOf(mins)));
				
				
				
				alarm.setAlarmTime(newAlarmTime);
				Database.init(getApplicationContext());
				Database.update(alarm);
				callMathAlarmScheduleService();
			}	
		}

		
		
		this.stopSelf();
		return super.onStartCommand(intent, flags, startId);
	}
	private void callMathAlarmScheduleService() {
		Intent mathAlarmServiceIntent = new Intent(getApplicationContext(),
				AlarmServiceBroadcastReciever.class);
		sendBroadcast(mathAlarmServiceIntent, null);
	}
}
