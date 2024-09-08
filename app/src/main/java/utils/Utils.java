package utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;


import coords_calc.DistToPoint;
import coords_calc.GPS;


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

    public static String readSensorCalibration(String str, Context ctx) {
        double v = Double.parseDouble(str);
        int index = Integer.parseInt(new MyRW_IntMem().MyRead("_unitofmeasure", ctx));
        if (index == 2 || index == 3) {
            return String.format("%.4f", v / 0.3048).replace(",", ".");
        } else if (index == 4 || index == 5) {
            double inches = v / 0.0254;
            double feet = (inches / 12);
            double leftover = inches % 12;
            if (feet < 0) {
                return ("-" + (Math.abs((int) feet)) + "' " + String.format("%.2f", Math.abs(leftover)));
            } else {
                return ((int) feet + "' " + String.format("%.2f", Math.abs(leftover)));
            }
        } else {
            return String.format("%.3f", v).replace(",", ".");
        }
    }

    @SuppressLint("DefaultLocale")
    public static String writeMetri(String str, Context ctx) {
        int index = Integer.parseInt(new MyRW_IntMem().MyRead("_unitofmeasure", ctx));
        if (index == 2 || index == 3) {
            double v = Double.parseDouble(str);
            return String.format("%.4f", v * 0.3048).replace(",", ".");
        } else if (index == 4 || index == 5) {
            double ft = Double.parseDouble(str.split("'")[0].trim());
            double inches = Double.parseDouble(str.split("'")[1].trim());
            double v = ft * 12 + inches;
            return String.format("%.4f", v * 0.0254).replace(",", ".");
        } else {
            double v = Double.parseDouble(str);
            return String.format("%.3f", v).replace(",", ".");
        }
    }

    @SuppressLint("DefaultLocale")
    public static String readUnitOfMeasure(String str, Context ctx) {
        double v = Double.parseDouble(str);
        int index = Integer.parseInt(new MyRW_IntMem().MyRead("_unitofmeasure", ctx));
        if (index == 2 || index == 3) {
            return String.format("%.4f", v / 0.3048).replace(",", ".");
        } else if (index == 4 || index == 5) {
            double inches = v / 0.0254;
            double feet = (inches / 12);
            double leftover = inches % 12;
            if (feet < 0) {
                return ("-" + (Math.abs((int) feet)) + "' " + String.format("%.2f", Math.abs(leftover)));
            } else {
                return ((int) feet + "' " + String.format("%.2f", Math.abs(leftover)));
            }
            //return String.format("%.2f", v / 0.0254).replace(",", ".");
        } else {
            return String.format("%.3f", v).replace(",", ".");
        }
    }


    @SuppressLint("DefaultLocale")
    public static String writeGradi(String str, Context ctx) {
        double p = Double.parseDouble(str);

        int index = Integer.parseInt(new MyRW_IntMem().MyRead("_unitofmeasure", ctx));
        if (index == 1 || index == 3 || index == 5) {

            //convertire % deg
            return String.format("%.3f", Math.toDegrees(Math.atan(p / 100))).replace(",", ".");
        } else {
            return String.format("%.3f", p).replace(",", ".");
        }
    }

    @SuppressLint("DefaultLocale")
    public static String readAngolo(String str, Context ctx) {
        double p = (Double.parseDouble(str));
        int index = Integer.parseInt(new MyRW_IntMem().MyRead("_unitofmeasure", ctx));
        if (index == 1 || index == 3 || index == 5) {
            double a = Math.toRadians(Double.parseDouble(str));
            //convertire % in deg

            return String.format("%.2f", (Math.tan(a) * 100.0d)).replace(",", ".");
        } else {
            return String.format("%.2f", p).replace(",", ".");
        }
    }

    public static String getGradiSimbol(Context ctx) {
        int index = Integer.parseInt(new MyRW_IntMem().MyRead("_unitofmeasure", ctx));
        if (index == 0 || index == 2 || index == 4)
            return " °";
        else
            return " %";
    }

    public static String getMetriSimbol(Context ctx) {
        int index = Integer.parseInt(new MyRW_IntMem().MyRead("_unitofmeasure", ctx));
        if (index == 0 || index == 1)
            return "(m)";
        else if (index == 2 || index == 3)
            return "(ft)";
        else
            return "(in)";
    }

    public static double slopeCalculator(GPS pA, GPS pB) {
        double val = new DistToPoint(pA.getX(), pA.getY(), 0, pB.getX(), pB.getY(), 0).getDist_to_point();//base

        if (Math.abs(val) > 0.1) {
            double cC = new DistToPoint(pA.getX(), pA.getY(), pA.getZ(), pB.getX(), pB.getY(), pB.getZ()).getDist_to_point();//lato lungo
            double a = Math.sqrt(Math.abs(cC * cC - val * val));//altezza
            double dist = (val * val) + (cC * cC) - (a * a); // Corrected formula
            double g = Math.toDegrees(Math.acos(dist / (2 * val * cC)));
            int neg = pA.getZ() < pB.getZ() ? 1 : -1;

            return Double.isNaN(g) ? 0 : (g * neg);
        }
        return 0;
    }

    public static double slopeCalculator_primitive(double[] A, double[] B) {
        double val = new DistToPoint(A[0], A[1], 0, B[0], B[1], 0).getDist_to_point();//base

        if (Math.abs(val) > 0.1) {
            double cC = new DistToPoint(A[0], A[1], A[2], B[0], B[1], B[2]).getDist_to_point();//lato lungo
            double a = Math.sqrt(Math.abs(cC * cC - val * val));//altezza
            double dist = (val * val) + (cC * cC) - (a * a); // Corrected formula
            double g = Math.toDegrees(Math.acos(dist / (2 * val * cC)));
            int neg = A[2] < B[2] ? 1 : -1;

            return Double.isNaN(g) ? 0 : (g * neg);
        }
        return 0;
    }

   /* private void executeMatrixOperations() {
        // Creating the A matrix

        //P=(A TA)^−1 A^T b

        double[][] dataA = {
                {1, 2},
                {3, 4},
                {5, 6}
        };

        // Creating the vector b
        double[][] dataB = {
                {7},
                {8},
                {9}
        };

        // Convert  arrays into  SimpleMatrix objects
        SimpleMatrix A = new SimpleMatrix(dataA);
        SimpleMatrix b = new SimpleMatrix(dataB);

        // Calc A^T (transpose of A)
        SimpleMatrix At = A.transpose();

        // Calc (A^T * A)
        SimpleMatrix AtA = At.mult(A);

        // Calc  (A^T * A) inverse
        SimpleMatrix AtA_inv = AtA.invert();

        // Calc (A^T * b)
        SimpleMatrix Atb = At.mult(b);

        // Calc P = (A^T * A)^-1 * (A^T * b)
        SimpleMatrix P = AtA_inv.mult(Atb);


        Log.w("mySimpleMatrix","Result: " +P);

    }*/
}


