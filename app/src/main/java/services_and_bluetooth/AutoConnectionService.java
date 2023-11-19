package services_and_bluetooth;

import static project.LoadProject.page;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import project.LoadProject;

public class AutoConnectionService extends Service {
    byte c=0;
    private int countG=-1, countCan=-1;
    private Bluetooth_CAN_Service mBluetoothService;
    private Bluetooth_GNSS_Service mBluetoothGpsService;
    private boolean mServiceBound = false;
    private boolean mServiceBoundGps = false;

    int id = 0x6FA;
    public static byte[] data_6FA = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};


    private Executor mExecutor;
    private static final int THREAD_POOL_SIZE = 4;
    Timer timer_6000, timer_3000;
    TimerTask timertask_6000, timertask_3000;


    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Bluetooth_CAN_Service.BluetoothCANBinder binder = (Bluetooth_CAN_Service.BluetoothCANBinder) service;
            mBluetoothService = binder.getService();
            mServiceBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }
    };
    private final ServiceConnection mServiceConnectionGps = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Bluetooth_GNSS_Service.BluetoothGNSSBinder binderG = (Bluetooth_GNSS_Service.BluetoothGNSSBinder) service;
            mBluetoothGpsService = binderG.getService();
            mServiceBoundGps = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBoundGps = false;
        }
    };


    @Override
    public void onCreate() {

        mExecutor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        Intent intent = new Intent(this, Bluetooth_CAN_Service.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

        Intent intentG = new Intent(this, Bluetooth_GNSS_Service.class);
        bindService(intentG, mServiceConnectionGps, Context.BIND_AUTO_CREATE);
        super.onCreate();


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
        if (mServiceBound) {
            unbindService(mServiceConnection);
            mServiceBound = false;
        }
        if (mServiceBoundGps) {
            unbindService(mServiceConnectionGps);
            mServiceBoundGps = false;
        }
        super.onDestroy();
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



                    if (!Bluetooth_GNSS_Service.gpsIsConnected && DataSaved.S_macAddres != null&&(countG%2==0)&&countG<=10) {

                        try {
                            if (!DataSaved.S_macAddres.equals("00:00:00:00:00:00")) {
                                startService(new Intent(AutoConnectionService.this, Bluetooth_GNSS_Service.class));
                                Log.d("BT SERVICE", "PROVO A CONNETTERMI AL GPS");
                            }
                        } catch (Exception e) {
                            //do nothing
                        }

                    }
                    if(Bluetooth_GNSS_Service.gpsIsConnected){
                        countG=0;
                    }

                    if (!Bluetooth_CAN_Service.canIsConnected && DataSaved.S_macAddress_CAN != null&&(countCan%2!=0)&&countCan<=10) {

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
                    if(Bluetooth_CAN_Service.canIsConnected){
                        countCan=0;
                    }
            if(countG<1000){
                    countG++;}
            if(countCan<100){
                    countCan++;}

                }
            };

            timer_6000.scheduleAtFixedRate(timertask_6000, 6000, 3000);
            timer_3000 = new

                    Timer();

            timertask_3000 = new

                    TimerTask() {
                        @Override
                        public void run() {
                        /*  if(Bluetooth_GNSS_Service.gpsIsConnected){
                                mBluetoothGpsService.sendGNSSata("Sample Message\r\n");
                                //Usare questo codice per scrivere su seriale da bluetooth
                            }*/

                                c++;
                            if (Bluetooth_CAN_Service.canIsConnected) {

                                if (mServiceBound) {
                                    try {
                                        mBluetoothService.sendCANData(id, new byte[]{page, data_6FA[0], data_6FA[1], data_6FA[2], data_6FA[3],c},false);

                                    } catch (Exception e) {
                                        mBluetoothService.sendCANData(id, new byte[]{(byte) 0xFF, 0, 0, 0, c},false);
                                    }
                                }
                            }

                        }
                    }

            ;
            timer_3000.scheduleAtFixedRate(timertask_3000, 1000, 100);


        }

    }


}
