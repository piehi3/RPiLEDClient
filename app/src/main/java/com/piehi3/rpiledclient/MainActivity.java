package com.piehi3.rpiledclient;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.widget.Button;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    Display display;//current screen display, need to get screen resolution and size

    //client side variable used for connection with the server
    ClientHandler client_handler;
    ColorHandler color_handler;
    RGBEditTextLisener[] text_listeners;

    //used for displaying current status with respect to the server
    Button button_connect;
    Button button_disconnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//sets the current activity as main

        display = getWindowManager().getDefaultDisplay();//gets the current display
        //graps the size of current display size, used to calculate the center of the color wheel
        final Point size = new Point();
        display.getSize(size);

        text_listeners = new RGBEditTextLisener[3];

        //get instances of the various views
        button_connect = findViewById(R.id.buttonConnect);
        button_disconnect = findViewById(R.id.buttonDisconnect);

        color_handler =  new ColorHandler(findViewById(R.id.colorWheel),
                findViewById(R.id.currentColor),findViewById(R.id.currentRedValue),
                findViewById(R.id.currentGreenValue),findViewById(R.id.currentBlueValue));

        for(int i = 0; i < 3; i++){
            text_listeners[i] = new RGBEditTextLisener(i,color_handler);
            color_handler.text_current_rgb_values[i].addTextChangedListener(text_listeners[i]);
        }
        openNewConnection();

        //adds a touch listeser to change the color of the led when relative to the wheel
        color_handler.image_color_wheel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (client_handler != null && client_handler.getConnetionStatus()) {
                    //checks if the client is connected to the server
                    float x = event.getX() - (size.x) / 2;
                    float y = event.getY() - (size.y) / 2 + 100;
                    if (x * x + y * y <= 552 * 552) {//checks if the click was preformed on the circle
                        //calculates the rgb value of the xy point, maybe should change to just checking the pixel color?
                        int[] rgb = ColorConverter.double_array_to_int(ColorConverter.normalize_colors(ColorConverter.xyr_to_rgb(x, y, 512)));
                        color_handler.setDisplayedRGB(rgb);

                        client_handler.sendColorUpdate(rgb);
                    }
                }
                return true;
            }
        });

        button_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewConnection();
            }
        });

        button_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(client_handler!=null) {
                    if (client_handler.getConnetionStatus()) {
                        client_handler.close();
                    }
                }
            }
        });
    }

    public void openNewConnection(){
        if(client_handler == null || !client_handler.getConnetionStatus()) {
            client_handler = new ClientHandler("192.168.0.20", 6066, findViewById(R.id.textStatus));
            client_handler.start();

            for(int i = 0; i < 3; i++){
                text_listeners[i].client_handler = client_handler;
            }
        }
    }

}

