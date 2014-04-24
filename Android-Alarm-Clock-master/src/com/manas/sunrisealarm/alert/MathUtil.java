package com.manas.sunrisealarm.alert;




public class MathUtil {

    //VARIABLES
    //small value
    public static float EPISLON = 0.0001f;

    //radians to degrees constant
    public static float RADIANS_TO_DEGREES = 57.2957795f;
    //degrees to radians constant
    public static float DEGREES_TO_RADIANS = 0.0174532925f;

    //PUBLIC METHODS
    /**Clamps the given number between the two thresholds
    @param value the value to clamp
    @param lower the lower threshold
    @param upper the upper threshold
    @return the clamped value*/
    public static float clamp(float value, float lower, float upper) {

        if (value < lower) {

            return lower;
        }
        if (value > upper) {

            return upper;
        }

        return value;
    }

    /**Compares if two floating point numbers are equal
    @param a the first value
    @param b the second value
    @return if they are equal*/
    public static boolean floatEquals(float a, float b) {

        return Math.abs(a - b) < EPISLON;
    }

    /**Compares if the first floating point number is greater than or equal to
    the second floating point number
    @param a the first value
    @param b the second value
    @return if a is equal or greater than b*/
    public static boolean floatEqualsOrGreater(float a, float b) {

        if (a > b) {

            return true;
        }

        return Math.abs(a - b) < EPISLON;
    }

    /**Compares if the first floating point number is less than or equal to
    the second floating point number
    @param a the first value
    @param b the second value
    @return if a is equal or less than b*/
    public static boolean floatEqualsOrLess(float a, float b) {

        if (a < b) {

            return true;
        }

        return Math.abs(a - b) < EPISLON;
    }
}