package rugbbyli.ilauncher;

import android.content.Context;
import android.content.Intent;
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

        //fixme: test code
        List<AppListItem> list = new ArrayList<>();
        for(int i = 0;i<12;i++){
            list.add(installApps.get(i));

            FolderItem item = new FolderItem("测试文件夹" + (i+1), getFolderIcon(list));
            installApps.add(item);
        }
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

    public Drawable getFolderIcon(List<AppListItem> children){
        int count = children.size();
        if(count > 9) count = 9;

        int padding = 40;
        int width = 144*3 + padding*2;
        int cols = (int)Math.ceil(Math.sqrt(count));
        int size = (width - padding*2) / cols;

        Drawable[] icons = new Drawable[count + 1];
        icons[0] = context.getResources().getDrawable(R.drawable.folder_close);
        icons[0].setBounds(0, 0, width, width);

        for(int i = 0;i<count;i++){
            BitmapDrawable bd = (BitmapDrawable)children.get(i).icon;
            icons[i + 1] = bd;
            bd.setBounds(0, 0, bd.getIntrinsicWidth(), bd.getIntrinsicHeight());
            //bd.setGravity(Gravity.LEFT);
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

        Bitmap bmp = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);

//        Paint paint = new Paint();
//        paint.setColor(Color.WHITE);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth(40);
//        canvas.drawRoundRect(0, 0, width, width, 100, 100, paint);
        //canvas.drawBitmap(((NinePatchDrawable)(icons[0]))., icons[0].getBounds(), new Rect(0, 0, width, width), null);

        for(int i = 0;i<count;i++){
            int row = i / cols;
            int col = i % cols;
            int left = col * size + padding;
            int top = row * size + padding;
            int right = width - left - size;
            int bottom = width - top - size;

            canvas.drawBitmap(((BitmapDrawable)(icons[i+1])).getBitmap(), icons[i+1].getBounds(), new Rect(left, top, left + size, top + size), null);
        }

        icons[0].draw(canvas);

        canvas.save(Canvas.ALL_SAVE_FLAG);
        return new BitmapDrawable(bmp);
    }

    private int dip2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private static AppHelper _current;
    public static AppHelper getCurrent(){
        return _current;
    }
}
