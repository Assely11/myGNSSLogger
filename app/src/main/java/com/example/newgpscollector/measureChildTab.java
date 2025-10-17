package com.example.newgpscollector;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.GnssClock;
import android.location.GnssMeasurement;
import android.os.Build;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.RequiresApi;

import java.lang.reflect.Parameter;

public class measureChildTab {
    private TextView viewAccumulatedDeltaRangeMeters;
    private TextView viewAccumulatedDeltaRangeState;
    private TextView viewAccumulatedDeltaRangeUncertaintyMeters;
    private TextView viewCarrierCycles;
    private TextView viewCarrierFrequencyHz;
    private TextView viewCarrierPhase;
    private TextView viewCarrierPhaseUncertainty;
    private TextView viewCn0DbHz;
    private TextView viewConstellationType;
    private TextView viewMultipathIndicator;
    private TextView viewPseudorangeRateMetersPerSecond;
    private TextView viewPseudorangeRateUncertaintyMetersPerSecond;
    private TextView viewReceivedSvTimeNanos;
    private TextView viewReceivedSvTimeUncertaintyNanos;
    private TextView viewSnrInDb;
    private TextView viewState;
    private TextView viewSvid;
    private TextView viewTimeOffsetNanos;
    private TextView viewhasCarrierCycles;
    private TextView viewhasCarrierFrequencyHz;
    private TextView viewhasCarrierPhase;
    private TextView viewhasCarrierPhaseUncertainty;
    private TextView viewhasSnrInDb;
    private TextView viewLength;

    private LinearLayout resView;

    private float textSize=11;
    private int padding=10;
    private int height=55;

    private void initChildView(Context context,Boolean head){
        this.viewAccumulatedDeltaRangeMeters=new TextView(context);
        this.viewAccumulatedDeltaRangeState=new TextView(context);
        this.viewAccumulatedDeltaRangeUncertaintyMeters=new TextView(context);
        this.viewCarrierCycles=new TextView(context);
        this.viewCarrierFrequencyHz=new TextView(context);
        this.viewCarrierPhase=new TextView(context);
        this.viewCarrierPhaseUncertainty=new TextView(context);
        this.viewCn0DbHz=new TextView(context);
        this.viewConstellationType=new TextView(context);
        this.viewMultipathIndicator=new TextView(context);
        this.viewPseudorangeRateMetersPerSecond=new TextView(context);
        this.viewPseudorangeRateUncertaintyMetersPerSecond=new TextView(context);
        this.viewReceivedSvTimeNanos=new TextView(context);
        this.viewReceivedSvTimeUncertaintyNanos=new TextView(context);
        this.viewSnrInDb=new TextView(context);
        this.viewState=new TextView(context);
        this.viewSvid=new TextView(context);
        this.viewTimeOffsetNanos=new TextView(context);
        this.viewhasCarrierCycles=new TextView(context);
        this.viewhasCarrierFrequencyHz=new TextView(context);
        this.viewhasCarrierPhase=new TextView(context);
        this.viewhasCarrierPhaseUncertainty=new TextView(context);
        this.viewhasSnrInDb=new TextView(context);
        this.viewLength=new TextView(context);

        int color=(head)? Color.parseColor("#000000"):Color.parseColor("#FF9900");

        this.viewAccumulatedDeltaRangeMeters.setTextColor(color);
        this.viewAccumulatedDeltaRangeState.setTextColor(color);
        this.viewAccumulatedDeltaRangeUncertaintyMeters.setTextColor(color);
        this.viewCarrierCycles.setTextColor(color);
        this.viewCarrierFrequencyHz.setTextColor(color);
        this.viewCarrierPhase.setTextColor(color);
        this.viewCarrierPhaseUncertainty.setTextColor(color);
        this.viewCn0DbHz.setTextColor(color);
        this.viewConstellationType.setTextColor(color);
        this.viewMultipathIndicator.setTextColor(color);
        this.viewPseudorangeRateMetersPerSecond.setTextColor(color);
        this.viewPseudorangeRateUncertaintyMetersPerSecond.setTextColor(color);
        this.viewReceivedSvTimeNanos.setTextColor(color);
        this.viewReceivedSvTimeUncertaintyNanos.setTextColor(color);
        this.viewSnrInDb.setTextColor(color);
        this.viewState.setTextColor(color);
        this.viewSvid.setTextColor(color);
        this.viewTimeOffsetNanos.setTextColor(color);
        this.viewhasCarrierCycles.setTextColor(color);
        this.viewhasCarrierFrequencyHz.setTextColor(color);
        this.viewhasCarrierPhase.setTextColor(color);
        this.viewhasCarrierPhaseUncertainty.setTextColor(color);
        this.viewhasSnrInDb.setTextColor(color);
        this.viewLength.setTextColor(color);

        this.viewAccumulatedDeltaRangeMeters.setTextSize(textSize);
        this.viewAccumulatedDeltaRangeState.setTextSize(textSize);
        this.viewAccumulatedDeltaRangeUncertaintyMeters.setTextSize(textSize);
        this.viewCarrierCycles.setTextSize(textSize);
        this.viewCarrierFrequencyHz.setTextSize(textSize);
        this.viewCarrierPhase.setTextSize(textSize);
        this.viewCarrierPhaseUncertainty.setTextSize(textSize);
        this.viewCn0DbHz.setTextSize(textSize);
        this.viewConstellationType.setTextSize(textSize);
        this.viewMultipathIndicator.setTextSize(textSize);
        this.viewPseudorangeRateMetersPerSecond.setTextSize(textSize);
        this.viewPseudorangeRateUncertaintyMetersPerSecond.setTextSize(textSize);
        this.viewReceivedSvTimeNanos.setTextSize(textSize);
        this.viewReceivedSvTimeUncertaintyNanos.setTextSize(textSize);
        this.viewSnrInDb.setTextSize(textSize);
        this.viewState.setTextSize(textSize);
        this.viewSvid.setTextSize(textSize);
        this.viewTimeOffsetNanos.setTextSize(textSize);
        this.viewhasCarrierCycles.setTextSize(textSize);
        this.viewhasCarrierFrequencyHz.setTextSize(textSize);
        this.viewhasCarrierPhase.setTextSize(textSize);
        this.viewhasCarrierPhaseUncertainty.setTextSize(textSize);
        this.viewhasSnrInDb.setTextSize(textSize);
        this.viewLength.setTextSize(textSize);

        this.viewAccumulatedDeltaRangeMeters.setPadding(padding,padding,padding,padding);
        this.viewAccumulatedDeltaRangeState.setPadding(padding,padding,padding,padding);
        this.viewAccumulatedDeltaRangeUncertaintyMeters.setPadding(padding,padding,padding,padding);
        this.viewCarrierCycles.setPadding(padding,padding,padding,padding);
        this.viewCarrierFrequencyHz.setPadding(padding,padding,padding,padding);
        this.viewCarrierPhase.setPadding(padding,padding,padding,padding);
        this.viewCarrierPhaseUncertainty.setPadding(padding,padding,padding,padding);
        this.viewCn0DbHz.setPadding(padding,padding,padding,padding);
        this.viewConstellationType.setPadding(padding,padding,padding,padding);
        this.viewMultipathIndicator.setPadding(padding,padding,padding,padding);
        this.viewPseudorangeRateMetersPerSecond.setPadding(padding,padding,padding,padding);
        this.viewPseudorangeRateUncertaintyMetersPerSecond.setPadding(padding,padding,padding,padding);
        this.viewReceivedSvTimeNanos.setPadding(padding,padding,padding,padding);
        this.viewReceivedSvTimeUncertaintyNanos.setPadding(padding,padding,padding,padding);
        this.viewSnrInDb.setPadding(padding,padding,padding,padding);
        this.viewState.setPadding(padding,padding,padding,padding);
        this.viewSvid.setPadding(padding,padding,padding,padding);
        this.viewTimeOffsetNanos.setPadding(padding,padding,padding,padding);
        this.viewhasCarrierCycles.setPadding(padding,padding,padding,padding);
        this.viewhasCarrierFrequencyHz.setPadding(padding,padding,padding,padding);
        this.viewhasCarrierPhase.setPadding(padding,padding,padding,padding);
        this.viewhasCarrierPhaseUncertainty.setPadding(padding,padding,padding,padding);
        this.viewhasSnrInDb.setPadding(padding,padding,padding,padding);
        this.viewLength.setPadding(padding,padding,padding,padding);

        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,height);
        params.gravity= Gravity.CENTER;

        this.viewAccumulatedDeltaRangeMeters.setLayoutParams(params);
        this.viewAccumulatedDeltaRangeState.setLayoutParams(params);
        this.viewAccumulatedDeltaRangeUncertaintyMeters.setLayoutParams(params);
        this.viewCarrierCycles.setLayoutParams(params);
        this.viewCarrierFrequencyHz.setLayoutParams(params);
        this.viewCarrierPhase.setLayoutParams(params);
        this.viewCarrierPhaseUncertainty.setLayoutParams(params);
        this.viewCn0DbHz.setLayoutParams(params);
        this.viewConstellationType.setLayoutParams(params);
        this.viewMultipathIndicator.setLayoutParams(params);
        this.viewPseudorangeRateMetersPerSecond.setLayoutParams(params);
        this.viewPseudorangeRateUncertaintyMetersPerSecond.setLayoutParams(params);
        this.viewReceivedSvTimeNanos.setLayoutParams(params);
        this.viewReceivedSvTimeUncertaintyNanos.setLayoutParams(params);
        this.viewSnrInDb.setLayoutParams(params);
        this.viewState.setLayoutParams(params);
        this.viewSvid.setLayoutParams(params);
        this.viewTimeOffsetNanos.setLayoutParams(params);
        this.viewhasCarrierCycles.setLayoutParams(params);
        this.viewhasCarrierFrequencyHz.setLayoutParams(params);
        this.viewhasCarrierPhase.setLayoutParams(params);
        this.viewhasCarrierPhaseUncertainty.setLayoutParams(params);
        this.viewhasSnrInDb.setLayoutParams(params);
        this.viewLength.setLayoutParams(params);
    }

    private void initResView(Context context,Boolean head){
        this.resView=new LinearLayout(context);
        this.resView.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        this.resView.setLayoutParams(params);
        Resources resources=context.getResources();
        Drawable headDraw=resources.getDrawable(R.drawable.measurechild);
        Drawable otherDraw=resources.getDrawable(R.drawable.measurechild1);
        this.resView.setBackground(head?headDraw:otherDraw);

        this.resView.addView(this.viewAccumulatedDeltaRangeMeters);
        this.resView.addView(this.viewAccumulatedDeltaRangeState);
        this.resView.addView(this.viewAccumulatedDeltaRangeUncertaintyMeters);
        this.resView.addView(this.viewCarrierCycles);
        this.resView.addView(this.viewCarrierFrequencyHz);
        this.resView.addView(this.viewCarrierPhase);
        this.resView.addView(this.viewCarrierPhaseUncertainty);
        this.resView.addView(this.viewCn0DbHz);
        this.resView.addView(this.viewConstellationType);
        this.resView.addView(this.viewMultipathIndicator);
        this.resView.addView(this.viewPseudorangeRateMetersPerSecond);
        this.resView.addView(this.viewPseudorangeRateUncertaintyMetersPerSecond);
        this.resView.addView(this.viewReceivedSvTimeNanos);
        this.resView.addView(this.viewReceivedSvTimeUncertaintyNanos);
        this.resView.addView(this.viewSnrInDb);
        this.resView.addView(this.viewState);
        this.resView.addView(this.viewSvid);
        this.resView.addView(this.viewTimeOffsetNanos);
        this.resView.addView(this.viewhasCarrierCycles);
        this.resView.addView(this.viewhasCarrierFrequencyHz);
        this.resView.addView(this.viewhasCarrierPhase);
        this.resView.addView(this.viewhasCarrierPhaseUncertainty);
        this.resView.addView(this.viewhasSnrInDb);
        this.resView.addView(this.viewLength);
    }

    public LinearLayout returnHeadView(Context context){
        this.initChildView(context,true);

        this.viewAccumulatedDeltaRangeMeters.setText("自上次通道重置以来的累计增量范围/m");
        this.viewAccumulatedDeltaRangeState.setText("累计增量范围状态");
        this.viewAccumulatedDeltaRangeUncertaintyMeters.setText("累积增量范围的不确定性");
        this.viewCarrierCycles.setText("卫星和接收机之间的全载波周期数");
        this.viewCarrierFrequencyHz.setText("载波频率/MHZ");
        this.viewCarrierPhase.setText("RF相位");
        this.viewCarrierPhaseUncertainty.setText("载波相位的不确定性");
        this.viewCn0DbHz.setText("载噪比密度(dB-Hz)");
        this.viewConstellationType.setText("卫星类型");
        this.viewMultipathIndicator.setText("多路径状态值");
        this.viewPseudorangeRateMetersPerSecond.setText("伪距速率,m/s");
        this.viewPseudorangeRateUncertaintyMetersPerSecond.setText("伪距速率不确定性");
        this.viewReceivedSvTimeNanos.setText("信号发出时间/ns");
        this.viewReceivedSvTimeUncertaintyNanos.setText("信号发出时间不确定性/ns");
        this.viewSnrInDb.setText("信噪比/dB");
        this.viewState.setText("同步状态");
        this.viewSvid.setText("ID");
        this.viewTimeOffsetNanos.setText("时间偏移/ns");
        this.viewhasCarrierCycles.setText("全载波周期数");
        this.viewhasCarrierFrequencyHz.setText("载波频率");
        this.viewhasCarrierPhase.setText("RF相位");
        this.viewhasCarrierPhaseUncertainty.setText("载波相位的不确定性");
        this.viewhasSnrInDb.setText("信噪比");
        this.viewLength.setText("伪距/km");

        this.initResView(context,true);

        return this.resView;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public LinearLayout returnChildView(Context context, GnssMeasurement gnssMeasurement, GnssClock gnssClock){
        initChildView(context,false);

        this.viewAccumulatedDeltaRangeMeters.setText(String.format("%f",gnssMeasurement.getAccumulatedDeltaRangeMeters()));
        this.viewAccumulatedDeltaRangeState.setText(MyGnssValue.getAccumulatedDeltaRangeState(gnssMeasurement.getAccumulatedDeltaRangeState()));
        this.viewAccumulatedDeltaRangeUncertaintyMeters.setText(String.format("%f",gnssMeasurement.getAccumulatedDeltaRangeUncertaintyMeters()));

        if(gnssMeasurement.hasCarrierCycles()){
            this.viewCarrierCycles.setText(String.format("%d",gnssMeasurement.getCarrierCycles()));
        }else{
            this.viewCarrierCycles.setText("不可用");
        }
        if(gnssMeasurement.hasCarrierFrequencyHz()){
            this.viewCarrierFrequencyHz.setText(String.format("%f",gnssMeasurement.getCarrierFrequencyHz()*1e-6));
        }else{
            this.viewCarrierFrequencyHz.setText("不可用");
        }
        if(gnssMeasurement.hasCarrierPhase()){
            this.viewCarrierPhase.setText(String.format("%f",gnssMeasurement.getCarrierPhase()));
        }else{
            this.viewCarrierPhase.setText("不可用");
        }
        if(gnssMeasurement.hasCarrierPhaseUncertainty()){
            this.viewCarrierPhaseUncertainty.setText(String.format("%f",gnssMeasurement.getCarrierPhaseUncertainty()));
        }else{
            this.viewCarrierPhaseUncertainty.setText("不可用");
        }

        this.viewCn0DbHz.setText(String.format("%f",gnssMeasurement.getCn0DbHz()));
        this.viewConstellationType.setText(MyGnssValue.getConstellationType(gnssMeasurement.getConstellationType()));
        this.viewMultipathIndicator.setText(MyGnssValue.getMultipathIndicator(gnssMeasurement.getMultipathIndicator()));
        this.viewPseudorangeRateMetersPerSecond.setText(String.format("%f",gnssMeasurement.getPseudorangeRateMetersPerSecond()));
        this.viewPseudorangeRateUncertaintyMetersPerSecond.setText(String.format("%f",gnssMeasurement.getPseudorangeRateUncertaintyMetersPerSecond()));
        this.viewReceivedSvTimeNanos.setText(String.format("%d",gnssMeasurement.getReceivedSvTimeNanos()));
        this.viewReceivedSvTimeUncertaintyNanos.setText(String.format("%d",gnssMeasurement.getReceivedSvTimeUncertaintyNanos()));

        if(gnssMeasurement.hasSnrInDb()){
            this.viewSnrInDb.setText(String.format("%f",gnssMeasurement.getSnrInDb()));
        }else{
            this.viewSnrInDb.setText("不可用");
        }

        this.viewState.setText(MyGnssValue.getState(gnssMeasurement.getState()));
        this.viewSvid.setText(String.format("%d",gnssMeasurement.getSvid()));
        this.viewTimeOffsetNanos.setText(String.format("%f",gnssMeasurement.getTimeOffsetNanos()));
        this.viewhasCarrierCycles.setText(MyGnssValue.getHas(gnssMeasurement.hasCarrierCycles()));
        this.viewhasCarrierFrequencyHz.setText(MyGnssValue.getHas(gnssMeasurement.hasCarrierFrequencyHz()));
        this.viewhasCarrierPhase.setText(MyGnssValue.getHas(gnssMeasurement.hasCarrierPhase()));
        this.viewhasCarrierPhaseUncertainty.setText(MyGnssValue.getHas(gnssMeasurement.hasCarrierPhaseUncertainty()));
        this.viewhasSnrInDb.setText(MyGnssValue.getHas(gnssMeasurement.hasSnrInDb()));
        this.viewLength.setText(MyGnssValue.getEstimateLength(gnssMeasurement,gnssClock));

        initResView(context,false);
        return this.resView;
    }
}
