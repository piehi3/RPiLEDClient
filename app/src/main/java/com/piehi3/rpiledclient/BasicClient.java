package com.piehi3.rpiledclient;

import java.io.*;
import java.net.*;

public class BasicClient {

    Socket client;
    DataOutputStream data_out;
    DataInputStream data_in;
    String init_data;

    public BasicClient(String server_name, int port){

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
        }catch (IOException e){
            e.printStackTrace();//prints error :/
        }
    }

    public void close() throws IOException{
        data_out.writeUTF("close");
        client.close();
        System.out.println("Disconnected from server " + client.getRemoteSocketAddress());
    }

    public boolean sendMessage(String message){
        try {
            data_out.writeUTF(message);
            if (message.equals("exit") || message.equals("kill")) {
                this.close();
                return false;
            }
            return true;
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

}
