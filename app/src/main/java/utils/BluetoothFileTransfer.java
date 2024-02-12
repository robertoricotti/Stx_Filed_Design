package utils;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothFileTransfer {

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // UUID per il servizio di trasferimento di file
    private static final int REQUEST_ENABLE_BT = 1;

    @SuppressLint("MissingPermission")
    public static void transferFileViaBluetooth(Context context, String deviceAddress, String filePath) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Verifica se il dispositivo supporta il Bluetooth
        if (bluetoothAdapter == null) {
            Toast.makeText(context, "Il dispositivo non supporta il Bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verifica se il Bluetooth è attivato
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(context, "Attiva il Bluetooth per trasferire il file", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ottieni il dispositivo Bluetooth remoto
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);

        try {
            // Crea un socket Bluetooth
            @SuppressLint("MissingPermission") BluetoothSocket socket = device.createRfcommSocketToServiceRecord(MY_UUID);

            // Connessione al dispositivo Bluetooth
            socket.connect();

            // Ottieni lo stream di output per inviare dati al dispositivo Bluetooth
            OutputStream outputStream = socket.getOutputStream();

            // Leggi il file e invia i dati al dispositivo Bluetooth
            File file = new File(filePath);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            // Chiudi gli stream e il socket
            outputStream.close();
            fileInputStream.close();
            socket.close();

            Toast.makeText(context, "File trasmesso con successo via Bluetooth", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
