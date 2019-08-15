package com.piehi3.rpiledclient;

import android.graphics.Color;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.lang.StringBuilder;

class ColorConverter{

    //no idea how this works, takes (h,s,v) tuple and outputs (r,g,b)
    static double[] hsv_to_rgb(@NotNull double[] hsv){
        double h,s,v;
        double r,g,b;
        r=0;
        g=0;
        b=0;
        h=hsv[0];
        s=hsv[1];
        v=hsv[2];

        double c=v*s;
        double x=c*(1-Math.abs(((h/60) % 2) - 1));
        double m=v-c;

        if(0<=h && h<60) {
            r = c;
            g = x;
        }else if(60<=h && h<120) {
            r = x;
            g = c;
        }else if(120<=h && h<180) {
            g = c;
            b = x;
        }else if(180<=h && h<240) {
            g = x;
            b = c;
        }else if(240<=h && h<300) {
            r = x;
            b = c;
        }else if(300<=h && h<360) {
            r = c;
            b = x;
        }
        return new double[]{(r+m)*255,(g+m)*255,(b+m)*255};
    }

    //x,y and radius , and a parced value pos to hue and saturation
    static double[] xyr_to_hsv(double x, double y, double r) {
        double s = 1;
        double theta = 0;

        r+=10;

        if (x == 0 && y<0) {
            theta = 3 * Math.PI / 2;
        }else if(x == 0 && y >= 0) {
            theta = Math.PI / 2;
        }else{
            if (x < 0 && y>0) {
                theta = Math.PI- Math.atan(Math.abs(y / x));
            }else if(x < 0 && y <= 0){
                theta = Math.PI + Math.atan(Math.abs(y / x));
            }else if(x > 0 && y < 0){
                theta = 2 * Math.PI - Math.atan(Math.abs(y / x));
            }else{
                theta = Math.atan(y / x);
            }
        }

        return new double[]{theta * 180 / Math.PI,Math.sqrt(x * x + y * y) / r, s};
    }

    static double[] xyr_to_rgb(double x,double y,double r){
        return hsv_to_rgb(xyr_to_hsv(x,y,r));
    }

    //clamps color value to b/w 0 and 255
    static double[] normalize_colors(double[] rgb){
        double[] nrgb = {0,0,0};
        for(int i = 0; i < nrgb.length; i++){
            if(rgb[i]>=0){
               if(rgb[i]>=255){
                   nrgb[i]=255;
               }else{
                   nrgb[i]=rgb[i];
               }
            }
        }
        return nrgb;
    }

    //takes in (R,G,B) tuple and returns a hex string
    static int rgb_to_hex(int[] rgb) {
        String output = "#";
        for(int i = 0; i < 3; i++) {
            if((rgb[i])<16)
                output+="0";
            output += Integer.toHexString((rgb[i]));
        }
        return Color.parseColor(output);
    }

    //converts and packs an rgb value to a string of 9 numbers
    //ex 255 50 6 -> "255050006"
    static String rgb_to_message(double[] rgb){
        String message = "";
        for (int i = 0; i < 3; i++){
            if(rgb[i]<10)
                message+="0";
            if(rgb[i]<100)
                message+="0";
            message+=Integer.toString((int)rgb[i]);
        }
        return message;
    }

    //parse the packed data back to intergers
    @Nullable
    static int[] parseData(@NotNull String raw_input){
        if(raw_input.length()!=9)
            return null;

        try{
            Integer.parseInt(raw_input);
        }catch(NumberFormatException | NullPointerException nfe) {
            return null;
        }

        return new int[]{Integer.parseInt(raw_input.substring(0, 3)), Integer.parseInt(raw_input.substring(3, 6)), Integer.parseInt(raw_input.substring(6, 9))};

    }

}

