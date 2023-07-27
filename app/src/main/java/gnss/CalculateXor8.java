package gnss;


/***************************************************************
 *
 * Class to Calculate XOR 8bit
 * returns an int "xor"
 * passing a bytw Array
 *
 *
 *****************************************************************/


public class CalculateXor8 {

   public  int xor;



    public CalculateXor8(byte[] str) {
        StringBuilder sb = new StringBuilder();
        for (byte b : str) {
            sb.append(String.format("%02X ", b));
        }
        String str1=sb.toString();

        String[] arr = str1.split(" ");

        for (int i = 0; i < arr.length; i++)
            xor ^= Integer.parseInt(arr[i], 16);








        }
}

