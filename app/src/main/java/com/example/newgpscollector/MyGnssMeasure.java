package com.example.newgpscollector;

import android.content.Context;
import android.icu.util.Calendar;
import android.location.GnssClock;
import android.location.GnssMeasurement;
import android.location.GnssMeasurementsEvent;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.Collection;
import java.util.Iterator;

public class MyGnssMeasure {
    public static GnssMeasurementsEvent.Callback GnssMeasureListener;
    public static Collection<GnssMeasurement> gnssMeasurements=null;
    public static GnssClock gnssClock=null;

    public static void setGnssMeasureListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            GnssMeasureListener=new GnssMeasurementsEvent.Callback() {
                @Override
                public void onGnssMeasurementsReceived(GnssMeasurementsEvent eventArgs) {
                    super.onGnssMeasurementsReceived(eventArgs);
                    //Log.e("debug",gnssMeasurements.size()+"");
                    gnssClock=eventArgs.getClock();
                    gnssMeasurements=eventArgs.getMeasurements();
                    /*
                    gnssMeasurement.getAccumulatedDeltaRangeState();//int,累计增量范围”状态
                    //GnssMeasurement.ADR_STATE_UNKNOWN;//累计差状态未知
                    //GnssMeasurement.ADR_STATE_CYCLE_SLIP;//周跳
                    //GnssMeasurement.ADR_STATE_RESET;//重置
                    //GnssMeasurement.ADR_STATE_VALID;//有效
                    gnssMeasurement.getAccumulatedDeltaRangeUncertaintyMeters();//double,累积增量范围的不确定性
                    gnssMeasurement.getCarrierCycles();//long,卫星和接收机之间的全载波周期数
                    gnssMeasurement.getCarrierFrequencyHz();//float,载波频率HZ
                    gnssMeasurement.getCarrierPhase();//double,RF相位
                    gnssMeasurement.getCarrierPhaseUncertainty();//double，载波相位的不确定性
                    gnssMeasurement.getCn0DbHz();//double,载噪比密度(dB-Hz)
                    gnssMeasurement.getConstellationType();//int,卫星类型
                    gnssMeasurement.getMultipathIndicator();//int,多路径状态值
                    //GnssMeasurement.MULTIPATH_INDICATOR_DETECTED;//有多路径迹象
                    //GnssMeasurement.MULTIPATH_INDICATOR_NOT_DETECTED;//无多路径迹象
                    //GnssMeasurement.MULTIPATH_INDICATOR_UNKNOWN；//未知
                    gnssMeasurement.getPseudorangeRateMetersPerSecond();//double,伪距速率,m/s
                    gnssMeasurement.getPseudorangeRateUncertaintyMetersPerSecond();//double,伪距速率不确定性
                    gnssMeasurement.getReceivedSvTimeNanos();//long,ns,信号发出时间
                    gnssMeasurement.getReceivedSvTimeUncertaintyNanos();//long,ns,信号发出时间不确定性
                    gnssMeasurement.getSnrInDb();//double，信噪比/dB
                    gnssMeasurement.getState();//int,同步状态
                    //GnssMeasurement.STATE_BDS_D2_BIT_SYNC;//北斗,D2位同步
                    //GnssMeasurement.STATE_BDS_D2_SUBFRAME_SYNC;//北斗,D2子帧同步
                    //GnssMeasurement.STATE_BIT_SYNC;//比特同步
                    //GnssMeasurement.STATE_CODE_LOCK;//代码锁定
                    //GnssMeasurement.STATE_GAL_E1BC_CODE_LOCK;//伽利略,E1B/C码锁定
                    //GnssMeasurement.STATE_GAL_E1B_PAGE_SYNC;//伽利略,E1B页面同步
                    //GnssMeasurement.STATE_GAL_E1C_2ND_CODE_LOCK;//伽利略,E1C二级密码锁定
                    //GnssMeasurement.STATE_GLO_STRING_SYNC;//Glonass,字符串同步
                    //GnssMeasurement.STATE_GLO_TOD_DECODED;//Glonass,时间解码
                    //GnssMeasurement.STATE_MSEC_AMBIGUOUS;//包含毫秒含糊
                    //GnssMeasurement.STATE_SBAS_SYNC;//全二级同步
                    //GnssMeasurement.STATE_SUBFRAME_SYNC;//子帧同步
                    //GnssMeasurement.STATE_SYMBOL_SYNC;//符号同步
                    //GnssMeasurement.STATE_TOW_DECODED;//时间解码
                    //GnssMeasurement.STATE_UNKNOWN;//未知
                    gnssMeasurement.getSvid();//int,ID
                    gnssMeasurement.getTimeOffsetNanos();//double,ns,时间偏移
                    gnssMeasurement.hasCarrierCycles();//boolean,全载波周期数(是否可用)
                    gnssMeasurement.hasCarrierFrequencyHz();//boolean,载波频率(是否可用)
                    gnssMeasurement.hasCarrierPhase();//boolean,RF相位(是否可用)
                    gnssMeasurement.hasCarrierPhaseUncertainty();//boolean,载波相位的不确定性(是否可用)
                    gnssMeasurement.hasSnrInDb();//boolean,信噪比(是否可用)
                     */
                }

                @Override
                public void onStatusChanged(int status) {
                    super.onStatusChanged(status);
                }
            };
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getDate(){
        //String res="";
        Calendar c=Calendar.getInstance();
        int Year=c.get(java.util.Calendar.YEAR);
        int Month=c.get(java.util.Calendar.MONTH);
        int Day=c.get(java.util.Calendar.DAY_OF_MONTH);
        int Hour=c.get(java.util.Calendar.HOUR);
        int Min=c.get(java.util.Calendar.MINUTE);

        String res=Year+"-"+Month+"-"+Day+" "+Hour+"-"+Min;

        return  res;
    }


}
