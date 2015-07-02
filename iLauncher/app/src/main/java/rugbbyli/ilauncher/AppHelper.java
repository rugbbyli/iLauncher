package rugbbyli.ilauncher;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
                installApps.add(app);
            }
        }

        Collections.sort(installApps);

        AppItem add = new AppItem(Constants.id_new_folder, "新建文件夹", context.getResources().getDrawable(R.drawable.add));

        //add.icon.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.ADD);

        installApps.add(add);

        //在列表顶部排序并插入文件夹
        Collections.sort(folderItems);
        for(int i = 0;i<folderItems.size();i++){
            installApps.add(i, setFolderIcon(folderItems.get(i)));
        }

//        boolean open = false;
//        List<AppListItem> testitems = new ArrayList<>();
//        for(int i = 0;i<12;i++) {
//            testitems.add(installApps.get(i));
//            Drawable d1 = getFolderIcon(open, testitems);
//            FolderItem item = new FolderItem("文件夹" + (i+1), d1);
//            installApps.add(item);
//            open = !open;
//        }
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

    public Drawable getFolderIcon2(boolean isOpen, List<AppListItem> children){
        int count = children.size();
        if(count > 9) count = 9;

        int width = dip2px(400);
        int height = dip2px(400);
        int cols = (int)Math.ceil(Math.sqrt(count));
        int margin = 0;
        int size = (width / cols) - 2*margin;

        if(children.size() == 4)
            Log.w("icon message:", String.format("%d, %d, %d, %d, %d", width, count, cols, margin, size));

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Drawable bg = context.getDrawable(isOpen ? R.drawable.folder_open : R.drawable.folder_close);
        bg.setBounds(0,0,width,height);
        bg.draw(canvas);

        for(int i = 0;i<count;i++){
            Drawable icon = children.get(i).icon;

            int c = i%cols;
            int r = i/cols;

            int left = c*(size + margin*2) + margin;
            int right = width - left - size;
            int top = r*(size + margin*2) + margin;
            int bottom = height - top - size;

            icon.setBounds(left, top, size, size);
            icon.draw(canvas);

            if(children.size() == 4) Log.w("icon position:", String.format("name: %s, left:%d, top:%d, right:%d, bottom:%d", children.get(i).name, left, top, right, bottom));

        }

        canvas.save(Canvas.ALL_SAVE_FLAG);

        saveMyBitmap(bitmap, "fuck");

        BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
        bitmapDrawable.setBounds(0, 0, width, height);

        return bitmapDrawable;
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

        BitmapDrawable bg = (BitmapDrawable)context.getResources().getDrawable(isOpen ? R.drawable.folder_open : R.drawable.folder_close);

        if(children == null || children.size() == 0)
            return bg;

        int count = children.size();
        if(count > 9) count = 9;

        int padding = 20;
        int width = 200;
        int cols = (int)Math.ceil(Math.sqrt(count));
        int margin = 20 / cols;
        int rowWidth = (width - padding*2) / cols;
        int size = rowWidth - 2*margin;

        Bitmap bmp = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);

        bg.setBounds(0, 0, bg.getIntrinsicWidth(), bg.getIntrinsicHeight());

        if(isOpen){
            canvas.drawBitmap(bg.getBitmap(), bg.getBounds(), new Rect(0, 0, width, width), null);
        }

        for(int i = 0;i<count;i++){
            BitmapDrawable bd = (BitmapDrawable)children.get(i).icon;

            int row = i / cols;
            int col = i % cols;
            int left = col * rowWidth + margin + padding;
            int top = row * rowWidth + margin + padding;
            //int right = width - left - size;
            //int bottom = width - top - size;
            bd.setBounds(0, 0, bd.getIntrinsicWidth(), bd.getIntrinsicHeight());

            canvas.drawBitmap(bd.getBitmap(), bd.getBounds(), new Rect(left, top, left + size, top + size), null);
        }

        if(!isOpen){
            canvas.drawBitmap(bg.getBitmap(), bg.getBounds(), new Rect(0, 0, width, width), null);
        }

//        LayerDrawable layer = new LayerDrawable(icons);
//
//        layer.setLayerInset(0, 0, 0, 0, 0);
//
//        for(int i = 0;i<count;i++){
//            int row = i / cols;
//            int col = i % cols;
//            int left = col * size + padding;
//            int top = row * size + padding;
//            int right = width - left - size;
//            int bottom = width - top - size;
//
//            layer.setLayerInset(i + 1, left, top, right, bottom);
//
//            Log.w("icon location:", String.format("%s, %d, %d, %d, %d, %d", children.get(i).name, size, left, top, right, bottom));
//        }
//
//        return  layer;



//        Paint paint = new Paint();
//        paint.setColor(Color.WHITE);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth(40);
//        canvas.drawRoundRect(0, 0, width, width, 100, 100, paint);
        //canvas.drawBitmap(((NinePatchDrawable)(icons[0]))., icons[0].getBounds(), new Rect(0, 0, width, width), null);

//        for(int i = 0;i<count;i++){
//            int row = i / cols;
//            int col = i % cols;
//            int left = col * size + padding;
//            int top = row * size + padding;
//            int right = width - left - size;
//            int bottom = width - top - size;
//
//            canvas.drawBitmap(((BitmapDrawable)(icons[i+1])).getBitmap(), icons[i+1].getBounds(), new Rect(left, top, left + size, top + size), null);
//        }
//
//        icons[0].draw(canvas);

        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();

        return new BitmapDrawable(bmp);
    }

    public FolderItem setFolderIcon(FolderItem item){
        item.icon = getFolderIcon(item.isOpen, item.getItems());
        return item;
    }

    public void StartAppWithPackageName(String pkgName){

            Intent i = context.getPackageManager().getLaunchIntentForPackage(pkgName);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            i.setAction(Intent.ACTION_MAIN);
            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            i.setFlags(270532608);
            context.getApplicationContext().startActivity(i);

    }

    public void doStartApplicationWithPackageName(String packagename) {

        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = context.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);

            intent.setComponent(cn);
            context.startActivity(intent);
        }
    }

    public int dip2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private static AppHelper _current;
    public static AppHelper getCurrent(){
        return _current;
    }
}
