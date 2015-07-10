package rugbbyli.ilauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by zxq on 2015/7/3.
 */
public class AppEventReceiver extends BroadcastReceiver {

    private static AppEventReceiver current;
    public static AppEventReceiver getCurrent(){
        return current;
    }

    public AppEventReceiver(){
        current = this;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {  // install
            String packageName = intent.getDataString();

            if(listener != null){
                listener.install(packageName);
            }
        }

        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) { // uninstall
            String packageName = intent.getDataString();

            if(listener != null){
                listener.uninstall(packageName);
            }
        }


    }

    private AppEventListener listener;
    public void setEventListener(AppEventListener listener){
        this.listener = listener;
    }


    public interface AppEventListener{
        void install(String packageName);
        void uninstall(String packageName);
    }
}
