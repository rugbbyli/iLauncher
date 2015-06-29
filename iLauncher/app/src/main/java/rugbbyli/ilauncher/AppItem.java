package rugbbyli.ilauncher;

import android.graphics.drawable.Drawable;

import java.text.Collator;
import java.lang.Comparable;
import java.util.Locale;

/**
 * Created by yangg on 2015/1/18.
 */
public class AppItem extends AppListItem {

    CharSequence id;

    public AppItem(CharSequence id, CharSequence name, Drawable icon){
        super(name, icon);
        this.id = id;
        type = AppListItemType.App;
    }
}
