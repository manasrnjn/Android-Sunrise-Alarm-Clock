package com.manas.sunrisealarm.database;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import com.manas.sunrisealarm.alarm.Alarm;
import com.manas.sunrisealarm.alarm.Alarm.Difficulty;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/* 
 * usage:  
 * DatabaseSetup.init(egActivityOrContext); 
 * DatabaseSetup.createEntry() or DatabaseSetup.getContactNames() or DatabaseSetup.getDb() 
 * DatabaseSetup.deactivate() then job done 
 */

public class Database extends SQLiteOpenHelper {
	static Database instance = null;
	static SQLiteDatabase database = null;
	
	static final String DATABASE_NAME = "DB7";
	static final int DATABASE_VERSION = 7;
	
	public static final String ALARM_TABLE = "alarm";
	public static final String COLUMN_ALARM_ID = "_id";
	public static final String COLUMN_ALARM_ACTIVE = "alarm_active";	
	public static final String COLUMN_ALARM_TIME = "alarm_time";
	public static final String COLUMN_ALARM_DAYS = "alarm_days";
	public static final String COLUMN_ALARM_DIFFICULTY = "alarm_difficulty";
	public static final String COLUMN_ALARM_TONE = "alarm_tone";
	public static final String COLUMN_ALARM_VIBRATE = "alarm_vibrate";
	public static final String COLUMN_ALARM_NAME = "alarm_name";	
	public static final String COLUMN_ALARM_VOLUME = "alarm_volume";
	public static final String COLUMN_ALARM_TYPE = "alarm_type";
	public static final String COLUMN_OFFSET = "offset";
	public static final String COLUMN_FADEIN="fadein";
	public static final String COLUMN_DA="da";
	
	public static void init(Context context) {
		if (null == instance) {
			instance = new Database(context);
		}
	}

	public static SQLiteDatabase getDatabase() {
		if (null == database) {
			database = instance.getWritableDatabase();
		}
		return database;
	}

	public static void deactivate() {
		if (null != database && database.isOpen()) {
			database.close();
		}
		database = null;
		instance = null;
	}

	public static long create(Alarm alarm) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_ALARM_ACTIVE, alarm.getAlarmActive());
		cv.put(COLUMN_ALARM_TIME, alarm.getAlarmTimeString());
		
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
		    ObjectOutputStream oos = null;
		    oos = new ObjectOutputStream(bos);
		    oos.writeObject(alarm.getDays());
		    byte[] buff = bos.toByteArray();
		    
		    cv.put(COLUMN_ALARM_DAYS, buff);
		    
		} catch (Exception e){
		}		
		
		cv.put(COLUMN_ALARM_DIFFICULTY, alarm.getDifficulty().ordinal());
		cv.put(COLUMN_ALARM_TONE, alarm.getAlarmTonePath());
		cv.put(COLUMN_ALARM_VIBRATE, alarm.getVibrate());
		cv.put(COLUMN_ALARM_NAME, alarm.getAlarmName());
		cv.put(COLUMN_ALARM_VOLUME, alarm.getVolume());
		cv.put(COLUMN_ALARM_TYPE, alarm.getAlarmType());
		cv.put(COLUMN_OFFSET, alarm.getOffset());
		cv.put(COLUMN_FADEIN,alarm.getfadein());
		cv.put(COLUMN_DA,alarm.getDa());
		
		return getDatabase().insert(ALARM_TABLE, null, cv);
	}
	public static int update(Alarm alarm) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_ALARM_ACTIVE, alarm.getAlarmActive());
		cv.put(COLUMN_ALARM_TIME, alarm.getAlarmTimeString());
		
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
		    ObjectOutputStream oos = null;
		    oos = new ObjectOutputStream(bos);
		    oos.writeObject(alarm.getDays());
		    byte[] buff = bos.toByteArray();
		    
		    cv.put(COLUMN_ALARM_DAYS, buff);
		    
		} catch (Exception e){
		}		
		
		cv.put(COLUMN_ALARM_DIFFICULTY, alarm.getDifficulty().ordinal());
		cv.put(COLUMN_ALARM_TONE, alarm.getAlarmTonePath());
		cv.put(COLUMN_ALARM_VIBRATE, alarm.getVibrate());
		cv.put(COLUMN_ALARM_NAME, alarm.getAlarmName());
		cv.put(COLUMN_ALARM_VOLUME, alarm.getVolume());
		cv.put(COLUMN_ALARM_TYPE, alarm.getAlarmType());
		cv.put(COLUMN_OFFSET, alarm.getOffset());
		cv.put(COLUMN_FADEIN,alarm.getfadein());
		cv.put(COLUMN_DA,alarm.getDa());
		return getDatabase().update(ALARM_TABLE, cv, "_id=" + alarm.getId(), null);
	}
	
	public static int snooze(Alarm alarm,String time) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_ALARM_ACTIVE, alarm.getAlarmActive());
		cv.put(COLUMN_ALARM_TIME, time);
		
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
		    ObjectOutputStream oos = null;
		    oos = new ObjectOutputStream(bos);
		    oos.writeObject(alarm.getDays());
		    byte[] buff = bos.toByteArray();
		    
		    cv.put(COLUMN_ALARM_DAYS, buff);
		    
		} catch (Exception e){
		}		
		
		cv.put(COLUMN_ALARM_DIFFICULTY, alarm.getDifficulty().ordinal());
		cv.put(COLUMN_ALARM_TONE, alarm.getAlarmTonePath());
		cv.put(COLUMN_ALARM_VIBRATE, alarm.getVibrate());
		cv.put(COLUMN_ALARM_NAME, alarm.getAlarmName());
		cv.put(COLUMN_ALARM_VOLUME, alarm.getVolume());	
		cv.put(COLUMN_ALARM_TYPE, alarm.getAlarmType());
		cv.put(COLUMN_OFFSET, alarm.getOffset());
		cv.put(COLUMN_FADEIN,alarm.getfadein());
		cv.put(COLUMN_DA,alarm.getDa());
		return getDatabase().update(ALARM_TABLE, cv, "_id=" + alarm.getId(), null);
	}
	
	
	public static int deleteEntry(Alarm alarm){
		return deleteEntry(alarm.getId());
	}
	
	public static int deleteEntry(int id){
		return getDatabase().delete(ALARM_TABLE, COLUMN_ALARM_ID + "=" + id, null);
	}
	
	public static int deleteAll(){
		return getDatabase().delete(ALARM_TABLE, "1", null);
	}
	
	public static Alarm getAlarm(int id) {
		// TODO Auto-generated method stub
		String[] columns = new String[] { 
				COLUMN_ALARM_ID, 
				COLUMN_ALARM_ACTIVE,
				COLUMN_ALARM_TIME,
				COLUMN_ALARM_DAYS,
				COLUMN_ALARM_DIFFICULTY,
				COLUMN_ALARM_TONE,
				COLUMN_ALARM_VIBRATE,
				COLUMN_ALARM_NAME,
				COLUMN_ALARM_VOLUME,
				COLUMN_ALARM_TYPE,
				COLUMN_OFFSET,
				COLUMN_FADEIN
				};
		Cursor c = getDatabase().query(ALARM_TABLE, columns, COLUMN_ALARM_ID+"="+id, null, null, null,
				null);
		Alarm alarm = null;
		
		if(c.moveToFirst()){
			
			alarm =  new Alarm();
			alarm.setId(c.getInt(1));
			alarm.setAlarmActive(c.getInt(2)==1);
			alarm.setAlarmTime(c.getString(3));
			byte[] repeatDaysBytes = c.getBlob(4);
			
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(repeatDaysBytes);
			try {
				ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
				Alarm.Day[] repeatDays;
				Object object = objectInputStream.readObject();
				if(object instanceof Alarm.Day[]){
					repeatDays = (Alarm.Day[]) object;
					alarm.setDays(repeatDays);
				}								
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
						
			alarm.setDifficulty(Difficulty.values()[c.getInt(5)]);
			alarm.setAlarmTonePath(c.getString(6));
			alarm.setVibrate(c.getInt(7)==1);
			alarm.setAlarmName(c.getString(8));
			alarm.setVolume(c.getString(9));
			alarm.setVolume(c.getString(10));
			alarm.setOffset(c.getString(11));
			alarm.setfadein(c.getString(12));
			alarm.setDA(c.getString(13));
		}
		c.close();
		return alarm;
	}
	
	public static Cursor getCursor() {
		// TODO Auto-generated method stub
		String[] columns = new String[] { 
				COLUMN_ALARM_ID, 
				COLUMN_ALARM_ACTIVE,
				COLUMN_ALARM_TIME,
				COLUMN_ALARM_DAYS,
				COLUMN_ALARM_DIFFICULTY,
				COLUMN_ALARM_TONE,
				COLUMN_ALARM_VIBRATE,
				COLUMN_ALARM_NAME,
				COLUMN_ALARM_VOLUME,
				COLUMN_ALARM_TYPE,
				COLUMN_OFFSET,
				COLUMN_FADEIN,
				COLUMN_DA
				};
		return getDatabase().query(ALARM_TABLE, columns, null, null, null, null,
				null);
	}

	Database(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS " + ALARM_TABLE + " ( " 
				+ COLUMN_ALARM_ID + " INTEGER primary key autoincrement, " 
				+ COLUMN_ALARM_ACTIVE + " INTEGER NOT NULL, " 
				+ COLUMN_ALARM_TIME + " TEXT NOT NULL, " 
				+ COLUMN_ALARM_DAYS + " BLOB NOT NULL, " 
				+ COLUMN_ALARM_DIFFICULTY + " INTEGER NOT NULL, "
				+ COLUMN_ALARM_TONE + " TEXT NOT NULL, " 
				+ COLUMN_ALARM_VIBRATE + " INTEGER NOT NULL, " 
				+ COLUMN_ALARM_NAME + " TEXT NOT NULL, "
				+ COLUMN_ALARM_VOLUME + " TEXT NOT NULL, "
		        + COLUMN_ALARM_TYPE + " TEXT NOT NULL, "
		        + COLUMN_OFFSET + " TEXT, " + COLUMN_FADEIN + " TEXT, " + COLUMN_DA + " TEXT)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + ALARM_TABLE);
		onCreate(db);
	}

	public static List<Alarm> getAll() {
		List<Alarm> alarms = new ArrayList<Alarm>();
		Cursor cursor = Database.getCursor();
		if (cursor.moveToFirst()) {

			do {
				// COLUMN_ALARM_ID,
				// COLUMN_ALARM_ACTIVE,
				// COLUMN_ALARM_TIME,
				// COLUMN_ALARM_DAYS,
				// COLUMN_ALARM_DIFFICULTY,
				// COLUMN_ALARM_TONE,
				// COLUMN_ALARM_VIBRATE,
				// COLUMN_ALARM_NAME

				Alarm alarm = new Alarm();
				alarm.setId(cursor.getInt(0));
				alarm.setAlarmActive(cursor.getInt(1) == 1);
				alarm.setAlarmTime(cursor.getString(2));
				byte[] repeatDaysBytes = cursor.getBlob(3);

				ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
						repeatDaysBytes);
				try {
					ObjectInputStream objectInputStream = new ObjectInputStream(
							byteArrayInputStream);
					Alarm.Day[] repeatDays;
					Object object = objectInputStream.readObject();
					if (object instanceof Alarm.Day[]) {
						repeatDays = (Alarm.Day[]) object;
						alarm.setDays(repeatDays);
					}
				} catch (StreamCorruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}

				alarm.setDifficulty(Difficulty.values()[cursor.getInt(4)]);
				alarm.setAlarmTonePath(cursor.getString(5));
				alarm.setVibrate(cursor.getInt(6) == 1);
				alarm.setAlarmName(cursor.getString(7));
				alarm.setVolume(cursor.getString(8));
				alarm.setAlarmType(cursor.getString(9));
				alarm.setOffset(cursor.getString(10));
				alarm.setfadein(cursor.getString(11));
				alarm.setDA(cursor.getString(12));
				alarms.add(alarm);

			} while (cursor.moveToNext());			
		}
		cursor.close();
		return alarms;
	}
	
	
	

	public void updateFriend(String time) {
		
		SQLiteDatabase database = this.getWritableDatabase();	 
		String UpdateQuery = "update " + ALARM_TABLE
				+ " set alarm_time='"+time+"' where alarm_type='Sunrise'";
		
		database.execSQL(UpdateQuery);
	}
	
	
	
	
	
	
	
	
	
}