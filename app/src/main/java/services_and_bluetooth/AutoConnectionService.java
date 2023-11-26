package services_and_bluetooth;

import static project.LoadProject.page;

import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.OnNmeaMessageListener;

import android.content.Intent;

import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;


import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import activity.MyApp;
import gnss.Nmea_In;
import utils.LocationUtils;


public class AutoConnectionService extends Service {

    static String androidNmea = "$GPGGA,210230,3855.4487,N,09446.0071,W,1,07,1.1,370.5,M,-29.5,M,,*7A";
    byte c;

    int id = 0x6FA, countG = -1, countCan = -1;
    public static byte[] data_6FA = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};


    private Executor mExecutor;
    private static final int THREAD_POOL_SIZE = 4;
    Timer timer_6000, timer_3000;
    TimerTask timertask_6000, timertask_3000;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private OnNmeaMessageListener nmeaMessageListener;


    @Override
    public void onCreate() {


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                double altitude = location.getAltitude();

                // Ora puoi utilizzare latitude, longitude, altitude come necessario
            }

            // Altri metodi di LocationListener rimossi per brevità
        };

        // Aggiungi il listener per le stringhe NMEA
        nmeaMessageListener = new OnNmeaMessageListener() {
            @Override
            public void onNmeaMessage(String message, long timestamp) {
                Log.d("NMEA", message);
                androidNmea = message;
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


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);


        mExecutor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mExecutor.execute(new MyAsync_Excecutor());
        Log.d("AUTOCONNECTION", "AUTOCONNECTION STARTED");

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
            timer_6000.cancel();
            timer_6000 = null;

            timertask_6000.cancel();
            timertask_6000 = null;

            timer_3000.cancel();
            timer_3000 = null;

            timertask_3000.cancel();
            timertask_3000 = null;

        } catch (Exception e) {
        }
        ((ExecutorService) mExecutor).shutdown();


    }

    private class MyAsync_Excecutor implements Runnable {

        @Override
        public void run() {
            timer_6000 = new Timer();
            timertask_6000 = new TimerTask() {
                @Override
                public void run() {
                    if (!LocationUtils.isLocationEnabled(MyApp.visibleActivity)) {
                        // Se la localizzazione è disabilitata, richiedi all'utente di attivarla
                        LocationUtils.requestLocationSettings(MyApp.visibleActivity);
                    }

                    countG++;
                    countCan++;
                    if (!Bluetooth_GNSS_Service.gpsIsConnected && DataSaved.S_macAddres != null && (countG % 2 == 0) && countG <= 10) {

                        try {
                            if (!DataSaved.S_macAddres.equals("00:00:00:00:00:00")) {
                                startService(new Intent(AutoConnectionService.this, Bluetooth_GNSS_Service.class));
                                Log.d("BT SERVICE", "PROVO A CONNETTERMI AL GPS");
                            }
                        } catch (Exception e) {
                            //do nothing
                        }

                    }
                    if (Bluetooth_GNSS_Service.gpsIsConnected) {
                        countG = 0;
                    }

                    if (!Bluetooth_CAN_Service.canIsConnected && DataSaved.S_macAddress_CAN != null && (countCan % 2 != 0) && countCan <= 10) {

                        try {
                            if (!DataSaved.S_macAddress_CAN.equals("00:00:00:00:00:00")) {
                                startService(new Intent(AutoConnectionService.this, Bluetooth_CAN_Service.class));
                                Log.d("BT SERVICE", "PROVO A CONNETTERMI AL CAN");
                            }
                        } catch (
                                Exception e) {
                            //do nothing
                        }

                    }
                    if (Bluetooth_CAN_Service.canIsConnected) {
                        countCan = 0;
                    }

                    Log.d("AUTOCONNECTION", "CountG: " + countG + "  CountCan: " + countCan);

                }
            };

            timer_6000.scheduleAtFixedRate(timertask_6000, 3000, 3000);

            timer_3000 = new Timer();

            timertask_3000 = new

                    TimerTask() {
                        @Override
                        public void run() {
                            if (!Bluetooth_GNSS_Service.gpsIsConnected) {

                                //Bluetooth_GNSS_Service.sendGNSSata("Sample Message\r\n");
                                new Nmea_In(androidNmea);
                                //Usare questo codice per scrivere su seriale da bluetooth

                            }


                            if (Bluetooth_CAN_Service.canIsConnected) {
                                c++;

                                try {
                                    Bluetooth_CAN_Service.sendCANData(id, new byte[]{page, data_6FA[0], data_6FA[1], data_6FA[2], data_6FA[3], c});

                                } catch (Exception e) {
                                    Bluetooth_CAN_Service.sendCANData(id, new byte[]{(byte) 0xFF, 0, 0, 0, (byte) (c & 0xff)});

                                }
                            }

                        }
                    };
            timer_3000.scheduleAtFixedRate(timertask_3000, 100, 100);


        }


    }


}
