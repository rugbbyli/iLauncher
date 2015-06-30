package rugbbyli.ilauncher.sql;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yangg on 2015/6/22.
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    public DBOpenHelper(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String appListDics = "AppListDics";
        String sql = String.format("create table %s(dic varchar(64) primary key)", appListDics);
        db.execSQL(sql);

        String appListDicApps = "AppListDicApps";
        sql = String.format("create table %s(appid varchar(128) primary key,dic varchar(64))", appListDicApps);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private static final String name = "database.db"; //表示数据库的名称
    private static final int version = 1; //表示数据库的版本号

    private static DBOpenHelper instance;
    public static void Init(Application app){
        instance = new DBOpenHelper(app.getApplicationContext());
    }
    public static DBOpenHelper GetInstance(){
        return instance;
    }
}
