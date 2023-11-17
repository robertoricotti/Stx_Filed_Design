package can;


import static can.Can_Decoder.Deg_pitch;
import static can.Can_Decoder.Deg_roll;


public class Excavator_RealValues {

    public static double realRoll(double offset) {
        double a;
        double d = Deg_roll;
        a = (d - offset);

        return a;
    }

    public static double realPitch(double offset) {
        double a;
        double d = Deg_pitch;
        a = (d - offset);

        return a;
    }










}
