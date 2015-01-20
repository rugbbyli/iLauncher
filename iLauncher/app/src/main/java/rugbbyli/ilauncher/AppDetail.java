package rugbbyli.ilauncher;

import android.graphics.drawable.Drawable;

import java.text.Collator;
import java.lang.Comparable;
import java.util.Locale;

/**
 * Created by yangg on 2015/1/18.
 */
public class AppDetail implements Comparable<AppDetail> {
    CharSequence name;
    Drawable icon;
    CharSequence id;
    static final Collator collator = Collator.getInstance(Locale.CHINA);

    @Override
    public int compareTo(AppDetail appDetail) {
        return -collator.compare(appDetail.name.toString(), name.toString());
    }
}
