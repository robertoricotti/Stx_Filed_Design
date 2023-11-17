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
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import can.Can_Decoder;
import can.PLC_DataTypes_BigEndian;
import gnss.Nmea_In;
import services.DataSaved;

public class BT_Conn_CAN extends BluetoothClass.Device {

    public static boolean CANerviceState = false;
    public static String CAN_DEVICE;
    private static OutputStream mmOutputStream = null;

    private static BluetoothSocket mmSocket = null;
    private static InputStream mmInputStream = null;
    private static BluetoothAdapter mBluetoothAdapter_CAN;
    private static BluetoothDevice mmDevice_CAN;

    // Aggiunto ExecutorService per eseguire  in background


    @SuppressLint("MissingPermission")
    public void CAN_Connection(Context context, boolean CANen) {

        final Thread[] workerThread_CAN = new Thread[1];
        final byte[][] readBuffer_CAN = new byte[1][1];
        final int[] readBufferPosition_CAN = new int[1];
        final boolean[] stopWorker_CAN = new boolean[1];
        IntentFilter filterG = new IntentFilter();
        filterG.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filterG.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filterG.addAction(BluetoothDevice.EXTRA_DEVICE);
        context.registerReceiver(broadcastReceiverCAN, filterG);
        if (CANen) {


            mBluetoothAdapter_CAN = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter_CAN == null) {
                Toast.makeText(context.getApplicationContext(), "!!!No Bluetooth Available !!!", Toast.LENGTH_LONG).show();
                return;
            }
            if (!mBluetoothAdapter_CAN.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                context.startActivity(enableBluetooth);
            }

            @SuppressLint("MissingPermission") Set<BluetoothDevice> pairedDevices = mBluetoothAdapter_CAN.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getAddress().equals(DataSaved.S_macAddress_CAN)) {
                        mmDevice_CAN = device;
                        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                        try {
                            mmSocket = mmDevice_CAN.createRfcommSocketToServiceRecord(uuid);
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
                            CAN_DEVICE = mmDevice_CAN.getAddress();
                            final Handler handler = new Handler();
                            final int delimiter = 13;//verificare
                            readBuffer_CAN[0] = new byte[1024];
                            workerThread_CAN[0] = new Thread(new Runnable() {
                                public void run() {
                                    while (!Thread.currentThread().isInterrupted() && !stopWorker_CAN[0]) {
                                        try {
                                            int bytesAvailable = mmInputStream.available();
                                            if (bytesAvailable > 0) {
                                                byte[] packetBytes = new byte[bytesAvailable];
                                                mmInputStream.read(packetBytes);
                                                char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
                                                char[] hexChars = new char[packetBytes.length * 2];
                                                int v;
                                                for (int j = 0; j < packetBytes.length; j++) {
                                                    v = packetBytes[j] & 0xFF;
                                                    hexChars[j * 2] = hexArray[v / 16];
                                                    hexChars[j * 2 + 1] = hexArray[v % 16];
                                                }
                                                for (int i = 0; i < bytesAvailable; i++) {
                                                    byte b = packetBytes[i];

                                                    if (b == delimiter) {
                                                        byte[] encodedBytes = new byte[readBufferPosition_CAN[0]];
                                                        System.arraycopy(readBuffer_CAN[0], 0, encodedBytes, 0, encodedBytes.length);
                                                        readBufferPosition_CAN[0] = 0;
                                                        handler.post(new Runnable() {
                                                            public void run() {
                                                                new Can_Decoder(encodedBytes);
                                                            }
                                                        });
                                                    } else {
                                                        readBuffer_CAN[0][readBufferPosition_CAN[0]++] = b;
                                                    }
                                                }
                                            }
                                        } catch (Exception ex) {
                                            stopWorker_CAN[0] = true;
                                        }
                                    }
                                }
                            });
                            workerThread_CAN[0].start();

                        } catch (Exception en) {
                        }
                        break;
                    }
                }
            }


        }
        if (!CANen) {
            if (CANerviceState) {
                for (int i = 0; i < 2; i++) {
                    if (i == 1) {
                        Toast.makeText(context.getApplicationContext(), "Disconnecting...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            stopWorker_CAN[0] = true;

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


    private final BroadcastReceiver broadcastReceiverCAN = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice device;
            String action = intent.getAction();
            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (CAN_DEVICE != null) {
                if (CAN_DEVICE.equals(device.getAddress())) {
                    if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                        CANerviceState = true;
                        Toast.makeText(context.getApplicationContext(), "CAN CONNECTED", Toast.LENGTH_SHORT).show();
                    } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                        CANerviceState = false;
                        Toast.makeText(context.getApplicationContext(), "CAN DISCONNECTED", Toast.LENGTH_SHORT).show();

                    }
                }
            } else {
                if (broadcastReceiverCAN.getDebugUnregister()) {
                    broadcastReceiverCAN.abortBroadcast();
                }
            }
        }
    };


    public void sendCAN(int id, byte[] data) {
        try {
            byte sof = 0x43; // SOF start of frame
            byte dlc = (byte) (data.length + 3);
            byte[] identifier = PLC_DataTypes_BigEndian.U16_to_bytes_be(id);
            int totalLength = 2 + 1 + 1 + identifier.length + data.length;//lunghezza totale del messaggio su cui calcolare xor
            byte[] xor = new byte[totalLength];
            int currentIndex = 0;
            xor[currentIndex++] = sof;
            xor[currentIndex++] = dlc;
            xor[currentIndex++] = 0; // Placeholder come da manuale IFM
            System.arraycopy(identifier, 0, xor, currentIndex, identifier.length);
            currentIndex += identifier.length;
            System.arraycopy(data, 0, xor, currentIndex, data.length);

            byte xorResult = (byte) new CalculateXor8(xor).xor;//checksum

            byte eof = 0x0D; // EOF end of frame

            // Creare l'array risultante e copiare i byte uno per uno
            int msgLength = 1 + 1 + 1 + identifier.length + data.length + 1 + 1; // sof + dlc + 0 + identifier + data + xorResult + eof
            byte[] msg = new byte[msgLength];
            currentIndex = 0;

            msg[currentIndex++] = sof;
            msg[currentIndex++] = dlc;
            msg[currentIndex++] = 0; // Placeholder
            System.arraycopy(identifier, 0, msg, currentIndex, identifier.length);
            currentIndex += identifier.length;
            System.arraycopy(data, 0, msg, currentIndex, data.length);
            currentIndex += data.length;
            msg[currentIndex++] = xorResult;
            msg[currentIndex] = eof;


            mmOutputStream.write(msg);
        } catch (Exception e) {

        }
    }
}