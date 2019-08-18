package com.piehi3.rpiledclient;


import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import java.io.*;
import java.net.*;

public class ClientHandler extends Thread{

    Socket client;
    DataOutputStream data_out;
    DataInputStream data_in;
    String init_data;

    ColorHandler color_handler;
    Activity current_activity;

    TextView text_status;//displaying the current status of the connection

    private String message_out;
    private Boolean is_new_message;
    private String server_name;
    private int port;
    private Boolean is_connected;


    public ClientHandler(String server_name, int port,View text_status,Activity current_activity){

        this.message_out = "";
        this.init_data="";
        this.is_new_message = false;
        this.server_name = server_name;
        this.port = port;
        this.is_connected = false;
        this.current_activity = current_activity;

        this.text_status = (TextView)text_status;
    }

    public void run() {
        try{
            //sets up basic socket connection
            System.out.println("Connecting to " + server_name + " on port " + port);
            this.client = new Socket(server_name,port);
            System.out.println("Now Connected to " + this.client.getRemoteSocketAddress());

            //sets the local output stream
            OutputStream  output_stream = this.client.getOutputStream();
            this.data_out = new DataOutputStream(output_stream);
            //data_out.writeUTF(message);

            //input data from the server
            InputStream input_stream = this.client.getInputStream();
            this.data_in = new DataInputStream(input_stream);
            this.init_data = data_in.readUTF();
            System.out.println("Inital data " + this.init_data);
            color_handler.setDisplayedRGB(ColorConverter.parseData(this.init_data));
            changeConectionSatus(true);

            while (true){

                //pushes data to the socket output stream
                if(is_new_message){
                    try {
                        data_out.writeUTF(message_out);
                        if (message_out.equals("exit") || message_out.equals("kill")) {
                            this.close_socket();
                            break;
                        }
                        is_new_message = false;
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                Thread.sleep(10);//stop this thread from drawing too many resources and pings the server too often
            }


        }catch (IOException | InterruptedException e){
            e.printStackTrace();//prints error :/
        }
    }

    public void close(){
        sendMessage("exit");
    }

    private void close_socket() throws IOException{
        data_out.writeUTF("close");
        client.close();
        changeConectionSatus(false);
        System.out.println("Disconnected from server " + client.getRemoteSocketAddress());
    }

    public boolean sendMessage(String message){
        this.message_out = message;
        this.is_new_message = true;
        return !(message.equals("exit")||message.equals("kill"));
    }

    public void sendColorUpdate(int[] rgb){
        System.out.println("pushing update to server " + color_handler.current_rgb[0] + " " + + color_handler.current_rgb[1] + " " + color_handler.current_rgb[2]);
        this.sendMessage(ColorConverter.rgb_to_message(rgb));//queues the new rgb value to be pushed to the server
    }

    private void changeConectionSatus(final boolean is_connected){
        this.is_connected=is_connected;
        current_activity.runOnUiThread(new Runnable() {
            final boolean CONNECTION = is_connected;//why must java be like this
            @Override
            public void run() {
                if(CONNECTION){
                    text_status.setText("CONNECTED");
                }else {
                    text_status.setText("DISCONNECTED");
                }
            }
        });
    }

    public Boolean getConnetionStatus(){
        return is_connected;
    }

}
