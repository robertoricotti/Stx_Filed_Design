package bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import activity.MyApp;
import dialogs.CustomToast;
import gnss.HDTCalculator;
import gnss.My_LocationCalc;
import gnss.NmeaListenerGGAH;
import services.DataSaved;


public class BT_Conn extends BluetoothClass.Device {
    boolean isStop;
    static double x1, y1, x2, y2,hd1,hd2,hd3,hdTOT;
    public static boolean GNSSServiceState = false;

    public static String GpsDEVICE;

    private int count;
    static OutputStream mmOutputStream = null;
    Context con;
    static BluetoothSocket mmSocket = null;
    static InputStream mmInputStream = null;
    BluetoothAdapter mBluetoothAdapter_GNSS;

    BluetoothDevice mmDevice_Gnss;


    @SuppressLint("MissingPermission")
    public void GNSS_Connection(Context context, boolean GNSSen, String MACADDRESSGnss) {
        con = context;
        final Thread[] workerThread_Gnss = new Thread[1];
        final byte[][] readBuffer_Gnss = new byte[1][1];
        final int[] readBufferPosition_Gnss = new int[1];
        final boolean[] stopWorker_Gnss = new boolean[1];
        IntentFilter filterG = new IntentFilter();
        filterG.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filterG.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filterG.addAction(BluetoothDevice.EXTRA_DEVICE);
        context.registerReceiver(broadcastReceiverGNSS, filterG);
        if (GNSSen) {

            mBluetoothAdapter_GNSS = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter_GNSS == null) {

                Toast.makeText(context.getApplicationContext(), "!!!No Bluetooth Available !!!", Toast.LENGTH_LONG).show();
            }
            if (!mBluetoothAdapter_GNSS.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                context.startActivity(enableBluetooth);
            }

            @SuppressLint("MissingPermission") Set<BluetoothDevice> pairedDevices = mBluetoothAdapter_GNSS.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getAddress().equals(MACADDRESSGnss)) {
                        mmDevice_Gnss = device;
                        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                        try {
                            mmSocket = mmDevice_Gnss.createRfcommSocketToServiceRecord(uuid);
                        } catch (IOException e) {

                        }
                        try {
                            mmSocket.connect();
                        } catch (IOException e) {

                        }
                        try {
                            mmInputStream = mmSocket.getInputStream();
                        } catch (IOException e) {

                        }
                        try {
                            mmOutputStream = mmSocket.getOutputStream();
                        } catch (IOException e) {

                        }
                        try {
                            GpsDEVICE = mmDevice_Gnss.getAddress();
                            final Handler handler = new Handler();
                            final int delimiter = 10;
                            stopWorker_Gnss[0] = false;
                            readBufferPosition_Gnss[0] = 0;
                            readBuffer_Gnss[0] = new byte[1024];
                            workerThread_Gnss[0] = new Thread(new Runnable() {
                                public void run() {
                                    while (!Thread.currentThread().isInterrupted() && !stopWorker_Gnss[0]) {
                                        try {
                                            int bytesAvailable = mmInputStream.available();
                                            if (bytesAvailable > 0) {
                                                byte[] packetBytes = new byte[bytesAvailable];
                                                mmInputStream.read(packetBytes);
                                                for (int i = 0; i < bytesAvailable; i++) {
                                                    byte b = packetBytes[i];
                                                    if (b == delimiter) {
                                                        byte[] encodedBytes = new byte[readBufferPosition_Gnss[0]];
                                                        System.arraycopy(readBuffer_Gnss[0], 0, encodedBytes, 0, encodedBytes.length);
                                                        final String data = new String(encodedBytes, StandardCharsets.US_ASCII);
                                                        readBufferPosition_Gnss[0] = 0;
                                                        handler.post(new Runnable() {
                                                            public void run() {

                                                                new NmeaListenerGGAH(data);
                                                                DataSaved.S_nmea = data;
                                                                /*
                                                                if (!isStop) {
                                                                    x1 = NmeaListenerGGAH.Est1;
                                                                    y1 = NmeaListenerGGAH.Nord1;
                                                                    isStop = true;
                                                                }

                                                                if (((Math.abs(NmeaListenerGGAH.Est1 - x1) > 0.25)||(Math.abs(NmeaListenerGGAH.Nord1 - y1) > 0.25)) && isStop) {
                                                                    x2 = NmeaListenerGGAH.Est1;
                                                                    y2 = NmeaListenerGGAH.Nord1;
                                                                    hdTOT = My_LocationCalc.calcBearingXY(x1, y1, x2, y2);
                                                                    count++;
                                                                    isStop = false;
                                                                }
                                                                if(hdTOT>180){
                                                                    hdTOT-=360;
                                                                }else if(hdTOT<-180){
                                                                    hdTOT+=360;
                                                                }
                                                                if(count==1){
                                                                    hd1=hdTOT;
                                                                }else if(count==2){
                                                                    hd2=hdTOT;
                                                                }else if(count==3){
                                                                    hd3=hdTOT;
                                                                    if(Math.abs(hd3-hd2)<5d){
                                                                    DataSaved.HDT_Calc=(hd1+hd2+hd3)/3;}
                                                                    count=0;
                                                                }*/
                                                            }

                                                        });
                                                    } else {
                                                        readBuffer_Gnss[0][readBufferPosition_Gnss[0]++] = b;
                                                    }
                                                }
                                            }
                                        } catch (Exception ex) {
                                            stopWorker_Gnss[0] = true;
                                        }
                                    }
                                }
                            });
                            workerThread_Gnss[0].start();
                        } catch (Exception en) {
                        }
                        break;
                    }
                }
            }

        }
        if (!GNSSen) {
            if (GNSSServiceState) {
                for (int i = 0; i < 2; i++) {
                    if (i == 1) {
                        Toast.makeText(context.getApplicationContext(), "Disconnecting...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            stopWorker_Gnss[0] = true;

            try {

                mmInputStream.close();
            } catch (Exception e) {
            }
            try {
                mmOutputStream.close();
            } catch (Exception e) {
            }

            try {
                mmSocket.close();

            } catch (Exception e) {
            }
        }

    }

    public void sendGnss(String msg) {
        try {

            mmOutputStream.write(msg.getBytes());
        } catch (Exception e) {

        }

    }


    private final BroadcastReceiver broadcastReceiverGNSS = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice device;
            String action = intent.getAction();
            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (GpsDEVICE != null) {
                if (GpsDEVICE.equals(device.getAddress())) {
                    if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {

                        GNSSServiceState = true;

                    } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {

                        GNSSServiceState = false;
                    }
                }
            } else {
                if (broadcastReceiverGNSS.getDebugUnregister()) {
                    broadcastReceiverGNSS.abortBroadcast();
                }
            }
        }
    };


}











