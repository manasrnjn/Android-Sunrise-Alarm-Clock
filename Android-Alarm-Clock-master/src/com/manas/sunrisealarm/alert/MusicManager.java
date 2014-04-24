package com.manas.sunrisealarm.alert;


import android.content.Context;
import android.media.MediaPlayer;

public class MusicManager {

    //ENUMERATOR
    //the fading states
    private enum FadeState {
        
        NONE,
        IN,
        OUT
    };
    
    //VARIABLES
    //the android context
    private static Context context;
    
    //the media player
    private static MediaPlayer mediaPlayer;
    
    //is true if the music manager is paused
    private static boolean paused = false;
    
    //the max volume of the music
    public static float maxVolume = 0.0f;
    //the current volume of the music player
    public static float currentVolume = 0.0f;
    //the fade speed
    public static float fadeSpeed = 0.1f;
    
    //the fading state
    private static FadeState fadeState = FadeState.IN;
    
    //PUBLIC METHODS
    /**Initalises the music manager
    @param context the android context*/
    public static void init(final Context context) {
        
        MusicManager.context = context;
    }
    
    /**Starts play back of a music file
    @param file the file to play
    @param volume the playback volume*/
    public static void play(int file, float volume) {
        
        maxVolume = volume;
        currentVolume = volume;
        
      //  if (!DebugValues.DISABLE_MUSIC) {
            
            mediaPlayer = MediaPlayer.create(context, file);
            mediaPlayer.setVolume(currentVolume, currentVolume);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
            paused = false;
        }
    //}
    
    /**Starts play back with a fade in
    @param file the file to play
    @param volume the playback volume
    @param fadeSpeed the fade in speed*/
    public static void fadeIn(int file, float volume, float fadeSpeed) {
        
        maxVolume = volume;
        MusicManager.fadeSpeed = fadeSpeed * volume;
        fadeState = FadeState.IN;
        
   //     if (!DebugValues.DISABLE_MUSIC) {
            
            mediaPlayer = MediaPlayer.create(context, file);
            mediaPlayer.setVolume(currentVolume, currentVolume);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
            paused = false;
        }
    //}

    /**Resumes the music player*/
    public static void resume() {
        
        if (mediaPlayer != null && paused) {
            
            mediaPlayer.start();
            paused = false;
        }
    }
    
    /**Pauses the music player*/
    public static void pause() {
        
        if (mediaPlayer != null) {
        
            mediaPlayer.pause();
            paused = true;
        }
    }
    
    /**Stops the media player*/
    public static void stop() {
        
        if (mediaPlayer != null) {
            
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    
    /**Stops the music and fades out first
    @param fadeSpeed the fading speed*/
    public static void fadeOut(float fadeSpeed) {
        
        MusicManager.fadeSpeed = fadeSpeed * maxVolume;
        fadeState = FadeState.OUT;
    }
    
    /**@return if the current music is fading*/
    public static boolean isFading() {
        
        return fadeState != FadeState.NONE;
    }
    
    /**Updates the music player every frame
    #WARNING: should not be manually called*/
    public static void update(MediaPlayer mp) {
       
    	
    	  
          
            switch (fadeState) {
                
                case IN: {
                	
                    if (currentVolume < maxVolume) {
                        
                        currentVolume += fadeSpeed * 1.0;
                        currentVolume = MathUtil.clamp(
                                currentVolume, 0.0f, maxVolume);
                        mp.setVolume(currentVolume, currentVolume);
                    }
                    else {
                        
                        fadeState = FadeState.NONE;
                    }
                    break;
                }
                case OUT: {
                    
                    if (currentVolume > 0.0f) {
                        
                        currentVolume -= fadeSpeed * 1.0;
                        currentVolume = MathUtil.clamp(
                                currentVolume, 0.0f, maxVolume);
                        mediaPlayer.setVolume(currentVolume, currentVolume);
                    }
                    else {
                        
                        fadeState = FadeState.NONE;
                        stop();
                    }
                    break;
                }
                default: {
                    
                    break;
                }
            }
        
    }
}