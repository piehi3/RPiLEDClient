package com.piehi3.rpiledclient;

import android.text.Editable;
import android.text.TextWatcher;

public class RGBEditTextLisener implements TextWatcher {

    ColorHandler color_handler;
    ClientHandler client_handler;
    int rgb_index;

    public RGBEditTextLisener(int rgb_index,ColorHandler color_handler){
        this.color_handler = color_handler;
        this.rgb_index = rgb_index;
    }

    public void onTextChanged(CharSequence s, int start, int before,
                              int count) {
        if(client_handler != null && client_handler.getConnetionStatus()){
            if(!s.equals("")) {
            int new_color = Integer.parseInt(s.toString());
                if(new_color>=0 && new_color<=255) {
                    color_handler.current_rgb[rgb_index]=new_color;
                    color_handler.text_display_current_color.setBackgroundColor(ColorConverter.rgb_to_hex(color_handler.current_rgb));
                }
            }
        }
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    //TODO: push updates to server
    public void afterTextChanged(Editable s) {
        if(client_handler != null && client_handler.getConnetionStatus()){
            client_handler.sendColorUpdate(color_handler.current_rgb);
        }
    }
}
