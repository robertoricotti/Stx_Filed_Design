package services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import bluetooth.BT_Conn_CAN;
import bluetooth.BT_Conn_GPS;

public class AutoConnectionService extends Service {

    private static final String TAG = "AutoConnectionService";
    private Handler handler;
    private Runnable runnable;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        startRepeatingTask();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }

    private void startRepeatingTask() {
        Toast.makeText(this, "Service Attivo", Toast.LENGTH_SHORT).show();
        runnable = new Runnable() {
            @Override
            public void run() {
                if(!BT_Conn_CAN.CANerviceState){
                    new BT_Conn_CAN().CAN_Connection(AutoConnectionService.this,!BT_Conn_CAN.CANerviceState);
                }
                if(!BT_Conn_GPS.GNSSServiceState){
                    new BT_Conn_GPS().GNSS_Connection(AutoConnectionService.this, !BT_Conn_GPS.GNSSServiceState);
                }


                handler.postDelayed(this, 3000);
            }
        };


        handler.post(runnable);
    }

    private void stopRepeatingTask() {
        // Interrompe il ciclo di esecuzione del Runnable
        Toast.makeText(this, "Service Stoppato", Toast.LENGTH_SHORT).show();
        handler.removeCallbacks(runnable);
    }
}
