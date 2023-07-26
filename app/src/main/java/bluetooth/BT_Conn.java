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
import android.widget.Toast;

import androidx.annotation.RequiresApi;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

import services.DataSaved;


public class BT_Conn extends BluetoothClass.Device {

    public static boolean GNSSServiceState = false;

    public static String GpsDEVICE;

    static OutputStream mmOutputStream=null;
    Context con;
    static BluetoothSocket mmSocket = null;
    static InputStream mmInputStream = null;
    BluetoothAdapter mBluetoothAdapter_GNSS;

    BluetoothDevice mmDevice_Gnss;

    @SuppressLint("MissingPermission")
    public void GNSS_Connection(Context context, boolean GNSSen, String MACADDRESSGnss) {
        con = context;
        Thread workerThread_Gnss;
        byte[] readBuffer_Gnss;
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
                            Toast.makeText(context.getApplicationContext(), "GNSS Connected", Toast.LENGTH_SHORT).show();
                            GNSSServiceState = true;
                            final Handler handler = new Handler();
                            final int delimiter = 10;
                            stopWorker_Gnss[0] = false;
                            readBufferPosition_Gnss[0] = 0;
                            readBuffer_Gnss = new byte[1024];
                            workerThread_Gnss = new Thread(new Runnable() {
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
                                                        System.arraycopy(readBuffer_Gnss, 0, encodedBytes, 0, encodedBytes.length);
                                                        final String data = new String(encodedBytes, StandardCharsets.US_ASCII);
                                                        readBufferPosition_Gnss[0] = 0;
                                                        handler.post(new Runnable() {
                                                            @RequiresApi(api = Build.VERSION_CODES.M)
                                                            public void run() {
                                                                DataSaved.S_nmea=data;

                                                            }
                                                        });
                                                    } else {
                                                        readBuffer_Gnss[readBufferPosition_Gnss[0]++] = b;
                                                    }
                                                }
                                            }
                                        } catch (Exception ex) {
                                            stopWorker_Gnss[0] = true;
                                        }
                                    }
                                }
                            });
                            workerThread_Gnss.start();
                        } catch (Exception en) {
                            Toast.makeText(context.getApplicationContext(), "GNSS Disconnected", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                }
            }
        }
        if (!GNSSen) {
            stopWorker_Gnss[0] = true;

            try{

                mmInputStream.close();
            } catch (Exception e) {
            }
            try{
                mmOutputStream.close();
            } catch (Exception e) {
            }

            try {
                mmSocket.close();
                GNSSServiceState = false;
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











