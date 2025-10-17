package com.example.newgpscollector;

import android.location.GnssMeasurement;
import android.location.GnssStatus;
import android.os.Build;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
//import com.github.mikephil.charting.charts.CombinedChart;
//import com.github.mikephil.charting.charts.LineChart;
//import com.github.mikephil.charting.data.CombinedData;

import java.util.LinkedList;


public class MyGnssStatus {
    public static GnssStatus.Callback callback;
    public static LinkedList<Satellite> satellites=new LinkedList<>();
    public static GnssStatus gnssStatus=null;

    //public static LinkedList<IsUsed> isUseds=new LinkedList<IsUsed>();
    public static int isUsed=0;
    public static int count=0;
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void registerGnss(){
        callback=new GnssStatus.Callback() {
            @Override
            public void onSatelliteStatusChanged(@NonNull GnssStatus status) {
                super.onSatelliteStatusChanged(status);
                isUsed=0;
                satellites=new LinkedList<>();
                gnssStatus=status;

                count=status.getSatelliteCount();
                for(int i=0;i<status.getSatelliteCount();i++){
                    satellites.add(new Satellite(
                            status.getConstellationType(i),
                            status.getElevationDegrees(i),
                            status.getAzimuthDegrees(i)));
                    if(status.usedInFix(i)){
                        isUsed++;
                    }
                }
                MainActivity.gnssChanged=true;
                //status.getSvid()SVID
                //status.getCarrierFrequencyHz()载波
                //status.getBasebandCn0DbHz()
                //status.hasAlmanacData()历书
                //status.getAzimuthDegrees()方位角
                //status.getElevationDegrees()高度角
                //status.hasEphemerisData()星历
                //status.getBasebandCn0DbHz()载波信噪比
                //status.getCn0DbHz()天线信噪比
                //status.usedInFix()是否可用
                //status.getConstellationType()类型
                //(true)?a:b;
                //GnssMeasurement gnssMeasurement=GnssMeasurement()
            }
        };
    }
    /*
    public static void GnssListener(CombinedChart chart, TextView time){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            callback=new GnssStatus.Callback() {
                @Override
                public void onSatelliteStatusChanged(@NonNull GnssStatus status) {
                    super.onSatelliteStatusChanged(status);
                    int isUsed=0;
                    satellites=new LinkedList<Satellite>();
                    for(int i=0;i<status.getSatelliteCount();i++){
                        if(status.usedInFix(i)){
                            isUsed++;
                            int type=status.getConstellationType(i);
                            float elevation=status.getElevationDegrees(i);
                            float azimuth=status.getAzimuthDegrees(i);
                            satellites.add(new Satellite(type,elevation,azimuth));
                        }
                    }
                    isUseds.add(new IsUsed(,isUsed));
                    if(isUseds.size()>50){
                        isUseds.removeFirst();
                    }//控制数量，保证运算效率
                    time.setText(PDR.getCurentTime());
                    DrawLines.drawCombined(chart,satellites);
                    //Toast.makeText(MainActivity.context,isUsed+"",Toast.LENGTH_SHORT).show();
                }
            };
        }
    }*/
    static class Satellite{
        public int type;
        public float elevation;
        public float azimuthDegree;
        public Satellite(int _type,float _elevation,float _azimuth){
            type=_type;
            elevation=_elevation;
            azimuthDegree=_azimuth;
        }
    }
    /*
    static class IsUsed{
        public long time;
        public int count;
        public IsUsed(long _time,int _count){
            time=_time;
            count=_count;
        }
    }*/
}

