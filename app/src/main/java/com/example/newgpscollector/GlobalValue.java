package com.example.newgpscollector;

import android.Manifest;

public class GlobalValue {
    public static final String[] permissions = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.INTERNET
    };

    public static final double L1Frequency = 1575.42 * 1E6;
    public static final double L2Frequency = 1227.60 * 1E6;
    public static final double c_ON_NANO = 299792458E-9;
    public static final double WEEK_SECOND = 604800;
    public static final double WEEK_NANOSECOND = 604800 * 1E9;
    public static final double DAY_NANOSECOND = 86400 * 1E9;

    public static final double LightSpeed = 299792458;

    public static final long hourNanos=(long)(60*60*1E9);
    public static final long minNanos=(long)(60*1E9);

    public static final double glc_OMGE=7.2921151467E-5;

    public static final double GM=3.986005e14;

    public static double dayMilliSecond=24*60*60*1e3;

    public static String error_Location="useful satellites:";
}
