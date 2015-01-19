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

        Intent i = new Intent(Intent.ACTION_MAIN, null);

        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);

        for(ResolveInfo ri:availableActivities){

            AppDetail app = new AppDetail();

            app.name = ri.activityInfo.loadLabel(manager);
            app.id = ri.activityInfo.packageName;
            app.icon = ri.activityInfo.loadIcon(manager);

            installApps.add(app);
        }
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
