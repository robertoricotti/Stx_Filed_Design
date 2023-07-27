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
    ImageView btn_exit, btn_search, btn_stop,img_connect,img_cbt;
    TextView tv_macaddress;
    TextView textCoord, txtSat, txtFix, txtCq, txtHdt, txtAltezzaAnt, txtRtk;
    private boolean isSearching=false;

    private Handler handler;
    private boolean mRunning = true;
    private BluetoothAdapter bluetoothAdapter;
    private List<BluetoothDevice> deviceList;
    private ListView deviceListView;
    private ArrayAdapter<String> deviceListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_devices);
        FullscreenActivity.setFullScreen(this);
        findView();
        onClick();
        updateUI();
    }

    private void findView() {
        btn_exit = findViewById(R.id.btn_exit);
        btn_search = findViewById(R.id.img1);
        btn_stop = findViewById(R.id.img2);
        tv_macaddress = findViewById(R.id.txt_coord);
        img_connect = findViewById(R.id.img_connetti);
        img_cbt=findViewById(R.id.img3);
        txtSat = findViewById(R.id.txt_satnr);
        txtFix = findViewById(R.id.txt_quality);
        txtCq = findViewById(R.id.txt_precision);
        txtHdt = findViewById(R.id.txt_hdt);
        txtAltezzaAnt = findViewById(R.id.txt_speed);
        txtRtk = findViewById(R.id.txt_rtk);
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
        img_connect.setOnClickListener(view -> {
            new ConnectDialog(this).show();

        });
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
            @SuppressLint("MissingPermission")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                BluetoothDevice selectedDevice = deviceList.get(position);
                new CustomToast(BT_DevicesActivity.this, selectedDevice.getAddress()).show();
                new MyRW_IntMem().MyWrite("_macaddress", selectedDevice.getAddress().toUpperCase().toString(), BT_DevicesActivity.this);
                try {
                    new MyRW_IntMem().MyWrite("_gpsname", selectedDevice.getName().toUpperCase().toString(), BT_DevicesActivity.this);
                } catch (Exception e) {

                }
                startService(new Intent(BT_DevicesActivity.this, UpdateValues.class));
                pairWithDevice(selectedDevice);
            }

        });

    }

    private void updateUI() {

        handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mRunning) {


                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(isSearching&&!BT_Conn.GNSSServiceState){
                                tv_macaddress.setText("...Searching...");
                            }else{tv_macaddress.setText(DataSaved.S_gpsname+"\t"+DataSaved.S_macAddres);}

                            txtAltezzaAnt.setText(String.format("%.3f", DataSaved.D_AltezzaAnt).replace(",","."));
                            if (BT_Conn.GNSSServiceState) {
                                img_connect.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.green));

                                txtSat.setText("\t"+NmeaListenerGGAH.ggaSat);
                                if(NmeaListenerGGAH.ggaQuality!=null){
                                    switch (NmeaListenerGGAH.ggaQuality) {
                                        case "":
                                        case "0":
                                        case "1":
                                            txtFix.setText("\tAUTONOMOUS");
                                            break;
                                        case "2":
                                            txtFix.setText("\tDGNSS");
                                            break;
                                        case "4":
                                            txtFix.setText("\tFIX");
                                            break;
                                        case "5":
                                            txtFix.setText("\tFLOAT");
                                            break;
                                        case "6":
                                            txtFix.setText("\tINS");
                                            break;
                                        default:txtFix.setText("\tAUTONOMOUS");
                                            break;
                                    }}
                                if(NmeaListenerGGAH.sCQ_v!=null){
                                    txtCq.setText("\tH: "+NmeaListenerGGAH.sCQ_h.replace(",",".")+"\tV: "+NmeaListenerGGAH.sCQ_v.replace(",","."));}
                                else {txtCq.setText("H:---.-- V:---.--");}
                                txtHdt.setText("\t"+NmeaListenerGGAH.sHDT);
                                txtRtk.setText("\t"+NmeaListenerGGAH.ggaRtk);

                            } else {
                                img_connect.setImageTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.white));


                                txtSat.setText("--");
                                txtFix.setText("---");
                                txtCq.setText("H:---.-- V:---.--");
                                txtHdt.setText("---.--");
                                txtRtk.setText("----");
                            }


                        }
                    });
                    // sleep per intervallo update UI
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    @SuppressLint("MissingPermission")
    private void pairWithDevice(BluetoothDevice device) {
        // Avvia il processo di abbinamento
        @SuppressLint("MissingPermission") boolean pairingStarted = device.createBond();
        if (pairingStarted) {
            new CustomToast(BT_DevicesActivity.this, "PAIRING PROCESS STARTED WITH: " + device.getName()).show();
        } else {
            new CustomToast(BT_DevicesActivity.this, "CHECK IF ALREADY PAIRED: " + device.getName()).show();
        }
    }


    private void searchDevices() {
        if (!bluetoothAdapter.isEnabled()) {
            // Se il Bluetooth non è abilitato, richiedi all'utente di abilitarlo
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivityForResult(enableBtIntent, 1);


        } else {
            // Se il Bluetooth è abilitato, avvia la ricerca dei dispositivi
            deviceListAdapter.clear();
            deviceList.clear();
            bluetoothAdapter.startDiscovery();
            isSearching=true;
        }
    }

    @SuppressLint("MissingPermission")
    private void stopSearch() {
        bluetoothAdapter.cancelDiscovery();
        isSearching=false;
        // Mostra il ProgressDialog

    }

    // BroadcastReceiver per ricevere gli aggiornamenti sullo stato dei dispositivi Bluetooth
    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {

        @SuppressLint("MissingPermission")
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                deviceList.add(device);
                String deviceName = device.getName();
                String deviceAddress = device.getAddress();
                if (deviceName == null) {
                    deviceName = "UNKNOWN";
                }
                if(!deviceName.contains("UNKNOWN")){
                deviceListAdapter.add(deviceName + "\n" + deviceAddress);
                }
            }
        }
    };

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRunning = false;
        // Assicurati di deregistrare il BroadcastReceiver quando l'Activity viene distrutta
        unregisterReceiver(bluetoothReceiver);
    }
}