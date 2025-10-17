package com.example.newgpscollector;

import android.app.AlertDialog;
import android.content.Context;
import android.location.GnssClock;
import android.location.GnssMeasurement;
import android.location.GnssStatus;
import android.location.Location;
import android.os.Build;
import android.os.ConditionVariable;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.RequiresApi;

import java.io.*;
import java.util.Collection;

public class Write {
    public static void write(String fileName,String text,Context context,Boolean append){
        File path=new File("sdcard/documents");
        if(!path.exists()){
            path.mkdir();
        }
        File file=new File(path,fileName);
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("提示");
        if(!file.exists()){
            try{
                file.createNewFile();
                FileOutputStream outputStream=new FileOutputStream(file,append);
                outputStream.write(text.getBytes());
                outputStream.close();
                builder.setMessage("文件写入成功/记录开始");
                //Toast.makeText(context,"文件写入成功",Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                //Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();
                builder.setMessage(e.toString());
            }
        }else{
            try{
                FileOutputStream outputStream=new FileOutputStream(file,append);
                outputStream.write(text.getBytes());
                outputStream.close();
                builder.setMessage("文件写入成功/记录开始");
                //Toast.makeText(context,"文件写入成功",Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                builder.setMessage(e.toString());
                //Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();
            }
        }
        //builder.show();
    }
    public static void writeLocation(String fileName, Location location, Context context){
        String text="提供者,卫星时间ms,纬度°,经度°,精度m,高度m,速度m/s,速度精度m/s,卫星时间ns,时间不确定性ns,当前位置的bearing°,bearing 的精度°,\n";
        text=text+location.getProvider()+",";
        text=text+String.format("%d,",location.getTime());
        text=text+String.format("%f,%f,",location.getLatitude(),location.getLongitude());
        text=text+String.format("%f,%f,",location.getAccuracy(),location.getAltitude());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            text=text+String.format("%f,%f,",location.getSpeed(),location.getSpeedAccuracyMetersPerSecond());
        }else{
            text=text+",,";
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            text=text+String.format("%d,%f,",
                    location.getElapsedRealtimeNanos(),location.getElapsedRealtimeUncertaintyNanos());
        }else{
            text=text+",,";
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            text=text+String.format("%f,%f°,\n",
                    location.getBearing(),location.getBearingAccuracyDegrees());
        }else{
            text=text+",,\n";
        }

        write(fileName,text,context,false);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void writeStatus(String fileName, GnssStatus status, Context context){
        String text="";
        text=text+"方位角,高度角,类型,SVID,载波频率,信噪比,历书,星历,使用\n";
        for(int i=0;i<status.getSatelliteCount();i++){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                text=text+String.format("%.10f,%.10f,%s,%d,%f,%f,%s,%s,%s\n",
                        status.getAzimuthDegrees(i),
                        status.getElevationDegrees(i),
                        MyGnssValue.getConstellationType(status.getConstellationType(i)),
                        status.getSvid(i),
                        status.getCarrierFrequencyHz(i)*1e-6,//需要新api
                        status.getCn0DbHz(i),
                        MyGnssValue.getHas(status.hasAlmanacData(i)),
                        MyGnssValue.getHas(status.hasEphemerisData(i)),
                        MyGnssValue.getHas(status.usedInFix(i)));
            }
        }
        write(fileName,text,context,false);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void writeMeasure(String fileName, Collection<GnssMeasurement> measurements, GnssClock clock, Context context,Boolean append){
        String text=String.format("android本地时钟,%s\n",MyGnssValue.getCurrentTime());
        if(measurements!=null&&clock!=null){
            text=text+String.format("本地硬件时钟/ns,%d\n",clock.getTimeNanos());
            text=text+String.format("本地硬件时钟与GPST的偏差(FullBiasNanos)/ns,%d\n",clock.getFullBiasNanos());
            text=text+String.format("FullBiasNanos的亚纳秒级偏差/ns,%f\n",clock.getBiasNanos());

            text=text+"自上次通道重置以来的累计增量范围/m,累计增量范围状态,累积增量范围的不确定性/m,卫星和接收机之间的全载波周期数," +
                    "载波频率/MHz,RF相位,载波相位的不确定性,载噪比密度(dB-Hz),卫星类型,多路径状态值,伪距速率,伪距速率不确定性," +
                    "信号发出时间,信号发出时间不确定性,信噪比,同步状态,SVID,时间偏移/ns,全载波周期数(是否可用),载波频率(是否可用),RF相位(是否可用)," +
                    "载波相位的不确定性(是否可用),信噪比(是否可用),伪距/km\n";

            for(GnssMeasurement measurement:measurements){
                if(MyGnssValue.NOW_CONSTELLATION!=MyGnssValue.CONSTELLATION_ALL){
                    if(measurement.getConstellationType()!= MyGnssValue.NOW_CONSTELLATION){
                        continue;
                    }
                }
                text=text+String.format("%f,",measurement.getAccumulatedDeltaRangeMeters());
                text=text+String.format("%s,", MyGnssValue.getAccumulatedDeltaRangeState(measurement.getAccumulatedDeltaRangeState()));
                text=text+String.format("%f,", measurement.getAccumulatedDeltaRangeUncertaintyMeters());
                if(measurement.hasCarrierCycles()){
                    text=text+String.format("%d,",measurement.getCarrierCycles());
                }else{
                    text=text+String.format("未知,");
                }
                if(measurement.hasCarrierFrequencyHz()){
                    text=text+String.format("%f,",measurement.getCarrierFrequencyHz()*1e-6);
                }else{
                    text=text+String.format("未知,",measurement.getCarrierFrequencyHz());
                }
                if(measurement.hasCarrierPhase()){
                    text=text+String.format("%f,",measurement.getCarrierPhase());
                }else{
                    text=text+String.format("未知,");
                }
                if(measurement.hasCarrierPhaseUncertainty()){
                    text=text+String.format("%f,",measurement.getCarrierPhaseUncertainty());
                }else{
                    text=text+String.format("未知,");
                }
                text=text+String.format("%f,",measurement.getCn0DbHz());
                text=text+String.format("%s,",MyGnssValue.getConstellationType(measurement.getConstellationType()));
                text=text+String.format("%s,",MyGnssValue.getMultipathIndicator(measurement.getMultipathIndicator()));
                text=text+String.format("%f,",measurement.getPseudorangeRateMetersPerSecond());
                text=text+String.format("%f,",measurement.getPseudorangeRateUncertaintyMetersPerSecond());
                text=text+String.format("%d,",measurement.getReceivedSvTimeNanos());
                text=text+String.format("%d,",measurement.getReceivedSvTimeUncertaintyNanos());
                if(measurement.hasSnrInDb()){
                    text=text+String.format("%d,",measurement.getSnrInDb());
                }else{
                    text=text+String.format("不可用,");
                }
                text=text+String.format("%s,",MyGnssValue.getState(measurement.getState()));
                text=text+String.format("%d,",measurement.getSvid());
                text=text+String.format("%f,",measurement.getTimeOffsetNanos());
                text=text+String.format("%s,",MyGnssValue.getHas(measurement.hasCarrierCycles()));
                text=text+String.format("%s,",MyGnssValue.getHas(measurement.hasCarrierFrequencyHz()));
                text=text+String.format("%s,",MyGnssValue.getHas(measurement.hasCarrierPhase()));
                text=text+String.format("%s,",MyGnssValue.getHas(measurement.hasCarrierPhaseUncertainty()));
                text=text+String.format("%s,",MyGnssValue.getHas(measurement.hasSnrInDb()));
                text=text+String.format("%s\n",MyGnssValue.getEstimateLength(measurement,clock));
            }
        }
        text=text+"\n";
        write(fileName,text,context,append);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void writeMeasureNew(String fileName, Collection<GnssMeasurement> measurements, GnssClock clock, Context context,Boolean append){
        String text=String.format("android本地时钟,%s\n",MyGnssValue.getCurrentTime());
        if(measurements!=null&&clock!=null){
            text=text+String.format("本地硬件时钟/ns,%d\n",clock.getTimeNanos());
            float[] gnssTime=MyGnssValue.getGNSSTime(clock);
            long week=MyGnssValue.getGPSWeek(gnssTime);
            double tow=MyGnssValue.getGPSTow(gnssTime);
            text=text+String.format("当前周数,%s\n",String.valueOf(week));
            //text=text+String.format("周内秒,%f\n",tow);
            text=text+String.format("本地硬件时钟与GPST的偏差(FullBiasNanos)/ns,%d\n",clock.getFullBiasNanos());
            text=text+String.format("FullBiasNanos的亚纳秒级偏差/ns,%f\n",clock.getBiasNanos());

            text=text+"相位观测值/m,相位状态,累积增量范围的不确定性/m,卫星和接收机之间的全载波周期数," +
                    "载波频率/MHz,RF相位,载波相位的不确定性,载噪比密度(dB-Hz),卫星类型," +
                    "多路径状态值,伪距速率,伪距速率不确定性,信号发出时间,信号发出时间不确定性," +
                    "信噪比,同步状态,SVID,时间偏移/ns,全载波周期数(是否可用),载波频率(是否可用)," +
                    "RF相位(是否可用),载波相位的不确定性(是否可用),信噪比(是否可用),伪距/km,多普勒漂移/m*s-1\n";

            for(GnssMeasurement measurement:measurements){
                if(MyGnssValue.NOW_CONSTELLATION!=MyGnssValue.CONSTELLATION_ALL){
                    if(measurement.getConstellationType()!= MyGnssValue.NOW_CONSTELLATION){
                        continue;
                    }
                }
                text=text+String.format("%f,",measurement.getAccumulatedDeltaRangeMeters());// 相位观测值/m
                text=text+String.format("%s,", MyGnssValue.getAccumulatedDeltaRangeState(measurement.getAccumulatedDeltaRangeState()));// 相位状态
                text=text+String.format("%f,", measurement.getAccumulatedDeltaRangeUncertaintyMeters());// 累积增量范围的不确定性/m
                if(measurement.hasCarrierCycles()){
                    text=text+String.format("%d,",measurement.getCarrierCycles());// 卫星和接收机之间的全载波周期数
                }else{
                    text=text+String.format("未知,");
                }
                if(measurement.hasCarrierFrequencyHz()){
                    text=text+String.format("%f,",measurement.getCarrierFrequencyHz()*1e-6);// 载波频率/MHz
                }else{
                    text=text+String.format("未知,",measurement.getCarrierFrequencyHz());
                }
                if(measurement.hasCarrierPhase()){
                    text=text+String.format("%f,",measurement.getCarrierPhase());// RF相位
                }else{
                    text=text+String.format("未知,");
                }
                if(measurement.hasCarrierPhaseUncertainty()){
                    text=text+String.format("%f,",measurement.getCarrierPhaseUncertainty());// 载波相位的不确定性
                }else{
                    text=text+String.format("未知,");
                }
                text=text+String.format("%f,",measurement.getCn0DbHz());// 载噪比密度(dB-Hz)
                text=text+String.format("%s,",MyGnssValue.getConstellationType(measurement.getConstellationType()));// 卫星类型
                text=text+String.format("%s,",MyGnssValue.getMultipathIndicator(measurement.getMultipathIndicator()));// 多路径状态值
                text=text+String.format("%f,",measurement.getPseudorangeRateMetersPerSecond());// 伪距速率
                text=text+String.format("%f,",measurement.getPseudorangeRateUncertaintyMetersPerSecond());// 伪距速率不确定性
                text=text+String.format("%d,",measurement.getReceivedSvTimeNanos());// 信号发出时间
                text=text+String.format("%d,",measurement.getReceivedSvTimeUncertaintyNanos());// 信号发出时间不确定性
                if(measurement.hasSnrInDb()){
                    text=text+String.format("%d,",measurement.getSnrInDb());// 信噪比
                }else{
                    text=text+String.format("不可用,");
                }
                text=text+String.format("%s,",MyGnssValue.getState(measurement.getState()));// 同步状态
                text=text+String.format("%d,",measurement.getSvid());// SVID
                text=text+String.format("%f,",measurement.getTimeOffsetNanos());// 时间偏移/ns
                text=text+String.format("%s,",MyGnssValue.getHas(measurement.hasCarrierCycles()));// 全载波周期数(是否可用)
                text=text+String.format("%s,",MyGnssValue.getHas(measurement.hasCarrierFrequencyHz()));// 载波频率(是否可用)
                text=text+String.format("%s,",MyGnssValue.getHas(measurement.hasCarrierPhase()));// RF相位(是否可用)
                text=text+String.format("%s,",MyGnssValue.getHas(measurement.hasCarrierPhaseUncertainty()));// 载波相位的不确定性(是否可用)
                text=text+String.format("%s,",MyGnssValue.getHas(measurement.hasSnrInDb()));// 信噪比(是否可用)
                text=text+String.format("%s,",MyGnssValue.getEstimateLength(measurement,clock));// 伪距/km
                text=text+String.format("%f\n",measurement.getPseudorangeRateMetersPerSecond());// 多普勒漂移/m*s-1
            }
        }
        text=text+"\n";
        write(fileName,text,context,append);
    }

    public static void writeIMU(String fileName,Context context,Boolean append,
                                String imuStr){
        write(fileName,"IMU,"+imuStr,context,append);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void writeMeasureIMU(String fileName,
                                       Collection<GnssMeasurement> measurements, GnssClock clock,
                                       Context context,Boolean append,
                                       String imuStr){
        String text=String.format("android本地时钟,%s\n",MyGnssValue.getCurrentTime());
        if(measurements!=null&&clock!=null){
            text=text+String.format("本地硬件时钟/ns,%d\n",clock.getTimeNanos());
            float[] gnssTime=MyGnssValue.getGNSSTime(clock);
            long week=MyGnssValue.getGPSWeek(gnssTime);
            double tow=MyGnssValue.getGPSTow(gnssTime);
            text=text+String.format("当前周数,%s\n",String.valueOf(week));
            //text=text+String.format("周内秒,%f\n",tow);
            text=text+String.format("本地硬件时钟与GPST的偏差(FullBiasNanos)/ns,%d\n",clock.getFullBiasNanos());
            text=text+String.format("FullBiasNanos的亚纳秒级偏差/ns,%f\n",clock.getBiasNanos());

            text=text+"相位观测值/m,相位状态,累积增量范围的不确定性/m,卫星和接收机之间的全载波周期数," +
                    "载波频率/MHz,RF相位,载波相位的不确定性,载噪比密度(dB-Hz),卫星类型," +
                    "多路径状态值,伪距速率,伪距速率不确定性,信号发出时间,信号发出时间不确定性," +
                    "信噪比,同步状态,SVID,时间偏移/ns,全载波周期数(是否可用),载波频率(是否可用)," +
                    "RF相位(是否可用),载波相位的不确定性(是否可用),信噪比(是否可用),伪距/km,多普勒漂移/m*s-1\n";

            for(GnssMeasurement measurement:measurements){
                if(MyGnssValue.NOW_CONSTELLATION!=MyGnssValue.CONSTELLATION_ALL){
                    if(measurement.getConstellationType()!= MyGnssValue.NOW_CONSTELLATION){
                        continue;
                    }
                }
                text=text+String.format("%f,",measurement.getAccumulatedDeltaRangeMeters());// 相位观测值/m
                text=text+String.format("%s,", MyGnssValue.getAccumulatedDeltaRangeState(measurement.getAccumulatedDeltaRangeState()));// 相位状态
                text=text+String.format("%f,", measurement.getAccumulatedDeltaRangeUncertaintyMeters());// 累积增量范围的不确定性/m
                if(measurement.hasCarrierCycles()){
                    text=text+String.format("%d,",measurement.getCarrierCycles());// 卫星和接收机之间的全载波周期数
                }else{
                    text=text+String.format("未知,");
                }
                if(measurement.hasCarrierFrequencyHz()){
                    text=text+String.format("%f,",measurement.getCarrierFrequencyHz()*1e-6);// 载波频率/MHz
                }else{
                    text=text+String.format("未知,",measurement.getCarrierFrequencyHz());
                }
                if(measurement.hasCarrierPhase()){
                    text=text+String.format("%f,",measurement.getCarrierPhase());// RF相位
                }else{
                    text=text+String.format("未知,");
                }
                if(measurement.hasCarrierPhaseUncertainty()){
                    text=text+String.format("%f,",measurement.getCarrierPhaseUncertainty());// 载波相位的不确定性
                }else{
                    text=text+String.format("未知,");
                }
                text=text+String.format("%f,",measurement.getCn0DbHz());// 载噪比密度(dB-Hz)
                text=text+String.format("%s,",MyGnssValue.getConstellationType(measurement.getConstellationType()));// 卫星类型
                text=text+String.format("%s,",MyGnssValue.getMultipathIndicator(measurement.getMultipathIndicator()));// 多路径状态值
                text=text+String.format("%f,",measurement.getPseudorangeRateMetersPerSecond());// 伪距速率
                text=text+String.format("%f,",measurement.getPseudorangeRateUncertaintyMetersPerSecond());// 伪距速率不确定性
                text=text+String.format("%d,",measurement.getReceivedSvTimeNanos());// 信号发出时间
                text=text+String.format("%d,",measurement.getReceivedSvTimeUncertaintyNanos());// 信号发出时间不确定性
                if(measurement.hasSnrInDb()){
                    text=text+String.format("%d,",measurement.getSnrInDb());// 信噪比
                }else{
                    text=text+String.format("不可用,");
                }
                text=text+String.format("%s,",MyGnssValue.getState(measurement.getState()));// 同步状态
                text=text+String.format("%d,",measurement.getSvid());// SVID
                text=text+String.format("%f,",measurement.getTimeOffsetNanos());// 时间偏移/ns
                text=text+String.format("%s,",MyGnssValue.getHas(measurement.hasCarrierCycles()));// 全载波周期数(是否可用)
                text=text+String.format("%s,",MyGnssValue.getHas(measurement.hasCarrierFrequencyHz()));// 载波频率(是否可用)
                text=text+String.format("%s,",MyGnssValue.getHas(measurement.hasCarrierPhase()));// RF相位(是否可用)
                text=text+String.format("%s,",MyGnssValue.getHas(measurement.hasCarrierPhaseUncertainty()));// 载波相位的不确定性(是否可用)
                text=text+String.format("%s,",MyGnssValue.getHas(measurement.hasSnrInDb()));// 信噪比(是否可用)
                text=text+String.format("%s,",MyGnssValue.getEstimateLength(measurement,clock));// 伪距/km
                text=text+String.format("%f\n",measurement.getPseudorangeRateMetersPerSecond());// 多普勒漂移/m*s-1
            }
        }

        text=text+"IMU:"+imuStr;
        text=text+"\n";

        write(fileName,text,context,append);
    }


//    public static void writeIMU(String fileName, int type, float[] datas, Context context,Boolean append){
//        String text=String.format("android本地时钟,%s\n",MyGnssValue.getCurrentTime());
//        if(measurements!=null&&clock!=null){
//            text=text+String.format("本地硬件时钟/ns,%d\n",clock.getTimeNanos());
//            float[] gnssTime=MyGnssValue.getGNSSTime(clock);
//            long week=MyGnssValue.getGPSWeek(gnssTime);
//            double tow=MyGnssValue.getGPSTow(gnssTime);
//            text=text+String.format("当前周数,%s\n",String.valueOf(week));
//            text=text+String.format("周内秒,%f\n",tow);
//            text=text+String.format("本地硬件时钟与GPST的偏差(FullBiasNanos)/ns,%d\n",clock.getFullBiasNanos());
//            text=text+String.format("FullBiasNanos的亚纳秒级偏差/ns,%f\n",clock.getBiasNanos());
//
//            text=text+"相位观测值/m,相位状态,累积增量范围的不确定性/m,卫星和接收机之间的全载波周期数," +
//                    "载波频率/MHz,RF相位,载波相位的不确定性,载噪比密度(dB-Hz),卫星类型," +
//                    "多路径状态值,伪距速率,伪距速率不确定性,信号发出时间,信号发出时间不确定性," +
//                    "信噪比,同步状态,SVID,时间偏移/ns,全载波周期数(是否可用),载波频率(是否可用)," +
//                    "RF相位(是否可用),载波相位的不确定性(是否可用),信噪比(是否可用),伪距/km,多普勒漂移/m*s-1\n";
//
//            for(GnssMeasurement measurement:measurements){
//                if(MyGnssValue.NOW_CONSTELLATION!=MyGnssValue.CONSTELLATION_ALL){
//                    if(measurement.getConstellationType()!= MyGnssValue.NOW_CONSTELLATION){
//                        continue;
//                    }
//                }
//                text=text+String.format("%f,",measurement.getAccumulatedDeltaRangeMeters());// 相位观测值/m
//                text=text+String.format("%s,", MyGnssValue.getAccumulatedDeltaRangeState(measurement.getAccumulatedDeltaRangeState()));// 相位状态
//                text=text+String.format("%f,", measurement.getAccumulatedDeltaRangeUncertaintyMeters());// 累积增量范围的不确定性/m
//                if(measurement.hasCarrierCycles()){
//                    text=text+String.format("%d,",measurement.getCarrierCycles());// 卫星和接收机之间的全载波周期数
//                }else{
//                    text=text+String.format("未知,");
//                }
//                if(measurement.hasCarrierFrequencyHz()){
//                    text=text+String.format("%f,",measurement.getCarrierFrequencyHz()*1e-6);// 载波频率/MHz
//                }else{
//                    text=text+String.format("未知,",measurement.getCarrierFrequencyHz());
//                }
//                if(measurement.hasCarrierPhase()){
//                    text=text+String.format("%f,",measurement.getCarrierPhase());// RF相位
//                }else{
//                    text=text+String.format("未知,");
//                }
//                if(measurement.hasCarrierPhaseUncertainty()){
//                    text=text+String.format("%f,",measurement.getCarrierPhaseUncertainty());// 载波相位的不确定性
//                }else{
//                    text=text+String.format("未知,");
//                }
//                text=text+String.format("%f,",measurement.getCn0DbHz());// 载噪比密度(dB-Hz)
//                text=text+String.format("%s,",MyGnssValue.getConstellationType(measurement.getConstellationType()));// 卫星类型
//                text=text+String.format("%s,",MyGnssValue.getMultipathIndicator(measurement.getMultipathIndicator()));// 多路径状态值
//                text=text+String.format("%f,",measurement.getPseudorangeRateMetersPerSecond());// 伪距速率
//                text=text+String.format("%f,",measurement.getPseudorangeRateUncertaintyMetersPerSecond());// 伪距速率不确定性
//                text=text+String.format("%d,",measurement.getReceivedSvTimeNanos());// 信号发出时间
//                text=text+String.format("%d,",measurement.getReceivedSvTimeUncertaintyNanos());// 信号发出时间不确定性
//                if(measurement.hasSnrInDb()){
//                    text=text+String.format("%d,",measurement.getSnrInDb());// 信噪比
//                }else{
//                    text=text+String.format("不可用,");
//                }
//                text=text+String.format("%s,",MyGnssValue.getState(measurement.getState()));// 同步状态
//                text=text+String.format("%d,",measurement.getSvid());// SVID
//                text=text+String.format("%f,",measurement.getTimeOffsetNanos());// 时间偏移/ns
//                text=text+String.format("%s,",MyGnssValue.getHas(measurement.hasCarrierCycles()));// 全载波周期数(是否可用)
//                text=text+String.format("%s,",MyGnssValue.getHas(measurement.hasCarrierFrequencyHz()));// 载波频率(是否可用)
//                text=text+String.format("%s,",MyGnssValue.getHas(measurement.hasCarrierPhase()));// RF相位(是否可用)
//                text=text+String.format("%s,",MyGnssValue.getHas(measurement.hasCarrierPhaseUncertainty()));// 载波相位的不确定性(是否可用)
//                text=text+String.format("%s,",MyGnssValue.getHas(measurement.hasSnrInDb()));// 信噪比(是否可用)
//                text=text+String.format("%s,",MyGnssValue.getEstimateLength(measurement,clock));// 伪距/km
//                text=text+String.format("%f\n",measurement.getPseudorangeRateMetersPerSecond());// 多普勒漂移/m*s-1
//            }
//        }
//        text=text+"\n";
//        write(fileName,text,context,append);
//    }

}
