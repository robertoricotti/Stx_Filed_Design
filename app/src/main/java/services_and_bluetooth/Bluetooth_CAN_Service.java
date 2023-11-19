package services_and_bluetooth;


import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import can.Can_Decoder;
import can.PLC_DataTypes_BigEndian;
import gnss.CalculateXor8;

public class Bluetooth_CAN_Service extends Service {
    final int[] readBufferPosition_CAN = new int[1];
    private final IBinder mBinder = new BluetoothCANBinder();
    private byte[] messageFrame = new byte[]{0x01, 0x01};//heartib data every 3 sec
    private int idHeartBeat = 0x186fe;//heartbit every 3 sec
    public static boolean canIsConnected;
    final int handlerState = 0;
    //used to identify handler message
    Handler bluetoothIn;
    private BluetoothAdapter btAdapter = null;

    private ConnectingThread mConnectingThread;
    private ConnectedThread mConnectedThread;

    private boolean stopThread;
    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // String for MAC address


    private StringBuilder recDataString = new StringBuilder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("BT SERVICE CAN", "SERVICE CREATED");
        stopThread = false;
    }

    @SuppressLint("HandlerLeak")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("BT SERVICE CAN", "SERVICE STARTED");
        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                Log.d("DEBUG CAN", "handleMessage");
                if (msg.what == handlerState) {

                    recDataString.append(msg.obj.toString());
                    Log.d("INPUT CAN", recDataString.toString());

                }

                recDataString.delete(0, recDataString.length());
            }
        };
        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();
        return super.onStartCommand(intent, flags, startId);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            bluetoothIn.removeCallbacksAndMessages(null);

        } catch (Exception e) {
            System.out.println(e.toString());
        }
        stopThread = true;
        if (mConnectedThread != null) {
            mConnectedThread.closeStreams();
        }
        if (mConnectingThread != null) {
            mConnectingThread.closeSocket();
        }
        canIsConnected = false;
        Log.d("Dialog", "onDestroy");


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    @SuppressLint("MissingPermission")
    private void checkBTState() {
        if (btAdapter == null) {
            Log.d("BT SERVICE CAN", "BLUETOOTH NOT SUPPORTED BY DEVICE, STOPPING SERVICE");
            stopSelf();
        } else {
            if (btAdapter.isEnabled()) {
                Log.d("DEBUG BT CAN", "BT ENABLED! BT ADDRESS : " + btAdapter.getAddress()
                        + " , BT NAME : " + btAdapter.getName());
                try {
                    BluetoothDevice device = btAdapter.getRemoteDevice(DataSaved.S_macAddress_CAN);
                    Log.d("DEBUG BT CAN", "ATTEMPTING TO CONNECT TO REMOTE DEVICE : " + DataSaved.S_macAddress_CAN);
                    mConnectingThread = new ConnectingThread(device);
                    mConnectingThread.start();
                } catch (IllegalArgumentException e) {
                    Log.d("DEBUG BT CAN", "PROBLEM WITH MAC ADDRESS : " + e.toString());
                    Log.d("BT SEVICE CAN", "ILLEGAL MAC ADDRESS, STOPPING SERVICE");

                    stopSelf();
                }
            } else {
                Log.d("BT SERVICE CAN", "BLUETOOTH NOT ON, STOPPING SERVICE");
                stopSelf();
            }
        }
    }

    // New Class for Connecting Thread
    private class ConnectingThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        @SuppressLint("MissingPermission")
        ConnectingThread(BluetoothDevice device) {
            Log.d("DEBUG BT CAN", "IN CONNECTING THREAD");
            mmDevice = device;
            BluetoothSocket temp = null;
            Log.d("DEBUG BT CAN", "MAC ADDRESS : " + DataSaved.S_macAddress_CAN);
            Log.d("DEBUG BT CAN", "BT UUID : " + BTMODULEUUID);
            try {
                temp = mmDevice.createRfcommSocketToServiceRecord(BTMODULEUUID);
                Log.d("DEBUG BT CAN", "SOCKET CREATED : " + temp.toString());

            } catch (IOException e) {
                Log.d("DEBUG BT CAN", "SOCKET CREATION FAILED :" + e.toString());
                Log.d("BT SERVICE CAN", "SOCKET CREATION FAILED, STOPPING SERVICE");
                canIsConnected = false;
                stopSelf();
            }
            mmSocket = temp;
        }

        @SuppressLint("MissingPermission")
        @Override
        public void run() {
            super.run();
            Log.d("DEBUG BT CAN", "IN CONNECTING THREAD RUN");
            // Establish the Bluetooth socket connection.
            // Cancelling discovery as it may slow down connection

            btAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
                Log.d("DEBUG BT CAN", "BT SOCKET CONNECTED");
                canIsConnected = true;
                mConnectedThread = new ConnectedThread(mmSocket);
                mConnectedThread.start();
                Log.d("DEBUG BT CAN", "CONNECTED THREAD STARTED");
                //I send a character when resuming.beginning transmission to check device is connected
                //If it is not an exception will be thrown in the write method and finish() will be called
                mConnectedThread.writeCAN(idHeartBeat, messageFrame, false);
            } catch (IOException e) {
                try {
                    Log.d("DEBUG BT CAN", "SOCKET CONNECTION FAILED : " + e.toString());
                    Log.d("BT SERVICE CAN", "SOCKET CONNECTION FAILED, STOPPING SERVICE");

                    mmSocket.close();
                    stopSelf();
                } catch (IOException e2) {
                    Log.d("DEBUG BT CAN", "SOCKET CLOSING FAILED :" + e2.toString());
                    Log.d("BT SERVICE CAN", "SOCKET CLOSING FAILED, STOPPING SERVICE");
                    stopSelf();
                    //insert code to deal with this
                }
            } catch (IllegalStateException e) {
                Log.d("DEBUG BT CAN", "CONNECTED THREAD START FAILED : " + e.toString());
                Log.d("BT SERVICE CAN", "CONNECTED THREAD START FAILED, STOPPING SERVICE");
                stopSelf();
            }
        }

        void closeSocket() {
            try {
                //Don't leave Bluetooth sockets open when leaving activity
                mmSocket.close();
            } catch (IOException e2) {
                //insert code to deal with this
                Log.d("DEBUG BT CAN", e2.toString());
                Log.d("BT SERVICE CAN", "SOCKET CLOSING FAILED, STOPPING SERVICE");
                stopSelf();
            }
        }
    }

    // New Class for Connected Thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        ConnectedThread(BluetoothSocket socket) {
            Log.d("DEBUG BT CAN", "IN CONNECTED THREAD");

            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.d("DEBUG BT CAN", e.toString());
                Log.d("BT SERVICE CAN", "UNABLE TO READ/WRITE, STOPPING SERVICE");

                // stopSelf();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.d("DEBUG BT CAN", "IN CONNECTED THREAD RUN");
            byte[] buffer = new byte[1024];
            int bytesRead;

            // Keep looping to listen for received messages
            while (true && !stopThread) {
                try {
                    bytesRead = mmInStream.read(buffer); // read bytes from input buffer


                    if (bytesRead > 0) {
                        byte[] packetBytes = new byte[bytesRead];
                        packetBytes = buffer;
                        char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
                        char[] hexChars = new char[packetBytes.length * 2];
                        int v;
                        for (int j = 0; j < packetBytes.length; j++) {
                            v = packetBytes[j] & 0xFF;
                            hexChars[j * 2] = hexArray[v / 16];
                            hexChars[j * 2 + 1] = hexArray[v % 16];
                        }
                        for (int i = 0; i < bytesRead; i++) {
                            byte b = packetBytes[i];

                            if (b == 0x0D) {
                                byte[] encodedBytes = new byte[readBufferPosition_CAN[0]];
                                System.arraycopy(buffer, 0, encodedBytes, 0, encodedBytes.length);
                                readBufferPosition_CAN[0] = 0;
                                Log.d("DEBUG BT CAN ARRAY", Arrays.toString(encodedBytes));
                                new Can_Decoder(encodedBytes);

                            } else {
                                buffer[readBufferPosition_CAN[0]++] = b;
                            }
                        }
                    }

                } catch (Exception e) {
                    Log.d("DEBUG BT CAN", e.toString());
                    Log.d("BT SERVICE CAN", "UNABLE TO READ/WRITE, STOPPING SERVICE");

                    //stopSelf();
                    break;
                }
            }
        }


        //write method
        void writeCAN(int id, byte[] data, boolean ext) {
            try {
                byte placeHolder = 0;
                if (ext) {
                    placeHolder = (byte) 0xD0;
                } else {
                    placeHolder = 0;
                }
                byte sof = 0x43; // SOF start of frame
                byte dlc = (byte) (data.length + 3);
                byte[] identifier = PLC_DataTypes_BigEndian.U32_to_bytes_be(id);
                int totalLength = 2 + 1 + 1 + identifier.length + data.length;//lunghezza totale del messaggio su cui calcolare xor
                byte[] xor = new byte[totalLength];
                int currentIndex = 0;
                xor[currentIndex++] = sof;
                xor[currentIndex++] = dlc;
                xor[currentIndex++] = placeHolder; // Placeholder come da manuale IFM
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
                msg[currentIndex++] = placeHolder; // Placeholder
                System.arraycopy(identifier, 0, msg, currentIndex, identifier.length);
                currentIndex += identifier.length;
                System.arraycopy(data, 0, msg, currentIndex, data.length);
                currentIndex += data.length;
                msg[currentIndex++] = xorResult;
                msg[currentIndex] = eof;


                mmOutStream.write(msg);
            } catch (Exception e) {
                canIsConnected = false;
                stopSelf();
            }
        }

        void closeStreams() {
            try {
                //Don't leave Bluetooth sockets open when leaving activity
                mmInStream.close();
                mmOutStream.close();
            } catch (IOException e2) {
                //insert code to deal with this
                Log.d("DEBUG BT 263", e2.toString());
                Log.d("BT SERVICE", "STREAM CLOSING FAILED, STOPPING SERVICE");

                // stopSelf();
            }
        }
    }

    public class BluetoothCANBinder extends Binder {
        Bluetooth_CAN_Service getService() {
            return Bluetooth_CAN_Service.this;
        }
    }

    public void sendCANData(int id, byte[] data, boolean ext) {
        if (mConnectedThread != null) {
            mConnectedThread.writeCAN(id, data, ext);
        }
    }


}