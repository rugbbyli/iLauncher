package rugbbyli.ilauncher;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import rugbbyli.ilauncher.sql.AppListDAO;

/**
 * Created by yangg on 2015/1/18.
 */
public class AppHelper {
    private Context context;
    private PackageManager manager;
    private List<AppListItem> installApps;

    private void getAppList(){
        installApps = new ArrayList<>();


        Intent intent = new Intent(Intent.ACTION_MAIN, null);

        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = manager.queryIntentActivities(intent, 0);

        List<String> launchers = getLauncherApp();
        HashMap<String, String> folderApps = AppListDAO.GetAllFolderApps();
        List<String> folders = AppListDAO.GetAllFolders();

        List<FolderItem> folderItems = new ArrayList<>(folders.size());

        Drawable folderIcon = context.getResources().getDrawable(R.drawable.add);
        for(int i = 0;i<folders.size();i++){
            folderItems.add(new FolderItem(folders.get(i), folderIcon));
        }

        for(ResolveInfo info:availableActivities){
            String id = info.activityInfo.packageName;

            if(launchers.contains(id)) continue;

            AppItem app = new AppItem(id, info.activityInfo.loadLabel(manager), info.activityInfo.loadIcon(manager));

            //对于文件夹内的app
            if(folderApps.containsKey(id)){
                String folderName = folderApps.get(id);
                if(folders.contains(folderName)){
                    folderItems.get(folders.indexOf(folderName)).getItems().add(app);
                }
                else {
                    FolderItem item = new FolderItem(folderName, folderIcon);
                    item.getItems().add(app);
                    folderItems.add(item);
                }
            }
            else{
                installApps.add(app);
            }



        }

        Collections.sort(installApps);

        AppItem add = new AppItem(Constants.id_new_folder, "新建文件夹", context.getResources().getDrawable(R.drawable.add));

        add.icon.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.ADD);

        installApps.add(add);
    }

    private List<String> getLauncherApp(){
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> ret = manager.queryIntentActivities(i, 0);

        List<String> launcher = new ArrayList<>(ret.size());
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

    public List<AppListItem> getInstallApps(){
        return installApps;
    }

    private static AppHelper _current;
    public static AppHelper getCurrent(){
        return _current;
    }
}
