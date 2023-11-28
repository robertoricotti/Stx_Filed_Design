package project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import activity.MyApp;
import coords_calc.CoordsConverter;
import coords_calc.GPS;
import dialogs.CustomToast;
import gnss.My_LocationCalc;
import services_and_bluetooth.DataSaved;
import services_and_bluetooth.UpdateValues;
import utils.MyRW_IntMem;


public class DataProjectSingleton {


    @SuppressLint("StaticFieldLeak")
    private static volatile DataProjectSingleton INSTANCE = null;

    public LinkedHashMap<String, GPS> points;
    public String projectName;
    public String epsgCode;
    public String units;
    public String distanceID;
    public boolean delaunay;
    public float offsetX;
    public float offsetY;
    public float mScaleFactor;
    public float scale;
    public float radius;
    public float rotate;
    public double rtLength;
    public double rtSlope;
    public double ltLength;
    public double ltSlope;
    public double zB;
    public double distanceAB;
    public double slopeAB;

    private DataProjectSingleton() {
        initializeDefaults();
    }

    private void initializeDefaults() {
        points = new LinkedHashMap<>();
        offsetX = 0;
        offsetY = 0;
        mScaleFactor = 1f;
        scale = 100;
        radius = 10;
        rotate = 0;
        rtLength = 20;
        rtSlope = 0;
        ltLength = 20;
        ltSlope = 0;
        zB = 0;
        distanceAB = 0;
        slopeAB = 0;
        delaunay = false;
    }

    public static DataProjectSingleton getInstance() {
        if (INSTANCE == null) {
            synchronized (DataProjectSingleton.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DataProjectSingleton();
                }
            }
        }
        return INSTANCE;
    }

    public int getSize() {
        return points.size();
    }

    public LinkedHashMap<String, GPS> getPoints() {
        return points;
    }

    public void setEpsgCode(String epsgCode, Context context) {

        this.epsgCode = epsgCode;
        new MyRW_IntMem().MyWrite("_crs", epsgCode.toString(), context);
        DataSaved.S_CRS = epsgCode;


        Pattern pattern = Pattern.compile("\\+units=([^,\\s]+)");

        Matcher matcher = pattern.matcher(CoordsConverter.getInfoParams(this.epsgCode));

        MyApp.visibleActivity.startService(new Intent(MyApp.visibleActivity, UpdateValues.class));
        if (matcher.find())
            this.units = matcher.group(1);
    }

    public double abOrient() {
        GPS a = getPoints().get("A");

        GPS b = getPoints().get("B");
        try {
            return My_LocationCalc.calcBearingXY(a.getX(), a.getY(), b.getX(), b.getY());

        } catch (Exception e) {
            return 0;
        }
    }

    public String getDistanceID() {
        return distanceID;
    }

    public void setDistanceID(String distanceID) {
        this.distanceID = distanceID;
    }

    public boolean isDelaunay() {
        return delaunay;
    }

    public void setDelaunay(boolean delaunay) {
        this.delaunay = delaunay;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    public float getmScaleFactor() {
        return mScaleFactor;
    }

    public void setRotate(float rotate) {
        this.rotate = rotate;
    }

    public float getRotate() {
        return rotate;
    }

    public void setmScaleFactor(float mScaleFactor) {
        this.mScaleFactor = mScaleFactor;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public String getUnits() {
        return units;
    }

    public double getRtLength() {
        return rtLength;
    }

    public void setRtLength(double rtLength) {
        this.rtLength = rtLength;
    }

    public double getRtSlope() {
        return rtSlope;
    }

    public void setRtSlope(double rtSlope) {
        this.rtSlope = rtSlope;
    }

    public double getLtLength() {
        return ltLength;
    }

    public void setLtLength(double ltLength) {
        this.ltLength = ltLength;
    }

    public double getLtSlope() {
        return ltSlope;
    }

    public void setLtSlope(double ltSlope) {
        this.ltSlope = ltSlope;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getEpsgCode() {
        return epsgCode;
    }

    public double getzB() {
        return zB;
    }

    public void setzB(double zB) {
        this.zB = zB;
    }

    public double getDistanceAB() {
        return distanceAB;
    }

    public void setDistanceAB(double distanceAB) {
        this.distanceAB = distanceAB;
    }

    public double getSlopeAB() {
        return slopeAB;
    }

    public void setSlopeAB(double slopeAB) {
        this.slopeAB = slopeAB;
    }

    public void toggleDelaunay() {
        if (getSize() >= 3)
            delaunay = !delaunay;
    }

    public GPS getSinglePoint() {
        if (!points.containsKey(distanceID))
            return null;
        return points.get(distanceID);
    }

    public boolean addCoordinate(String id, GPS gps) {
        if (points.containsKey(id))
            return false;
        points.put(id, gps);
        return true;
    }

    public boolean updateCoordinate(String id, GPS gps) {
        if (points.containsKey(id)) {
            points.put(id, gps);
            return true;
        }
        return false;
    }

    public boolean deleteCoordinate(String id) {
        if (points.containsKey(id)) {
            points.remove(id);
            return true;
        }
        return false;
    }

    public boolean deleteAllCoordinate() {
        if (!points.isEmpty()) {
            points.clear();
            return true;
        }
        return false;
    }

    public boolean readProject(String path) {
        Log.d("PATTY_single",path);
        clearData();
        new MyRW_IntMem().MyWrite("projectPath", path, MyApp.visibleActivity);

        try {

            CSVReader reader = new CSVReader(new FileReader(path));

            String[] info = reader.readNext();

            this.projectName = info[0];
            this.epsgCode = info[1];


            new MyRW_IntMem().MyWrite("_crs", this.epsgCode, MyApp.visibleActivity);
            MyApp.visibleActivity.startService(new Intent(MyApp.visibleActivity, UpdateValues.class));
            String[] row;

            while ((row = reader.readNext()) != null) {
                String id = row[0];
                addCoordinate(id, new GPS(info[1], Double.parseDouble(row[1]), Double.parseDouble(row[2]), Double.parseDouble(row[3])));
            }
            return true;

        } catch (Exception e) {
            new CustomToast(MyApp.visibleActivity,"Error Reading File...").show();
            return false;

        }


    }


    public boolean saveProject(String path, String fileName) {
        try {
            File f = new File(path, fileName);

            CSVWriter writer = new CSVWriter(new FileWriter(f));

            String[] info = {fileName, epsgCode, String.valueOf(getSize())};

            writer.writeNext(info);

            for (Map.Entry<String, GPS> entry : points.entrySet()) {
                String key = entry.getKey();
                GPS value = entry.getValue();
                writer.writeNext(new String[]{key, String.valueOf(value.getX()), String.valueOf(value.getY()), String.valueOf(value.getZ()), "P"});
            }
            writer.close();
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }


    public boolean clearData() {
        if (INSTANCE != null) {
            initializeDefaults();
            epsgCode = null;
            units = null;
            projectName = null;
            distanceID = null;

            return true;
        }
        return false;
    }
}
