package services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bluetooth.BT_Conn_CAN;
import bluetooth.BT_Conn_GPS;

public class AutoConnectionService extends Service {
    int count=0;

    static byte[]messageIn=new byte[]{0x01,0x01};//heartib data every 3 sec
    static  int idHeartBeat=0x6fe;//heartbit every 3 sec

    private Executor mExecutor;
    private static final int THREAD_POOL_SIZE = 4;
    Timer timer_2000,timer_50;
    TimerTask timertask_2000,timertask_50;


    @Override
    public void onCreate() {
        mExecutor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

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
        super.onDestroy();
        try {
            timer_2000.cancel();
            timer_2000 = null;

            timertask_2000.cancel();
            timertask_2000 =null;

            timer_50.cancel();
            timer_50 = null;

            timertask_50.cancel();
            timertask_50 =null;

        } catch (Exception e) {
        }
        ((ExecutorService) mExecutor).shutdown();

    }
    private class MyAsync_Excecutor implements Runnable {
        @Override
        public void run() {
            timer_2000 = new Timer();
            timertask_2000 = new TimerTask() {
                @Override
                public void run() {

                }
            };
            timer_2000.scheduleAtFixedRate(timertask_2000, 200, 2000);
            timer_50 = new Timer();
            timertask_50 = new TimerTask() {
                @Override
                public void run() {

                    if(BT_Conn_CAN.CANerviceState) {
                        new BT_Conn_CAN().sendCAN(idHeartBeat, messageIn);//invia heartbeat se connesso al can

                    }

                }
            };
            timer_50.scheduleAtFixedRate(timertask_50, 1000, 100);

        }
    }





}
