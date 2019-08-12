package com.piehi3.rpiledclient;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Display display;

    ImageView colorWheel;

    BasicClient client;
    Boolean isConnected = false;

    EditText[] currentColorValues={null,null,null};
    TextView currentColor;

    Button connect;
    Button disconnect;
    TextView status_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);

        connect = findViewById(R.id.buttonConnect);
        disconnect = findViewById(R.id.buttonDisconnect);
        status_text = findViewById(R.id.textStatus);

        currentColorValues[0] = findViewById(R.id.currentRedValue);
        currentColorValues[1] = findViewById(R.id.currentGreenValue);
        currentColorValues[2] = findViewById(R.id.currentBlueValue);
        currentColor = findViewById(R.id.currentColor);
        colorWheel = findViewById(R.id.colorWheel);



        colorWheel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                if(event.getAction()==MotionEvent.ACTION_DOWN)
                    updateConnectionStatus();
                if((event.getAction()==MotionEvent.ACTION_DOWN||true)&&isConnected){
                    float x=event.getX()-(size.x)/2;
                    float y=event.getY()-(size.y)/2+100;
                    if (x*x+y*y<=552*552) {
                        double[] rgb = ColorConverter.normalize_colors(ColorConverter.xyr_to_rgb(x,y,512));
                        updateRGBUI(rgb);
                        client.sendMessage(ColorConverter.rgb_to_message(rgb));
                    }
                }
                return true;
            }
        });

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isConnected){
                    client = new BasicClient("192.168.0.20",6066);
                    client.start();
                    updateConnectionStatus();
                }
            }
        });

        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConnected){
                    client.close();
                    updateConnectionStatus();
                }
            }
        });

    }

    protected  void updateRGBUI(double[] rgb){
        int[] rgbi = {(int)rgb[0],(int)rgb[1],(int)rgb[2]};
        updateRGBUI(rgbi);
    }

    protected  void updateRGBUI(int[] rgb){
        for(int i = 0; i <  currentColorValues.length; i++){
            currentColorValues[i].setText(String.valueOf((rgb[i])));
        }
        currentColor.setBackgroundColor(ColorConverter.rgb_to_hex(rgb));
    }

    //should be replace with lang file
    protected void updateConnectionStatus(){
        if(client!=null){
            this.isConnected = client.getConnetionStatus();
            if(this.isConnected){
                status_text.setText("CONNECTED");
                if(client.init_data!=""){
                    System.out.println(client.init_data);
                    updateRGBUI(ColorConverter.parseData(client.init_data));
                    client.init_data = "";
                }
            }else{
                status_text.setText("DISCONNECTED");
            }
        }else{
            status_text.setText("DISCONNECTED");
            this.isConnected=false;
        }

    }

}
