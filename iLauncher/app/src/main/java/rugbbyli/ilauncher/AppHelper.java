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

        boolean open = false;
        List<AppListItem> testitems = new ArrayList<>();
        for(int i = 0;i<12;i++) {
            testitems.add(installApps.get(i));
            Drawable d1 = getFolderIcon2(open, testitems);
            FolderItem item = new FolderItem("文件夹" + (i+1), d1);
            installApps.add(item);
            open = !open;
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
