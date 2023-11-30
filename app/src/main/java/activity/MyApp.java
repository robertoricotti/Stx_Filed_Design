package activity;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.icu.text.DecimalFormat;
import android.icu.text.DecimalFormatSymbols;
import android.icu.text.NumberFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.stx_field_design.R;

import java.math.RoundingMode;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import can.Can_Decoder;
import dialogs.CloseAppDialog;
import dialogs.ConnectDialog;
import gnss.My_LocationCalc;
import gnss.Nmea_In;
import services_and_bluetooth.Bluetooth_CAN_Service;
import services_and_bluetooth.Bluetooth_GNSS_Service;
import services_and_bluetooth.DataSaved;
import utils.FullscreenActivity;


public class MyApp extends Application implements Application.ActivityLifecycleCallbacks {
    static boolean showCoord;
    static float screenWidth;
    private volatile boolean mRunning = false;
    private ScheduledExecutorService executorService;
    Activity whoLaunch;
    ImageView btn1, btn2, btn3, btn4, btn5;
    TextView txt1, txt2, txt3, txt4, txt5, txt_coord, txt_canstat;
    LinearLayout topLayout;

    public static Activity visibleActivity;


    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);


    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {


        switch (activity.toString().substring(0, (activity.toString().indexOf("@")))) {
            case "activity.UOM_Activity":
                activity.setContentView(R.layout.activity_uom);//setta il layout di riferimento dell'activity
                whoLaunch = activity;
                updateUI(whoLaunch, true);
                btn1.setImageResource(R.drawable.btn_to_indietro);
                btn2.setVisibility(View.INVISIBLE);
                btn3.setVisibility(View.INVISIBLE);
                btn4.setVisibility(View.INVISIBLE);
                btn5.setVisibility(View.INVISIBLE);
                btn1.setOnClickListener(view -> {
                    activity.startActivity(new Intent(activity, MainActivity.class));//gestisce gli eventi
                    activity.finish();
                });
                break;

            case "activity.MainActivity":
                activity.setContentView(R.layout.activity_main);//setta il layout di riferimento dell'activity
                whoLaunch = activity;
                updateUI(whoLaunch, true);

                btn2.setVisibility(View.INVISIBLE);
                btn3.setVisibility(View.INVISIBLE);
                btn4.setVisibility(View.INVISIBLE);
                btn1.setImageResource(R.drawable.btn_poweroff);
                btn5.setImageResource(R.drawable.btn_ecu_connect);


                btn1.setOnClickListener(view -> {
                    new CloseAppDialog(activity).show();
                });
                btn5.setOnClickListener(view -> {
                    new ConnectDialog(activity, 2).show();
                });
                break;
            case "activity.ABProject":
                activity.setContentView(R.layout.ab_project);
                whoLaunch = activity;
                updateUI(whoLaunch, true);
                btn1.setImageResource(R.drawable.btn_to_indietro);
                btn2.setImageResource(R.drawable.btn_selectfakepos);
                btn3.setImageResource(R.drawable.btn_calcola);
                btn4.setImageResource(R.drawable.btn_save);
                btn5.setVisibility(View.GONE);
                btn1.setOnClickListener(view -> {
                    ((ABProject)activity).metodoBack();
                    activity.startActivity(new Intent(activity, MenuProject.class));
                    activity.finish();
                });
                btn2.setOnClickListener(view -> {
                    ((ABProject) activity).metodoPick();
                });
                btn3.setOnClickListener(view -> {
                    ((ABProject) activity).metodoCalcola();
                });
                btn4.setOnClickListener(view -> {
                    ((ABProject) activity).metodoSave();
                });

                break;
            case "activity.AntennaMeasure":
                activity.setContentView(R.layout.activity_antenna_measure);
                whoLaunch = activity;
                break;
            case "activity.BT_DevicesActivity":
                activity.setContentView(R.layout.activity_bt_devices);
                whoLaunch = activity;
                updateUI(whoLaunch, true);
                btn1.setImageResource(R.drawable.btn_to_indietro);
                btn2.setVisibility(View.INVISIBLE);
                btn3.setVisibility(View.INVISIBLE);
                btn4.setVisibility(View.INVISIBLE);
                btn5.setVisibility(View.INVISIBLE);
                btn1.setOnClickListener(view -> {
                    activity.startActivity(new Intent(activity, MainActivity.class));
                    activity.finish();
                });
                break;
            case "activity.ExcavatorMeasureXYZ":
                activity.setContentView(R.layout.activity_excavator_measure_xyz);
                whoLaunch = activity;
                break;
            case "activity.FilesActivity":
                activity.setContentView(R.layout.activity_files);
                whoLaunch = activity;
                break;
            case "activity.MchMeaureActivity":
                activity.setContentView(R.layout.activity_mch_meaure);
                whoLaunch = activity;
                break;
            case "activity.AB_WorkActivity":
                activity.setContentView(R.layout.activity_load_project);
                whoLaunch = activity;
                updateUI(whoLaunch, true);
                btn5.setVisibility(View.GONE);
                btn1.setImageResource(R.drawable.btn_to_indietro);
                btn2.setImageResource(R.drawable.btn_coordinate_list);
                btn3.setImageResource(R.drawable.btn_infoapp);
                btn4.setImageResource(R.drawable.btn_ecu_connect);
                btn1.setOnClickListener(view -> {
                    ((AB_WorkActivity) activity).metodoBack();
                    activity.startActivity(new Intent(activity, MainActivity.class));
                    activity.finish();
                });
                btn2.setOnClickListener(view -> {
                    ((AB_WorkActivity) activity).metodoLineId();
                });
                btn3.setOnClickListener(view -> {
                    ((AB_WorkActivity) activity).metodoOpenList();
                });
                btn4.setOnClickListener(view -> {
                    new ConnectDialog(activity,2).show();
                });
                break;
            case "activity.MenuProject":
                activity.setContentView(R.layout.activity_menu_project);
                whoLaunch = activity;
                updateUI(whoLaunch, true);
                btn2.setVisibility(View.INVISIBLE);
                btn3.setVisibility(View.INVISIBLE);
                btn4.setVisibility(View.INVISIBLE);
                btn1.setImageResource(R.drawable.btn_to_indietro);
                btn5.setImageResource(R.drawable.btn_load_project);
                btn1.setOnClickListener(view -> {
                    activity.startActivity(new Intent(activity,MainActivity.class));
                    activity.finish();
                });

                btn5.setOnClickListener(view -> {
                    ((MenuProject)activity).metodoLoadProject();
                });

                break;
            case "activity.SettingsActivity":
                activity.setContentView(R.layout.activity_settings);
                whoLaunch = activity;
                updateUI(whoLaunch, true);
                btn2.setVisibility(View.INVISIBLE);
                btn3.setVisibility(View.INVISIBLE);
                btn4.setVisibility(View.INVISIBLE);
                btn1.setImageResource(R.drawable.btn_to_indietro);
                btn5.setImageResource(R.drawable.btn_save);
                btn1.setOnClickListener(view -> {
                    activity.startActivity(new Intent(activity,MainActivity.class));
                    activity.finish();
                });
                btn5.setOnClickListener(view -> {
                    ((SettingsActivity)activity).metodoSave();
                });
                break;
            case "activity.UsbActivity":
                //questa activity viene gestita dalla classe UsbActivity.class
                activity.setContentView(R.layout.activity_usb_inout);
                FullscreenActivity.setFullScreen(activity);
                break;
            case "activity.LaunchScreenActivity":
                printDisplayDimensions(activity);
                break;


        }
    }


    @Override
    public void onActivityStarted(Activity activity) {
        if (activity != null) {
            visibleActivity = activity;
            DataSaved.Actualactivity = String.valueOf(activity);
            setLocale(visibleActivity, "en");


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

        updateUI(activity, false);
    }

    public void findViewUPDW(Activity activity) {

        //inizializza i componenti
        topLayout = activity.findViewById(R.id.topLayout);
        btn1 = activity.findViewById(R.id.btn_1);
        btn2 = activity.findViewById(R.id.btn_2);
        btn3 = activity.findViewById(R.id.btn_3);
        btn4 = activity.findViewById(R.id.btn_4);
        btn5 = activity.findViewById(R.id.btn_5);
        txt1 = activity.findViewById(R.id.txt_1);
        txt2 = activity.findViewById(R.id.txt_2);
        txt3 = activity.findViewById(R.id.txt_3);
        txt4 = activity.findViewById(R.id.txt_4);
        txt5 = activity.findViewById(R.id.txt_5);
        txt_coord = activity.findViewById(R.id.txt_coord);
        txt_canstat = activity.findViewById(R.id.txt_canstat);
        if(screenWidth<400f){
            txt_coord.setTextSize(12f);
            txt_canstat.setTextSize(12f);
        }else {
            txt_coord.setTextSize(16f);
            txt_canstat.setTextSize(16f);
        }

        txt_coord.setOnClickListener(view -> {
            showCoord = !showCoord;
        });
        topLayout.setOnClickListener(view -> {
            new ConnectDialog(activity, 1).show();
        });
    }
    public String setQuality(String s) {
        String out = "";
        switch (s) {

            case "0":
            case "1":
                out = "SINGLE";
                break;
            case "2":
                out = "DGNSS";
                break;
            case "4":
                out = "FIX";
                break;
            case "5":
                out = "FLOAT";
                break;
            case "6":
                out = "INS";
                break;
            default:
                out = "DISCONNECTED";
                break;


        }
        return out;
    }
    public void updateUI(Activity activity, boolean mRunning) {
        if (activity.equals(whoLaunch)) {
            FullscreenActivity.setFullScreen(activity);
            findViewUPDW(activity);
            if (mRunning && executorService == null) {

                this.mRunning = true;

                executorService = Executors.newSingleThreadScheduledExecutor();
                executorService.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                txt1.setText(Nmea_In.ggaSat);
                                txt2.setText(setQuality(Nmea_In.ggaQuality));
                                txt3.setText("CQ: " + Nmea_In.VRMS_);
                                txt4.setText(Nmea_In.ggaRtk);
                                txt5.setText(String.format("%.3f", DataSaved.D_AltezzaAnt));
                                if (showCoord) {
                                    txt_coord.setText("Lat:" + My_LocationCalc.decimalToDMS(Nmea_In.mLat_1) + "\tLon:"
                                            + My_LocationCalc.decimalToDMS(Nmea_In.mLon_1) + "  Z:"
                                            + String.format("%.3f", Nmea_In.Quota1).replace(",", "."));
                                } else {
                                    txt_coord.setText("E: " + String.format("%.3f", Nmea_In.Crs_Est).replace(",", ".") + "\t\tN: "
                                            + String.format("%.3f", Nmea_In.Crs_Nord).replace(",", ".") + "  Z: "
                                            + String.format("%.3f", Nmea_In.Quota1).replace(",", "."));
                                }
                                if(!Bluetooth_CAN_Service.canIsConnected){
                                    txt_canstat.setTextColor(Color.RED);
                                    txt_canstat.setText("CAN DISCONNECTED");
                                }else{
                                    txt_canstat.setTextColor(Color.BLUE);
                                    txt_canstat.setText(String.valueOf("Pitch: " + String.format("%.2f", Can_Decoder.correctPitch).replace(",", ".") + "°       Roll: " + String.format("%.2f", Can_Decoder.correctRoll).replace(",", ".") + "°"));

                                }
                                if(!Bluetooth_GNSS_Service.gpsIsConnected){
                                    txt_coord.setTextColor(Color.RED);
                                }else {
                                    txt_coord.setTextColor(Color.BLUE);
                                }
                                System.out.println("DIOCANEEEEEEEEEEEEEEEE");

                            }
                        });
                    }
                }, 0, 50, TimeUnit.MILLISECONDS);
            } else if (!mRunning && executorService != null) {
                // Ferma il ciclo solo se è stato avviato
                this.mRunning = false;
                executorService.shutdown();
                executorService = null;
            }
        }
    }

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


    public static void printDisplayDimensions(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        if (windowManager != null) {
            Display display = windowManager.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);

            int widthPixels = metrics.widthPixels;
            int heightPixels = metrics.heightPixels;

            float density = metrics.density;
            int densityDpi = metrics.densityDpi;

            float widthDp = widthPixels / density;
            float heightDp = heightPixels / density;
            screenWidth=widthDp;
            Log.d("mYdISPLAY","Width pixels: " + String.valueOf(widthPixels));
            Log.d("mYdISPLAY","Height pixels: "+ String.valueOf(heightPixels));
            Log.d("mYdISPLAY","Density : "+ String.valueOf(+ density));
            Log.d("mYdISPLAY","densityDpi : "+ String.valueOf(+ densityDpi));
            Log.d("mYdISPLAY","widthDp : "+ String.valueOf(+ widthDp));
            Log.d("mYdISPLAY","heightDp : "+ String.valueOf(+ heightDp));
        }
    }
}

