package activity_portrait;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class StartActivityOnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
            if(Build.DEVICE.equals("UT56")){

                Intent star = new Intent(context, LaunchScreenActivity.class);
                star.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(star);
            } else {
                Intent star = new Intent(context, LaunchScreenActivity.class);
                star.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(star);
            }

        }
    }


}
