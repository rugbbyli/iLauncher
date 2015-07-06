package rugbbyli.ilauncher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;


public class HomeActivity extends Activity implements GestureListener.IGestureListener {

    private GestureDetector m_gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        new AppHelper(this);

        m_gestureDetector = new GestureDetector(this, new GestureListener(this));
        findViewById(R.id.shortcutList_Home).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return m_gestureDetector.onTouchEvent(motionEvent);
            }
        });

        ShortcutHelper.getAllShortcuts();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the HomeActivity/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(KeyEvent.KEYCODE_BACK==keyCode)
            return true ;
        return super.onKeyDown(keyCode, event);
    }

    public void onAppListButtonClick(View v){

        openApplistActivity();
    }

    private void openApplistActivity(){
        Intent i = new Intent(this, AppListActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.zoomin, 0);
    }

    @Override
    public void GestureEvent(GestureListener.GestureType type) {
        switch (type){
            case FlipDown:
                DeviceHelper.openNotificationBar(true);
                break;
            case FlipUp:
                openApplistActivity();
                break;
        }
    }
}
