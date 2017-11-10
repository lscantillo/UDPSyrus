package com.uninorte.udpsyrus;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.*;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


import static android.content.ContentValues.TAG;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;



import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;


import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;




public class MainActivity extends AppCompatActivity {


    //Button buttonConnect;
    TextView textViewState, textViewRx,tvRpmObd,tvDatos,tvBtConnection;
    UdpClientHandler udpClientHandler;
    UdpClientThread udpClientThread;
    private LocationManager locationManager;
    private LocationListener listener;

    private static final int PETICION_PERMISO_LOCALIZACION = 101;
    private static final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Double lat,lng,lon;
    int rpm;
    String rpmValue;
    String address= "52.14.138.255";
    int port=56303;
    private String deviceAddress;
    private BluetoothSocket socket;
    private BluetoothAdapter btAdapter = null;
    private BluetoothDevice device = null;
    private Set<BluetoothDevice> pairedDevices;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvBtConnection = (TextView) findViewById(R.id.btConnection);
        textViewState = (TextView) findViewById(R.id.state);
        textViewRx = (TextView) findViewById(R.id.received);
        tvRpmObd = (TextView) findViewById(R.id.rpmOBD);
        tvDatos= (TextView) findViewById(R.id.tvDatos);
        // Bluetooth code
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null) {
            //Show a mensag. that thedevice has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
        } else
        {
            if (btAdapter.isEnabled())
            { }
            else
            {
                //Ask to the user turn the bluetooth on
                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBTon,1);
            }
        }

        //miUbicacion();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        udpClientHandler = new UdpClientHandler(this);




    }






            private void updateState(String state){
                textViewState.setText(state);
            }

            private void updateRxMsg(String rxmsg){
                textViewRx.append(rxmsg + "\n");
            }

            private void clientEnd(){
                //udpClientThread = null;
                textViewState.setText("ClientEnd()");
                //buttonConnect.setEnabled(true);
            }


        public static class UdpClientHandler extends Handler {
                public static final int UPDATE_STATE = 0;
                public static final int UPDATE_MSG = 1;
                public static final int UPDATE_END = 2;
                private MainActivity parent;

                public UdpClientHandler(MainActivity parent) {
                    super();
                    this.parent = parent;
                }

                @Override
                public void handleMessage(Message msg) {

                    switch (msg.what){
                        case UPDATE_STATE:
                            parent.updateState((String)msg.obj);
                            break;
                        case UPDATE_MSG:
                            parent.updateRxMsg((String)msg.obj);
                            break;
                        case UPDATE_END:
                            parent.clientEnd();
                            break;
                        default:
                            super.handleMessage(msg);
                    }

                }
        }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }


    void configure_button() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }

        // this code won'textView execute IF permissions are not allowed, because in the line above there is return statement.
        locationManager.requestLocationUpdates("gps", 5000, 0, listener);
    }

    //INICIO BT

    public void btnBtOnClick(View view) {
        final ArrayList<String> deviceStrs = new ArrayList<>();
        final ArrayList<String> devices = new ArrayList<>();
        pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0)
        {
            // this for instruction add the paired devices into the list
            for (BluetoothDevice device : pairedDevices)
            {
                deviceStrs.add(device.getName() + "\n" + device.getAddress());
                devices.add(device.getAddress());
            }
        }

        // Show the list of devices
        final AlertDialog.Builder alertDialogBt = new AlertDialog.Builder(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.select_dialog_singlechoice,
                deviceStrs.toArray(new String[deviceStrs.size()]));

        alertDialogBt.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInt, int which) {
                dialogInt.dismiss();
                int position = ((AlertDialog) dialogInt).getListView().getCheckedItemPosition();
                deviceAddress = devices.get(position);

                btAdapter = BluetoothAdapter.getDefaultAdapter();
                device = btAdapter.getRemoteDevice(deviceAddress);
                tvBtConnection.setText(device.getName());
                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

                try {
                    socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
                    socket.connect();
                    Toast.makeText(getApplicationContext(), "Dispositivo OBD" + "\n" + "Puede proceder con obtener datos", Toast.LENGTH_LONG).show();
                    new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                    new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                    new TimeoutCommand(2000).run(socket.getInputStream(), socket.getOutputStream());
                    new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());
                    socket.getOutputStream();
                } catch (IOException | InterruptedException e) {
                    Toast.makeText(getApplicationContext(), "No es un dispositivo OBD" + "\n" + "La conexión se cerrará automaticamente", Toast.LENGTH_LONG).show();
                }
            }
        });

        alertDialogBt.setTitle("Choose Bluetooth device");
        alertDialogBt.show();

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                lat = location.getLatitude();
                lon = location.getLongitude();
                Date date = new Date();
                DecimalFormat formato = new DecimalFormat("#.00000");


                RPMCommand engineRpmCommand = new RPMCommand();

                try {
                    engineRpmCommand.run(socket.getInputStream(), socket.getOutputStream());
                    rpmValue = engineRpmCommand.getCalculatedResult();
                    tvRpmObd.setText(engineRpmCommand.getCalculatedResult());
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }


                String data = "ABCRApp/+" + formato.format(lat)+ "/" + formato.format(lon) + "/" + sdf.format(date) + "/"+rpmValue + "/" ;
                tvDatos.setText(data);
                udpClientThread = new UdpClientThread(
                        address,
                        port,
                        lat,
                        lon,
                        rpmValue,
                        udpClientHandler);
                udpClientThread.start();

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) { }

            @Override
            public void onProviderEnabled(String s) { }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
        configure_button();

    }
    //FIN BT





}
