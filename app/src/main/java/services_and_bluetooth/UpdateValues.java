package services_and_bluetooth;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;

import android.util.Log;

import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;
import org.locationtech.proj4j.InvalidValueException;
import org.locationtech.proj4j.ProjCoordinate;
import org.locationtech.proj4j.UnknownAuthorityCodeException;
import org.locationtech.proj4j.UnsupportedParameterException;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import activity_portrait.LaunchScreenActivity;
import activity_portrait.MainActivity;
import utils.MyRW_IntMem;

public class UpdateValues extends Service {
    private Executor mExecutor;
    private static final int THREAD_POOL_SIZE = 4;
    MyRW_IntMem myRW_intMem;
    CRSFactory crsFactory = new CRSFactory();
    CoordinateReferenceSystem WGS84;
    CoordinateReferenceSystem UTM;
    CoordinateTransformFactory ctFactory;
    public static CoordinateTransform wgsToUtm;
    public static ProjCoordinate result;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        mExecutor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        myRW_intMem = new MyRW_IntMem();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mExecutor.execute(new MyAsync_Excecutor());

        return START_STICKY;

    }
    private class MyAsync_Excecutor implements Runnable {

        @Override
        public void run() {
            String deviceType=myRW_intMem.MyRead("_deviceType", UpdateValues.this);
            String macaddress = myRW_intMem.MyRead("_macaddress", UpdateValues.this);
            String gpsname = myRW_intMem.MyRead("_gpsname", UpdateValues.this);
            String macaddresscan = myRW_intMem.MyRead("_macaddress_can", UpdateValues.this);
            String canname = myRW_intMem.MyRead("_canname", UpdateValues.this);
            //String crs = myRW_intMem.MyRead("_crs", UpdateValues.this);
            String altezzaAnt=myRW_intMem.MyRead("_altezzaantenna",UpdateValues.this);
            String offsetA=myRW_intMem.MyRead("_offset",UpdateValues.this);
            String leftEdge=myRW_intMem.MyRead("_leftedge",UpdateValues.this);
            String rightEdge=myRW_intMem.MyRead("_rightedge",UpdateValues.this);
            String unitOfMeasure = myRW_intMem.MyRead("_unitofmeasure", UpdateValues.this);
            String points=myRW_intMem.MyRead("pointssaved", UpdateValues.this);
            String boomresult=myRW_intMem.MyRead("boomresult", UpdateValues.this);
            String rmcSize=myRW_intMem.MyRead("rmcSize", UpdateValues.this);
            String useRmc=myRW_intMem.MyRead("useRmc", UpdateValues.this);
            String zoomF=myRW_intMem.MyRead("zoomF", UpdateValues.this);
            String rot=myRW_intMem.MyRead("rot", UpdateValues.this);
            String ztol=myRW_intMem.MyRead("z_tol", UpdateValues.this);
            String xytol=myRW_intMem.MyRead("xy_tol", UpdateValues.this);
            String tilt_tol=myRW_intMem.MyRead("tilt_tol", UpdateValues.this);
            String hdt_tol=myRW_intMem.MyRead("hdt_tol", UpdateValues.this);
            String maprotmode=myRW_intMem.MyRead("_maprotmode",UpdateValues.this);
            String offsetPitch=myRW_intMem.MyRead("_offsetpitch",UpdateValues.this);
            String offsetRoll=myRW_intMem.MyRead("_offsetpitch",UpdateValues.this);
            String useTilt=myRW_intMem.MyRead("_usetilt",UpdateValues.this);
            String projectName=myRW_intMem.MyRead("projectName", UpdateValues.this);
            String imgMode=myRW_intMem.MyRead("imgMode", UpdateValues.this);
            String display=myRW_intMem.MyRead("display", UpdateValues.this);
            String pselect=myRW_intMem.MyRead("_pointselected", UpdateValues.this);
            String usedemo=myRW_intMem.MyRead("_usedemo", UpdateValues.this);
            if(deviceType==null){
                myRW_intMem.MyWrite("_deviceType","RUGSTORM",UpdateValues.this);
            }

            if(macaddress==null){
                myRW_intMem.MyWrite("_macaddress","00:00:00:00:00:00",UpdateValues.this);
            }
            if(gpsname==null){
                myRW_intMem.MyWrite("_gpsname","UNKNOWN",UpdateValues.this);
            }
            if(macaddresscan==null){
                myRW_intMem.MyWrite("_macaddress_can","00:00:00:00:00:00",UpdateValues.this);
            }
            if(canname==null){
                myRW_intMem.MyWrite("_canname","UNKNOWN",UpdateValues.this);
            }

           /* if(crs==null){
                myRW_intMem.MyWrite("_crs","3004",UpdateValues.this);
            }*/
            if(altezzaAnt==null){
                myRW_intMem.MyWrite("_altezzaantenna","2",UpdateValues.this);
            }
            if(offsetA==null){
                myRW_intMem.MyWrite("_offset","0",UpdateValues.this);
            }
            if(leftEdge==null){
                myRW_intMem.MyWrite("_leftedge","1.5",UpdateValues.this);
            }
            if(rightEdge==null){
                myRW_intMem.MyWrite("_rightedge","1.5",UpdateValues.this);
            }
            if (unitOfMeasure == null) {
                myRW_intMem.MyWrite("_unitofmeasure", "0", UpdateValues.this);
            }
            if (points == null) {
                myRW_intMem.MyWrite("pointssaved", "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0", UpdateValues.this);
            }
            if(boomresult==null){
                myRW_intMem.MyWrite("boomresult", "0,0,0", UpdateValues.this);
            }
            if(rmcSize==null){
                myRW_intMem.MyWrite("rmcSize", "20", UpdateValues.this);
            }
            if(useRmc==null){
                myRW_intMem.MyWrite("useRmc", "0", UpdateValues.this);
            }
            if(zoomF==null){
                myRW_intMem.MyWrite("zoomF", "0.5", UpdateValues.this);
            }
            if(rot==null){
                myRW_intMem.MyWrite("rot", "0", UpdateValues.this);
            }
            if(ztol==null){
                myRW_intMem.MyWrite("z_tol", "0.03", UpdateValues.this);
            }
            if(tilt_tol==null){
                myRW_intMem.MyWrite("tilt_tol", "0.5", UpdateValues.this);
            }
            if(hdt_tol==null){
                myRW_intMem.MyWrite("hdt_tol", "1.0", UpdateValues.this);
            }
            if(xytol==null){
                myRW_intMem.MyWrite("xy_tol", "0.03", UpdateValues.this);
            }
            if(maprotmode==null){
                myRW_intMem.MyWrite("_maprotmode", "0", UpdateValues.this);
            }
            if(offsetPitch==null){
                myRW_intMem.MyWrite("_offsetpitch", "0", UpdateValues.this);
            }
            if(offsetRoll==null){
                myRW_intMem.MyWrite("_offsetroll", "0", UpdateValues.this);
            }
            if(useTilt==null){
                myRW_intMem.MyWrite("_usetilt", "0", UpdateValues.this);
            }
            if(projectName==null){
                myRW_intMem.MyWrite("progectName","test",UpdateValues.this);
            }
            if(imgMode==null){
                myRW_intMem.MyWrite("imgMode","0",UpdateValues.this);
            }
            if(display==null){
                myRW_intMem.MyWrite("display","0",UpdateValues.this);
            }
            if(pselect==null){
                myRW_intMem.MyWrite("_pointselected","AB Line",UpdateValues.this);
            }
            if(usedemo==null){
                myRW_intMem.MyWrite("_usedemo","0",UpdateValues.this);
            }


            DataSaved.S_macAddres=myRW_intMem.MyRead("_macaddress",UpdateValues.this);

            DataSaved.S_gpsname=myRW_intMem.MyRead("_gpsname",UpdateValues.this);
            DataSaved.S_macAddress_CAN=myRW_intMem.MyRead("_macaddress_can",UpdateValues.this);

            DataSaved.S_can_name=myRW_intMem.MyRead("_canname",UpdateValues.this);
            DataSaved.D_AltezzaAnt=Double.parseDouble(myRW_intMem.MyRead("_altezzaantenna",UpdateValues.this).replace(",","."));
            DataSaved.D_Offset=Double.parseDouble(myRW_intMem.MyRead("_offset",UpdateValues.this).replace(",","."));
            DataSaved.D_Leftedge=Double.parseDouble(myRW_intMem.MyRead("_leftedge",UpdateValues.this).replace(",","."));
            DataSaved.D_Rightedge=Double.parseDouble(myRW_intMem.MyRead("_rightedge",UpdateValues.this).replace(",","."));
            DataSaved.I_UnitOfMeasure=Integer.parseInt(myRW_intMem.MyRead("_unitofmeasure",UpdateValues.this));
            DataSaved.rmcSize=Integer.parseInt(myRW_intMem.MyRead("rmcSize",UpdateValues.this));
            DataSaved.useRmc=Integer.parseInt(myRW_intMem.MyRead("useRmc",UpdateValues.this));
            DataSaved.xy_tol=Double.parseDouble(myRW_intMem.MyRead("xy_tol",UpdateValues.this).replace(",","."));
            DataSaved.z_tol=Double.parseDouble(myRW_intMem.MyRead("z_tol",UpdateValues.this).replace(",","."));
            DataSaved.tilt_Tol=Double.parseDouble(myRW_intMem.MyRead("tilt_tol",UpdateValues.this).replace(",","."));
            DataSaved.hdt_Tol=Double.parseDouble(myRW_intMem.MyRead("hdt_tol",UpdateValues.this).replace(",","."));

            DataSaved.MapRotMode=myRW_intMem.MyRead("_maprotmode",UpdateValues.this);
            DataSaved.offsetPitch=Double.parseDouble(myRW_intMem.MyRead("_offsetpitch",UpdateValues.this).replace(",","."));
            DataSaved.offsetRoll=Double.parseDouble(myRW_intMem.MyRead("_offsetroll",UpdateValues.this).replace(",","."));
            DataSaved.useTilt=Integer.parseInt(myRW_intMem.MyRead("_usetilt",UpdateValues.this));
            DataSaved.S_projectName=myRW_intMem.MyRead("projectName",UpdateValues.this);
            DataSaved.imgMode=Integer.parseInt(myRW_intMem.MyRead("imgMode",UpdateValues.this));
            DataSaved.DisplayOrient=Integer.parseInt(myRW_intMem.MyRead("display",UpdateValues.this));
            DataSaved.useDemo=Integer.parseInt(myRW_intMem.MyRead("_usedemo",UpdateValues.this));


          /*  try {
                WGS84 = crsFactory.createFromName("epsg:" +"4326");
                try {
                    UTM = crsFactory.createFromName("epsg:" +  DataSaved.S_CRS);
                    Log.d("INIZIALIZZA CRS",DataSaved.S_CRS);
                } catch (UnsupportedParameterException e) {
                    throw new RuntimeException(e);
                } catch (InvalidValueException e) {
                    Log.d("SDF","UTM.getName()");
                } catch (UnknownAuthorityCodeException e) {
                    Log.d("SDF","UTM.getName()");
                }
                ctFactory = new CoordinateTransformFactory();
                wgsToUtm = ctFactory.createTransform(WGS84, UTM);
                result = new ProjCoordinate();

                Log.d("INIZIALIZZA CRS", Arrays.toString(UTM.getParameters()));

            } catch (Exception e) {

            }*/


            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        saveBTdevices( new File(Environment.getExternalStorageDirectory().getAbsoluteFile().getPath(), "Stx Field/Devices").toString());
        ((ExecutorService) mExecutor).shutdown();

    }
    private void saveBTdevices(String path){


        File devicesDirectory = new File(path);


        if (!devicesDirectory.exists()) {
            devicesDirectory.mkdirs();
        }

        // Creare un oggetto File per il file .txt nella cartella
        File txtFile = new File(devicesDirectory, "bt_devices.txt");

        try {
            // Creare un oggetto FileWriter per scrivere nel file .txt
            FileWriter writer = new FileWriter(txtFile);

            // Scrivere le due stringhe nel file (puoi sostituire con le tue stringhe)
            writer.write("GPS MACADDRESS: "+new MyRW_IntMem().MyRead("_macaddress", UpdateValues.this)+"\n");
            writer.write("GPS NAME      : "+new MyRW_IntMem().MyRead("_gpsname",UpdateValues.this)+"\n");
            writer.write("CAN MACADDRESS: "+new MyRW_IntMem().MyRead("_macaddress_can",UpdateValues.this)+"\n");
            writer.write("CAN NAME      : "+new MyRW_IntMem().MyRead("_canname",UpdateValues.this)+"\n");

            // Chiudere il writer per salvare le modifiche
            writer.close();

            System.out.println("File scritto con successo!");

        } catch (IOException e) {
            // Gestire l'eccezione in modo appropriato
            e.printStackTrace();
        }
    }
}