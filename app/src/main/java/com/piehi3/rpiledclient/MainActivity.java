package com.piehi3.rpiledclient;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.widget.EditText;
import android.widget.ImageView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import com.piehi3.rpiledclient.ColorConverter;

public class MainActivity extends AppCompatActivity {

    Display display;

    ImageView colorWheel;

    EditText[] currentColorValues={null,null,null};
    TextView currentColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);

        currentColorValues[0] = findViewById(R.id.currentRedValue);
        currentColorValues[1] = findViewById(R.id.currentGreenValue);
        currentColorValues[2] = findViewById(R.id.currentBlueValue);
        currentColor = findViewById(R.id.currentColor);
        colorWheel = findViewById(R.id.colorWheel);

        colorWheel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN||true){
                    float x=event.getX()-(size.x)/2;
                    float y=event.getY()-(size.y)/2+100;
                    if (x*x+y*y<=552*552) {
                        double[] rgb = ColorConverter.normalize_colors(ColorConverter.xyr_to_rgb(x,y,512));
                        for(int i = 0; i <  currentColorValues.length; i++){
                            currentColorValues[i].setText(String.valueOf((int)(rgb[i])));
                        }
                        currentColor.setBackgroundColor(ColorConverter.rgb_to_hex(rgb));
                    }
                }
                return true;
            }
        });

    }
}
