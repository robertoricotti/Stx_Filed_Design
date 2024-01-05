package services_and_bluetooth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.van.jni.VanMcu;

import can.Can_Decoder;

public class CanService extends Service implements VanMcu.OnCanListener {
    public CanService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        VanMcu.setCanSpeed(0, 250000);
        VanMcu.CanSwFilterClear(0);
        VanMcu.setOnCanListener(this);
        Bluetooth_CAN_Service.canIsConnected = true;

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void OnCan(VanMcu.CanMsg msg) {
         Can_Decoder.Physical_Can(msg.id,msg.data);
    }
}