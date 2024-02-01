package activity_portrait;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.stx_field_design.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dialogs.CustomMenu;
import dialogs.CustomToast;
import eventbus.CMD_Event;
import eventbus.SerialEvent;
import services_and_bluetooth.Bluetooth_GNSS_Service;
import services_and_bluetooth.DataSaved;
import utils.MyRW_IntMem;

public class Activity_Gnss_Setup extends AppCompatActivity {
    int isSc600 = 0;
    int countVisibility = 0;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> dataList;

    private boolean mRunning = true;
    private Handler mHandler;
    boolean enableWrite = false;
    ProgressBar progressBar;

    LinearLayout layoutGPS, layoutBT;
    CheckBox ck600, ck980;


    TextView debugtitle, macaddress, txtGGA, txtGGAH, txtHDT, txtGST, txtRMC, txtGSV, txtRadioenable, txtRadioCh, txtRadioProto, txtRadioSpac, txtRadioFunction, txtRtk;
    String fgga = "0", fggah = "0", fhdt = "0", fgst = "0", frmc = "0", fgsv = "0", cmdStatust, radioEn = "NO", sCh = "1", sProto = "3", sSpacing = "25",
            sRadioF = "RTK_IN", sRtk = "Off";


    private int indexOfMachine, comSelected;
    private boolean play = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findView();
        onClick();
    }

    public void exBtn2() {
        readAll();
    }

    public void exBtn3() {
        if (enableWrite) {
            writeAll();
        } else {
            new CustomToast(Activity_Gnss_Setup.this, "READ FIRST");
        }
    }

    public void exBtn4() {
        adapter.clear();
    }

    public void exBtn5() {
        play = !play;
    }

    private void findView() {


        layoutGPS = findViewById(R.id.gnssLayout);

        layoutBT = findViewById(R.id.layoutBT);


        macaddress = findViewById(R.id.stmacAddress);

        txtGGA = findViewById(R.id.txt_gga);
        txtGGAH = findViewById(R.id.txt_ggah);
        txtHDT = findViewById(R.id.txt_hdt);
        txtGST = findViewById(R.id.txt_gst);
        txtRMC = findViewById(R.id.txt_rmc);
        txtGSV = findViewById(R.id.txt_gsv);
        txtRadioenable = findViewById(R.id.txt_radioenable);
        txtRadioCh = findViewById(R.id.txt_radioch);
        txtRadioProto = findViewById(R.id.txt_radioproto);
        txtRadioSpac = findViewById(R.id.txt_radiospacing);
        txtRadioFunction = findViewById(R.id.txt_radiofunction);
        txtRtk = findViewById(R.id.txt_radiortk);

        progressBar = findViewById(R.id.progressBar2);

        listView = findViewById(R.id.txtnmeaDebug);
        debugtitle = findViewById(R.id.txtdebugtitle);

        ck600 = findViewById(R.id.ck600);
        ck980 = findViewById(R.id.ck980);

        dataList = new ArrayList<>();


        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);


        listView.setAdapter(adapter);
        comSelected = 4;


        updateUI();
        if (isSc600 == 0) {
            ck980.setChecked(true);
        } else if (isSc600 == 1) {
            ck600.setChecked(true);
        }


    }

    private void onClick() {
        ck600.setOnClickListener(view -> {
            ck600.setChecked(true);
            ck980.setChecked(false);
            isSc600 = 1;

        });
        ck980.setOnClickListener(view -> {
            ck600.setChecked(false);
            ck980.setChecked(true);
            isSc600 = 0;

        });


        txtGGA.setOnClickListener(view -> {
            List<String> menuItems = Arrays.asList(getResources().getStringArray(R.array.rate_options));
            CustomMenu customMenu = new CustomMenu(Activity_Gnss_Setup.this, "GGA");
            customMenu.show(menuItems, new CustomMenu.OnItemSelectedListener() {
                @Override
                public void onItemSelected(String selectedItem) {
                    //
                    if (isSc600 == 1) {
                        if (comSelected == 0 || comSelected == 1 || comSelected == 2 || comSelected == 4) {
                            switch (selectedItem) {


                                case "Off":
                                    txtGGA.setText("GGA: Off");
                                    fgga = "0";
                                    break;
                                case "10 Sec.":
                                    txtGGA.setText("GGA: 10 Sec");
                                    fgga = "10000";
                                    break;
                                case "5 Sec.":
                                    txtGGA.setText("GGA: 5 Sec");
                                    fgga = "5000";
                                    break;
                                case "2 Sec.":
                                    txtGGA.setText("GGA: 2 Sec");
                                    fgga = "2000";
                                    break;
                                case "1 Sec.":
                                    txtGGA.setText("GGA: 1 Sec");
                                    fgga = "1000";
                                    break;
                                case "2 Hz":
                                    txtGGA.setText("GGA: 2 Hz");
                                    fgga = "500";
                                    break;
                                case "5 Hz":
                                    txtGGA.setText("GGA: 5 Hz");
                                    fgga = "200";
                                    break;
                                case "10 Hz":
                                    txtGGA.setText("GGA: 10 Hz");
                                    fgga = "100";
                                    break;
                                case "20 Hz":
                                    txtGGA.setText("GGA: 20 Hz");
                                    fgga = "50";
                                    break;
                            }
                        }
                    } else {
                        if (comSelected == 0 || comSelected == 1 || comSelected == 2 || comSelected == 4) {
                            switch (selectedItem) {


                                case "Off":
                                    txtGGA.setText("GGA: Off");
                                    fgga = "0";
                                    break;
                                case "10 Sec.":
                                    txtGGA.setText("GGA: 10 Sec");
                                    fgga = "10000";
                                    break;
                                case "5 Sec.":
                                    txtGGA.setText("GGA: 5 Sec");
                                    fgga = "5000";
                                    break;
                                case "2 Sec.":
                                    txtGGA.setText("GGA: 2 Sec");
                                    fgga = "2000";
                                    break;
                                case "1 Sec.":
                                    txtGGA.setText("GGA: 1 Sec");
                                    fgga = "1000";
                                    break;
                                case "2 Hz":
                                    txtGGA.setText("GGA: 2 Hz");
                                    fgga = "500";
                                    break;
                                case "5 Hz":
                                    txtGGA.setText("GGA: 5 Hz");
                                    fgga = "200";
                                    break;
                                case "10 Hz":
                                    txtGGA.setText("GGA: 10 Hz");
                                    fgga = "100";
                                    break;
                                case "20 Hz":
                                    txtGGA.setText("GGA: 20 Hz");
                                    fgga = "50";
                                    break;
                            }
                        }
                    }
                    //
                }
            });


        });
        txtGGAH.setOnClickListener(view -> {

            List<String> menuItems = Arrays.asList(getResources().getStringArray(R.array.rate_options));
            CustomMenu customMenu = new CustomMenu(Activity_Gnss_Setup.this, "GGAH");
            customMenu.show(menuItems, new CustomMenu.OnItemSelectedListener() {
                @Override
                public void onItemSelected(String selectedItem) {
                    if (isSc600 == 1 || isSc600 == 0) {
                        if (comSelected == 0 || comSelected == 1 || comSelected == 2 || comSelected == 4) {
                            switch (selectedItem) {
                                case "Off":
                                    txtGGAH.setText("GGAH: Off");
                                    fggah = "0";
                                    break;
                                case "10 Sec.":
                                    txtGGAH.setText("GGAH: 10 Sec");
                                    fggah = "10000";
                                    break;
                                case "5 Sec.":
                                    txtGGAH.setText("GGAH: 5 Sec");
                                    fggah = "5000";
                                    break;
                                case "2 Sec.":
                                    txtGGAH.setText("GGAH: 2 Sec");
                                    fggah = "2000";
                                    break;
                                case "1 Sec.":
                                    txtGGAH.setText("GGAH: 1 Sec");
                                    fggah = "1000";
                                    break;
                                case "2 Hz":
                                    txtGGAH.setText("GGAH: 2 Hz");
                                    fggah = "500";
                                    break;
                                case "5 Hz":
                                    txtGGAH.setText("GGAH: 5 Hz");
                                    fggah = "200";
                                    break;
                                case "10 Hz":
                                    txtGGAH.setText("GGAH: 10 Hz");
                                    fggah = "100";
                                    break;
                                case "20 Hz":
                                    txtGGAH.setText("GGAH: 20 Hz");
                                    fggah = "50";
                                    break;
                            }
                        }
                    }
                }
            });


        });
        txtHDT.setOnClickListener(view -> {

            List<String> menuItems = Arrays.asList(getResources().getStringArray(R.array.rate_options));
            CustomMenu customMenu = new CustomMenu(Activity_Gnss_Setup.this, "HDT");
            customMenu.show(menuItems, new CustomMenu.OnItemSelectedListener() {
                @Override
                public void onItemSelected(String selectedItem) {
                    if (isSc600 == 1 || isSc600 == 0) {
                        if (comSelected == 0 || comSelected == 1 || comSelected == 2 || comSelected == 4) {
                            switch (selectedItem) {
                                case "Off":
                                    txtHDT.setText("HDT: Off");
                                    fhdt = "0";
                                    break;
                                case "10 Sec.":
                                    txtHDT.setText("HDT: 10 Sec");
                                    fhdt = "10000";
                                    break;
                                case "5 Sec.":
                                    txtHDT.setText("HDT: 5 Sec");
                                    fhdt = "5000";
                                    break;
                                case "2 Sec.":
                                    txtHDT.setText("HDT: 2 Sec");
                                    fhdt = "2000";
                                    break;
                                case "1 Sec.":
                                    txtHDT.setText("HDT: 1 Sec");
                                    fhdt = "1000";
                                    break;
                                case "2 Hz":
                                    txtHDT.setText("HDT: 2 Hz");
                                    fhdt = "500";
                                    break;
                                case "5 Hz":
                                    txtHDT.setText("HDT: 5 Hz");
                                    fhdt = "200";
                                    break;
                                case "10 Hz":
                                    txtHDT.setText("HDT: 10 Hz");
                                    fhdt = "100";
                                    break;
                                case "20 Hz":
                                    txtHDT.setText("HDT: 20 Hz");
                                    fhdt = "50";
                                    break;
                            }
                        }
                    }
                }
            });


        });
        txtGST.setOnClickListener(view -> {

            List<String> menuItems = Arrays.asList(getResources().getStringArray(R.array.rate_options));
            CustomMenu customMenu = new CustomMenu(Activity_Gnss_Setup.this, "GST");
            customMenu.show(menuItems, new CustomMenu.OnItemSelectedListener() {
                @Override
                public void onItemSelected(String selectedItem) {
                    if (isSc600 == 1 || isSc600 == 0) {
                        if (comSelected == 0 || comSelected == 1 || comSelected == 2 || comSelected == 4) {
                            switch (selectedItem) {
                                case "Off":
                                    txtGST.setText("GST: Off");
                                    fgst = "0";
                                    break;
                                case "10 Sec.":
                                    txtGST.setText("GST: 10 Sec");
                                    fgst = "10000";
                                    break;
                                case "5 Sec.":
                                    txtGST.setText("GST: 5 Sec");
                                    fgst = "5000";
                                    break;
                                case "2 Sec.":
                                    txtGST.setText("GST: 2 Sec");
                                    fgst = "2000";
                                    break;
                                case "1 Sec.":
                                    txtGST.setText("GST: 1 Sec");
                                    fgst = "1000";
                                    break;
                                case "2 Hz":
                                    txtGST.setText("GST: 2 Hz");
                                    fgst = "500";
                                    break;
                                case "5 Hz":
                                    txtGST.setText("GST: 5 Hz");
                                    fgst = "200";
                                    break;
                                case "10 Hz":
                                    txtGST.setText("GST: 10 Hz");
                                    fgst = "100";
                                    break;
                                case "20 Hz":
                                    txtGST.setText("GST: 20 Hz");
                                    fgst = "50";
                                    break;
                            }
                        }
                    }
                }
            });


        });
        txtRMC.setOnClickListener(view -> {

            List<String> menuItems = Arrays.asList(getResources().getStringArray(R.array.rate_options));
            CustomMenu customMenu = new CustomMenu(Activity_Gnss_Setup.this, "RMC");
            customMenu.show(menuItems, new CustomMenu.OnItemSelectedListener() {
                @Override
                public void onItemSelected(String selectedItem) {
                    if (isSc600 == 1 || isSc600 == 0) {
                        if (comSelected == 0 || comSelected == 1 || comSelected == 2 || comSelected == 4) {
                            switch (selectedItem) {
                                case "Off":
                                    txtRMC.setText("RMC: Off");
                                    frmc = "0";
                                    break;
                                case "10 Sec.":
                                    txtRMC.setText("RMC: 10 Sec");
                                    frmc = "10000";
                                    break;
                                case "5 Sec.":
                                    txtRMC.setText("RMC: 5 Sec");
                                    frmc = "5000";
                                    break;
                                case "2 Sec.":
                                    txtRMC.setText("RMC: 2 Sec");
                                    frmc = "2000";
                                    break;
                                case "1 Sec.":
                                    txtRMC.setText("RMC: 1 Sec");
                                    frmc = "1000";
                                    break;
                                case "2 Hz":
                                    txtRMC.setText("RMC: 2 Hz");
                                    frmc = "500";
                                    break;
                                case "5 Hz":
                                    txtRMC.setText("RMC: 5 Hz");
                                    frmc = "200";
                                    break;
                                case "10 Hz":
                                    txtRMC.setText("RMC: 10 Hz");
                                    frmc = "100";
                                    break;
                                case "20 Hz":
                                    txtRMC.setText("RMC: 20 Hz");
                                    frmc = "50";
                                    break;
                            }
                        }
                    }
                }
            });


        });
        txtGSV.setOnClickListener(view -> {

            List<String> menuItems = Arrays.asList(getResources().getStringArray(R.array.rate_options));
            CustomMenu customMenu = new CustomMenu(Activity_Gnss_Setup.this, "GSV");
            customMenu.show(menuItems, new CustomMenu.OnItemSelectedListener() {
                @Override
                public void onItemSelected(String selectedItem) {
                    if (isSc600 == 1 || isSc600 == 0) {
                        if (comSelected == 0 || comSelected == 1 || comSelected == 2 || comSelected == 4) {
                            switch (selectedItem) {
                                case "Off":
                                    txtGSV.setText("GSV: Off");
                                    fgsv = "0";
                                    break;
                                case "10 Sec.":
                                    txtGSV.setText("GSV: 10 Sec");
                                    fgsv = "10000";
                                    break;
                                case "5 Sec.":
                                    txtGSV.setText("GSV: 5 Sec");
                                    fgsv = "5000";
                                    break;
                                case "2 Sec.":
                                    txtGSV.setText("GSV: 2 Sec");
                                    fgsv = "2000";
                                    break;
                                case "1 Sec.":
                                    txtGSV.setText("GSV: 1 Sec");
                                    fgsv = "1000";
                                    break;
                                case "2 Hz":
                                    txtGSV.setText("GSV: 2 Hz");
                                    fgsv = "500";
                                    break;
                                case "5 Hz":
                                    txtGSV.setText("GSV: 5 Hz");
                                    fgsv = "200";
                                    break;
                                case "10 Hz":
                                    txtGSV.setText("GSV: 10 Hz");
                                    fgsv = "100";
                                    break;
                                case "20 Hz":
                                    txtGSV.setText("GSV: 20 Hz");
                                    fgsv = "50";
                                    break;
                            }
                        }
                    }
                }
            });


        });
        txtRadioenable.setOnClickListener(view -> {

            List<String> menuItems = Arrays.asList(getResources().getStringArray(R.array.uhf_options));
            CustomMenu customMenu = new CustomMenu(Activity_Gnss_Setup.this, "UHF");
            customMenu.show(menuItems, new CustomMenu.OnItemSelectedListener() {
                @Override
                public void onItemSelected(String selectedItem) {
                    if (isSc600 == 1 || isSc600 == 0) {
                        if (comSelected == 0 || comSelected == 1 || comSelected == 2 || comSelected == 4) {
                            switch (selectedItem) {
                                case "OFF":
                                    txtRadioenable.setText("UHF:\nOFF");
                                    radioEn = "NO";
                                    break;
                                case "ENABLE":
                                    txtRadioenable.setText("UHF:\nENABLED");
                                    radioEn = "YES";
                                    break;
                            }
                        }
                    }
                }
            });


        });
        txtRadioCh.setOnClickListener(view -> {

            List<String> menuItems = Arrays.asList(getResources().getStringArray(R.array.radio_ch));
            CustomMenu customMenu = new CustomMenu(Activity_Gnss_Setup.this, "RADIO CHANNEL");
            customMenu.show(menuItems, new CustomMenu.OnItemSelectedListener() {
                @Override
                public void onItemSelected(String selectedItem) {
                    if (isSc600 == 1 || isSc600 == 0) {
                        if (comSelected == 0 || comSelected == 1 || comSelected == 2 || comSelected == 4) {
                            switch (selectedItem) {
                                case "1":
                                    txtRadioCh.setText("Ch: 1");
                                    sCh = "1";
                                    break;
                                case "2":
                                    txtRadioCh.setText("Ch: 2");
                                    sCh = "2";
                                    break;
                                case "3":
                                    txtRadioCh.setText("Ch: 3");
                                    sCh = "3";
                                    break;
                                case "4":
                                    txtRadioCh.setText("Ch: 4");
                                    sCh = "4";
                                    break;
                                case "5":
                                    txtRadioCh.setText("Ch: 5");
                                    sCh = "5";
                                    break;
                                case "6":
                                    txtRadioCh.setText("Ch: 6");
                                    sCh = "6";
                                    break;
                                case "7":
                                    txtRadioCh.setText("Ch: 7");
                                    sCh = "7";
                                    break;
                                case "8":
                                    txtRadioCh.setText("Ch: 8");
                                    sCh = "8";
                                    break;
                                case "9":
                                    txtRadioCh.setText("Ch: 9");
                                    sCh = "9";
                                    break;
                                case "10":
                                    txtRadioCh.setText("Ch: 10");
                                    sCh = "10";
                                    break;
                                case "11":
                                    txtRadioCh.setText("Ch: 11");
                                    sCh = "11";
                                    break;
                                case "12":
                                    txtRadioCh.setText("Ch: 12");
                                    sCh = "12";
                                    break;
                                case "13":
                                    txtRadioCh.setText("Ch: 13");
                                    sCh = "13";
                                    break;
                                case "14":
                                    txtRadioCh.setText("Ch: 14");
                                    sCh = "14";
                                    break;
                                case "15":
                                    txtRadioCh.setText("Ch: 15");
                                    sCh = "15";
                                    break;
                                case "16":
                                    txtRadioCh.setText("Ch: 16");
                                    sCh = "16";
                                    break;
                            }
                        }
                    }
                }

            });


        });
        txtRadioProto.setOnClickListener(view -> {
            List<String> menuItems = Arrays.asList(getResources().getStringArray(R.array.radio_proto));
            CustomMenu customMenu = new CustomMenu(Activity_Gnss_Setup.this, "RADIO PROTOCOL");
            customMenu.show(menuItems, new CustomMenu.OnItemSelectedListener() {
                @Override
                public void onItemSelected(String selectedItem) {
                    {
                        if (comSelected == 0 || comSelected == 1 || comSelected == 2 || comSelected == 4) {
                            switch (selectedItem) {
                                case "Satel":
                                    txtRadioProto.setText("Protocol:\nSatel");
                                    sProto = "0";
                                    break;
                                case "PCC-4FSK":
                                    txtRadioProto.setText("Protocol:\nPCC-4FSK");
                                    sProto = "1";
                                    break;
                                case "PCC-GMSK":
                                    txtRadioProto.setText("Protocol:\nPCC-GMSK");
                                    sProto = "2";
                                    break;
                                case "TrimTalk 450S":
                                    txtRadioProto.setText("Protocol:\nTrimTalk 450S");
                                    sProto = "3";
                                    break;
                                case "South 9600":
                                    txtRadioProto.setText("Protocol:\nSouth 9600");
                                    sProto = "4";
                                    break;
                                case "HITARGET(9600)":
                                    txtRadioProto.setText("Protocol:\nHITARGET(9600)");
                                    sProto = "6";
                                    break;
                                case "HITARGET(19200)":
                                    txtRadioProto.setText("Protocol:\nHITARGET(19200)");
                                    sProto = "7";
                                    break;
                                case "TrimMark III":
                                    txtRadioProto.setText("Protocol:\nTrimMark III");
                                    sProto = "9";
                                    break;
                                case "South 19200":
                                    txtRadioProto.setText("Protocol:\nSouth 19200");
                                    sProto = "10";
                                    break;
                                case "TrimTalk(4800)":
                                    txtRadioProto.setText("Protocol:\nTrimTalk(4800)");
                                    sProto = "11";
                                    break;
                                case "GEOTALK":
                                    txtRadioProto.setText("Protocol:\nGEOTALK");
                                    sProto = "13";
                                    break;
                                case "GEOMARK":
                                    txtRadioProto.setText("Protocol:\nGEOMARK");
                                    sProto = "14";
                                    break;
                                case "900M Hopping":
                                    txtRadioProto.setText("Protocol:\n900M Hopping");
                                    sProto = "15";
                                    break;
                                case "HZSZ":
                                    txtRadioProto.setText("Protocol:\nHZSZ");
                                    sProto = "16";
                                    break;
                                case "GEO FHSS":
                                    txtRadioProto.setText("Protocol:\nGEO FHSS");
                                    sProto = "17";
                                    break;
                                case "Satel_ADL":
                                    txtRadioProto.setText("Protocol:\nSatel_ADL");
                                    sProto = "19";
                                    break;
                                case "PCCFST":
                                    txtRadioProto.setText("Protocol:\nPCCFST");
                                    sProto = "20";
                                    break;
                                case "PCCFST_ADL":
                                    txtRadioProto.setText("Protocol:\nPCCFST_ADL");
                                    sProto = "21";
                                    break;
                            }
                        }
                    }
                }
            });


        });
        txtRadioSpac.setOnClickListener(view -> {

            List<String> menuItems = Arrays.asList(getResources().getStringArray(R.array.radio_spacing));
            CustomMenu customMenu = new CustomMenu(Activity_Gnss_Setup.this, "SPACING");
            customMenu.show(menuItems, new CustomMenu.OnItemSelectedListener() {
                @Override
                public void onItemSelected(String selectedItem) {
                    if (isSc600 == 1) {
                        if (comSelected == 0 || comSelected == 1 || comSelected == 2 || comSelected == 4) {
                            switch (selectedItem) {
                                case "25":
                                    txtRadioSpac.setText("Ch Spacing:\n25");
                                    sSpacing = "25";
                                    break;
                                case "12.5":
                                    txtRadioSpac.setText("Ch Spacing:\n12.5");
                                    sSpacing = "12.5";
                                    break;
                                case "6.5":
                                    txtRadioSpac.setText("Ch Spacing:\n6.5");
                                    sSpacing = "6.5";
                                    break;
                            }
                        }
                    }
                }
            });


        });
        txtRtk.setOnClickListener(view -> {

            List<String> menuItems = Arrays.asList(getResources().getStringArray(R.array.radio_rtk));
            CustomMenu customMenu = new CustomMenu(Activity_Gnss_Setup.this, "RTK");
            customMenu.show(menuItems, new CustomMenu.OnItemSelectedListener() {
                @Override
                public void onItemSelected(String selectedItem) {
                    if (comSelected == 0 || comSelected == 1 || comSelected == 2 || comSelected == 4) {
                        switch (selectedItem) {
                            case "Off":
                                txtRtk.setText("Rtk:\nOff");
                                sRtk = "Off";
                                break;
                            case "RTCM2.3":
                                txtRtk.setText("Rtk:\nRTCM2.3");
                                sRtk = "RTCM2";
                                break;
                            case "RTCM3.0":
                                txtRtk.setText("Rtk:\nRTCM3.0");
                                sRtk = "RTCM3";
                                break;
                            case "RTCM3.2":
                                txtRtk.setText("Rtk:\nRTCM3.2");
                                sRtk = "RTCM32";
                                break;
                        }
                    }
                }
            });

        });


    }

    private void updateUI() {


        mHandler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mRunning) {
                    //do something

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (progressBar.getVisibility() == View.VISIBLE) {

                                txtGGA.setEnabled(false);
                                txtGGAH.setEnabled(false);
                                txtHDT.setEnabled(false);
                                txtGST.setEnabled(false);
                                txtRMC.setEnabled(false);
                                txtGSV.setEnabled(false);
                                txtRadioenable.setEnabled(false);
                                txtRadioCh.setEnabled(false);
                                txtRadioProto.setEnabled(false);
                                txtRadioSpac.setEnabled(false);
                                txtRadioFunction.setEnabled(false);
                                txtRtk.setEnabled(false);
                                countVisibility++;
                            } else {
                                countVisibility = 0;


                                txtGGA.setEnabled(true);
                                txtGGAH.setEnabled(true);
                                txtHDT.setEnabled(true);
                                txtGST.setEnabled(true);
                                txtRMC.setEnabled(true);
                                txtGSV.setEnabled(true);
                                txtRadioenable.setEnabled(true);
                                txtRadioCh.setEnabled(true);
                                txtRadioProto.setEnabled(true);
                                txtRadioSpac.setEnabled(true);
                                txtRadioFunction.setEnabled(true);
                                txtRtk.setEnabled(true);
                            }
                            if (countVisibility == 50) {
                                progressBar.setVisibility(View.INVISIBLE);
                                countVisibility = 0;
                            }
                            if (!play) {
                                debugtitle.setTextColor(getColor(R.color.white));
                                debugtitle.setText("DEBUG STOPPED");
                                debugtitle.setBackgroundColor(getColor(R.color._____cancel_text));
                            } else {
                                debugtitle.setTextColor(getColor(R.color._____cancel_text));
                                debugtitle.setText("DEBUG RUNNING...");
                                debugtitle.setBackgroundColor(getColor(R.color.colorStonexBlue));
                            }
                            macaddress.setText(DataSaved.S_macAddres + "\t\t\t" + DataSaved.S_gpsname);


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

    public void readAll() {
        if (comSelected == 4) {
            final Handler handler = new Handler(Looper.getMainLooper());
            for (int i = 0; i < 20; i++) {
                final int finalI = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ///
                        if (isSc600 == 1) {
                            switch (finalI) {
                                case 0:
                                    progressBar.setVisibility(View.VISIBLE);
                                    Bluetooth_GNSS_Service.sendGNSSata("set,PORTS.COM3.FUNCTION,CMD\r\n");
                                    break;
                                case 1:
                                    Bluetooth_GNSS_Service.sendGNSSata("set,PORTS.BLUETOOTH.FUNCTION,NMEA\r\n");
                                    break;
                                case 2:
                                    Bluetooth_GNSS_Service.sendGNSSata("get,ports.bluetooth.nmea,gga|ggah|hdt|gst|gsv\r\n");
                                    break;
                                case 3:
                                    Bluetooth_GNSS_Service.sendGNSSata("get,ports.radio.enable\r\n");
                                    break;
                                case 4:
                                    Bluetooth_GNSS_Service.sendGNSSata("get,ports.radio.function\r\n");
                                    break;
                                case 5:
                                    Bluetooth_GNSS_Service.sendGNSSata("get,ports.radio.rtk\r\n");
                                    break;
                                case 6:
                                    Bluetooth_GNSS_Service.sendGNSSata("get,radio.mode\r\n");
                                    break;
                                case 7:
                                    Bluetooth_GNSS_Service.sendGNSSata("get,radio.channel\r\n");
                                    break;
                                case 8:
                                    Bluetooth_GNSS_Service.sendGNSSata("get,radio.channel_spacing\r\n");
                                    break;
                                case 9:

                                    Bluetooth_GNSS_Service.sendGNSSata("set,radio.reset\r\n");
                                    Bluetooth_GNSS_Service.sendGNSSata("set,ports.reset,2\r\n");
                                    Bluetooth_GNSS_Service.sendGNSSata("set,ports.reset,1\r\n");
                                    Bluetooth_GNSS_Service.sendGNSSata("set,ports.reset,16\r\n");

                                    break;

                                case 10:
                                    //wait
                                    break;
                                case 11:
                                    //wait
                                    break;
                                case 12:
                                    enableWrite = true;
                                    progressBar.setVisibility(View.INVISIBLE);
                                    break;

                            }
                        } else {
                            switch (finalI) {
                                case 0:
                                    progressBar.setVisibility(View.VISIBLE);
                                    //Bluetooth_GNSS_Service.sendGNSSata("set,PORTS.COM3.FUNCTION,CMD\r\n");
                                    break;
                                case 1:
                                    //Bluetooth_GNSS_Service.sendGNSSata("set,PORTS.BLUETOOTH.FUNCTION,NMEA\r\n");
                                    break;
                                case 2:
                                    Bluetooth_GNSS_Service.sendGNSSata("get,device.loglist,gga|ggah|hdt|gst|gsv\r\n");
                                    break;
                                case 3:
                                    Bluetooth_GNSS_Service.sendGNSSata("get,radio.enable\r\n");
                                    break;
                                case 4:
                                    Bluetooth_GNSS_Service.sendGNSSata("get,radio.function\r\n");
                                    break;
                                case 5:
                                    Bluetooth_GNSS_Service.sendGNSSata("get,radio.rtk\r\n");
                                    break;
                                case 6:
                                    Bluetooth_GNSS_Service.sendGNSSata("get,radio.mode\r\n");
                                    break;
                                case 7:
                                    Bluetooth_GNSS_Service.sendGNSSata("get,radio.channel\r\n");
                                    break;
                                case 8:
                                    Bluetooth_GNSS_Service.sendGNSSata("get,radio.channel_spacing\r\n");
                                    break;
                                case 9:

                                    Bluetooth_GNSS_Service.sendGNSSata("set,radio.reset\r\n");
                                    Bluetooth_GNSS_Service.sendGNSSata("set,ports.reset,2\r\n");
                                    Bluetooth_GNSS_Service.sendGNSSata("set,ports.reset,1\r\n");
                                    Bluetooth_GNSS_Service.sendGNSSata("set,ports.reset,16\r\n");

                                    break;

                                case 10:
                                    //wait
                                    break;
                                case 11:
                                    //wait
                                    break;
                                case 12:
                                    enableWrite = true;
                                    progressBar.setVisibility(View.INVISIBLE);
                                    break;

                            }
                        }
                        ///

                    }
                }, 250 * i); // Ritardo di 250 millisecondi fra un msg e l'altro
            }
        }
    }


    private void writeAll() {
        if (comSelected == 4 && enableWrite) {
            final Handler handler = new Handler(Looper.getMainLooper());
            for (int i = 0; i < 10; i++) {
                final int finalI = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ////
                        if (isSc600 == 1) {
                            switch (finalI) {
                                case 0:
                                    progressBar.setVisibility(View.VISIBLE);
                                    Bluetooth_GNSS_Service.sendGNSSata("set,ports.bluetooth.nmea,GGA:" + fgga + "|GSA:0|GSV:" + fgsv + "|ZDA:0|RMC:" + frmc + "|VTG:0|GST:" + fgst + "|GLL:0|HDT:" + fhdt + "|HPR:0|GGA2:" + fggah + "|GRS:0|\r\n");
                                    Bluetooth_GNSS_Service.sendGNSSata("set,ports.radio.enable,YES\r\n");
                                    Bluetooth_GNSS_Service.sendGNSSata("set,ports.radio.function,RTK_IN\r\n");

                                    break;
                                case 1:
                                    Bluetooth_GNSS_Service.sendGNSSata("set,ports.radio.enable,YES\r\n");
                                    Bluetooth_GNSS_Service.sendGNSSata("set,radio.channel_spacing," + sSpacing + "\r\n");
                                    Bluetooth_GNSS_Service.sendGNSSata("set,radio.fec,off\r\n");
                                    Bluetooth_GNSS_Service.sendGNSSata("set,radio.power,low\r\n");
                                    Bluetooth_GNSS_Service.sendGNSSata("set,radio.mode," + sProto + "\r\n");
                                    Bluetooth_GNSS_Service.sendGNSSata("set,radio.channel," + sCh + "\r\n");
                                    Bluetooth_GNSS_Service.sendGNSSata("set,ports.radio.rtk," + sRtk + "\r\n");
                                    Bluetooth_GNSS_Service.sendGNSSata("set,radio.reset\r\n");
                                    Bluetooth_GNSS_Service.sendGNSSata("set,ports.reset,2\r\n");
                                    Bluetooth_GNSS_Service.sendGNSSata("set,ports.reset,1\r\n");
                                    Bluetooth_GNSS_Service.sendGNSSata("set,ports.reset,16\r\n");


                                    break;
                                case 2:

                                    break;
                                case 3:

                                    break;
                                case 4:

                                    break;
                                case 5:


                                    break;
                                case 9:
                                    progressBar.setVisibility(View.INVISIBLE);
                                    break;
                            }
                        } else {
                            switch (finalI) {
                                case 0:
                                    progressBar.setVisibility(View.VISIBLE);
                                    Bluetooth_GNSS_Service.sendGNSSata("set,device.loglist,GGA:" + fgga + "|GSA:0|GSV:" + fgsv + "|ZDA:0|RMC:" + frmc + "|VTG:0|GST:" + fgst + "|GLL:0|HDT:" + fhdt + "|HPR:0|GGA2:" + fggah + "|GRS:0|\r\n");
                                    //Bluetooth_GNSS_Service.sendGNSSata("set,ports.radio.enable,YES\r\n");
                                    //Bluetooth_GNSS_Service.sendGNSSata("set,ports.radio.function,RTK_IN\r\n");

                                    break;
                                case 1:
                                    // Bluetooth_GNSS_Service.sendGNSSata("set,ports.radio.enable,YES\r\n");
                                    Bluetooth_GNSS_Service.sendGNSSata("set,radio.channel_spacing," + sSpacing + "\r\n");
                                    Bluetooth_GNSS_Service.sendGNSSata("set,radio.fec,off\r\n");
                                    Bluetooth_GNSS_Service.sendGNSSata("set,radio.power,low\r\n");
                                    Bluetooth_GNSS_Service.sendGNSSata("set,radio.mode," + sProto + "\r\n");
                                    Bluetooth_GNSS_Service.sendGNSSata("set,radio.channel," + sCh + "\r\n");
                                    //Bluetooth_GNSS_Service.sendGNSSata("set,ports.radio.rtk," + sRtk + "\r\n");
                                    Bluetooth_GNSS_Service.sendGNSSata("set,radio.reset\r\n");
                                    Bluetooth_GNSS_Service.sendGNSSata("set,ports.reset,2\r\n");
                                    Bluetooth_GNSS_Service.sendGNSSata("set,ports.reset,1\r\n");
                                    Bluetooth_GNSS_Service.sendGNSSata("set,ports.reset,16\r\n");


                                    break;
                                case 2:

                                    break;
                                case 3:

                                    break;
                                case 4:

                                    break;
                                case 5:


                                    break;
                                case 9:
                                    progressBar.setVisibility(View.INVISIBLE);
                                    break;
                            }
                        }
                        ////

                    }
                }, 250 * i); // Ritardo di 200 millisecondi fra un msg e l'altro
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void serialEvent(SerialEvent serialEvent) {
        if (play) {
            dataList.add(serialEvent.nmeaData);
            adapter.notifyDataSetChanged();
            listView.smoothScrollToPosition(dataList.size() - 1);
        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void cmdEvent(CMD_Event cmdEvent) {
        Log.d("EVENTO_CMD", String.valueOf(cmdEvent));
        if (comSelected == 0 || comSelected == 1 || comSelected == 2 || comSelected == 4) {
            //bt connection to SC600
            cmdStatust = cmdEvent.cmdIn;
            String[] substrings = cmdStatust.split("\\|");
            try {
                ///
                if (isSc600 == 1 || isSc600 == 0) {
                    for (String substring : substrings) {
                        if (substring.contains("OK,GGA:")) {
                            fgga = substring.substring(substring.indexOf(":") + 1);
                            switch (fgga) {
                                case "0":
                                    txtGGA.setText("GGA: Off");
                                    break;
                                case "60000":
                                    txtGGA.setText("GGA: 60 Sec");
                                    break;
                                case "30000":
                                    txtGGA.setText("GGA: 30 Sec");
                                    break;
                                case "15000":
                                    txtGGA.setText("GGA: 15 Sec");
                                    break;
                                case "10000":
                                    txtGGA.setText("GGA: 10 Sec");
                                    break;
                                case "5000":
                                    txtGGA.setText("GGA: 5 Sec");
                                    break;
                                case "2000":
                                    txtGGA.setText("GGA: 2 Sec");
                                    break;
                                case "1000":
                                    txtGGA.setText("GGA: 1 Sec");
                                    break;
                                case "500":
                                    txtGGA.setText("GGA: 2 Hz");
                                    break;
                                case "200":
                                    txtGGA.setText("GGA: 5 Hz");
                                    break;
                                case "100":
                                    txtGGA.setText("GGA: 10 Hz");
                                    break;
                                case "50":
                                    txtGGA.setText("GGA: 20 Hz");
                                    break;
                                default:
                                    txtGGA.setText("GGA: " + fgga);
                                    break;
                            }
                        } else if (substring.startsWith("HDT:")) {
                            fhdt = substring.substring(substring.indexOf(":") + 1);
                            switch (fhdt) {
                                case "0":
                                    txtHDT.setText("HDT: Off");
                                    break;
                                case "60000":
                                    txtHDT.setText("HDT: 60 Sec");
                                    break;
                                case "30000":
                                    txtHDT.setText("HDT: 30 Sec");
                                    break;
                                case "15000":
                                    txtHDT.setText("HDT: 15 Sec");
                                    break;
                                case "10000":
                                    txtHDT.setText("HDT: 10 Sec");
                                    break;
                                case "5000":
                                    txtHDT.setText("HDT: 5 Sec");
                                    break;
                                case "2000":
                                    txtHDT.setText("HDT: 2 Sec");
                                    break;
                                case "1000":
                                    txtHDT.setText("HDT: 1 Sec");
                                    break;
                                case "500":
                                    txtHDT.setText("HDT: 2 Hz");
                                    break;
                                case "200":
                                    txtHDT.setText("HDT: 5 Hz");
                                    break;
                                case "100":
                                    txtHDT.setText("HDT: 10 Hz");
                                    break;
                                case "50":
                                    txtHDT.setText("HDT: 20 Hz");
                                    break;
                                default:
                                    txtHDT.setText("HDT: " + fhdt);
                                    break;
                            }
                        } else if (substring.startsWith("GGA2:")) {
                            fggah = substring.substring(substring.indexOf(":") + 1);
                            switch (fggah) {
                                case "0":
                                    txtGGAH.setText("GGAH: Off");
                                    break;
                                case "60000":
                                    txtGGAH.setText("GGAH: 60 Sec");
                                    break;
                                case "30000":
                                    txtGGAH.setText("GGAH: 30 Sec");
                                    break;
                                case "15000":
                                    txtGGAH.setText("GGAH: 15 Sec");
                                    break;
                                case "10000":
                                    txtGGAH.setText("GGAH: 10 Sec");
                                    break;
                                case "5000":
                                    txtGGAH.setText("GGAH: 5 Sec");
                                    break;
                                case "2000":
                                    txtGGAH.setText("GGAH: 2 Sec");
                                    break;
                                case "1000":
                                    txtGGAH.setText("GGAH: 1 Sec");
                                    break;
                                case "500":
                                    txtGGAH.setText("GGAH: 2 Hz");
                                    break;
                                case "200":
                                    txtGGAH.setText("GGAH: 5 Hz");
                                    break;
                                case "100":
                                    txtGGAH.setText("GGAH: 10 Hz");
                                    break;
                                case "50":
                                    txtGGAH.setText("GGAH: 20 Hz");
                                    break;
                                default:
                                    txtGGAH.setText("GGAH: " + fggah);
                                    break;
                            }
                        } else if (substring.startsWith("GST:")) {
                            fgst = substring.substring(substring.indexOf(":") + 1);
                            switch (fgst) {
                                case "0":
                                    txtGST.setText("GST: Off");
                                    break;
                                case "60000":
                                    txtGST.setText("GST: 60 Sec");
                                    break;
                                case "30000":
                                    txtGST.setText("GST: 30 Sec");
                                    break;
                                case "15000":
                                    txtGST.setText("GST: 15 Sec");
                                    break;
                                case "10000":
                                    txtGST.setText("GST: 10 Sec");
                                    break;
                                case "5000":
                                    txtGST.setText("GST: 5 Sec");
                                    break;
                                case "2000":
                                    txtGST.setText("GST: 2 Sec");
                                    break;
                                case "1000":
                                    txtGST.setText("GST: 1 Sec");
                                    break;
                                case "500":
                                    txtGST.setText("GST: 2 Hz");
                                    break;
                                case "200":
                                    txtGST.setText("GST: 5 Hz");
                                    break;
                                case "100":
                                    txtGST.setText("GST: 10 Hz");
                                    break;
                                case "50":
                                    txtGST.setText("GST: 20 Hz");
                                    break;
                                default:
                                    txtGST.setText("GST: " + fgst);
                                    break;
                            }
                        } else if (substring.startsWith("RMC:")) {
                            frmc = substring.substring(substring.indexOf(":") + 1);
                            switch (frmc) {
                                case "0":
                                    txtRMC.setText("RMC: Off");
                                    break;
                                case "60000":
                                    txtRMC.setText("RMC: 60 Sec");
                                    break;
                                case "30000":
                                    txtRMC.setText("RMC: 30 Sec");
                                    break;
                                case "15000":
                                    txtRMC.setText("RMC: 15 Sec");
                                    break;
                                case "10000":
                                    txtRMC.setText("RMC: 10 Sec");
                                    break;
                                case "5000":
                                    txtRMC.setText("RMC: 5 Sec");
                                    break;
                                case "2000":
                                    txtRMC.setText("RMC: 2 Sec");
                                    break;
                                case "1000":
                                    txtRMC.setText("RMC: 1 Sec");
                                    break;
                                case "500":
                                    txtRMC.setText("RMC: 2 Hz");
                                    break;
                                case "200":
                                    txtRMC.setText("RMC: 5 Hz");
                                    break;
                                case "100":
                                    txtRMC.setText("RMC: 10 Hz");
                                    break;
                                case "50":
                                    txtRMC.setText("RMC: 20 Hz");
                                    break;
                                default:
                                    txtRMC.setText("RMC: " + frmc);
                                    break;
                            }
                        } else if (substring.startsWith("GSV:")) {
                            fgsv = substring.substring(substring.indexOf(":") + 1);
                            switch (fgsv) {
                                case "0":
                                    txtGSV.setText("GSV: Off");
                                    break;
                                case "60000":
                                    txtGSV.setText("GSV: 60 Sec");
                                    break;
                                case "30000":
                                    txtGSV.setText("GSV: 30 Sec");
                                    break;
                                case "15000":
                                    txtGSV.setText("GSV: 15 Sec");
                                    break;
                                case "10000":
                                    txtGSV.setText("GSV: 10 Sec");
                                    break;
                                case "5000":
                                    txtGSV.setText("GSV: 5 Sec");
                                    break;
                                case "2000":
                                    txtGSV.setText("GSV: 2 Sec");
                                    break;
                                case "1000":
                                    txtGSV.setText("GSV: 1 Sec");
                                    break;
                                case "500":
                                    txtGSV.setText("GSV: 2 Hz");
                                    break;
                                case "200":
                                    txtGSV.setText("GSV: 5 Hz");
                                    break;
                                case "100":
                                    txtGSV.setText("GSV: 10 Hz");
                                    break;
                                case "50":
                                    txtGSV.setText("GSV: 20 Hz");
                                    break;
                                default:
                                    txtGSV.setText("GSV: " + fgsv);
                                    break;
                            }
                        }

                    }
                }
                ///
            } catch (Exception e) {

            }
            try {
                if (isSc600 == 1 || isSc600 == 0) {
                    String[] substringsR = cmdStatust.split(",");

                    if (substringsR[2].equals("ports.radio.enable")) {
                        radioEn = substringsR[4].substring(0, substringsR[4].indexOf("*"));
                        switch (radioEn) {
                            case "NO":
                                txtRadioenable.setText("UHF:\nOFF");
                                break;
                            case "YES":
                                txtRadioenable.setText("UHF:\nENABLED");
                                break;
                            case "OK":
                                txtRadioenable.setText("UHF:\nENABLED");
                                break;
                        }
                    } else if (substringsR[2].equals("radio.channel") && substringsR[3].equals("OK")) {
                        sCh = substringsR[4].substring(0, substringsR[4].indexOf("*"));
                        txtRadioCh.setText("CH: " + sCh);
                    } else if (substringsR[2].equals("radio.mode") && substringsR[3].equals("OK")) {
                        sProto = substringsR[4].substring(0, substringsR[4].indexOf("*"));
                        switch (sProto) {
                            case "0":
                                txtRadioProto.setText("Protocol:\nSatel");
                                break;
                            case "1":
                                txtRadioProto.setText("Protocol:\nPCC-4FSK");
                                break;
                            case "2":
                                txtRadioProto.setText("Protocol:\nPCC-GMSK");
                                break;
                            case "3":
                                txtRadioProto.setText("Protocol:\nTrimTalk 450S");
                                break;
                            case "4":
                                txtRadioProto.setText("Protocol:\nSouth 9600");
                                break;
                            case "6":
                                txtRadioProto.setText("Protocol:\nHITARGET(9600)");
                                break;
                            case "7":
                                txtRadioProto.setText("Protocol:\nHITARGET(19200)");
                                break;
                            case "9":
                                txtRadioProto.setText("Protocol:\nTrimMark III");
                                break;
                            case "10":
                                txtRadioProto.setText("Protocol:\nSouth 19200");
                                break;
                            case "11":
                                txtRadioProto.setText("Protocol:\nTrimTalk(4800)");
                                break;
                            case "13":
                                txtRadioProto.setText("Protocol:\nGEOTALK");
                                break;
                            case "14":
                                txtRadioProto.setText("Protocol:\nGEOMARK");
                                break;
                            case "15":
                                txtRadioProto.setText("Protocol:\n900M Hopping");
                                break;
                            case "16":
                                txtRadioProto.setText("Protocol:\nHZSZ");
                                break;
                            case "17":
                                txtRadioProto.setText("Protocol:\nGEO FHSS");
                                break;
                            case "19":
                                txtRadioProto.setText("Protocol:\nSatel_ADL");
                                break;
                            case "20":
                                txtRadioProto.setText("Protocol:\nPCCFST");
                                break;
                            case "21":
                                txtRadioProto.setText("Protocol:\nPCCFST_ADL");
                                break;
                            default:
                                txtRadioProto.setText("Protocol:\n");
                                break;
                        }

                    } else if (substringsR[2].equals("radio.channel_spacing") && substringsR[3].equals("OK")) {
                        sSpacing = substringsR[4].substring(0, substringsR[4].indexOf("*"));
                        txtRadioSpac.setText("Ch Spacing:\n" + sSpacing);
                    } else if (substringsR[2].equals("ports.radio.function") && substringsR[3].equals("OK")) {//verificare come compare la stringa
                        sRadioF = substringsR[4].substring(0, substringsR[4].indexOf("*"));
                        txtRadioFunction.setText("Function:\n" + sRadioF);
                    } else if (substringsR[2].equals("ports.radio.rtk") && substringsR[3].equals("OK")) {//verificare come compare la stringa
                        sRtk = substringsR[4].substring(0, substringsR[4].indexOf("*"));
                        txtRtk.setText("Rtk:\n" + sRtk);
                    }

                }
            } catch (Exception e) {

            }


        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        enableWrite = false;
        mRunning = false;

    }
}