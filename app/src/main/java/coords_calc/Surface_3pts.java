package coords_calc;

import org.locationtech.jts.geom.Coordinate;

import gnss.My_LocationCalc;

public class Surface_3pts {


    static double x1, y1, z1, x2, y2, z2, x3, y3, z3;

    public Surface_3pts() {}

    public double getAltitude(double currentX, double currentY) {
        double areaTotal = calcTriangleArea(x1, y1, x2, y2, x3, y3);
        double area1 = calcTriangleArea(currentX, currentY, x2, y2, x3, y3);
        double area2 = calcTriangleArea(x1, y1, currentX, currentY, x3, y3);
        double area3 = calcTriangleArea(x1, y1, x2, y2, currentX, currentY);

        return (z1 * area1 + z2 * area2 + z3 * area3) / areaTotal;
    }


    public double getAltitudeDifference(double currentX, double currentY, double currentZ) {

        double surfaceZ = getAltitude(currentX, currentY);
        if (isPointInsideTriangle(currentX, currentY)) {
            return currentZ - surfaceZ;
        }
        else return 0;

    }

    public  boolean isPointInsideTriangle(double currX, double currY) {
        double areaTotal = calcTriangleArea(x1, y1, x2, y2, x3, y3);
        double area1 = calcTriangleArea(currX, currY, x2, y2, x3, y3);
        double area2 = calcTriangleArea(x1, y1, currX, currY, x3, y3);
        double area3 = calcTriangleArea(x1, y1, x2, y2, currX, currY);
        return Math.abs(areaTotal - (area1 + area2 + area3)) <= 1e-3; // valore di tolleranza in/out incrementare se troppo piccolo
    }

    private static double calcTriangleArea(double x1, double y1, double x2, double y2, double x3, double y3) {
        return 0.5 * Math.abs(x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2));
    }


    public void updateData(Coordinate[] points) {
        if (points.length != 9) {
            System.out.println("Points must be 9");
        }

        x1 = points[0].x;
        y1 = points[0].y;
        z1 = points[0].z;
        x2 = points[1].x;
        y2 = points[1].y;
        z2 = points[1].z;
        x3 = points[2].x;
        y3 = points[2].y;
        z3 = points[2].z;
        if (z1 == z2 && z2 == z3) {
            System.out.println("Points must have different Z coordinates");
            z1 = z2 + 0.001;
            z3 = z2 + 0.001;
        }
    }

    public boolean isSurfOk() {
        double hdt1 = 0;
        double hdt2 = 0;
        hdt1 = My_LocationCalc.calcBearingXY(x1, y1, x2, y2);
        hdt2 = My_LocationCalc.calcBearingXY(x2, y2, x3, y3);
        int res = (int) (Math.abs(hdt1) - Math.abs(hdt2));
        return Math.abs(res) >= 1;
    }


}