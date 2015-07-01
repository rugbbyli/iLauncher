package rugbbyli.ilauncher.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rugbbyli.ilauncher.*;

/**
 * Created by zxq on 2015/6/29.
 */
public class AppListDAO {
    private static List<String> _folders;
    private static HashMap<String, String> _folderApps;

    public static boolean ContainsFolder(final String name){
        if(_folders != null) {
            if (_folders.contains(name)) {
                return true;
            }
        }
        return false;
    }

    public static boolean AddFolder(final FolderItem item){

        if(ContainsFolder(item.name.toString())) return false;

        boolean ret = (boolean)SafeDBOperate(new DBOperator(){
            @Override
            public Object Operate(SQLiteDatabase db){
                db.execSQL(String.format("insert into %s(%s) values (%s)", DBConfig.Table_Folder, DBConfig.Key_Folder, item.name));

                List<AppListItem> apps = item.getItems();
                for(int i = 0;i<apps.size();i++){
                    db.execSQL(String.format("insert into %s(%s,%s) values (%s,%s)", DBConfig.Table_FolderApp, DBConfig.Key_Appid, DBConfig.Key_Folder, ((AppItem)apps.get(i)).id, item.name));
                }
                return true;
            }
        });

        if(ret){
            _folders.add(item.name.toString());

            List<AppListItem> apps = item.getItems();
            for(int i = 0;i<apps.size();i++){
                _folderApps.put(((AppItem) apps.get(i)).id.toString(), item.name.toString());
            }
        }

        return ret;
    }

    public static boolean RemoveFolder(final FolderItem item){
        if(_folders != null) {
            if (!_folders.contains(item.name)) {
                return false;
            }
        }

        boolean ret = (boolean)SafeDBOperate(new DBOperator(){
            @Override
            public Object Operate(SQLiteDatabase db){
                db.execSQL(String.format("delete from %s where %s = %s", DBConfig.Table_Folder, DBConfig.Key_Folder, item.name));

                List<AppListItem> apps = item.getItems();
                for(int i = 0;i<apps.size();i++){
                    db.execSQL(String.format("delete from %s where %s = %s", DBConfig.Table_FolderApp, DBConfig.Key_Appid, ((AppItem)apps.get(i)).id));
                }
                return true;
            }
        });

        if(ret){
            _folders.remove(item.name.toString());

            List<AppListItem> apps = item.getItems();
            for(int i = 0;i<apps.size();i++){
                _folderApps.remove(((AppItem) apps.get(i)).id.toString());
            }
        }

        return ret;
    }

    public static List<String> GetAllFolders(){

        if(_folders == null) {

            _folders = (List<String>) SafeDBOperate(new DBOperator() {
                @Override
                public Object Operate(SQLiteDatabase db) {
                    final List<String> list = new ArrayList<>();
                    Cursor cursor = db.rawQuery(String.format("select * from %s", DBConfig.Table_Folder), null);
                    while (cursor.moveToNext()) {
                        list.add(cursor.getString(0));
                    }
                    cursor.close();
                    return list;
                }
            });
        }

        return _folders;
    }

    public static boolean AddFolderApp(final String appid, final FolderItem item){

        boolean ret = (boolean)SafeDBOperate(new DBOperator() {
            @Override
            public Object Operate(SQLiteDatabase db) {
                db.execSQL(String.format("insert into ?(?,?) values (?,?)", DBConfig.Table_FolderApp, DBConfig.Key_Appid, DBConfig.Key_Folder, appid, item.name));
                return true;
            }
        });

        if(ret){
            _folderApps.put(appid, item.name.toString());
        }
        return ret;
    }

    public static HashMap<String, String> GetAllFolderApps(){

        if(_folderApps == null) {
            _folderApps = (HashMap<String, String>) SafeDBOperate(new DBOperator() {
                @Override
                public Object Operate(SQLiteDatabase db) {
                    final HashMap<String, String> list = new HashMap<>();
                    Cursor cursor = db.rawQuery(String.format("select * from %s", DBConfig.Table_FolderApp), null);
                    while (cursor.moveToNext()) {
                        String folder = cursor.getString(1);
                        String appid = cursor.getString(0);

                        list.put(appid, folder);
                    }
                    cursor.close();
                    return list;
                }
            });
        }

        return _folderApps;
    }

    public static boolean RemoveFolderApp(final String appid){

        if(_folderApps != null && !_folderApps.containsKey(appid)) return false;

        boolean ret = (boolean)SafeDBOperate(new DBOperator() {
            @Override
            public Object Operate(SQLiteDatabase db) {

                db.execSQL(String.format("delete from %s where %s = %s", DBConfig.Table_FolderApp, DBConfig.Key_Appid, appid));
                return true;
            }
        });

        if(ret){
            _folderApps.remove(appid);
        }
        return ret;
    }

    private static Object SafeDBOperate(DBOperator ope){
        SQLiteDatabase db = null;
        Object ret = false;
        try{
            db = DBOpenHelper.GetInstance().getWritableDatabase();
            ret = ope.Operate(db);
        }
        finally {
            if(db != null){
                db.close();
            }
        }
        return ret;
    }

    private interface DBOperator{
        Object Operate(SQLiteDatabase db);
    }
}
