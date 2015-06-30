package rugbbyli.ilauncher;

import android.app.Application;

import rugbbyli.ilauncher.sql.DBOpenHelper;

/**
 * Created by yangg on 2015/6/30.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DBOpenHelper.Init(this);
    }
}
