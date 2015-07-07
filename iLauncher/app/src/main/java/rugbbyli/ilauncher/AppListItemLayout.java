package rugbbyli.ilauncher;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Checkable;
import android.widget.RelativeLayout;

/**
 * Created by yangg on 2015/7/2.
 */
public class AppListItemLayout extends RelativeLayout implements Checkable {
    public AppListItemLayout(Context context) {
        super(context);
    }

    public AppListItemLayout(Context context, AttributeSet attr){
        super(context, attr);
    }

    @Override
    public void setChecked(boolean checked) {
        if(m_isChecked != checked){
            m_isChecked = checked;
            refreshDrawableState();
            //Log.w("item state:" ,"" + checked);
        }
    }

    @Override
    public boolean isChecked() {
        return m_isChecked;
    }

    @Override
    public void toggle() {
        setChecked(!m_isChecked);
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (m_isChecked) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        Log.w("item drawable", "onCreateDrawableState");
        return drawableState;
    }

    private static final int[] CHECKED_STATE_SET = {
            android.R.attr.state_checked
    };

    private boolean m_isChecked;
}
