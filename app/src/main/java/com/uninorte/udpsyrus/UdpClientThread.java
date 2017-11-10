package com.uninorte.udpsyrus;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import static android.content.ContentValues.TAG;


public class UdpClientThread extends Thread  {


    String dstAddress,rpm2;
    int dstPort;
    double lat2,lng2;




    private boolean running;
    MainActivity.UdpClientHandler handler;
    private static final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");



    DatagramSocket socket;
    InetAddress address;


    public UdpClientThread(String addr, int port,double lat,double lng,String rpm ,MainActivity.UdpClientHandler handler) {
        super();
        dstAddress = addr;
        dstPort = port;
        lat2=lat;
        lng2=lng;
        rpm2=rpm;
        this.handler = handler;

    }



    public void setRunning(boolean running){
        this.running = running;
    }

    private void sendState(String state){
        handler.sendMessage(
                Message.obtain(handler,
                        MainActivity.UdpClientHandler.UPDATE_STATE, state));
    }



    @Override
    public void run() {
        sendState("connecting...");


        running = true;



       //while (running ==true) {

            try {


               // UdpClientThread(dstAddress,dstPort,lat2,lng2, );

                socket = new DatagramSocket();
                address = InetAddress.getByName(dstAddress);

                // send request
                byte[] buf = new byte[1000];

                   /* DatagramPacket packet =
                            new DatagramPacket(buf, buf.length, address, dstPort);
                    socket.send(packet);*/

                //sendState("connected");

                //testinicio
                Date date = new Date();
                DecimalFormat formato = new DecimalFormat("#.00000");



                //String data= lat +" "+lng+" "+ sdf.format(date);
                String data = "ABCRApp/+" + formato.format(lat2)+ "/" + formato.format(lng2) + "/" + sdf.format(date) + "/"+rpm2  ;
                //String outString = "HI SNIFFER!!";        // message to send
                Log.d(TAG,data + "UDP");
                buf = data.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, address, dstPort);
                socket.send(packet);
                Log.d(TAG, "Data Sended2!!!!!!!!!!");
                //testfin
                sendState("Connected");

                // get response
                packet = new DatagramPacket(buf, buf.length);
                Log.d(TAG, "Data Sended3!!!!!!!!!!");

                //socket.receive(packet);

                String line = new String(packet.getData(), 0, packet.getLength());
                Log.d(TAG, "Data Sended5!!!!!!!!!!");
                handler.sendMessage(
                        Message.obtain(handler, MainActivity.UdpClientHandler.UPDATE_MSG, line));
                Log.d(TAG, "Data Sended6!!!!!!!!!!");


                Thread.sleep(10000);



            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }  catch (InterruptedException ie)
            {
                ie.printStackTrace(); //catch the exception for thread.sleep
            }  finally {
                if (socket != null) {
                    socket.close();
                    handler.sendEmptyMessage(MainActivity.UdpClientHandler.UPDATE_END);
                }
            }

       // }
    }



}
