package coords_calc;

import org.locationtech.jts.geom.Coordinate;

public class Surface_4ptsPlus {

    public double A1, A2;
    public double B1, B2;
    public double C1, C2;
    public double D1, D2;
    public double mdetMc1, mdetMd1, mdetMa1, mdetMb1;
    public double mdetMc2, mdetMd2, mdetMa2, mdetMb2;
    Coordinate[] quad1 = new Coordinate[4];
    Coordinate[] quad2 = new Coordinate[4];
    Coordinate[] points;


    public void setData(Coordinate[] coords){

        points = new Coordinate[coords.length];

        System.arraycopy(coords, 0, points, 0, points.length);

        double[] res;

        if(points.length == 4){
            res = calculateSurfaceEquation(
                    coords[0].x, coords[0].y, coords[0].z,
                    coords[1].x, coords[1].y, coords[1].z,
                    coords[2].x, coords[2].y, coords[2].z,
                    coords[3].x, coords[3].y, coords[3].z);

            A1 = res[1] / res[0];
            B1 = -res[2] / res[0];
            C1 = res[3] / res[0];
            D1 = -res[4] / res[0];

            mdetMa1 = res[1];
            mdetMb1 = res[2];
            mdetMc1 = res[3];
            mdetMd1 = res[4];

            quad1[0] = points[0];
            quad1[1] = points[1];
            quad1[2] = points[2];
            quad1[3] = points[3];
        }

        if(points.length == 6){

            res = calculateSurfaceEquation(
                    coords[0].x, coords[0].y, coords[0].z,//a
                    coords[1].x, coords[1].y, coords[1].z,//b
                    coords[2].x, coords[2].y, coords[2].z,//c
                    coords[3].x, coords[3].y, coords[3].z);//d

            A1 = res[1] / res[0];
            B1 = -res[2] / res[0];
            C1 = res[3] / res[0];
            D1 = -res[4] / res[0];

            mdetMa1 = res[1];
            mdetMb1 = res[2];
            mdetMc1 = res[3];
            mdetMd1 = res[4];

            quad1[0] = points[0];
            quad1[1] = points[1];
            quad1[2] = points[2];
            quad1[3] = points[3];

            res = calculateSurfaceEquation(
                    coords[5].x, coords[5].y, coords[5].z,
                    coords[4].x, coords[4].y, coords[4].z,
                    coords[1].x, coords[1].y, coords[1].z,
                    coords[0].x, coords[0].y, coords[0].z);

            A2 = res[1] / res[0];
            B2 = -res[2] / res[0];
            C2 = res[3] / res[0];
            D2 = -res[4] / res[0];

            mdetMa2 = res[1];
            mdetMb2 = res[2];
            mdetMc2 = res[3];
            mdetMd2 = res[4];

            quad2[0] = points[5];
            quad2[1] = points[4];
            quad2[2] = points[1];
            quad2[3] = points[0];
        }
    }

    private double[] calculateSurfaceEquation(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4) {
        double[][] M = {
                {x1, y1, z1, 1},
                {x2, y2, z2, 1},
                {x3, y3, z3, 1},
                {x4, y4, z4, 1}
        };
        double detM = Matrix_3D.determinant(M);

        double[][] Ma = {
                {y1, z1, 1},
                {y2, z2, 1},
                {y3, z3, 1},
                {y4, z4, 1}
        };
        double[][] Mb = {
                {x1, z1, 1},
                {x2, z2, 1},
                {x3, z3, 1},
                {x4, z4, 1}
        };
        double[][] Mc = {
                {x1, y1, 1},
                {x2, y2, 1},
                {x3, y3, 1},
                {x4, y4, 1}
        };
        double[][] Md = {
                {x1, y1, z1},
                {x2, y2, z2},
                {x3, y3, z3},
                {x4, y4, z4}
        };
        double detMa = Matrix_3D.determinant(Ma);
        double detMb = Matrix_3D.determinant(Mb);
        double detMc = Matrix_3D.determinant(Mc);
        double detMd = Matrix_3D.determinant(Md);


        return new double[]{detM, detMa, detMb, detMc, detMd};
    }

    public boolean isSurfOk() {
        if (points.length == 4)
            return mdetMc1 != 0.0 || mdetMd1 != 0.0 || mdetMb1 != 0.0 || mdetMa1 != 0.0;

        if (points.length == 6)
            return (mdetMc1 != 0.0 || mdetMd1 != 0.0 || mdetMb1 != 0.0 || mdetMa1 != 0.0) || (mdetMc2 != 0.0 || mdetMd2 != 0.0 || mdetMb2 != 0.0 || mdetMa2 != 0.0);

        return false;
    }

    public double getAltitude(double currentX, double currentY) {

        if(isPointInside1(currentX, currentY)){
            if (Double.isNaN(this.A1)) this.A1 = 0;
            if (Double.isNaN(this.B1)) this.B1 = 0;
            if (Double.isNaN(this.C1)) this.C1 = 0;
            if (Double.isNaN(this.D1)) this.D1 = 0;

            return (-this.A1 * currentX - this.B1 * currentY - this.D1) / this.C1;
        }

        if(isPointInside2(currentX, currentY)){
            if (Double.isNaN(this.A2)) this.A2 = 0;
            if (Double.isNaN(this.B2)) this.B2 = 0;
            if (Double.isNaN(this.C2)) this.C2 = 0;
            if (Double.isNaN(this.D2)) this.D2 = 0;

            return (-this.A2 * currentX - this.B2 * currentY - this.D2) / this.C2;
        }

        return 0;

    }

    // Restituisce la differenza di quota tra il punto corrente e la superficie
    public double getAltitudeDifference(double currentX, double currentY, double currentZ) {
        double surfaceZ = getAltitude(currentX, currentY);

        if(isPointInside1(currentX, currentY) || isPointInside2(currentX, currentY))
            return currentZ - surfaceZ;

        return 0;
    }

    public boolean isPointInside1(double currentX, double currentY) {
        // calcola i vettori dai punti alle coordinate attuali
        double[] v1 = {currentX - quad1[0].x, currentY - quad1[0].y};
        double[] v2 = {currentX - quad1[1].x, currentY - quad1[1].y};
        double[] v3 = {currentX - quad1[2].x, currentY - quad1[2].y};
        double[] v4 = {currentX - quad1[3].x, currentY - quad1[3].y};
        // calcola i prodotti vettoriali tra i vettori consecutivi
        double cross1 = crossProduct(v1, v2);
        double cross2 = crossProduct(v2, v3);
        double cross3 = crossProduct(v3, v4);
        double cross4 = crossProduct(v4, v1);
        // controlla se i prodotti vettoriali hanno lo stesso segno
        return (cross1 >= 0 && cross2 >= 0 && cross3 >= 0 && cross4 >= 0) || (cross1 <= 0 && cross2 <= 0 && cross3 <= 0 && cross4 <= 0);
    }

    public boolean isPointInside2(double currentX, double currentY) {
        // calcola i vettori dai punti alle coordinate attuali
        double[] v1 = {currentX - quad2[0].x, currentY - quad2[0].y};
        double[] v2 = {currentX - quad2[1].x, currentY - quad2[1].y};
        double[] v3 = {currentX - quad2[2].x, currentY - quad2[2].y};
        double[] v4 = {currentX - quad2[3].x, currentY - quad2[3].y};
        // calcola i prodotti vettoriali tra i vettori consecutivi
        double cross1 = crossProduct(v1, v2);
        double cross2 = crossProduct(v2, v3);
        double cross3 = crossProduct(v3, v4);
        double cross4 = crossProduct(v4, v1);
        // controlla se i prodotti vettoriali hanno lo stesso segno
        return (cross1 >= 0 && cross2 >= 0 && cross3 >= 0 && cross4 >= 0) || (cross1 <= 0 && cross2 <= 0 && cross3 <= 0 && cross4 <= 0);
    }

    private double crossProduct(double[] v1, double[] v2) {
        return v1[0] * v2[1] - v1[1] * v2[0];
    }


}
