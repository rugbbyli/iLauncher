package rugbbyli.ilauncher;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by zxq on 2015/7/6.
 */
public class DeviceHelper {

    public static int getSystemVersion(){
        return Build.VERSION.SDK_INT;
        //Build.VERSION_CODES.LOLLIPOP
    }

    public static void openNotificationBar(boolean setting){
        Object sbservice = App.getCurrent().getSystemService("statusbar");
        Class<?> statusbarManager = null;
        try {
            statusbarManager = Class.forName( "android.app.StatusBarManager" );

            Method showsb;
            int version = getSystemVersion();
            if(version >= Build.VERSION_CODES.LOLLIPOP && setting){
                showsb = statusbarManager.getMethod("expandSettingsPanel");
            }
            else if (version >= 17) {
                showsb = statusbarManager.getMethod("expandNotificationsPanel");
            }
            else {
                showsb = statusbarManager.getMethod("expand");
            }
            Log.w("open notification", showsb.toString());

            showsb.invoke( sbservice );
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e){
            e.printStackTrace();
        } catch (InvocationTargetException e){
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static DeviceHelper _current;
    public static DeviceHelper getCurrent(){
        return _current;
    }
    private Context m_context;
    public DeviceHelper(Context context)
    {
        _current = this;
        this.m_context = context;
    }
}
