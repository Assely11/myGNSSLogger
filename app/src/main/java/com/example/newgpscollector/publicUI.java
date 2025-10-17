package com.example.newgpscollector;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class publicUI {
    public static TextView getButton(String text, int marginButton, Context context){
        TextView button=new TextView(context);
        Resources resources=context.getResources();
        Drawable drawable=resources.getDrawable(R.drawable.mybutton);
        button.setBackground(drawable);
        button.setTextColor(Color.parseColor("#ffffff"));
        button.setText(text);
        button.setTextSize(15);
        LinearLayout.LayoutParams buttonParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,100);
        buttonParams.setMargins(200,20,200,marginButton);
        button.setLayoutParams(buttonParams);
        button.setPadding(0,20,0,0);
        button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        return button;
    }
}
