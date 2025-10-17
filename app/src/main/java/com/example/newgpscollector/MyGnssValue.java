package com.example.newgpscollector;

import android.location.GnssClock;
import android.location.GnssStatus;
import android.location.GnssMeasurement;
import android.os.Build;
import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.TimeZone;

public class MyGnssValue {
    private static final double L1Frequency = 1575.42 * 1E6;
    private static final double L2Frequency = 1227.60 * 1E6;
    private static final double c_ON_NANO = 299792458E-9;
    private static final double WEEK_SECOND = 604800;
    private static final double WEEK_NANOSECOND = 604800 * 1E9;
    private static final double DAY_NANOSECOND = 86400 * 1E9;

    public final static Integer CONSTELLATION_ALL=-1;

    public static Integer NOW_CONSTELLATION=GnssStatus.CONSTELLATION_GPS;
    //public static Long _lastTime=null;

    public static String getConstellationType(int ConstellationType){
        String res="";
        switch (ConstellationType){
            case GnssStatus.CONSTELLATION_BEIDOU:
                res="BeiDou";//5
                break;
            case GnssStatus.CONSTELLATION_GALILEO:
                res="Galileo";//6
                break;
            case GnssStatus.CONSTELLATION_GLONASS:
                res="Glonass";//3
                break;
            case GnssStatus.CONSTELLATION_GPS:
                res="GPS";//1
                break;
            case GnssStatus.CONSTELLATION_IRNSS:
                res="Irnss";//7
                break;
            case GnssStatus.CONSTELLATION_QZSS:
                res="Qzss";//4
                break;
            case GnssStatus.CONSTELLATION_SBAS:
                res="Sbas";//2
                break;
            case GnssStatus.CONSTELLATION_UNKNOWN:
                res="UNKNOWN";//0
                break;
        }
        return res;
    }
    public static String getAccumulatedDeltaRangeState(int AccumulatedDeltaRangeState){
        String res="";
        switch (AccumulatedDeltaRangeState){
            case GnssMeasurement.ADR_STATE_UNKNOWN:
                res="未知";
                break;
            case GnssMeasurement.ADR_STATE_CYCLE_SLIP:
                res="周跳";
                break;
            case GnssMeasurement.ADR_STATE_RESET:
                res="重置";
                break;
            case GnssMeasurement.ADR_STATE_VALID:
                res="有效";
                break;
            default:
                res="未知";
                break;
        }
        return res;
    }
    public static String getMultipathIndicator(int MultipathIndicator){
        String res="";
        switch (MultipathIndicator){
            case GnssMeasurement.MULTIPATH_INDICATOR_DETECTED:
                res="有多路径迹象";
                break;
            case GnssMeasurement.MULTIPATH_INDICATOR_NOT_DETECTED:
                res="无多路径迹象";
                break;
            case GnssMeasurement.MULTIPATH_INDICATOR_UNKNOWN:
                res="未知";
                break;
            default:
                res="未知";
                break;
        }
        return res;
    }
    public static String getState(int State){
        String res="";
        switch (State){
            case GnssMeasurement.STATE_BDS_D2_BIT_SYNC:
                res="北斗,D2位同步";
                break;
            case GnssMeasurement.STATE_BDS_D2_SUBFRAME_SYNC:
                res="北斗,D2子帧同步";
                break;
            case GnssMeasurement.STATE_BIT_SYNC:
                res="比特同步";
                break;
            case GnssMeasurement.STATE_CODE_LOCK:
                res="代码锁定";
                break;
            case GnssMeasurement.STATE_GAL_E1BC_CODE_LOCK:
                res="伽利略,E1B/C码锁定";
                break;
            case GnssMeasurement.STATE_GAL_E1B_PAGE_SYNC:
                res="伽利略,E1B页面同步";
                break;
            case GnssMeasurement.STATE_GAL_E1C_2ND_CODE_LOCK:
                res="伽利略,E1C二级密码锁定";
                break;
            case GnssMeasurement.STATE_GLO_STRING_SYNC:
                res="Glonass,字符串同步";
                break;
            case GnssMeasurement.STATE_GLO_TOD_DECODED:
                res="Glonass,时间解码";
                break;
            case GnssMeasurement.STATE_MSEC_AMBIGUOUS:
                res="包含毫秒含糊";
                break;
            case GnssMeasurement.STATE_SBAS_SYNC:
                res="全二级同步";
                break;
            case GnssMeasurement.STATE_SUBFRAME_SYNC:
                res="子帧同步";
                break;
            case GnssMeasurement.STATE_SYMBOL_SYNC:
                res="符号同步";
                break;
            case GnssMeasurement.STATE_TOW_DECODED:
                res="时间解码";
                break;
            case GnssMeasurement.STATE_UNKNOWN:
                res="未知";
                break;
            default:
                res="未知";
                break;
        }
        return res;
    }
    public static String getHas(Boolean has){
        return (has)?"可用":"不可用";
    }

    //返回伪距
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getEstimateLength(GnssMeasurement gnssMeasurement, GnssClock clock){
        String res="";

        double pseudorange=0;
        pseudorange=calcPseudorange(clock,gnssMeasurement);

        res=String.format("%f",pseudorange*1e-3);
        return res;
    }
    //计算伪距
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Double calcPseudorange(GnssClock clock, GnssMeasurement measurement){
        double TimeNanos = clock.getTimeNanos();
        double TimeOffsetNanos = measurement.getTimeOffsetNanos();
        double FullBiasNanos = clock.hasFullBiasNanos() ? clock.getFullBiasNanos() : 0;
        double BiasNanos = clock.hasBiasNanos() ? clock.getBiasNanos() : 0;
        double ReceivedSvTimeNanos = measurement.getReceivedSvTimeNanos();
        double LeapSecond = clock.hasLeapSecond() ? clock.getLeapSecond() : 0;
        // Arrival Time
        double tTxNanos = ReceivedSvTimeNanos;

        // Transmission Time
        int weekNumber = (int) Math.floor(-(double) (FullBiasNanos) * 1E-9 / WEEK_SECOND);
        double tRxNanos = (TimeNanos + TimeOffsetNanos) - (FullBiasNanos + BiasNanos) - weekNumber * WEEK_NANOSECOND;

        switch (measurement.getConstellationType()) {
            case GnssStatus.CONSTELLATION_GALILEO:
            case GnssStatus.CONSTELLATION_GPS:
                break;
            case GnssStatus.CONSTELLATION_BEIDOU:
                tRxNanos -= 14E9;
                break;
            case GnssStatus.CONSTELLATION_GLONASS:
                tRxNanos = tRxNanos - LeapSecond * 1E9 + 3 * 3600 * 1E9;
                tRxNanos = tRxNanos % DAY_NANOSECOND;
                break;
            default:
                tRxNanos = tTxNanos;
        }

        if(tRxNanos<tTxNanos){
            tRxNanos+=GlobalValue.WEEK_NANOSECOND;
        }
        return ((tRxNanos - tTxNanos) * c_ON_NANO);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getMeasureString(GnssMeasurement measurement,GnssClock clock){
        //String res="{\n";
        String res="";
        /*
        res=res+String.format(" 自上次通道重置以来的累计增量范围:%fm;\n",measurement.getAccumulatedDeltaRangeMeters());
        res=res+String.format("累计增量范围状态:%s;\n",
                getAccumulatedDeltaRangeState(measurement.getAccumulatedDeltaRangeState()));
        res=res+String.format(" 累积增量范围的不确定性:%fm;\n",
                measurement.getAccumulatedDeltaRangeUncertaintyMeters());
        if(measurement.hasCarrierCycles()){
            res=res+String.format(" 卫星和接收机之间的全载波周期数:%d;\n",measurement.getCarrierCycles());
        }else{
            res=res+String.format(" 卫星和接收机之间的全载波周期数:未知;\n");
        }*/
        res=res+getConstellationType(measurement.getConstellationType())+String.format("-%d:\n",measurement.getSvid());
        if(measurement.hasCarrierFrequencyHz()){
            res=res+String.format(" 载波频率:%fMHz;\n",measurement.getCarrierFrequencyHz());
        }else{
            res=res+String.format(" 载波频率:未知;\n",measurement.getCarrierFrequencyHz());
        }
        /*
        if(measurement.hasCarrierPhase()){
            res=res+String.format(" RF相位:%f;\n",measurement.getCarrierPhase());
        }else{
            res=res+String.format(" RF相位:未知;\n");
        }
        if(measurement.hasCarrierPhaseUncertainty()){
            res=res+String.format(" 载波相位的不确定性:%f;\n",measurement.getCarrierPhaseUncertainty());
        }else{
            res=res+String.format(" 载波相位的不确定性:未知;\n");
        }*/
        res=res+String.format(" 载噪比密度(dB-Hz):%f;\n",measurement.getCn0DbHz());
        //res=res+String.format(" 卫星类型:%s;\n",getConstellationType(measurement.getConstellationType()));
        /*
        res=res+String.format(" 多路径状态值:%s;\n",getMultipathIndicator(measurement.getMultipathIndicator()));
        */
        res=res+String.format(" 伪距速率:%fm/s;\n",measurement.getPseudorangeRateMetersPerSecond());
        //res=res+String.format(" 伪距速率不确定性:%fm/s;\n",measurement.getPseudorangeRateUncertaintyMetersPerSecond());
        res=res+String.format(" 信号发出时间:%dns;\n",measurement.getReceivedSvTimeNanos());
        //res=res+String.format(" 信号发出时间不确定性:%dns;\n",measurement.getReceivedSvTimeUncertaintyNanos());
        /*
        if(measurement.hasSnrInDb()){
            res=res+String.format(" 信噪比:%ddb;\n",measurement.getSnrInDb());
        }else{
            res=res+String.format(" 信噪比:未知;\n");
        }
        res=res+String.format(" 同步状态:%s;\n",getState(measurement.getState()));*/
        //res=res+String.format(" SVID:%d;\n",measurement.getSvid());
        /*
        res=res+String.format(" 时间偏移:%fns;\n",measurement.getTimeOffsetNanos());
        res=res+String.format(" 全载波周期数(是否可用):%s;\n",getHas(measurement.hasCarrierCycles()));
        res=res+String.format(" 载波频率(是否可用):%s;\n",getHas(measurement.hasCarrierFrequencyHz()));
        res=res+String.format(" RF相位(是否可用):%s;\n",getHas(measurement.hasCarrierPhase()));
        res=res+String.format(" 载波相位的不确定性(是否可用):%s;\n",getHas(measurement.hasCarrierPhaseUncertainty()));
        res=res+String.format(" 信噪比(是否可用):%s;\n",getHas(measurement.hasSnrInDb()));
         */
        res=res+String.format(" 伪距:%skm;\n",getEstimateLength(measurement,clock));
        //res=res+"},\n";

        return res;
    }
    public static String getCurrentTime(){
        Date date=new Date(System.currentTimeMillis());
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return format.format(date);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getCustomString(Collection<GnssMeasurement> gnssMeasurements){
        String res="";
        String tx1="TX1:",tx2="TX2:",tx3="TX3:",tx4="TX4:";
        for (GnssMeasurement measurement:gnssMeasurements){
            if(measurement.getConstellationType()!=GnssStatus.CONSTELLATION_GPS||Math.abs(measurement.getCarrierFrequencyHz()-L1Frequency)>2e6){
                continue;
            }

            switch (measurement.getSvid()){
                case 1:
                    tx1+="1/";
                    break;
                case 4:
                    tx1+="4/";
                    break;
                case 6:
                    tx1+="6/";
                    break;
                case 19:
                    tx1+="19/";
                    break;
                case 2:
                    tx2+="2/";
                    break;
                case 12:
                    tx2+="12/";
                    break;
                case 17:
                    tx2+="17/";
                    break;
                case 23:
                    tx2+="23/";
                    break;
                case 5:
                    tx3+="5/";
                    break;
                case 7:
                    tx3+="7/";
                    break;
                case 20:
                    tx3+="20/";
                    break;
                case 31:
                    tx3+="31/";
                    break;
                case 11:
                    tx4+="11/";
                    break;
                case 16:
                    tx4+="16/";
                    break;
                case 22:
                    tx4+="22/";
                    break;
                case 32:
                    tx4+="32";
                    break;
                default:
                    break;
            }


        }

        char[] c_tx1=tx1.toCharArray();
        char[] c_tx2=tx2.toCharArray();
        char[] c_tx3=tx3.toCharArray();
        char[] c_tx4=tx4.toCharArray();

        if(c_tx1[c_tx1.length-1]=='/'){
            c_tx1[c_tx1.length-1]='\0';
        }
        if(c_tx2[c_tx2.length-1]=='/'){
            c_tx2[c_tx2.length-1]='\0';
        }
        if(c_tx3[c_tx3.length-1]=='/'){
            c_tx3[c_tx3.length-1]='\0';
        }
        if(c_tx4[c_tx4.length-1]=='/'){
            c_tx4[c_tx4.length-1]='\0';
        }

        //c_tx1.toString();
        tx1=String.valueOf(c_tx1);
        tx2=String.valueOf(c_tx2);
        tx3=String.valueOf(c_tx3);
        tx4=String.valueOf(c_tx4);

        res=tx1+"\n"+tx2+"\n"+tx3+"\n"+tx4;
        return  res;
    }

    public static float[] getUTCTime(){
        float[] time=new float[7];
        Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        int Year=calendar.get(Calendar.YEAR);
        int Month=calendar.get(Calendar.MONTH)+1;
        int Day=calendar.get(Calendar.DAY_OF_MONTH);
        int Hour=calendar.get(Calendar.HOUR_OF_DAY);
        int Min=calendar.get(Calendar.MINUTE);
        int Second=calendar.get(Calendar.SECOND);
        int milSecond=calendar.get(Calendar.MILLISECOND);
        int dayOfYear=calendar.get(Calendar.DAY_OF_YEAR);

        time[0]=new Float(Year);
        time[1]=new Float(Month);
        time[2]=new Float(Day);
        time[3]=new Float(Hour);
        time[4]=new Float(Min);
        time[5]=new Float(Second+milSecond*1e-3);
        time[6]=dayOfYear;

        return time;
    }

    public static long getGPSWeek(float[] gpsTime){
        Date gpsRefTime=new Date(1980,1,6,0,0,0);
        Date nowTime=new Date((int) (gpsTime[0]),(int)(gpsTime[1]),(int)(gpsTime[2]),
                (int)(gpsTime[3]),(int) (gpsTime[4]),(int) (gpsTime[5]));

        long millSecond=(nowTime.getTime()-gpsRefTime.getTime())+(long)(GlobalValue.dayMilliSecond);

        return (long)(millSecond/(GlobalValue.WEEK_SECOND*1000));
    }

    public static double getGPSTow(float[] gpsTime){
        long gpsWeek=getGPSWeek(gpsTime);
        Date gpsRefTime=new Date(1980,1,6,0,0,0);
        Date nowTime=new Date((int) (gpsTime[0]),(int)(gpsTime[1]),(int)(gpsTime[2]),
                (int)(gpsTime[3]),(int) (gpsTime[4]),(int) (gpsTime[5]));

        long millSecond=(nowTime.getTime()-gpsRefTime.getTime())+(long)(GlobalValue.dayMilliSecond);

        return (double) (millSecond*1e-3-gpsWeek*GlobalValue.WEEK_SECOND)+gpsTime[5]-Math.floor(gpsTime[5]);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static float[] getGNSSTime(GnssClock clock){
        float[] time=getUTCTime();
        double gnssTimeNanos=clock.getTimeNanos()-(clock.getFullBiasNanos()+clock.getBiasNanos());

        long gnssTime=(long)gnssTimeNanos;

        //Long minSecondNanos=Math.
        long minSecondNanos=Math.floorMod(gnssTime,GlobalValue.hourNanos);
        long secondNanos=Math.floorMod(minSecondNanos,GlobalValue.minNanos);

        long minute=(long)((minSecondNanos-secondNanos)*1E-9/60);
        double second=secondNanos*(1E-9);

        float[] gpsTime=new float[]{
                (float) (time[0]),(float) (time[1]),(float) (time[2]),
                (float) (time[3]),(float) (minute),(float) (second),(float) (time[6])
        };

        return gpsTime;
    }
}
