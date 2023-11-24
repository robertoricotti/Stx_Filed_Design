package gnss;

import java.util.LinkedList;
import java.util.Queue;


public class MachineBearing_from_RMC {
    static Queue<Double> bearingQueue = new LinkedList<>();
    static double bearingValue;

    public static double machineBearing(double bearing,double speed,int size){
        if (speed > 0.4) {

            if (bearing > 180) {
                bearing -= 360;
            } else if (bearing < -180) {
                bearing += 360;
            }

            bearingQueue.offer(bearing);
            if (bearingQueue.size() >= size) {
                double sum = 0;
                for (double value : bearingQueue) {
                    sum += value;
                }
                bearingValue = sum / bearingQueue.size();
                bearingQueue.clear();
            }


        }
        return bearingValue;
    }

}
