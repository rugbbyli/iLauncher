package rugbbyli.ilauncher;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import rugbbyli.ilauncher.R;

public class RecyclerAppListActivity extends Activity {

    RecyclerView appListView;
    RecyclerAppListAdapter appListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_app_list);

        appListView = (RecyclerView)findViewById(R.id.gridViewAppList);
        StaggeredGridLayoutManager lm = new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL);
        appListView.setLayoutManager(lm);

        appListAdapter = new RecyclerAppListAdapter(this, appListView);
        appListView.setAdapter(appListAdapter);

        appListView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recycler_app_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
