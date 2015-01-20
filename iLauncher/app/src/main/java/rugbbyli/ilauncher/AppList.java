package rugbbyli.ilauncher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class AppList extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);
        updateView();
        addClickListener();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_app_list, menu);
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

    private GridView appGridView;
    private void updateView(){
        appGridView = (GridView)findViewById(R.id.gridViewAppList);

        ArrayAdapter<AppDetail> adapter = new ArrayAdapter<AppDetail>(this,

                R.layout.sample_app_item,

                AppHelper.getCurrent().getInstallApps()) {

            @Override

            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.sample_app_item, null);
                }

                ImageView appIcon = (ImageView)convertView.findViewById(R.id.icon);
                appIcon.setImageDrawable(AppHelper.getCurrent().getInstallApps().get(position).icon);
                TextView appName = (TextView)convertView.findViewById(R.id.app_label);
                appName.setText(AppHelper.getCurrent().getInstallApps().get(position).name);

                return convertView;
            }

        };

        appGridView.setAdapter(adapter);
    }

    private void addClickListener(){

        appGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override

            public void onItemClick(AdapterView<?> av, View v, int pos,

                                    long id) {
                String name = AppHelper.getCurrent().getInstallApps().get(pos).id.toString();

                if(catchItemClick(name)) return;

                Intent i = getPackageManager().getLaunchIntentForPackage(name);

                AppList.this.startActivity(i);

            }

        });

    }

    private boolean catchItemClick(String id){
        if(id.equals(Constants.id_new_folder)){
            return true;
        }
        return false;
    }

    private void showNewFolderPop(){

    }

    public void closeAppList(View v){
        this.finish();
    }
}
