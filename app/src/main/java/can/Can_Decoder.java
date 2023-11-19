package can;

import android.util.Log;

import java.util.Arrays;

import services_and_bluetooth.DataSaved;

public class Can_Decoder {
    int messageType,acc_x,acc_y,acc_z;
    double norm,ax_norm,ay_norm,az_norm;
    public static int counter;
    public static int mID;//ID CAN Rx
    public static int len;//DLC CAN Rx
    public static int Pitch, Roll;
    public static int auto;
    public static double Deg_roll,Deg_pitch,correctRoll,correctPitch;
    public static byte [] msgFrame;//corpo dati CAN Rx


    public Can_Decoder(byte[] msg) {
        try {
            messageType = (int) msg[0];
            len = (int) msg[1] - 3;
            mID = PLC_DataTypes_BigEndian.byte_to_U16_be(new byte[]{msg[3], msg[4]});
            msgFrame = new byte[len];
            System.arraycopy(msg, 5, msgFrame, 0, len);
            switch (mID) {
                case 0x7f0:
                    counter++;
                if (len == 2 || len == 3) {
                    Pitch = PLC_DataTypes_BigEndian.byte_to_S16_be(new byte[]{msg[5], msg[6]});
                } else if (len >= 4) {
                    Pitch = PLC_DataTypes_BigEndian.byte_to_S16_be(new byte[]{msg[5], msg[6]});
                    Roll = PLC_DataTypes_BigEndian.byte_to_S16_be(new byte[]{msg[7], msg[8]});

                }
                break;
                case 0x7ef:
                    auto=msg[5];
                    break;
                case 0x386:
                    Log.d("Pitch",Arrays.toString(msg));
                    acc_x = PLC_DataTypes_LittleEndian.byte_to_S16(new byte[]{msg[5], msg[6]});
                    acc_y = PLC_DataTypes_LittleEndian.byte_to_S16(new byte[]{msg[7], msg[8]});
                    acc_z = PLC_DataTypes_LittleEndian.byte_to_S16(new byte[]{msg[9], msg[10]});
                    norm = Math.sqrt(acc_x * acc_x + acc_y * acc_y + acc_z * acc_z);
                    ax_norm = (double) acc_x / norm;
                    ay_norm = (double) acc_y / norm;
                    az_norm = (double) acc_z / norm;

                    if (DataSaved.lrFrame == 1) {
                        Deg_roll = -((Math.atan2(ay_norm, az_norm) * 180 / Math.PI));
                        Deg_pitch = (Math.atan2(ax_norm, az_norm) * 180 / Math.PI);

                    } else if (DataSaved.lrFrame == 2) {
                        //right
                        Deg_roll = -(Math.atan2(ax_norm, az_norm) * 180 / Math.PI);
                        Deg_pitch = -(Math.atan2(ay_norm, az_norm) * 180 / Math.PI);

                    } else if (DataSaved.lrFrame == 3) {
                        Deg_roll = Math.atan2(ay_norm, az_norm) * 180 / Math.PI;
                        Deg_pitch = -(Math.atan2(ax_norm, az_norm) * 180 / Math.PI);

                    } else if (DataSaved.lrFrame == 4) {
                        //left
                        Deg_roll = Math.atan2(ax_norm, az_norm) * 180 / Math.PI;
                        Deg_pitch = Math.atan2(ay_norm, az_norm) * 180 / Math.PI;

                    } else {
                        Deg_roll = -((Math.atan2(ay_norm, az_norm) * 180 / Math.PI));
                        Deg_pitch = (Math.atan2(ax_norm, az_norm) * 180 / Math.PI);
                    }


                    Deg_roll += 180;
                    if (Deg_roll < -180) {
                        Deg_roll += 360;
                    } else if (Deg_roll > 180) {
                        Deg_roll -= 360;
                    }
                    Deg_pitch += 180;
                    if (Deg_pitch < -180) {
                        Deg_pitch += 360;
                    } else if (Deg_pitch > 180) {
                        Deg_pitch -= 360;
                    }

                    correctPitch = Excavator_RealValues.realPitch(DataSaved.offsetPitch);
                    correctRoll = Excavator_RealValues.realRoll(DataSaved.offsetRoll);


                    break;


            }


        } catch (Exception e) {
            messageType = 0;
            mID = 0;
            len = 0;
            Pitch = 0;
            Roll = 0;
            auto=0;
        }


    }
}
