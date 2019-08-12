package com.piehi3.rpiledclient;


import java.io.*;
import java.net.*;

public class BasicClient extends Thread{

    Socket client;
    DataOutputStream data_out;
    DataInputStream data_in;
    String init_data;
    private String message_out;
    private Boolean is_new_message;
    private String server_name;
    private int port;
    private Boolean isConnected;


    public BasicClient(String server_name, int port){

        this.message_out = "";
        this.init_data="";
        this.is_new_message = false;
        this.server_name = server_name;
        this.port = port;
        this.isConnected = false;
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
            this.isConnected = true;

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
                Thread.sleep(10);
            }


        }catch (IOException | InterruptedException e){
            e.printStackTrace();//prints error :/
        }
    }

    private void close_socket() throws IOException{
        data_out.writeUTF("close");
        client.close();
        this.isConnected = false;
        System.out.println("Disconnected from server " + client.getRemoteSocketAddress());
    }

    public boolean sendMessage(String message){
        this.message_out = message;
        this.is_new_message = true;
        return !(message.equals("exit")||message.equals("kill"));
    }

    public void close(){
        sendMessage("exit");
    }

    public Boolean getConnetionStatus(){
        return isConnected;
    }

}
