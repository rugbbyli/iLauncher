package rugbbyli.ilauncher.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import rugbbyli.ilauncher.*;

/**
 * Created by zxq on 2015/6/29.
 */
public class AppListDAO {
    public static void AddFolder(FolderItem item){
        SafeDBOperate(new DBOperator(){
            @Override
            public Object Operate(SQLiteDatabase db){
                final boolean ret = false;

                return ret;
            }
        });
    }


    public static List<String> GetAllFolders(){

        return (List<String>)SafeDBOperate(new DBOperator() {
            @Override
            public Object Operate(SQLiteDatabase db) {
                final List<String> list = new ArrayList<>();
                Cursor cursor = db.rawQuery("select * from AppListDics", null);
                while (cursor.moveToNext()) {
                    list.add(cursor.getString(0));
                }
                cursor.close();
                return list;
            }
        });
    }

    public static HashMap<String, String> GetAllFolderApps(){

        return (HashMap<String, String>)SafeDBOperate(new DBOperator() {
            @Override
            public Object Operate(SQLiteDatabase db) {
                final HashMap<String, String> list = new HashMap<>();
                Cursor cursor = db.rawQuery("select * from AppListDicApps", null);
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

    public boolean RemoveFolderApp(final String appid){
        return (boolean)SafeDBOperate(new DBOperator() {
            @Override
            public Object Operate(SQLiteDatabase db) {
                Object[] parms = {appid};
                db.execSQL("delete from AppListDicApps where appid = ?", parms);
                return true;
            }
        });
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
