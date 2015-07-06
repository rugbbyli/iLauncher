package rugbbyli.ilauncher;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by zxq on 2015/7/6.
 */
public class GestureListener implements GestureDetector.OnGestureListener {
    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        int FLING_MIN_DISTANCE = 10;
        int FLING_MIN_VELOCITY = 5;
        if (e1.getY() - e2.getY() > FLING_MIN_DISTANCE
                && Math.abs(velocityY) > FLING_MIN_VELOCITY) {
            //flip up
            DeviceHelper.openNotificationBar(false);
        } else
        if (e2.getY() - e1.getY() > FLING_MIN_DISTANCE
                && Math.abs(velocityY) > FLING_MIN_VELOCITY) {
            //flip down
            DeviceHelper.openNotificationBar(true);
        }

        return false;
    }
}
