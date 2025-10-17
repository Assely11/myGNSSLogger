package com.example.newgpscollector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.GnssClock;
import android.location.GnssMeasurement;
import android.location.GnssStatus;
import android.location.Location;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StatelliteTab {
    public LinearLayout mainLinear;
    private LinearLayout headLinear;


    public StatelliteTab(Context context){
        this.mainLinear=new LinearLayout(context);
        this.headLinear=new LinearLayout(context);

        this.setMainLinear();
        this.setHeadLinear(context);
        this.mainLinear.addView(this.headLinear);
    }

    private void setMainLinear(){
        this.mainLinear.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams=new
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,0,0,0);
        this.mainLinear.setLayoutParams(layoutParams);
    }

    private void setHeadLinear(Context context){
        cChildTab childTab=new cChildTab(context,
                "方位角","高度角","类型","SVID",
                "载波频率","信噪比","历书","星历","使用");
        this.headLinear=childTab.resTab;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public LinearLayout loadChildLinears(Context context, GnssStatus status){
        LinearLayout res=new LinearLayout(context);
        res.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams=new
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,0,0,0);
        res.setLayoutParams(layoutParams);

        //Toast.makeText(context,"当前有"+status.getSatelliteCount()+"颗卫星",Toast.LENGTH_SHORT).show();
        for(int i=0;i<status.getSatelliteCount();i++){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                cChildTab childTab=new cChildTab(context,
                        status.getAzimuthDegrees(i),
                        status.getElevationDegrees(i),
                        status.getConstellationType(i),
                        status.getSvid(i),
                        status.getCarrierFrequencyHz(i),//需要新api
                        status.getCn0DbHz(i),
                        status.hasAlmanacData(i),
                        status.hasEphemerisData(i),
                        status.usedInFix(i));
                res.addView(childTab.resTab);
                //Log.e("debug",status.getSatelliteCount()+"");
            }
        }

        return res;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public LinearLayout loadMeasureChild(Context context, GnssClock clock, Collection<GnssMeasurement> gnssMeasurements){
        LinearLayout linearLayout=new LinearLayout(context);
        for(GnssMeasurement gnssMeasurement:gnssMeasurements){
            measureChildTabNew childTabNew=new measureChildTabNew();
            linearLayout.addView(childTabNew.returnBodyView(context,gnssMeasurement,clock));
            //measureChildTab childTab=new measureChildTab();
            //linearLayout.addView(childTab.returnChildView(context,gnssMeasurement,clock));
        }
        return linearLayout;
    }
}
