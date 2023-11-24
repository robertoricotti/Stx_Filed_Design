package activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;
import android.widget.TextView;

import com.example.stx_field_design.R;

import java.io.File;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import gnss.ConfigurationDescriptor;
import gnss.DeviceDescriptor;

import gnss.UsbHelper;

public class UsbActivity extends Activity {
    private static final String TAG = "UsbEnumerator";

    /* USB system service */
    private UsbManager mUsbManager;

    /* UI elements */
    private TextView mStatusView, mResultView, outputTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usb);

        mStatusView = (TextView) findViewById(R.id.text_status);
        mResultView = (TextView) findViewById(R.id.text_result);
        outputTextView = (TextView) findViewById(R.id.textIN);
        mUsbManager = getSystemService(UsbManager.class);

        // Detach events are sent as a system-wide broadcast
        IntentFilter filter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, filter);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mUsbReceiver);
    }

    /**
     * Broadcast receiver to handle USB disconnect events.
     */
    BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null) {
                    printStatus(getString(R.string.status_removed));
                    printDeviceDescription(device);
                }
            }
        }
    };

    /**
     * Determine whether to list all devices or query a specific device from
     * the provided intent.
     *
     * @param intent Intent to query.
     */
    private void handleIntent(Intent intent) {
        UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        if (device != null) {
            printStatus(getString(R.string.status_added));
            printDeviceDetails(device);
        } else {
            // List all devices connected to USB host on startup
            printStatus(getString(R.string.status_list));
            printDeviceList();
        }
    }

    /**
     * Print the list of currently visible USB devices.
     */
    private void printDeviceList() {
        HashMap<String, UsbDevice> connectedDevices = mUsbManager.getDeviceList();

        if (connectedDevices.isEmpty()) {
            printResult("No Devices Currently Connected");
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("Connected Device Count: ");
            builder.append(connectedDevices.size());
            builder.append("\n\n");
            for (UsbDevice device : connectedDevices.values()) {
                //Use the last device detected (if multiple) to open
                builder.append(UsbHelper.readDevice(device));
                builder.append("\n\n");
            }
            printResult(builder.toString());
        }
    }

    /**
     * Print a basic description about a specific USB device.
     *
     * @param device USB device to query.
     */
    private void printDeviceDescription(UsbDevice device) {
        String result = UsbHelper.readDevice(device) + "\n\n";
        printResult(result);
    }

    /**
     * Initiate a control transfer to request the device information
     * from its descriptors.
     *
     * @param device USB device to query.
     */
    private void printDeviceDetails(UsbDevice device) {
        UsbDeviceConnection connection = mUsbManager.openDevice(device);

        String deviceString = "";
        try {
            //Parse the raw device descriptor
            deviceString = DeviceDescriptor.fromDeviceConnection(connection)
                    .toString();
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "Invalid device descriptor", e);
        }

        String configString = "";
        try {
            //Parse the raw configuration descriptor
            configString = ConfigurationDescriptor.fromDeviceConnection(connection)
                    .toString();
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "Invalid config descriptor", e);
        } catch (ParseException e) {
            Log.w(TAG, "Unable to parse config descriptor", e);
        }

        printResult(deviceString + "\n\n" + configString);
        connection.close();
    }

    /* Helpers to display user content */

    private void printStatus(String status) {
        mStatusView.setText(status);
        Log.i(TAG, status);
    }

    private void printResult(String result) {
        mResultView.setText(result);
        getUsbFolderPath();
        try {
            readFromUSB(getUsbFolderPath());

        } catch (Exception e) {
          outputTextView.setText(e.toString());
        }
        Log.i(TAG, result);
    }


        private String getUsbFolderPath() {
            StorageManager storageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);

            if (storageManager != null) {
                List<StorageVolume> storageVolumes = storageManager.getStorageVolumes();
                for (StorageVolume storageVolume : storageVolumes) {
                    // Verifica se il volume è montato e se è rimovibile (USB, SD, ecc.)
                    if (Environment.MEDIA_MOUNTED.equals(storageVolume.getState()) && storageVolume.isRemovable()) {
                        // Ottieni il percorso del volume
                        @SuppressLint({"NewApi", "LocalSuppress"}) File storageFile = storageVolume.getDirectory();
                        // Restituisci il percorso della cartella "IN" sulla USB stick
                        return new File(storageFile.getPath()).toString();
                    }
                }
            }

            return null;

    }
    private void readFromUSB(String usbFolderPath) {
        // Verifica se la cartella USB esiste
        File usbFolder = new File(usbFolderPath);

        if (usbFolder.exists() && usbFolder.isDirectory()) {
            // Percorso della cartella "IN" sulla USB stick
            File inFolder = new File(usbFolder, "IN");

            // Verifica se la cartella "IN" esiste
            if (inFolder.exists() && inFolder.isDirectory()) {
                // Ottieni la lista di file nella cartella "IN"
                File[] files = inFolder.listFiles();
                if (files != null) {
                    // Stampa la lista dei file
                    StringBuilder fileList = new StringBuilder("Files in 'IN' folder:\n");
                    for (File file : files) {
                        fileList.append(file.getName()).append("\n");
                    }
                    outputTextView.setText(fileList.toString());
                }
            } else {
                outputTextView.setText("Folder 'IN' not found on USB stick");
            }
        } else {
            outputTextView.setText("USB stick not found");
        }
    }




    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }


}