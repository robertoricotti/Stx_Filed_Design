package can;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;

import activity_portrait.CAN_DebugActivity;
import activity_portrait.MyApp;
import eventbus.CanEvents;
import services_and_bluetooth.DataSaved;

public class Can_Decoder {
    int messageType, acc_x, acc_y, acc_z;
    double norm, ax_norm, ay_norm, az_norm;
    public static int counter;
    public static int mID;//ID CAN Rx
    public static int len;//DLC CAN Rx
    public static int Pitch, Roll;
    public static int auto;
    public static double Deg_roll, Deg_pitch, correctRoll, correctPitch;
    public static byte[] msgFrame;//corpo dati CAN Rx


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
                    auto = msg[5];
                    break;
                case 897:
                case 898:
                case 899:
                case 900:
                case 901:
                case 902:
                case 903:

                    acc_x = PLC_DataTypes_LittleEndian.byte_to_S16(new byte[]{msg[5], msg[6]});
                    acc_y = PLC_DataTypes_LittleEndian.byte_to_S16(new byte[]{msg[7], msg[8]});
                    acc_z = PLC_DataTypes_LittleEndian.byte_to_S16(new byte[]{msg[9], msg[10]});
                    norm = Math.sqrt(acc_x * acc_x + acc_y * acc_y + acc_z * acc_z);
                    ax_norm = (double) acc_x / norm;
                    ay_norm = (double) acc_y / norm;
                    az_norm = (double) acc_z / norm;


                    Deg_pitch = -((Math.atan2(ay_norm, az_norm) * 180 / Math.PI));
                    Deg_roll = (Math.atan2(ax_norm, az_norm) * 180 / Math.PI);


                    if (Deg_roll < -180) {
                        Deg_roll += 360;
                    } else if (Deg_roll > 180) {
                        Deg_roll -= 360;
                    }

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
            auto = 0;
        }


    }

    public static void Physical_Can(int id, byte[] msg) {

            try {
                String s="ID: 0x"+Integer.toHexString(id)+"  DLC:"+msg.length+"   { "+Arrays.toString(msg)+" }";
                EventBus.getDefault().post(new CanEvents(s));
            } catch (Exception e) {

            }



        int acc_x, acc_y, acc_z;
        double norm, ax_norm, ay_norm, az_norm;

        System.out.println(String.valueOf(MyApp.visibleActivity));
        try {
            mID = id;
            msgFrame = msg;
            switch (mID) {
                case 0x7f0:
                    counter++;
                    if (len == 2 || len == 3) {
                        Pitch = PLC_DataTypes_BigEndian.byte_to_S16_be(new byte[]{msg[0], msg[1]});
                    } else if (len >= 4) {
                        Pitch = PLC_DataTypes_BigEndian.byte_to_S16_be(new byte[]{msg[0], msg[1]});
                        Roll = PLC_DataTypes_BigEndian.byte_to_S16_be(new byte[]{msg[2], msg[3]});

                    }
                    break;
                case 0x7ef:
                    auto = msg[0];
                    break;
                case 897:
                case 898:
                case 899:
                case 900:
                case 901:
                case 902:
                case 903:


                    acc_x = PLC_DataTypes_LittleEndian.byte_to_S16(new byte[]{msg[0], msg[1]});
                    acc_y = PLC_DataTypes_LittleEndian.byte_to_S16(new byte[]{msg[2], msg[3]});
                    acc_z = PLC_DataTypes_LittleEndian.byte_to_S16(new byte[]{msg[4], msg[5]});
                    norm = Math.sqrt(acc_x * acc_x + acc_y * acc_y + acc_z * acc_z);
                    ax_norm = (double) acc_x / norm;
                    ay_norm = (double) acc_y / norm;
                    az_norm = (double) acc_z / norm;


                    Deg_pitch = -((Math.atan2(ay_norm, az_norm) * 180 / Math.PI));
                    Deg_roll = (Math.atan2(ax_norm, az_norm) * 180 / Math.PI);


                    if (Deg_roll < -180) {
                        Deg_roll += 360;
                    } else if (Deg_roll > 180) {
                        Deg_roll -= 360;
                    }

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

            mID = 0;
            len = 0;
            Pitch = 0;
            Roll = 0;
            auto = 0;
        }
    }
}
