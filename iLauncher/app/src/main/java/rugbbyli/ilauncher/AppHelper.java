package rugbbyli.ilauncher;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;

import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.Gravity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private InstalledAppList installApps;

    private void getAppList(){

        List<AppListItem> list = new ArrayList<>();

        //get installed apps
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> availableActivities = manager.queryIntentActivities(intent, 0);

        //get launcher apps
        List<String> launchers = getLauncherApp();

        //get folder apps
        HashMap<String, String> folderApps = AppListDAO.GetAllFolderApps();
        List<String> folders = AppListDAO.GetAllFolders();
        List<FolderItem> folderItems = new ArrayList<>(folders.size());
        for(int i = 0;i<folders.size();i++){
            folderItems.add(new FolderItem(folders.get(i), null));
        }

        for(ResolveInfo info:availableActivities){
            String id = info.activityInfo.packageName;

            if(launchers.contains(id)) continue;

            ComponentName comopnentName = new ComponentName(info.activityInfo.applicationInfo.packageName, info.activityInfo.name);
            AppItem app = new AppItem(id, info.activityInfo.loadLabel(manager), info.activityInfo.loadIcon(manager));
            app.setStartInfo(comopnentName, 270532608);

            //对于文件夹内的app
            if(folderApps.containsKey(id)){
                String folderName = folderApps.get(id);
                //存在文件夹
                if(folders.contains(folderName)){
                    folderItems.get(folders.indexOf(folderName)).getItems().add(app);
                }
                //不存在文件夹
                else {
                    FolderItem item = new FolderItem(folderName, null);
                    item.getItems().add(app);
                    folderItems.add(item);
                }
            }
            else{
                list.add(app);
            }
        }

        //Collections.sort(installApps);

        //AppListItem add = new AppListItem("新建文件夹", context.getResources().getDrawable(R.drawable.add), AppListItemType.AddFolder);

        //add.icon.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.ADD);

        //installApps.add(add);

        //在列表顶部排序并插入文件夹
        //Collections.sort(folderItems);
        for(int i = 0;i<folderItems.size();i++){
            folderItems.get(i).refreshIcon();
            list.add(i, folderItems.get(i));
        }

        installApps = new InstalledAppList(list);
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
        return installApps.all();
    }

    public int insertApp(String packageName){
        Intent intent = getStartupIntent(packageName);
        ActivityInfo info = intent.resolveActivityInfo(manager, 0);
        AppItem appItem = new AppItem(packageName, info.loadLabel(manager), info.loadIcon(manager));
        appItem.setStartIntent(intent);

        return insertApp(appItem);
    }

    public int insertApp(AppListItem item){
        return installApps.insert(item);
    }

    private void saveMyBitmap(Bitmap bmp, String bitName) {
        File f = new File("/sdcard/FuckTest/" + bitName + ".png");

        if (!f.getParentFile().exists()) {
            if (!f.getParentFile().mkdirs()) {

            }
        }
        try {
            f.createNewFile();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Drawable getFolderIcon(boolean isOpen, List<AppListItem> children){

        int width = 300;

        NinePatchDrawable bg = (NinePatchDrawable)context.getResources().getDrawable(isOpen ? R.drawable.folder_background : R.drawable.folder_close);
        //bg.setBounds(0, 0, bg.getIntrinsicWidth(), bg.getIntrinsicHeight());
        //设置当bg绘制在canvas里面时绘制的大小和区域
        bg.setBounds(0, 0, width, width);

        //要画的图标
        Bitmap bmp = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);

        if(children != null && children.size() != 0) {
            int count = children.size();
            if (count > 9) count = 9;

            int padding = width / 20;

            int cols = (int) Math.ceil(Math.sqrt(count));
            int margin = 1;
            int rowWidth = (width - padding * 2) / cols;
            int size = rowWidth - 2 * margin;

            if (isOpen) {
                bg.draw(canvas);
            }

            for (int i = 0; i < count; i++) {
                BitmapDrawable bd = (BitmapDrawable) children.get(i).icon;

                int row = i / cols;
                int col = i % cols;
                int left = col * rowWidth + margin + padding;
                int top = row * rowWidth + margin + padding;

                bd.setBounds(0, 0, bd.getIntrinsicWidth(), bd.getIntrinsicHeight());

                canvas.drawBitmap(bd.getBitmap(), bd.getBounds(), new Rect(left, top, left + size, top + size), null);
            }

            if (!isOpen) {
                bg.draw(canvas);
            }
        }
        else {
            bg.draw(canvas);
        }

        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();

        return new BitmapDrawable(bmp);
    }

    public int dip2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public Intent getStartupIntent(String pkgName){

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setPackage(pkgName);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName comopnentName = intent.resolveActivity(manager);//mPackageManager为PackageManager实例

        //String cls = manager.getLaunchIntentForPackage(pkgName).resolveActivity(manager).getClassName();

        //ComponentName comopnentName = new ComponentName(pkgName, cls);

        intent.setComponent(comopnentName);
        intent.setFlags(270532608);

        return intent;
    }

    private static AppHelper _current;
    public static AppHelper getCurrent(){
        return _current;
    }
}
