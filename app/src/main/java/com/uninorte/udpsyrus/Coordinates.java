package com.uninorte.udpsyrus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class Coordinates extends AppCompatActivity  {

    TextView tvlatitud,tvlongitud,tvlat,tvlong,tvtime,tvfull;

    private static final int PETICION_PERMISO_LOCALIZACION = 101;
    private static final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //double lat;
    //double lng;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinates);

        tvlatitud = (TextView) findViewById(R.id.tvLatitud);
        tvlongitud = (TextView) findViewById(R.id.tvLongitud);
        tvlat = (TextView) findViewById(R.id.tvlat);
        tvlong = (TextView) findViewById(R.id.tvlong);
        tvtime = (TextView) findViewById(R.id.tvtime);
        tvfull= (TextView) findViewById(R.id.tvfull);
        miUbicacion();
       /* Intent datos= new Intent(Coordinates.this,MainActivity.class);
        datos.putExtra("lati",lat);
        datos.putExtra("longi",lng);*/
        //Log.d(TAG, "la latitud es:"+lat);

    }



       /* private static class coordenadas {
            public double lat;
            public double lng;

            public void setLat(double lat){
            //Aki puedes comprobar que la edad tenga unos rangos validos.. de 0 a 100 por ejmplo.
                this.lat = lat;
            }
            public double getLat(){
                return lat;
            }

            public void setLng(double lng){
                //Aki puedes comprobar que la edad tenga unos rangos validos.. de 0 a 100 por ejmplo.
                this.lng = lng;
            }
            public double getLng(){
                return lng;
            }
        }*/

    //private void
    public void actualizarUbicacion(android.location.Location location) {

        if (location != null) {
           double lat = location.getLatitude();
           double lng = location.getLongitude();

            tvlat.setText(lat + "");
            tvlong.setText(lng + "");
            Date date = new Date();
            // String data= lat +" "+lng+" "+ sdf.format(date);
            String data = "ABCRApp/+" + lat + "/" + lng + "/" + sdf.format(date) + "/";
            //"ABCRApp/+"+lat+"/"+lng+"/"+sdf.format(date)"/"
            tvtime.setText(sdf.format(date));
            tvfull.setText(data);
        } else {
            tvlat.setText("00.00000");
            tvlong.setText("00.00000");
        }


    }

    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            actualizarUbicacion(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    ///test permisos INICIO

    //private void
    public void miUbicacion() {

        //Conectado correctamente a Google Play Services

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PETICION_PERMISO_LOCALIZACION);
        } else {

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            android.location.Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            actualizarUbicacion(location);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locListener);
            if (location != null) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                tvlat.setText(lat + " ");
                tvlong.setText(lng + " ");
                Date date = new Date();
                //String data= lat +" "+lng+" "+ sdf.format(date);
                String data = "ABCRApp/+" + lat + "/" + lng + "/" + sdf.format(date) + "/";
                tvtime.setText(sdf.format(date));
                tvfull.setText(data);
                Intent datos= new Intent(Coordinates.this,MainActivity.class);
                datos.putExtra("lati",lat);
                datos.putExtra("longi",lng);
                Log.d(TAG, "la latitud es:"+lat);
                Log.d(TAG, "la longitud es:"+lng);



            } else {
                tvlat.setText("00.00000");
                tvlong.setText("00.00000");
            }

        }



    }
    //test permisos FIN



    public void Clickbtnregresar(View view) {
        Log.d(TAG, "BACK!!!!!!!!!!");
        Intent back = new Intent(Coordinates.this, MainActivity.class);
        startActivity(back);
    }



}
