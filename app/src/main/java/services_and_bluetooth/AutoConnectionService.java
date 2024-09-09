package services_and_bluetooth;

import static activity_portrait.AB_WorkActivity.page;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.OnNmeaMessageListener;

import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;


import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import activity_portrait.MyApp;
import can.Can_Decoder;
import eventbus.GpsEvents;
import gnss.Nmea_In;
import utils.LocationUtils;


public class AutoConnectionService extends Service {

    static String androidNmea = "$GPGGA,210230,3855.4487,N,09446.0071,W,1,07,1.1,370.5,M,-29.5,M,,*7A";
    byte c;

    int id = 0x6FA, countG = -1, countCan = -1;
    public static byte[] data_6FA = new byte[]{0, 0};
    public static byte[] data_6FA_2nd = new byte[]{0, 0, 0, 0};


    private Executor mExecutor;
    private static final int THREAD_POOL_SIZE = 1;
    Timer timer_100;
    TimerTask timertask_100;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private OnNmeaMessageListener nmeaMessageListener;


    @SuppressLint("InlinedApi")
    @Override
    public void onCreate() {
        if (!LocationUtils.isLocationEnabled(MyApp.visibleActivity.getApplicationContext())) {
            // Se la localizzazione è disabilitata apre la pag per attivazione
            LocationUtils.requestLocationSettings(MyApp.visibleActivity.getApplicationContext());
        }



        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                double altitude = location.getAltitude();

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


        nmeaMessageListener = new OnNmeaMessageListener() {
            @Override
            public void onNmeaMessage(String message, long timestamp) {

                androidNmea = message;
                if (DataSaved.useDemo == 1) {

                    //Bluetooth_GNSS_Service.sendGNSSata("Sample Message\r\n"); //Usare questo codice per scrivere su seriale da bluetooth
                    new Nmea_In(androidNmea);
                    DataSaved.S_nmea=message;
                    EventBus.getDefault().post(new GpsEvents(androidNmea));

                    Log.d("TEABLET_NMEA", androidNmea);
                }


                // Ora puoi gestire la stringa NMEA come necessario
            }
        };
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.addNmeaListener(nmeaMessageListener);


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);


        mExecutor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);




    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mExecutor.execute(new MyAsync_Excecutor());




        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
            locationManager.removeNmeaListener(nmeaMessageListener);
        }

        try {

            timer_100.cancel();
            timer_100 = null;

            timertask_100.cancel();
            timertask_100 = null;

        } catch (Exception e) {
        }
        ((ExecutorService) mExecutor).shutdown();


    }

    private class MyAsync_Excecutor implements Runnable {


        @Override
        public void run() {



            timer_100 = new Timer();

            timertask_100 = new

                    TimerTask() {

                        @Override
                        public void run() {


                            if (Bluetooth_CAN_Service.canIsConnected) {
                                c++;

                                try {
                                    Bluetooth_CAN_Service.sendCANData(id, new byte[]{page, data_6FA[0], data_6FA[1], data_6FA_2nd[0], data_6FA_2nd[1], c});

                                } catch (Exception e) {
                                    Bluetooth_CAN_Service.sendCANData(id, new byte[]{(byte) 0xFF, 0, 0, 0, (byte) (c & 0xff)});

                                }
                            } else {
                                Can_Decoder.auto = 0;
                            }

                        }
                    };
            timer_100.schedule(timertask_100, 100, 100);


        }


    }
   // private Handler handler = new Handler();




}
