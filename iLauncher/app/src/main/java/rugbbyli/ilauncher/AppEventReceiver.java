package rugbbyli.ilauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by zxq on 2015/7/3.
 */
public class AppEventReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {  // install
            String packageName = intent.getDataString();


        }

        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) { // uninstall
            String packageName = intent.getDataString();


        }
    }
}
