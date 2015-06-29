package rugbbyli.ilauncher;

import android.graphics.drawable.Drawable;

import java.text.Collator;
import java.util.Locale;

/**
 * Created by zxq on 2015/6/29.
 */
public class AppListItem implements Comparable<AppListItem> {
    CharSequence name;
    Drawable icon;
    AppListItemType type;

    public AppListItem(CharSequence name, Drawable icon){
        this.name = name;
        this.icon = icon;
    }

    static final Collator collator = Collator.getInstance(Locale.CHINA);

    @Override
    public int compareTo(AppListItem appListItem) {
        return -collator.compare(appListItem.name.toString(), this.name.toString());
    }
}

