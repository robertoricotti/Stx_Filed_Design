package utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

public class UsbReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if ("com.example.stx_field_design.USB_PERMISSION".equals(action)) {
            synchronized (this) {
                UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);

                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    if (accessory != null) {
                        // Autorizzazione USB concessa, puoi fare qualcosa con l'accessorio
                    }
                } else {
                    // Autorizzazione USB negata
                    Toast.makeText(context, "USB permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}