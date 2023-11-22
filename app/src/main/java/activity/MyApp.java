package activity;

import android.app.Activity;
import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.icu.text.DecimalFormat;
import android.icu.text.DecimalFormatSymbols;
import android.icu.text.NumberFormat;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import java.math.RoundingMode;
import java.util.Locale;

import services_and_bluetooth.DataSaved;


public class MyApp extends Application implements  Application.ActivityLifecycleCallbacks {
    int accCount = 0;
    public static Activity visibleActivity;


    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);


    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (activity != null){
            visibleActivity = activity;
        DataSaved.Actualactivity = String.valueOf(activity);
            setLocale(visibleActivity,"en");
            Log.d("MY_APP", String.valueOf(visibleActivity));

        }

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
    }
   /* public static void setLocale(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

    }*/
   public static void setLocale(Activity activity, String languageCode) {
       // Imposta la lingua
       Locale locale = new Locale(languageCode);
       Locale.setDefault(locale);

       // Imposta il separatore decimale come punto
       DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(locale);
       decimalFormatSymbols.setDecimalSeparator('.');

       // Applica le configurazioni alla risorsa
       Resources resources = activity.getResources();
       Configuration config = resources.getConfiguration();
       config.setLocale(locale);
       resources.updateConfiguration(config, resources.getDisplayMetrics());


   }


}
