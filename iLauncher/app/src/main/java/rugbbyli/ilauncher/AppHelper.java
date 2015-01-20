package rugbbyli.ilauncher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangg on 2015/1/18.
 */
public class AppHelper {
    private Context context;
    private PackageManager manager;
    private List<AppDetail> installApps;
    private void getAppList(){
        installApps = new ArrayList<AppDetail>();

        List<CharSequence> launchers = getLauncherApp();

        Intent i = new Intent(Intent.ACTION_MAIN, null);

        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);

        for(ResolveInfo info:availableActivities){
            CharSequence id = info.activityInfo.packageName;

            boolean pass = false;
            for(CharSequence ri:launchers){
                if(ri.equals(id)){
                    pass = true;
                    break;
                }
            }
            if (pass) continue;

            AppDetail app = new AppDetail();

            app.name = info.activityInfo.loadLabel(manager);
            app.id = id;
            app.icon = info.activityInfo.loadIcon(manager);

            installApps.add(app);
        }
    }

    private List<CharSequence> getLauncherApp(){
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> ret = manager.queryIntentActivities(i, 0);

        List<CharSequence> launcher = new ArrayList<CharSequence>(ret.size());
        for(ResolveInfo info:ret){
            launcher.add(info.activityInfo.packageName);
        }

        return launcher;
    }

    public AppHelper(Context context)
    {
        _current = this;
        this.context = context;
        manager = context.getPackageManager();
        getAppList();
    }

    public List<AppDetail> getInstallApps(){
        return installApps;
    }

    private static AppHelper _current;
    public static AppHelper getCurrent(){
        return _current;
    }
}
