package services_and_bluetooth;

import android.app.Service;
import android.content.Intent;
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
import org.locationtech.proj4j.datum.Datum;
import org.locationtech.proj4j.datum.Ellipsoid;


import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
            String macaddress = myRW_intMem.MyRead("_macaddress", UpdateValues.this);
            String gpsname = myRW_intMem.MyRead("_gpsname", UpdateValues.this);
            String macaddresscan = myRW_intMem.MyRead("_macaddress_can", UpdateValues.this);
            String canname = myRW_intMem.MyRead("_canname", UpdateValues.this);
            String crs = myRW_intMem.MyRead("_crs", UpdateValues.this);
            String altezzaAnt=myRW_intMem.MyRead("_altezzaantenna",UpdateValues.this);
            String unitOfMeasure = myRW_intMem.MyRead("_unitofmeasure", UpdateValues.this);
            String points=myRW_intMem.MyRead("pointssaved", UpdateValues.this);
            String boomresult=myRW_intMem.MyRead("boomresult", UpdateValues.this);
            String rmcSize=myRW_intMem.MyRead("rmcSize", UpdateValues.this);
            String useRmc=myRW_intMem.MyRead("useRmc", UpdateValues.this);
            String zoomF=myRW_intMem.MyRead("zoomF", UpdateValues.this);
            String rot=myRW_intMem.MyRead("rot", UpdateValues.this);
            String ztol=myRW_intMem.MyRead("z_tol", UpdateValues.this);
            String xytol=myRW_intMem.MyRead("xy_tol", UpdateValues.this);
            String maprotmode=myRW_intMem.MyRead("_maprotmode",UpdateValues.this);
            String offsetPitch=myRW_intMem.MyRead("_offsetpitch",UpdateValues.this);
            String offsetRoll=myRW_intMem.MyRead("_offsetpitch",UpdateValues.this);
            String useTilt=myRW_intMem.MyRead("_usetilt",UpdateValues.this);
            String projectName=myRW_intMem.MyRead("projectName", UpdateValues.this);

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

            if(crs==null){
                myRW_intMem.MyWrite("_crs","3004",UpdateValues.this);
            }
            if(altezzaAnt==null){
                myRW_intMem.MyWrite("_altezzaantenna","2",UpdateValues.this);
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
                myRW_intMem.MyWrite("useRmc", "1", UpdateValues.this);
            }
            if(zoomF==null){
                myRW_intMem.MyWrite("zoomF", "1.0", UpdateValues.this);
            }
            if(rot==null){
                myRW_intMem.MyWrite("rot", "0", UpdateValues.this);
            }
            if(ztol==null){
                myRW_intMem.MyWrite("z_tol", "0.03", UpdateValues.this);
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


            DataSaved.S_macAddres=myRW_intMem.MyRead("_macaddress",UpdateValues.this);
            DataSaved.S_gpsname=myRW_intMem.MyRead("_gpsname",UpdateValues.this);
            DataSaved.S_macAddress_CAN=myRW_intMem.MyRead("_macaddress_can",UpdateValues.this);
            DataSaved.S_can_name=myRW_intMem.MyRead("_canname",UpdateValues.this);
            DataSaved.S_CRS=myRW_intMem.MyRead("_crs",UpdateValues.this);
            DataSaved.D_AltezzaAnt=Double.parseDouble(myRW_intMem.MyRead("_altezzaantenna",UpdateValues.this).replace(",","."));
            DataSaved.I_UnitOfMeasure=Integer.parseInt(myRW_intMem.MyRead("_unitofmeasure",UpdateValues.this));
            DataSaved.rmcSize=Integer.parseInt(myRW_intMem.MyRead("rmcSize",UpdateValues.this));
            DataSaved.useRmc=Integer.parseInt(myRW_intMem.MyRead("useRmc",UpdateValues.this));
            DataSaved.xy_tol=Double.parseDouble(myRW_intMem.MyRead("xy_tol",UpdateValues.this).replace(",","."));
            DataSaved.z_tol=Double.parseDouble(myRW_intMem.MyRead("z_tol",UpdateValues.this).replace(",","."));
            DataSaved.MapRotMode=myRW_intMem.MyRead("_maprotmode",UpdateValues.this);
            DataSaved.offsetPitch=Double.parseDouble(myRW_intMem.MyRead("_offsetpitch",UpdateValues.this).replace(",","."));
            DataSaved.offsetRoll=Double.parseDouble(myRW_intMem.MyRead("_offsetroll",UpdateValues.this).replace(",","."));
            DataSaved.useTilt=Integer.parseInt(myRW_intMem.MyRead("_usetilt",UpdateValues.this));
            DataSaved.S_projectName=myRW_intMem.MyRead("projectName",UpdateValues.this);


            try {
                WGS84 = crsFactory.createFromName("epsg:" +"4326");
                try {
                    UTM = crsFactory.createFromName("epsg:" +  DataSaved.S_CRS);
                    Log.d("TEST_ROB ser",DataSaved.S_CRS);
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

            } catch (Exception e) {

            }

            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((ExecutorService) mExecutor).shutdown();
        Log.d("TEST_ROB","Servizio stoppato con successo");
    }
}