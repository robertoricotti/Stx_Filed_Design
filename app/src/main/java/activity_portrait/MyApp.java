package activity_portrait;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.icu.text.DecimalFormatSymbols;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.stx_field_design.R;
import com.van.jni.VanCmd;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import can.Can_Decoder;
import dialogs.CloseAppDialog;
import dialogs.ConnectDialog;
import dialogs.CustomToast;
import dialogs.Dialog_Edit_Zeta;
import gnss.My_LocationCalc;
import gnss.Nmea_In;
import services_and_bluetooth.Bluetooth_CAN_Service;
import services_and_bluetooth.Bluetooth_GNSS_Service;
import services_and_bluetooth.DataSaved;
import utils.FullscreenActivity;
import utils.LanguageSetter;
import utils.MyRW_IntMem;
import utils.Utils;


@SuppressLint("NewApi")
public class MyApp extends Application implements Application.ActivityLifecycleCallbacks {

    static boolean showCoord;
    static float screenWidth;
    private volatile boolean mRunning = false;
    private ScheduledExecutorService executorService;
    Activity whoLaunch;
    ImageView btn1, btn2, btn3, btn4, btn5,imgBTTop;
    TextView txt1, txt2, txt3, txt4, txt5, txt_coord, txt_canstat;
    ImageView imgConnetti;
    public int SCREEN_ORIENTATION;

    public static Activity visibleActivity;


    @SuppressLint("NewApi")
    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (activity != null) {
            LanguageSetter.setLocale(activity, "en");
        }
        if (new MyRW_IntMem().MyRead("display", this) == null) {
            SCREEN_ORIENTATION = (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (new MyRW_IntMem().MyRead("display", this).equals("1")) {

            SCREEN_ORIENTATION = (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            try {
                if (DataSaved.deviceType.equals("SRT8PROS") || DataSaved.deviceType.equals("SRT7PROS")) {
                    SCREEN_ORIENTATION = (ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                } else {
                    SCREEN_ORIENTATION = (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            } catch (Exception e) {
                SCREEN_ORIENTATION = (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

        }
        activity.setRequestedOrientation(SCREEN_ORIENTATION);
        switch (activity.toString().substring(0, (activity.toString().indexOf("@")))) {
            case "activity_portrait.MainActivity":
                m_MainActivity(activity);//method to manage components in activity...see below
                break;
            case "activity_portrait.UOM_Activity":
                m_UOM_Activity(activity);
                break;
            case "activity_portrait.ABProject":

                m_ABProject(activity);
                break;
            case "activity_portrait.AntennaMeasure":
                m_AntennaMeasure(activity);
                break;
            case "activity_portrait.BT_DevicesActivity":

                m_BT_DevicesActivity(activity);
                break;

            case "activity_portrait.MchMeaureActivity":

                m_MchMeasureActivity(activity);
                break;
            case "activity_portrait.AB_WorkActivity":

                m_AB_WorkingActivity(activity);
                break;
            case "activity_portrait.MenuProject":

                m_MenuProject(activity);
                break;
            case "activity_portrait.SettingsActivity":

                m_SettingsActivity(activity);
                break;
            case "activity_portrait.UsbActivity":
                m_UsbActivity(activity);

                break;
            case "activity_portrait.Antennas_Blade_Activity":
                m_Antennas_Blade_Activity(activity);
                break;
            case "activity_portrait.CAN_DebugActivity":
                m_CAN_Debug_Activity(activity);
                break;
            case "activity_portrait.Create_1P":
                m_Create_1P_Activity(activity);
                break;
            case "activity_portrait.P_WorkActivity":
                m_P_WorkActivity(activity);
                break;
            case "activity_portrait.Debug_Activity":
                m_Debug_Activity(activity);
                break;
            case "activity_portrait.Activity_Gnss_Setup":
                m_GnssSetup_Activity(activity);
                break;
            case "activity_portrait.Create_Area":
                m_Create_Area(activity);
                break;
            case "activity_portrait.LaunchScreenActivity":
                String b = Build.BRAND;
                DataSaved.deviceType = b;
                new MyRW_IntMem().MyWrite("_deviceType", b, activity);
                Log.d("VersioneAnd", b);
                if (DataSaved.deviceType.equals("SRT8PROS") || DataSaved.deviceType.equals("SRT7PROS")) {
                    VanCmd.exec("wm overscan 0,-210,0,-210", 10);
                }
                printDisplayDimensions(activity);//prendo i dati del display
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
        if (activity != null) {
            EventBus.getDefault().unregister(activity);
        }


    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

        m_updateUI(activity, false);
    }
    public void m_Create_Area(Activity activity){
        activity.setContentView(R.layout.activity_create_area);
        whoLaunch = activity;
        m_updateUI(whoLaunch, true);
        btn1.setImageResource(R.drawable.btn_to_indietro);
        btn2.setImageResource(R.drawable.btn_selectfakepos);
        btn3.setImageResource(R.drawable.btn_delete);
        btn4.setImageResource(R.drawable.btn_coordinate_list);
        btn5.setImageResource(R.drawable.btn_save);
        btn1.setOnClickListener(view -> {
            activity.startActivity(new Intent(activity, MenuProject.class));
            activity.finish();
        });
        btn2.setOnClickListener(view -> {
            ((Create_Area) activity).addPoint();
        });
        btn3.setOnClickListener(view -> {
            ((Create_Area) activity).clearPoint();
        });
        btn4.setOnClickListener(view -> {
            ((Create_Area) activity).showList();
        });
        btn5.setOnClickListener(view -> {
            ((Create_Area) activity).saveProj();
        });
    }

    public void m_GnssSetup_Activity(Activity activity){
        activity.setContentView(R.layout.activity_gnss_setup_nuova_7);
        whoLaunch = activity;
        m_updateUI(whoLaunch, true);
        btn1.setImageResource(R.drawable.btn_to_indietro);
        btn2.setImageResource(R.drawable.btn_copy_from_usb);
        btn3.setImageResource(R.drawable.btn_copy_to_usb);
        btn4.setImageResource(R.drawable.btn_delete);
        btn5.setImageResource(R.drawable.play_96);
        btn1.setOnClickListener(view -> {
            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.finish();
        });
        btn2.setOnClickListener(view -> {
            ((Activity_Gnss_Setup) activity).exBtn2();

        });
        btn3.setOnClickListener(view -> {
            ((Activity_Gnss_Setup) activity).exBtn3();
        });
        btn4.setOnClickListener(view -> {
            ((Activity_Gnss_Setup) activity).exBtn4();
        });
        btn5.setOnClickListener(view -> {
            ((Activity_Gnss_Setup) activity).exBtn5();
        });
    }
    public void m_Debug_Activity(Activity activity) {
        activity.setContentView(R.layout.activity_debug);//setta il layout di riferimento dell'activity
        whoLaunch = activity;
        m_updateUI(whoLaunch, true);
        btn5.setVisibility(View.INVISIBLE);
        btn1.setImageResource(R.drawable.btn_to_indietro);
        btn2.setVisibility(View.INVISIBLE);
        btn3.setVisibility(View.INVISIBLE);
        btn4.setImageResource(R.drawable.btn_ecu_connect);

        btn5.setVisibility(View.GONE);
        btn1.setOnClickListener(view -> {
            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.finish();
        });
        btn4.setOnClickListener(view -> {
            int i = 0;
            if (DataSaved.deviceType.equals("SRT8PROS") || DataSaved.deviceType.equals("SRT7PROS")) {
                i = 3;
            } else {
                i = 2;
            }
            new ConnectDialog(activity, i).show();
        });



    }
    public void m_P_WorkActivity(Activity activity) {
        activity.setContentView(R.layout.activity_pwork);//setta il layout di riferimento dell'activity
        whoLaunch = activity;
        m_updateUI(whoLaunch, true);
        btn5.setVisibility(View.GONE);
        btn1.setImageResource(R.drawable.btn_to_indietro);
        btn2.setVisibility(View.INVISIBLE);
        btn3.setVisibility(View.INVISIBLE);
        btn4.setImageResource(R.drawable.btn_ecu_connect);

        btn1.setOnClickListener(view -> {
            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.finish();
        });

        btn4.setOnClickListener(view -> {
            int i = 0;
            if (DataSaved.deviceType.equals("SRT8PROS") || DataSaved.deviceType.equals("SRT7PROS")) {
                i = 3;
            } else {
                i = 2;
            }
            new ConnectDialog(activity, i).show();
        });

    }
    public void m_Create_1P_Activity(Activity activity) {
        activity.setContentView(R.layout.activity_create1_p);//setta il layout di riferimento dell'activity
        whoLaunch = activity;
        m_updateUI(whoLaunch, true);
        btn2.setVisibility(View.INVISIBLE);
        btn3.setVisibility(View.INVISIBLE);
        btn4.setVisibility(View.INVISIBLE);
        btn1.setImageResource(R.drawable.btn_to_indietro);
        btn5.setImageResource(R.drawable.btn_save);
        btn1.setOnClickListener(view -> {
            activity.startActivity(new Intent(activity, MenuProject.class));
            activity.finish();
        });
        btn5.setOnClickListener(view -> {
            new Dialog_Edit_Zeta(activity,0).show();
        });

    }

    public void m_Antennas_Blade_Activity(Activity activity) {
        activity.setContentView(R.layout.activity_antennas_blade);//setta il layout di riferimento dell'activity
        whoLaunch = activity;
        m_updateUI(whoLaunch, true);
        btn2.setVisibility(View.INVISIBLE);
        btn3.setVisibility(View.INVISIBLE);
        btn4.setVisibility(View.INVISIBLE);
        btn1.setImageResource(R.drawable.btn_to_indietro);
        btn5.setImageResource(R.drawable.btn_save);
        btn1.setOnClickListener(view -> {
            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.finish();
        });
        btn5.setOnClickListener(view -> {
            ((Antennas_Blade_Activity) activity).saveData();
            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.finish();
        });
    }

    public void m_MainActivity(Activity activity) {
        activity.setContentView(R.layout.activity_main);//setta il layout di riferimento dell'activity
        whoLaunch = activity;
        m_updateUI(whoLaunch, true);
        btn2.setVisibility(View.INVISIBLE);
        btn3.setVisibility(View.INVISIBLE);
        btn5.setVisibility(View.GONE);
        btn1.setImageResource(R.drawable.btn_poweroff);
        btn4.setImageResource(R.drawable.btn_ecu_connect);

        btn1.setOnClickListener(view -> {
            new CloseAppDialog(activity).show();
        });
        btn4.setOnClickListener(view -> {
            int i = 0;
            if (DataSaved.deviceType.equals("SRT8PROS") || DataSaved.deviceType.equals("SRT7PROS")) {
                i = 3;
            } else {
                i = 2;
            }
            new ConnectDialog(activity, i).show();
        });

    }

    public void m_UsbActivity(Activity activity) {
        activity.setContentView(R.layout.activity_usb_inout);
        whoLaunch = activity;
        m_updateUI(whoLaunch, true);
        btn1.setImageResource(R.drawable.btn_to_indietro);
        btn2.setImageResource(R.drawable.btn_read_usb);
        btn3.setImageResource(R.drawable.btn_copy_from_usb);
        btn4.setImageResource(R.drawable.btn_copy_to_usb);

        btn5.setImageResource(R.drawable.btn_delete);
        if (Build.VERSION.SDK_INT <= 30) {
            btn3.setAlpha(0.3f);
            btn3.setEnabled(false);
            btn4.setAlpha(0.3f);
            btn4.setEnabled(false);

        }
        btn1.setOnClickListener(view -> {
            ((UsbActivity) activity).exBtn1();
        });
        btn2.setOnClickListener(view -> {
            ((UsbActivity) activity).exBtn2();
        });
        btn3.setOnClickListener(view -> {
            ((UsbActivity) activity).exBtn3();
        });
        btn4.setOnClickListener(view -> {
            ((UsbActivity) activity).exBtn4();
        });
        btn5.setOnClickListener(view -> {
            ((UsbActivity) activity).exBtn5();
        });
    }

    public void m_UOM_Activity(Activity activity) {
        activity.setContentView(R.layout.activity_uom);//setta il layout di riferimento dell'activity
        whoLaunch = activity;
        m_updateUI(whoLaunch, true);
        btn1.setImageResource(R.drawable.btn_to_indietro);
        btn3.setImageResource(R.drawable.misura_punto);

        btn2.setVisibility(View.INVISIBLE);
        btn3.setVisibility(View.VISIBLE);
        btn4.setVisibility(View.INVISIBLE);
        btn5.setVisibility(View.INVISIBLE);
        btn1.setOnClickListener(view -> {
            activity.startActivity(new Intent(activity, MainActivity.class));//gestisce gli eventi
            activity.finish();
        });
        btn3.setOnClickListener(view -> {
            //implementare logica misura punto

            if (((UOM_Activity) activity).size < 2) {
                ((UOM_Activity) activity).size += 1;
            }
            if (((UOM_Activity) activity).size == 1) {
                ((UOM_Activity) activity).A_coord = new double[]{Nmea_In.Crs_Est, Nmea_In.Crs_Nord, Nmea_In.Quota1};

                new CustomToast(visibleActivity, "P1").show();
            }
            if (((UOM_Activity) activity).size == 2) {

                ((UOM_Activity) activity).B_coord = new double[]{Nmea_In.Crs_Est, Nmea_In.Crs_Nord, Nmea_In.Quota1};

                new CustomToast(visibleActivity, "P2").show();
            }

        });
    }

    public void m_ABProject(Activity activity) {

        activity.setContentView(R.layout.activity_ab_project);
        whoLaunch = activity;
        m_updateUI(whoLaunch, true);
        btn1.setImageResource(R.drawable.btn_to_indietro);
        btn2.setImageResource(R.drawable.btn_selectfakepos);
        btn3.setImageResource(R.drawable.btn_calcola);
        btn5.setImageResource(R.drawable.btn_save);
        btn4.setVisibility(View.INVISIBLE);
        btn1.setOnClickListener(view -> {
            ((ABProject) activity).metodoBack();
            activity.startActivity(new Intent(activity, MenuProject.class));
            activity.finish();
        });
        btn2.setOnClickListener(view -> {
            if(activity instanceof ABProject && ((ABProject)activity).pickIndex==0){
            new Dialog_Edit_Zeta(activity,0).show();}
            else {
                ((ABProject)activity).metodoPick();
            }

        });
       btn3.setVisibility(View.INVISIBLE);
        btn5.setOnClickListener(view -> {
            ((ABProject) activity).metodoSave();
        });
    }

    public void m_AntennaMeasure(Activity activity) {
        activity.setContentView(R.layout.activity_antenna_measure);
        whoLaunch = activity;
        m_updateUI(whoLaunch, true);
        btn1.setImageResource(R.drawable.btn_to_indietro);
        btn2.setVisibility(View.INVISIBLE);
        btn3.setVisibility(View.INVISIBLE);
        btn4.setVisibility(View.INVISIBLE);
        btn5.setVisibility(View.INVISIBLE);
        btn1.setOnClickListener(view -> {
            ((AntennaMeasure) activity).goBack();
        });
    }

    public void m_BT_DevicesActivity(Activity activity) {
        activity.setContentView(R.layout.activity_bt_devices);

        whoLaunch = activity;
        m_updateUI(whoLaunch, true);
        btn1.setImageResource(R.drawable.btn_to_indietro);
        btn2.setVisibility(View.INVISIBLE);
        btn3.setVisibility(View.INVISIBLE);
        btn4.setVisibility(View.INVISIBLE);
        btn5.setVisibility(View.INVISIBLE);
        btn1.setOnClickListener(view -> {
            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.finish();
        });
    }

    public void m_MchMeasureActivity(Activity activity) {
        activity.setContentView(R.layout.activity_mch_meaure);
        whoLaunch = activity;
        m_updateUI(whoLaunch, true);
        btn1.setImageResource(R.drawable.btn_to_indietro);
        btn5.setImageResource(R.drawable.btn_to_indietro);
        btn5.setRotation(180f);
        btn2.setVisibility(View.INVISIBLE);
        btn3.setVisibility(View.INVISIBLE);
        btn4.setVisibility(View.INVISIBLE);
        btn1.setOnClickListener(view -> {
            ((MchMeaureActivity) activity).exit();
        });
        btn5.setOnClickListener(view -> {
            ((MchMeaureActivity) activity).toGnss();
        });


    }

    public void m_AB_WorkingActivity(Activity activity) {
        activity.setContentView(R.layout.activity_load_project);
        whoLaunch = activity;
        m_updateUI(whoLaunch, true);
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
            int i = 0;
            if (DataSaved.deviceType.equals("SRT8PROS") || DataSaved.deviceType.equals("SRT7PROS")) {
                i = 3;
            } else {
                i = 2;
            }
            new ConnectDialog(activity, i).show();
        });
    }

    public void m_MenuProject(Activity activity) {
        activity.setContentView(R.layout.activity_menu_project);
        whoLaunch = activity;
        m_updateUI(whoLaunch, true);
        btn2.setVisibility(View.INVISIBLE);
        btn3.setVisibility(View.INVISIBLE);
        btn4.setVisibility(View.INVISIBLE);
        btn1.setImageResource(R.drawable.btn_to_indietro);
        btn5.setImageResource(R.drawable.btn_load_project);
        btn1.setOnClickListener(view -> {
            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.finish();
        });

        btn5.setOnClickListener(view -> {
            ((MenuProject) activity).metodoLoadProject();
        });

    }

    public void m_SettingsActivity(Activity activity) {
        activity.setContentView(R.layout.activity_settings);
        whoLaunch = activity;
        m_updateUI(whoLaunch, true);
        btn2.setVisibility(View.INVISIBLE);
        btn3.setVisibility(View.INVISIBLE);
        btn4.setVisibility(View.INVISIBLE);
        btn1.setImageResource(R.drawable.btn_to_indietro);
        btn5.setImageResource(R.drawable.btn_save);
        btn1.setOnClickListener(view -> {
            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.finish();
        });
        btn5.setOnClickListener(view -> {
            ((SettingsActivity) activity).metodoSave();
        });
    }

    public void m_CAN_Debug_Activity(Activity activity) {
        activity.setContentView(R.layout.activity_can_debug);
        whoLaunch = activity;
        m_updateUI(whoLaunch, true);
        btn1.setImageResource(R.drawable.btn_to_indietro);
        btn5.setImageResource(R.drawable.btn_pause);
        btn3.setImageResource(R.drawable.btn_delete);
        btn2.setVisibility(View.INVISIBLE);
        btn3.setVisibility(View.VISIBLE);
        btn4.setVisibility(View.INVISIBLE);
        btn5.setVisibility(View.VISIBLE);
        btn1.setOnClickListener(view -> {
            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.finish();
        });
        btn3.setOnClickListener(view -> {
            ((CAN_DebugActivity) activity).clearList();

        });
        btn5.setOnClickListener(view -> {
            ((CAN_DebugActivity) activity).playpause();
            if (((CAN_DebugActivity) activity).play) {
                btn5.setImageResource(R.drawable.btn_pause);
            } else {
                btn5.setImageResource(R.drawable.btn_play);
            }
        });
    }

    public void m_findView(Activity activity) {
        //inizializza i componenti
        imgConnetti = activity.findViewById(R.id.imgConnect);
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
        imgBTTop=activity.findViewById(R.id.img_bt_top);
        txt_coord = activity.findViewById(R.id.txt_coord);
        txt_canstat = activity.findViewById(R.id.txt_canstat);
        if (screenWidth < 400f) {
            txt_coord.setTextSize(13f);
            txt_canstat.setTextSize(13f);
        } else if (screenWidth < 750f && screenWidth > 400f) {
            txt_coord.setTextSize(17f);
            txt_canstat.setTextSize(17f);
        } else if (screenWidth > 750f) {
            txt_coord.setTextSize(23f);
            txt_canstat.setTextSize(23f);
            txt1.setTextSize(17f);
            txt2.setTextSize(17f);
            txt3.setTextSize(17f);
            txt4.setTextSize(17f);
            txt5.setTextSize(17f);
        }

        txt_coord.setOnClickListener(view -> {
            showCoord = !showCoord;
        });
        imgBTTop.setOnClickListener(view -> {
            if(DataSaved.useDemo==0){
            new ConnectDialog(activity, 1).show();}
            else {
                new CustomToast(activity,"Internal GPS Selected").show();
            }
        });
    }

    public void m_updateUI(Activity activity, boolean mRunning) {
        if (activity.equals(whoLaunch)) {
            FullscreenActivity.setFullScreen(activity);
            m_findView(activity);
            if (mRunning && executorService == null) {

                this.mRunning = true;

                executorService = Executors.newSingleThreadScheduledExecutor();
                executorService.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        activity.runOnUiThread(new Runnable() {
                            @SuppressLint({"SetTextI18n", "DefaultLocale"})
                            @Override
                            public void run() {
                                if (activity instanceof ABProject) {
                                    if (((ABProject) activity).progressBar.getVisibility() == View.VISIBLE) {
                                        btn1.setEnabled(false);
                                        btn2.setEnabled(false);
                                        btn3.setEnabled(false);
                                        btn4.setEnabled(false);
                                        btn5.setEnabled(false);
                                    } else {
                                        btn1.setEnabled(true);
                                        btn2.setEnabled(true);
                                        btn3.setEnabled(true);
                                        btn4.setEnabled(true);
                                        btn5.setEnabled(true);
                                    }
                                }
                                txt1.setText(Nmea_In.ggaSat);
                                txt2.setText(setQuality(Nmea_In.ggaQuality));
                                txt3.setText("UTM");
                                txt4.setText(Nmea_In.ggaRtk);
                                txt5.setText(Utils.readUnitOfMeasure(String.valueOf(DataSaved.D_AltezzaAnt), visibleActivity));
                                if (showCoord) {
                                    txt_coord.setText("Lat:" + My_LocationCalc.decimalToDMS(Nmea_In.mLat_1) + "\tLon:"
                                            + My_LocationCalc.decimalToDMS(Nmea_In.mLon_1) + "  Z:"
                                            + String.format("%.3f", Nmea_In.Quota1).replace(",", "."));
                                } else {
                                    txt_coord.setText("E: " + Utils.readSensorCalibration(String.valueOf(Nmea_In.Crs_Est),visibleActivity) + "\t\tN: "
                                            + Utils.readSensorCalibration(String.valueOf(Nmea_In.Crs_Nord),visibleActivity) + "  Z: "
                                            + Utils.readSensorCalibration(String.valueOf(Nmea_In.Quota1),visibleActivity));
                                }

                                if(visibleActivity instanceof Debug_Activity||visibleActivity instanceof AB_WorkActivity||visibleActivity instanceof P_WorkActivity|| visibleActivity instanceof MainActivity){
                                    if(Bluetooth_CAN_Service.canIsConnected){
                                        btn4.setImageTintList(getApplicationContext().getColorStateList(R.color.green));
                                    }else {
                                        btn4.setImageTintList(getApplicationContext().getColorStateList(R.color.white));
                                    }
                                }else {
                                    btn4.setImageTintList(getApplicationContext().getColorStateList(R.color.white));
                                }
                                if (!Bluetooth_CAN_Service.canIsConnected) {
                                    txt_canstat.setTextColor(Color.RED);
                                    txt_canstat.setText("CAN DISCONNECTED");
                                    //txt_canstat.setBackgroundColor(getResources().getColor(R.color.white));
                                } else {
                                    if (!Bluetooth_CAN_Service.canEmpty) {
                                        txt_canstat.setTextColor(Color.BLACK);
                                        txt_canstat.setText(String.valueOf("Pitch: " + String.format("%.2f", Can_Decoder.correctPitch).replace(",", ".") + "°       Roll: " + String.format("%.2f", Can_Decoder.correctRoll).replace(",", ".") + "°"));
                                        //txt_canstat.setBackgroundColor(getResources().getColor(R.color.white));
                                    } else {
                                        txt_canstat.setTextColor(Color.BLACK);
                                        txt_canstat.setText("NO DATA");
                                        //txt_canstat.setBackgroundColor(getResources().getColor(R.color.white));
                                    }
                                }
                                if (!Bluetooth_GNSS_Service.gpsIsConnected) {
                                    txt_coord.setTextColor(Color.RED);
                                    txt_coord.setBackgroundColor(getResources().getColor(R.color.white));
                                    if(DataSaved.useDemo==0){
                                        imgBTTop.setImageTintList(getApplicationContext().getColorStateList(R.color.white));
                                        txt_coord.setText("GPS DISCONNECTED");
                                    }

                                } else {
                                    if(DataSaved.useDemo==0){
                                        imgBTTop.setImageTintList(getApplicationContext().getColorStateList(R.color.green));
                                    }
                                    txt_coord.setTextColor(Color.BLACK);
                                    if (txt2.getText().toString().equals("FIX")) {
                                        txt_coord.setBackgroundColor(getResources().getColor(R.color.green));
                                    } else if (txt2.getText().toString().equals("DGNSS") || txt2.getText().toString().equals("FLOAT")) {
                                        txt_coord.setBackgroundColor(getResources().getColor(R.color.light_yellow));
                                    } else {
                                        txt_coord.setBackgroundColor(getResources().getColor(R.color.white));
                                    }
                                }
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

    public String setQuality(String s) {
        String out = "DISC.";
        if (s != null) {
            switch (s) {

                case "0":
                    out = "";
                    break;
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
                    out = "DISC.";
                    break;


            }
        }
        return out;
    }

    @SuppressLint("NewApi")
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
            screenWidth = widthDp;
            Log.d("mYdISPLAY", "Width pixels: " + String.valueOf(widthPixels));
            Log.d("mYdISPLAY", "Height pixels: " + String.valueOf(heightPixels));
            Log.d("mYdISPLAY", "Density : " + String.valueOf(+density));
            Log.d("mYdISPLAY", "densityDpi : " + String.valueOf(+densityDpi));
            Log.d("mYdISPLAY", "widthDp : " + String.valueOf(+widthDp));
            Log.d("mYdISPLAY", "heightDp : " + String.valueOf(+heightDp));
        }
    }
}

