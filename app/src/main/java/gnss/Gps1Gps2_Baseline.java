package gnss;



public class Gps1Gps2_Baseline {
    public double orientation, baseline;

    public Gps1Gps2_Baseline(double Est1, double Nord1, double Quota1, double Est2, double Nord2, double Quota2) {
        try {
            double y1 = Nord1;
            double x1 = Est1;
            double y2 = Nord2;
            double x2 = Est2;
            double z1 = Quota1;
            double z2 = Quota2;

            orientation = Math.atan2(x2 - x1, y2 - y1);
            orientation = Math.toDegrees(orientation);
            orientation = (orientation + 360d) % 360;

            baseline = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2) + Math.pow((z2 - z1), 2));
        } catch (Exception e) {
           orientation=0;
           baseline=0;
        }

    }

    public double getOrientation() {
        return orientation;
    }

    public double getBaseline() {
        return baseline;
    }
}
