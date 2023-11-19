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



import utils.MyRW_IntMem;

public class UpdateValues extends Service {
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
        myRW_intMem = new MyRW_IntMem();

        String macaddress = myRW_intMem.MyRead("_macaddress", this);
        String gpsname = myRW_intMem.MyRead("_gpsname", this);
        String macaddresscan = myRW_intMem.MyRead("_macaddress_can", this);
        String canname = myRW_intMem.MyRead("_canname", this);
        String crs = myRW_intMem.MyRead("_crs", this);
        String altezzaAnt=myRW_intMem.MyRead("_altezzaantenna",this);
        String unitOfMeasure = myRW_intMem.MyRead("_unitofmeasure", this);
        String points=myRW_intMem.MyRead("pointssaved", this);
        String boomresult=myRW_intMem.MyRead("boomresult", this);
        String rmcSize=myRW_intMem.MyRead("rmcSize", this);
        String useRmc=myRW_intMem.MyRead("useRmc", this);
        String zoomF=myRW_intMem.MyRead("zoomF", this);
        String rot=myRW_intMem.MyRead("rot", this);
        String ztol=myRW_intMem.MyRead("z_tol", this);
        String xytol=myRW_intMem.MyRead("xy_tol", this);
        String maprotmode=myRW_intMem.MyRead("_maprotmode",this);
        String offsetPitch=myRW_intMem.MyRead("_offsetpitch",this);
        String offsetRoll=myRW_intMem.MyRead("_offsetpitch",this);
        String useTilt=myRW_intMem.MyRead("_usetilt",this);
        if(macaddress==null){
            myRW_intMem.MyWrite("_macaddress","00:00:00:00:00:00",this);
        }
        if(gpsname==null){
            myRW_intMem.MyWrite("_gpsname","UNKNOWN",this);
        }
        if(macaddresscan==null){
            myRW_intMem.MyWrite("_macaddress_can","00:00:00:00:00:00",this);
        }
        if(canname==null){
            myRW_intMem.MyWrite("_canname","UNKNOWN",this);
        }

        if(crs==null){
            myRW_intMem.MyWrite("_crs","3004",this);
        }
        if(altezzaAnt==null){
            myRW_intMem.MyWrite("_altezzaantenna","2",this);
        }
        if (unitOfMeasure == null) {
            myRW_intMem.MyWrite("_unitofmeasure", "0", this);
        }
        if (points == null) {
            myRW_intMem.MyWrite("pointssaved", "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0", this);
        }
        if(boomresult==null){
            myRW_intMem.MyWrite("boomresult", "0,0,0", this);
        }
        if(rmcSize==null){
            myRW_intMem.MyWrite("rmcSize", "5", this);
        }
        if(useRmc==null){
            myRW_intMem.MyWrite("useRmc", "0", this);
        }
        if(zoomF==null){
            myRW_intMem.MyWrite("zoomF", "1.0", this);
        }
        if(rot==null){
            myRW_intMem.MyWrite("rot", "0", this);
        }
        if(ztol==null){
            myRW_intMem.MyWrite("z_tol", "0.03", this);
        }
        if(xytol==null){
            myRW_intMem.MyWrite("xy_tol", "0.03", this);
        }
        if(maprotmode==null){
            myRW_intMem.MyWrite("_maprotmode", "0", this);
        }
        if(offsetPitch==null){
            myRW_intMem.MyWrite("_offsetpitch", "0", this);
        }
        if(offsetRoll==null){
            myRW_intMem.MyWrite("_offsetroll", "0", this);
        }
        if(useTilt==null){
            myRW_intMem.MyWrite("_usetilt", "0", this);
        }










        DataSaved.S_macAddres=myRW_intMem.MyRead("_macaddress",this);
        DataSaved.S_gpsname=myRW_intMem.MyRead("_gpsname",this);
        DataSaved.S_macAddress_CAN=myRW_intMem.MyRead("_macaddress_can",this);

        DataSaved.S_can_name=myRW_intMem.MyRead("_canname",this);
        DataSaved.S_CRS=myRW_intMem.MyRead("_crs",this);
        DataSaved.D_AltezzaAnt=Double.parseDouble(myRW_intMem.MyRead("_altezzaantenna",this).replace(",","."));
        DataSaved.I_UnitOfMeasure=Integer.parseInt(myRW_intMem.MyRead("_unitofmeasure",this));
        DataSaved.rmcSize=Integer.parseInt(myRW_intMem.MyRead("rmcSize",this));
        DataSaved.useRmc=Integer.parseInt(myRW_intMem.MyRead("useRmc",this));
        DataSaved.xy_tol=Double.parseDouble(myRW_intMem.MyRead("xy_tol",this).replace(",","."));
        DataSaved.z_tol=Double.parseDouble(myRW_intMem.MyRead("z_tol",this).replace(",","."));
        DataSaved.MapRotMode=myRW_intMem.MyRead("_maprotmode",this);
        DataSaved.offsetPitch=Double.parseDouble(myRW_intMem.MyRead("_offsetpitch",this).replace(",","."));
        DataSaved.offsetRoll=Double.parseDouble(myRW_intMem.MyRead("_offsetroll",this).replace(",","."));
        DataSaved.useTilt=Integer.parseInt(myRW_intMem.MyRead("_usetilt",this));

        try {
            Datum datum;

            WGS84 = crsFactory.createFromName("epsg:" +"4326");
            try {
                UTM = crsFactory.createFromName("epsg:" +  DataSaved.S_CRS);
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
            String[] parameters = UTM.getParameters();
            datum=UTM.getDatum();
            Ellipsoid ellipsoid = datum.getEllipsoid();

            String unit = "";
            for (String part : parameters) {
                if (part.trim().startsWith("+units=")) {
                    unit = part.trim().substring(7);
                    break;
                }
                else{
                    unit="q";
                }
            }
            Log.d("Unit+", unit+"");



        } catch (Exception e) {

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.stopSelf();
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}