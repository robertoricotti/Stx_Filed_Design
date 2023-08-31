package activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stx_field_design.R;

import java.util.ArrayList;
import java.util.List;

import bluetooth.BT_Conn;
import dialogs.ConnectDialog;
import dialogs.CustomToast;
import gnss.NmeaListenerGGAH;
import services.DataSaved;
import services.UpdateValues;
import utils.FullscreenActivity;
import utils.MyRW_IntMem;

public class BT_DevicesActivity extends AppCompatActivity {
    ImageView btn_exit, btn_search, btn_stop, img_cbt;


    private BluetoothAdapter bluetoothAdapter;
    private List<BluetoothDeviceInfo> deviceList;
    private ListView deviceListView;
    private ArrayAdapter<String> deviceListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_devices);
        FullscreenActivity.setFullScreen(this);
        findView();
        onClick();

    }

    private void findView() {
        btn_exit = findViewById(R.id.btn_exit);
        btn_search = findViewById(R.id.img1);
        btn_stop = findViewById(R.id.img2);

        img_cbt = findViewById(R.id.img3);

        // Inizializza l'adattatore Bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "No Bluetooth Supported on this device", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Inizializza la lista dei dispositivi
        deviceList = new ArrayList<>();

        // Inizializza la ListView
        deviceListView = findViewById(R.id.deviceListView);
        deviceListAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner);
        deviceListView.setAdapter(deviceListAdapter);
        // Registra il BroadcastReceiver per ricevere gli aggiornamenti sullo stato dei dispositivi Bluetooth
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothReceiver, filter);

    }

    private void onClick() {

        img_cbt.setOnClickListener(view -> {
            new ConnectDialog(this).show();
        });
        btn_exit.setOnClickListener(view -> {
            startActivity(new Intent(BT_DevicesActivity.this, MainActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
        btn_search.setOnClickListener(view -> {
            new CustomToast(BT_DevicesActivity.this, "START SEARCH...").show();
            searchDevices();
        });
        btn_stop.setOnClickListener(view -> {
            stopSearch();
        });

        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                BluetoothDeviceInfo selectedDeviceInfo = deviceList.get(position);
                new CustomToast(BT_DevicesActivity.this, selectedDeviceInfo.getDeviceAddress()).show();
                new MyRW_IntMem().MyWrite("_macaddress", selectedDeviceInfo.getDeviceAddress().toUpperCase(), BT_DevicesActivity.this);
                new MyRW_IntMem().MyWrite("_gpsname", selectedDeviceInfo.getDeviceName().toUpperCase(), BT_DevicesActivity.this);
                startService(new Intent(BT_DevicesActivity.this, UpdateValues.class));
                pairWithDevice(selectedDeviceInfo.getDeviceAddress());
            }
        });


    }


    @SuppressLint("MissingPermission")
    private void pairWithDevice(String deviceAddress) {
        // Get the BluetoothDevice object based on the device address
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);

        // Avvia il processo di abbinamento
        boolean pairingStarted = device.createBond();
        if (pairingStarted) {
            new CustomToast(BT_DevicesActivity.this, "PAIRING PROCESS STARTED WITH: " + device.getName()).show();
        } else {
            new CustomToast(BT_DevicesActivity.this, "MACADDRESS SAVED: " + device.getName()).show();
        }
    }


    @SuppressLint("MissingPermission")
    private void searchDevices() {
        if (!bluetoothAdapter.isEnabled()) {
            // Se il Bluetooth non è abilitato, richiedi all'utente di abilitarlo
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            startActivityForResult(enableBtIntent, 1);


        } else {
            // Se il Bluetooth è abilitato, avvia la ricerca dei dispositivi
            deviceListAdapter.clear();
            deviceList.clear();
            bluetoothAdapter.startDiscovery();

        }
    }

    @SuppressLint("MissingPermission")
    private void stopSearch() {
        bluetoothAdapter.cancelDiscovery();
        new CustomToast(BT_DevicesActivity.this, "STOPPED..").show();



    }

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        // ...

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                @SuppressLint("MissingPermission") BluetoothDeviceInfo deviceInfo = new BluetoothDeviceInfo(device.getName(), device.getAddress());
                deviceList.add(deviceInfo);


                // Update the list adapter with the new data
                deviceListAdapter.add(deviceInfo.toString());
            }
        }
        // ...
    };

    public class BluetoothDeviceInfo {
        private String deviceName;
        private String deviceAddress;

        public BluetoothDeviceInfo(String name, String address) {
            this.deviceName = name;
            this.deviceAddress = address;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public String getDeviceAddress() {
            return deviceAddress;
        }

        @Override
        public String toString() {
            return deviceName + "\n" + deviceAddress;
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothReceiver);
    }
}