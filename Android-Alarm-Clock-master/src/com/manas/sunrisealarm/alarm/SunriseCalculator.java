package com.manas.sunrisealarm.alarm;



import java.util.Calendar;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

public class SunriseCalculator implements LocationListener{

	private Context activity;
	
	 private LocationManager locationManager;
	 String officialSunrise,alarmtype,officialSunset;
	 String provider;
	public SunriseCalculator(Context context ,String alarmtype) 
	{

		this.activity=context;
		this.alarmtype=alarmtype;
		onPreExecute();

	}

	public String onPreExecute() 
	{          
    

		 Location location=null;
	
		   
		 locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
		
		   try{
		    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 400, 1, this);
		
		     location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		   }
		   catch(Exception e)
		   {
			   
			   Toast.makeText(activity, "Network not Available", Toast.LENGTH_SHORT).show();
		   }
		    if (location != null) {
		        
		        onLocationChanged(location);
		        
		       
		    }
		   
		    
		    SharedPreferences.Editor editor = activity.getSharedPreferences("sunrisesunset",Context.MODE_PRIVATE).edit();
			
			 editor.putString("sunrisetime", officialSunrise );
			 editor.putString("sunsettime", officialSunset);
			 editor.commit();
		    
		 
			
			 
			 if(alarmtype.contains("SUNRISE"))
			return officialSunrise;
		    
		    else
		    	return officialSunset;

		
	}
	
	
   

   
	    
	   
	   
	   
      
      
      
 
		
	

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		 com.luckycatlabs.sunrisesunset.dto.Location location1 = new com.luckycatlabs.sunrisesunset.dto.Location(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
		   
		  
		    Calendar calendar=Calendar.getInstance();
		    SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location1, calendar.getTimeZone());
		  
		    calendar.add(Calendar.DAY_OF_MONTH, 1);
		    officialSunrise = calculator.getOfficialSunriseForDate(calendar.getInstance());
		    officialSunset=calculator.getOfficialSunsetForDate(calendar.getInstance());
		    locationManager.removeUpdates(this);
		   
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
}
