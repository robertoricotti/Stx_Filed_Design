package utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

public class LocationUtils {

    public static boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static void requestLocationSettings(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Versione Android 6.0 e successiva
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            context.startActivity(intent);
        } else {
            // Versione Android precedente a 6.0
            Toast.makeText(context, "Impossibile aprire le impostazioni di localizzazione direttamente", Toast.LENGTH_SHORT).show();
        }
    }

    public static class LocationStateReceiver extends BroadcastReceiver {
        private final Context context;

        public LocationStateReceiver(Context context) {
            this.context = context;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (isLocationEnabled(context)) {
                context.unregisterReceiver(this);
            } else {
                requestLocationSettings(context);
            }
        }
    }
}

