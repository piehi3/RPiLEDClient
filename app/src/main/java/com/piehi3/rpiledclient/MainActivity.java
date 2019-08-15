package com.piehi3.rpiledclient;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Display display;//current screen display, need to get screen resolution and size

    ImageView image_color_wheel;//instance of the color wheel

    //client side variable used for connection with the server
    BasicClient client;
    Boolean is_connected = false;

    //used for displaying current status with respect to the server
    Button button_connect;
    Button button_disconnect;
    TextView text_status;

    EditText[] text_current_rgb_values ={null,null,null};//text used to display the current color vales to the user
    int[] current_rgb;

    TextView text_display_current_color; //has the background color of the current rgb color

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//sets the current activity as main

        current_rgb = new int[3];

        display = getWindowManager().getDefaultDisplay();//gets the current display
        //graps the size of current display size, used to calculate the center of the color wheel
        final Point size = new Point();
        display.getSize(size);

        //get instances of the various views
        button_connect = findViewById(R.id.buttonConnect);
        button_disconnect = findViewById(R.id.buttonDisconnect);
        text_status = findViewById(R.id.textStatus);

        text_current_rgb_values[0] = findViewById(R.id.currentRedValue);
        text_current_rgb_values[1] = findViewById(R.id.currentGreenValue);
        text_current_rgb_values[2] = findViewById(R.id.currentBlueValue);
        text_display_current_color = findViewById(R.id.currentColor);
        image_color_wheel = findViewById(R.id.colorWheel);


        //adds a touch listeser to change the color of the led when relative to the wheel
        image_color_wheel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                // TODO: replace with genartic update funtion
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    updateConnectionStatus();
                }

                //checks if the client is connected to the server
                float x=event.getX()-(size.x)/2;
                float y=event.getY()-(size.y)/2+100;
                if (x*x+y*y<=552*552) {//checks if the click was preformed on the circle
                    //calculates the rgb value of the xy point, maybe should change to just checking the pixel color?
                    double[] rgb = ColorConverter.normalize_colors(ColorConverter.xyr_to_rgb(x,y,512));
                    updateRGBUI(rgb);

                    if(is_connected){
                        client.sendMessage(ColorConverter.rgb_to_message(rgb));//queues the new rgb value to be pushed to the server
                    }
                }
                return true;
            }
        });

        button_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!is_connected){
                    client = new BasicClient("192.168.0.20",6066);
                    client.start();
                    updateConnectionStatus();
                }
            }
        });

        button_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(is_connected){
                    client.close();
                    updateConnectionStatus();
                }
            }
        });

        text_current_rgb_values[0].addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if(!s.equals("") && !text_current_rgb_values[0].equals(String.valueOf(current_rgb[0]))) {
                    int i = Integer.parseInt(s.toString());
                    if(i<=255 && i>=0) {
                        int[] rgb = new int[]{i,current_rgb[1],current_rgb[2]};
                        current_rgb[0]=rgb[0];
                        current_rgb[1]=rgb[1];
                        current_rgb[2]=rgb[2];
                        text_display_current_color.setBackgroundColor(ColorConverter.rgb_to_hex(rgb));
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            //TODO: add psuh to server on text comptly changed
            public void afterTextChanged(Editable s) {

            }
        });

        text_current_rgb_values[1].addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if(!s.equals("") ) {
                    int i = Integer.parseInt(s.toString());
                    if(i<=255 && i>=0) {
                        int[] rgb = new int[]{current_rgb[0],i,current_rgb[2]};
                        current_rgb[0]=rgb[0];
                        current_rgb[1]=rgb[1];
                        current_rgb[2]=rgb[2];
                        text_display_current_color.setBackgroundColor(ColorConverter.rgb_to_hex(rgb));
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            public void afterTextChanged(Editable s) { }
        });

        text_current_rgb_values[2].addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if(!s.equals("") ) {
                    int i = Integer.parseInt(s.toString());
                    if(i<=255 && i>=0) {
                        int[] rgb = new int[]{current_rgb[0],current_rgb[1],i};
                        current_rgb[0]=rgb[0];
                        current_rgb[1]=rgb[1];
                        current_rgb[2]=rgb[2];
                        text_display_current_color.setBackgroundColor(ColorConverter.rgb_to_hex(rgb));
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            public void afterTextChanged(Editable s) { }
        });

    }

    protected  void updateRGBUI(double[] rgb){
        updateRGBUI(new int[]{(int)rgb[0],(int)rgb[1],(int)rgb[2]});
    }

    //updates the rgb value of the displayed text and color
    protected  void updateRGBUI(int[] rgb){
        current_rgb[0]=rgb[0];
        current_rgb[1]=rgb[1];
        current_rgb[2]=rgb[2];
        for(int i = 0; i <  text_current_rgb_values.length; i++){
            text_current_rgb_values[i].setText(String.valueOf((rgb[i])));
        }
        text_display_current_color.setBackgroundColor(ColorConverter.rgb_to_hex(rgb));
    }

    //TODO: add lang file
    //TODO: fix null pointer checks necessity
    //updates the displayed connection status in the main activity
    protected void updateConnectionStatus(){
        if(client!=null) {//checks if the client has been initialized
            this.is_connected = client.getConnetionStatus();//sets the connection status to the current client
            if (this.is_connected) {
                text_status.setText("CONNECTED");//pushes the displayed status to true
                if (client.init_data.isEmpty()) {
                    System.out.println(client.init_data);
                    updateRGBUI(ColorConverter.parseData(client.init_data));
                    client.init_data = "";
                    return;
                }
            }
        }
        text_status.setText("DISCONNECTED");//pushes the displayed status to false
        this.is_connected = false;
    }

}
