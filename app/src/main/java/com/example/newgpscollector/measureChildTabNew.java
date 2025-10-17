package com.example.newgpscollector;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.GnssClock;
import android.location.GnssMeasurement;
import android.os.Build;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.RequiresApi;

public class measureChildTabNew {
    private float textSize=11;
    private int padding=10;
    private int height=1100;
    private int width=600;
    private int widthBody=450;
    public TextView returnHeadView(Context context){
        TextView resView=new TextView(context);
        resView.setPadding(padding,padding,padding,padding);
        Resources resources=context.getResources();
        Drawable drawable=resources.getDrawable(R.drawable.measurechild);
        resView.setBackground(drawable);
        resView.setTextSize(textSize);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(width, height);
        resView.setLayoutParams(params);
        resView.setGravity(Gravity.CENTER);
        resView.setTextColor(Color.parseColor("#000000"));

        String text="";
        text=text+"自上次通道重置以来的累计增量范围/m\n";
        text=text+"累计增量范围状态\n";
        text=text+"累积增量范围的不确定性\n";
        text=text+"卫星和接收机之间的全载波周期数\n";
        text=text+"载波频率/MHZ\n";
        text=text+"RF相位\n";
        text=text+"载波相位的不确定性\n";
        text=text+"载噪比密度(dB-Hz)\n";
        text=text+"卫星类型\n";
        text=text+"多路径状态值\n";
        text=text+"伪距速率,m/s\n";
        text=text+"伪距速率不确定性\n";
        text=text+"信号发出时间/ns\n";
        text=text+"信号发出时间不确定性/ns\n";
        text=text+"信噪比/dB\n";
        text=text+"同步状态\n";
        text=text+"卫星ID\n";
        text=text+"时间偏移/ns\n";
        text=text+"全载波周期数\n";
        text=text+"载波频率\n";
        text=text+"RF相位\n";
        text=text+"载波相位的不确定性";
        text=text+"信噪比\n";
        text=text+"伪距/km";

        resView.setText(text);
        return resView;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public TextView returnBodyView(Context context, GnssMeasurement gnssMeasurement, GnssClock clock){
        TextView resView=new TextView(context);
        resView.setPadding(padding,padding,padding,padding);
        Resources resources=context.getResources();
        Drawable drawable=resources.getDrawable(R.drawable.measurechild1);
        resView.setBackground(drawable);
        resView.setTextSize(textSize);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(widthBody, height);
        resView.setLayoutParams(params);
        resView.setGravity(Gravity.CENTER);
        resView.setTextColor(Color.parseColor("#FF9900"));

        String text="";
        text=text+String.format("%f\n",gnssMeasurement.getAccumulatedDeltaRangeMeters());
        text=text+MyGnssValue.getAccumulatedDeltaRangeState(gnssMeasurement.getAccumulatedDeltaRangeState())+"\n";
        text=text+String.format("%f\n",gnssMeasurement.getAccumulatedDeltaRangeUncertaintyMeters());

        if(gnssMeasurement.hasCarrierCycles()){
            text=text+String.format("%d\n",gnssMeasurement.getCarrierCycles());
        }else{
            text=text+"无\n";
        }
        if(gnssMeasurement.hasCarrierFrequencyHz()){
            text=text+String.format("%f\n",gnssMeasurement.getCarrierFrequencyHz()*1e-6);
        }else{
            text=text+"无\n";
        }
        if(gnssMeasurement.hasCarrierPhase()){
            text=text+String.format("%f\n",gnssMeasurement.getCarrierPhase());
        }else{
            text=text+"无\n";
        }
        if(gnssMeasurement.hasCarrierPhaseUncertainty()){
            text=text+String.format("%f\n",gnssMeasurement.getCarrierPhaseUncertainty());
        }else{
            text=text+"无\n";
        }

        text=text+String.format("%f\n",gnssMeasurement.getCn0DbHz());
        text=text+MyGnssValue.getConstellationType(gnssMeasurement.getConstellationType())+"\n";
        text=text+MyGnssValue.getMultipathIndicator(gnssMeasurement.getMultipathIndicator())+"\n";
        text=text+String.format("%f\n",gnssMeasurement.getPseudorangeRateMetersPerSecond());
        text=text+String.format("%f\n",gnssMeasurement.getPseudorangeRateUncertaintyMetersPerSecond());
        text=text+String.format("%d\n",gnssMeasurement.getReceivedSvTimeNanos());
        text=text+String.format("%d\n",gnssMeasurement.getReceivedSvTimeUncertaintyNanos());

        if(gnssMeasurement.hasSnrInDb()){
            text=text+String.format("%f\n",gnssMeasurement.getSnrInDb());
        }else{
            text=text+"无\n";
        }

        text=text+MyGnssValue.getState(gnssMeasurement.getState())+"\n";
        text=text+String.format("%d\n",gnssMeasurement.getSvid());
        text=text+String.format("%f\n",gnssMeasurement.getTimeOffsetNanos());
        text=text+MyGnssValue.getHas(gnssMeasurement.hasCarrierCycles())+"\n";
        text=text+MyGnssValue.getHas(gnssMeasurement.hasCarrierFrequencyHz())+"\n";
        text=text+MyGnssValue.getHas(gnssMeasurement.hasCarrierPhase())+"\n";
        text=text+MyGnssValue.getHas(gnssMeasurement.hasCarrierPhaseUncertainty())+"\n";
        text=text+MyGnssValue.getHas(gnssMeasurement.hasSnrInDb())+"\n";
        //text=text+MyGnssValue.getEstimateLength(gnssMeasurement,clock);

        resView.setText(text);
        return resView;
    }
}
