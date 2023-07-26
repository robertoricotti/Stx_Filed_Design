package utils;

import android.annotation.SuppressLint;
import android.content.Context;


public class Utils {

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static String readSensorCalibration(String str, Context ctx){
        double v = Double.parseDouble(str);
        int index = Integer.parseInt(new MyRW_IntMem().MyRead("Unit_Of_Measure", ctx));
        if(index == 2 || index == 3){
            return String.format("%.4f", v / 0.3048).replace(",", ".");
        }
        else if(index == 4 || index == 5){
            double inches = v / 0.0254;
            double feet =  (inches / 12);
            double leftover = inches % 12;
            if(feet<0){
                return ("-"+(Math.abs((int) feet)) + "' " + String.format("%.2f", Math.abs(leftover)));}
            else{
                return ((int) feet + "' " + String.format("%.2f", Math.abs(leftover)));
            }
        }
        else {
            return String.format("%.3f", v).replace(",", ".");
        }
    }

    @SuppressLint("DefaultLocale")
    public static String writeMetri(String str, Context ctx){
        int index = Integer.parseInt(new MyRW_IntMem().MyRead("Unit_Of_Measure", ctx));
        if(index == 2 || index == 3){
            double v = Double.parseDouble(str);
            return String.format("%.4f", v * 0.3048).replace(",", ".");
        }
        else if(index == 4 || index == 5){
            double ft = Double.parseDouble(str.split("'")[0].trim());
            double inches = Double.parseDouble(str.split("'")[1].trim());
            double v = ft * 12 + inches;
            return String.format("%.4f", v * 0.0254).replace(",", ".");
        }
        else {
            double v = Double.parseDouble(str);
            return String.format("%.3f", v).replace(",", ".");
        }
    }

    @SuppressLint("DefaultLocale")
    public static String readUnitOfMeasure(String str, Context ctx){
        double v = Double.parseDouble(str);
        int index = Integer.parseInt(new MyRW_IntMem().MyRead("Unit_Of_Measure", ctx));
        if(index == 2 || index == 3){
            return String.format("%.3f", v / 0.3048).replace(",", ".");
        }
        else if(index == 4 || index == 5){
            double inches = v / 0.0254;
            double feet =  (inches / 12);
            double leftover = inches % 12;
            if(feet<0){
            return ("-"+(Math.abs((int) feet)) + "' " + String.format("%.2f", Math.abs(leftover)));}
            else{
                return ((int) feet + "' " + String.format("%.2f", Math.abs(leftover)));
            }
            //return String.format("%.2f", v / 0.0254).replace(",", ".");
        }
        else {
            return String.format("%.2f", v).replace(",", ".");
        }
    }

    @SuppressLint("DefaultLocale")
    public static String readUnitOfMeasure_2(String str, Context ctx){
        double v = Double.parseDouble(str);
        int index = Integer.parseInt(new MyRW_IntMem().MyRead("Unit_Of_Measure", ctx));
        if(index == 2 || index == 3){
            return String.format("%.2f", v / 0.3048).replace(",", ".");
        }
        else if(index == 4 || index == 5){
            double inches = v / 0.0254;
            double feet =  (inches / 12);
            double leftover = inches % 12;
            if(feet<0){
                return ("-"+(Math.abs((int) feet)) + "' " + String.format("%.2f", Math.abs(leftover)));}
            else{
                return ((int) feet + "' " + String.format("%.2f", Math.abs(leftover)));
            }
            //return String.format("%.2f", v / 0.0254).replace(",", ".");
        }
        else {
            return String.format("%.2f", v).replace(",", ".");
        }
    }


    @SuppressLint("DefaultLocale")
    public static String writeGradi(String str, Context ctx){
        double p = Double.parseDouble(str);

        int index = Integer.parseInt(new MyRW_IntMem().MyRead("Unit_Of_Measure", ctx));
        if(index == 1 || index == 3 || index == 5){

            //convertire % deg
            return String.format("%.3f", Math.toDegrees(Math.atan(p / 100))).replace(",", ".");
        }
        else {
            return String.format("%.3f", p).replace(",", ".");
        }
    }

    @SuppressLint("DefaultLocale")
    public static String readAngolo(String str, Context ctx){
        double p = (Double.parseDouble(str));
        int index = Integer.parseInt(new MyRW_IntMem().MyRead("Unit_Of_Measure", ctx));
        if(index == 1 || index == 3 || index == 5){
            double a = Math.toRadians(Double.parseDouble(str));
            //convertire % in deg

            return String.format("%.2f", (Math.tan(a) * 100.0d)).replace(",", ".");
        }
        else {
            return String.format("%.2f", p).replace(",", ".");
        }
    }

    public static String getGradiSimbol(Context ctx){
        int index = Integer.parseInt(new MyRW_IntMem().MyRead("Unit_Of_Measure", ctx));
        if(index == 0 || index == 2 || index == 4)
            return " °";
        else
            return " %";
    }
    public static String getMetriSimbol(Context ctx){
        int index = Integer.parseInt(new MyRW_IntMem().MyRead("Unit_Of_Measure", ctx));
        if(index == 0 || index == 1)
            return "(m)";
        else if(index == 2 || index == 3)
            return "(ft)";
        else
            return "(in)";
    }
}


/*
public class Utils {

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static String readSensorCalibration(String str, Context ctx){
        double v = Double.parseDouble(str);
        int index = Integer.parseInt(new MyRW_IntMem().MyRead("Unit_Of_Measure", ctx));
        if(index == 2 || index == 3){
            return String.format("%.4f", v / 0.3048).replace(",", ".");
        }
        else if(index == 4 || index == 5){
            return String.format("%.4f", v / 0.0254).replace(",", ".");
        }
        else {
            return String.format("%.3f", v).replace(",", ".");
        }
    }

    @SuppressLint("DefaultLocale")
    public static String writeMetri(String str, Context ctx){
        int index = Integer.parseInt(new MyRW_IntMem().MyRead("Unit_Of_Measure", ctx));
        if(index == 2 || index == 3){
            double v = Double.parseDouble(str);
            return String.format("%.4f", v * 0.3048).replace(",", ".");
        }
        else if(index == 4 || index == 5){
            double ft = Double.parseDouble(str.split("'")[0].trim());
            double inches = Double.parseDouble(str.split("'")[1].trim());
            double v = ft * 12 + inches;
            return String.format("%.4f", v * 0.0254).replace(",", ".");
        }
        else {
            double v = Double.parseDouble(str);
            return String.format("%.3f", v).replace(",", ".");
        }
    }

    @SuppressLint("DefaultLocale")
    public static String readUnitOfMeasure(String str, Context ctx){
        double v = Double.parseDouble(str);
        int index = Integer.parseInt(new MyRW_IntMem().MyRead("Unit_Of_Measure", ctx));
        if(index == 2 || index == 3){
            return String.format("%.3f", v / 0.3048).replace(",", ".");
        }
        else if(index == 4 || index == 5){
            double inches = v /0.0254;
            double feet =  (inches / 12);
            double leftover = inches % 12;

            return ((int)feet + "' " + String.format("%.2f", Math.abs(leftover)));


        }
        else {
            return String.format("%.2f", v).replace(",", ".");
        }
    }

    @SuppressLint("DefaultLocale")
    public static String writeGradi(String str, Context ctx){
        double p = Double.parseDouble(str);

        int index = Integer.parseInt(new MyRW_IntMem().MyRead("Unit_Of_Measure", ctx));
        if(index == 1 || index == 3 || index == 5){

            //convertire % deg
            return String.format("%.3f", Math.toDegrees(Math.atan(p / 100))).replace(",", ".");
        }
        else {
            return String.format("%.3f", p).replace(",", ".");
        }
    }

    @SuppressLint("DefaultLocale")
    public static String readAngolo(String str, Context ctx){
        double p = (Double.parseDouble(str));
        int index = Integer.parseInt(new MyRW_IntMem().MyRead("Unit_Of_Measure", ctx));
        if(index == 1 || index == 3 || index == 5){
            double a = Math.toRadians(Double.parseDouble(str));
            //convertire % in deg

            return String.format("%.2f", (Math.tan(a) * 100.0d)).replace(",", ".");
        }
        else {
            return String.format("%.2f", p).replace(",", ".");
        }
    }

    public static String getGradiSimbol(Context ctx){
        int index = Integer.parseInt(new MyRW_IntMem().MyRead("Unit_Of_Measure", ctx));
        if(index == 0 || index == 2 || index == 4)
            return " °";
        else
            return " %";
    }
    public static String getMetriSimbol(Context ctx){
        int index = Integer.parseInt(new MyRW_IntMem().MyRead("Unit_Of_Measure", ctx));
        if(index == 0 || index == 1)
            return "(m)";
        else if(index == 2 || index == 3)
            return "(ft)";
        else
            return "(in)";
    }
}




/*
public class Utils {

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static String readSensorCalibration(String str, Context ctx){
        double v = Double.parseDouble(str);
        int index = Integer.parseInt(new MyRW_IntMem().MyRead("Unit_Of_Measure", ctx));
        if(index == 2 || index == 3){
            return String.format("%.4f", v / 0.3048).replace(",", ".");
        }
        else if(index == 4 || index == 5){
            return String.format("%.4f", v / 0.0254).replace(",", ".");
        }
        else {
            return String.format("%.3f", v).replace(",", ".");
        }
    }

    @SuppressLint("DefaultLocale")
    public static String writeMetri(String str, Context ctx){
        double v = Double.parseDouble(str);
        int index = Integer.parseInt(new MyRW_IntMem().MyRead("Unit_Of_Measure", ctx));
        if(index == 2 || index == 3){
            return String.format("%.4f", v * 0.3048).replace(",", ".");
        }
        else if(index == 4 || index == 5){
            return String.format("%.4f", v * 0.0254).replace(",", ".");
        }
        else {
            return String.format("%.3f", v).replace(",", ".");
        }
    }

    @SuppressLint("DefaultLocale")
    public static String readUnitOfMeasure(String str, Context ctx){
        double v = Double.parseDouble(str);
        int index = Integer.parseInt(new MyRW_IntMem().MyRead("Unit_Of_Measure", ctx));
        if(index == 2 || index == 3){
            return String.format("%.3f", v / 0.3048).replace(",", ".");
        }
        else if(index == 4 || index == 5){
            double inches = v/0.0254;
            double feet =  (inches / 12);
            double leftover = inches % 12;

            return (String.format("%.0f",feet)+"' "+String.format("%.2f",Math.abs(leftover)));


            //return String.format("%.2f", v / 0.0254).replace(",", ".");
        }
        else {
            return String.format("%.2f", v).replace(",", ".");
        }
    }

    @SuppressLint("DefaultLocale")
    public static String writeGradi(String str, Context ctx){
        double p = Double.parseDouble(str);

        int index = Integer.parseInt(new MyRW_IntMem().MyRead("Unit_Of_Measure", ctx));
        if(index == 1 || index == 3 || index == 5){

            //convertire % deg
            return String.format("%.3f", Math.toDegrees(Math.atan(p / 100))).replace(",", ".");
        }
        else {
            return String.format("%.3f", p).replace(",", ".");
        }
    }

    @SuppressLint("DefaultLocale")
    public static String readAngolo(String str, Context ctx){
        double p = (Double.parseDouble(str));
        int index = Integer.parseInt(new MyRW_IntMem().MyRead("Unit_Of_Measure", ctx));
        if(index == 1 || index == 3 || index == 5){
            double a = Math.toRadians(Double.parseDouble(str));
            //convertire % in deg

            return String.format("%.2f", (Math.tan(a) * 100.0d)).replace(",", ".");
        }
        else {
            return String.format("%.2f", p).replace(",", ".");
        }
    }

    public static String getGradiSimbol(Context ctx){
        int index = Integer.parseInt(new MyRW_IntMem().MyRead("Unit_Of_Measure", ctx));
        if(index == 0 || index == 2 || index == 4)
            return " °";
        else
            return " %";
    }
    public static String getMetriSimbol(Context ctx){
        int index = Integer.parseInt(new MyRW_IntMem().MyRead("Unit_Of_Measure", ctx));
        if(index == 0 || index == 1)
            return "(m)";
        else if(index == 2 || index == 3)
            return "(ft)";
        else
            return "(in)";
    }
}
*/