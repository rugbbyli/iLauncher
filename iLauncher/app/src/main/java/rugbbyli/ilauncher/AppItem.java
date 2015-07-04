package rugbbyli.ilauncher;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import java.text.Collator;
import java.lang.Comparable;
import java.util.Locale;

/**
 * Created by yangg on 2015/1/18.
 */
public class AppItem extends AppListItem {

    public CharSequence id;

    public Intent intent;

    public AppItem(CharSequence id, CharSequence name, Drawable icon){
        super(name, icon, AppListItemType.App);
        this.id = id;
    }

    public void setStartInfo(ComponentName componentName, int flag){
        this.intent = new Intent("android.intent.action.MAIN");
        this.intent.addCategory("android.intent.category.LAUNCHER");
        this.intent.setComponent(componentName);
        this.intent.setFlags(flag);
    }
}
