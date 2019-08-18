package com.piehi3.rpiledclient;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ColorHandler {

    Activity current_activity;

    ImageView image_color_wheel;//instance of the color wheel
    TextView text_display_current_color; //has the background color of the current rgb color

    EditText[] text_current_rgb_values ={null,null,null};//text used to display the current color vales to the user
    int[] current_rgb;//array holding the current rgb data

    public ColorHandler(View image_color_wheel,
                        View text_display_current_color, View text_current_r_value,
                        View text_current_g_value, View text_current_b_value){

        current_rgb = new int[3];

        this.image_color_wheel = (ImageView)image_color_wheel;
        this.text_display_current_color = (TextView)text_display_current_color;

        this.text_current_rgb_values[0] = (EditText)text_current_r_value;
        this.text_current_rgb_values[1] = (EditText)text_current_g_value;
        this.text_current_rgb_values[2] = (EditText)text_current_b_value;

    }

    //updates the rgb value of the displayed text and color, and the rgb color array
    //for ui purposes
    public void setDisplayedRGB(int r,int g,int b){
        current_rgb[0]=r;
        current_rgb[1]=g;
        current_rgb[2]=b;

        current_activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text_display_current_color.setBackgroundColor(ColorConverter.rgb_to_hex(current_rgb));
                for(int i = 0; i <  text_current_rgb_values.length; i++){
                    text_current_rgb_values[i].setText(String.valueOf((current_rgb[i])));
                }
            }
        });
    }

    public void setDisplayedRGB(int[] rgb){
        setDisplayedRGB(rgb[0],rgb[1],rgb[2]);
    }

}
