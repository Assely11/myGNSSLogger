package com.example.newgpscollector;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.location.GnssStatus;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class cChildTab {
    private TextView azimuthD;
    private TextView elevationD;
    private TextView type;
    private TextView svid;
    private TextView frequency;
    //private TextView BasedCn0d;
    private TextView Cn0d;
    private TextView almanacD;
    private TextView ephemerisD;
    private TextView used;
    private float textSize=11;

    public LinearLayout resTab;

    private void initText(Context context,boolean head){
        this.azimuthD=new TextView(context);
        this.elevationD=new TextView(context);
        this.type=new TextView(context);
        this.svid=new TextView(context);
        this.frequency=new TextView(context);
        //this.BasedCn0d=new TextView(context);
        this.Cn0d=new TextView(context);
        this.almanacD=new TextView(context);
        this.ephemerisD=new TextView(context);
        this.used=new TextView(context);

        this.azimuthD.setTextSize(textSize);
        this.elevationD.setTextSize(textSize);
        this.type.setTextSize(textSize);
        this.svid.setTextSize(textSize);
        this.frequency.setTextSize(textSize-1);
        //this.BasedCn0d.setTextSize(textSize);
        this.Cn0d.setTextSize(textSize);
        this.almanacD.setTextSize(textSize);
        this.ephemerisD.setTextSize(textSize);
        this.used.setTextSize(textSize);

        this.azimuthD.setGravity(Gravity.CENTER);
        this.elevationD.setGravity(Gravity.CENTER);
        this.type.setGravity(Gravity.CENTER);
        this.svid.setGravity(Gravity.CENTER);
        this.frequency.setGravity(Gravity.CENTER);
        //this.BasedCn0d.setGravity(Gravity.CENTER);
        this.Cn0d.setGravity(Gravity.CENTER);
        this.almanacD.setGravity(Gravity.CENTER);
        this.ephemerisD.setGravity(Gravity.CENTER);
        this.used.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams mostParams=new LinearLayout.LayoutParams(120, LinearLayout.LayoutParams.WRAP_CONTENT);
        mostParams.gravity=Gravity.CENTER;
        this.azimuthD.setLayoutParams(mostParams);
        this.elevationD.setLayoutParams(mostParams);

        LinearLayout.LayoutParams intParams=new LinearLayout.LayoutParams(85,LinearLayout.LayoutParams.WRAP_CONTENT);
        intParams.gravity=Gravity.CENTER;
        this.type.setLayoutParams(mostParams);
        this.svid.setLayoutParams(intParams);

        LinearLayout.LayoutParams wideParams=new LinearLayout.LayoutParams(120,LinearLayout.LayoutParams.WRAP_CONTENT);
        wideParams.gravity=Gravity.CENTER;
        this.frequency.setLayoutParams(wideParams);
        //this.BasedCn0d.setLayoutParams(mostParams);
        this.Cn0d.setLayoutParams(mostParams);

        this.almanacD.setLayoutParams(intParams);
        this.ephemerisD.setLayoutParams(intParams);
        this.used.setLayoutParams(intParams);

        this.azimuthD.getPaint().setFakeBoldText(true);
        this.elevationD.getPaint().setFakeBoldText(true);
        this.type.getPaint().setFakeBoldText(true);
        this.svid.getPaint().setFakeBoldText(true);
        this.frequency.getPaint().setFakeBoldText(true);
        //this.BasedCn0d.getPaint().setFakeBoldText(true);
        this.Cn0d.getPaint().setFakeBoldText(true);
        this.almanacD.getPaint().setFakeBoldText(true);
        this.ephemerisD.getPaint().setFakeBoldText(true);
        this.used.getPaint().setFakeBoldText(true);

        int color=(head)?Color.parseColor("#000000"):Color.parseColor("#FF9900");

        this.azimuthD.setTextColor(color);
        this.elevationD.setTextColor(color);
        this.type.setTextColor(color);
        this.svid.setTextColor(color);
        this.frequency.setTextColor(color);
        //this.BasedCn0d.setTextColor(color);
        this.Cn0d.setTextColor(color);
        this.almanacD.setTextColor(color);
        this.ephemerisD.setTextColor(color);
        this.used.setTextColor(color);
    }

    public cChildTab(
            Context context,
            String _azimuthD,
            String _elevationD,
            String _type,
            String _svid,
            String _frequency,
            //String _BasedCn0d,
            String _Cn0d,
            String _almanacD,
            String _ephemerisD,
            String _used
    ){
        boolean head=true;

        initText(context,head);

        this.azimuthD.setText(_azimuthD);
        this.elevationD.setText(_elevationD);
        this.type.setText(_type);
        this.svid.setText(_svid);
        this.frequency.setText(_frequency);
        //this.BasedCn0d.setText(_BasedCn0d);
        this.Cn0d.setText(_Cn0d);
        this.almanacD.setText(_almanacD);
        this.ephemerisD.setText(_ephemerisD);
        this.used.setText(_used);

        initresTab(context,head);
    }
    public cChildTab(
            Context context,
            float _azimuthD,
            float _elevationD,
            int _type,
            int _svid,
            float _frequency,
            //float _BasedCn0d,
            float _Cn0d,
            boolean _almanacD,
            boolean _ephemerisD,
            boolean _used
    ){
        boolean head=false;

        initText(context,head);

        this.azimuthD.setText(String.format("%.0f",_azimuthD));
        this.elevationD.setText(String.format("%.0f",_elevationD));
        this.type.setText(MyGnssValue.getConstellationType(_type));
        this.svid.setText(String.format("%d",_svid));
        this.frequency.setText(String.format("%.2f",_frequency*1e-6));
        //this.BasedCn0d.setText(String.format("%.1f",_BasedCn0d));
        this.Cn0d.setText(String.format("%.1f",_Cn0d));
        this.almanacD.setText((_almanacD)?"√":"-");
        this.ephemerisD.setText((_ephemerisD)?"√":"-");
        this.used.setText((_used)?"√":"-");

        initresTab(context,head);
    }
    private void initresTab(Context context,boolean head){
        this.resTab=new LinearLayout(context);
        this.resTab.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,0,10,0);
        //params.gravity=Gravity.CENTER;
        this.resTab.setLayoutParams(params);

        this.resTab.setGravity(Gravity.CENTER);
        Resources resources=context.getResources();
        Drawable drawable=resources.getDrawable(
                (head)?R.drawable.statellitechild:R.drawable.statellitechild2
        );
        this.resTab.setBackground(drawable);

        resTab.addView(this.azimuthD);
        resTab.addView(this.elevationD);
        resTab.addView(this.type);
        resTab.addView(this.svid);
        resTab.addView(this.frequency);
        //resTab.addView(this.BasedCn0d);
        resTab.addView(this.Cn0d);
        resTab.addView(this.almanacD);
        resTab.addView(this.ephemerisD);
        resTab.addView(this.used);

    }
}
