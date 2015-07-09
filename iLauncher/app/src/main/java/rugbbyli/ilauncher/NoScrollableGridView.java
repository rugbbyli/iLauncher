package rugbbyli.ilauncher;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by zxq on 2015/7/9.
 */
public class NoScrollableGridView extends GridView {

    public NoScrollableGridView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, mExpandSpec);
    }
}

