package rugbbyli.ilauncher;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class AppList extends Activity implements NewFolderFragment.OnFragmentInteractionListener {

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

                if(appGridView.getChoiceMode() == AbsListView.CHOICE_MODE_MULTIPLE){
                    if(appGridView.isItemChecked(position)){
                        convertView.setBackgroundResource(R.drawable.item_select);
                    }
                    else{
                        convertView.setBackgroundResource(0);
                    }
                }

                ImageView appIcon = (ImageView)convertView.findViewById(R.id.icon);
                appIcon.setImageDrawable(AppHelper.getCurrent().getInstallApps().get(position).icon);
                TextView appName = (TextView)convertView.findViewById(R.id.app_label);
                appName.setText(AppHelper.getCurrent().getInstallApps().get(position).name);

                //if(appGridView.getSelectedItemPosition())

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

                boolean isChecked = appGridView.isItemChecked(pos);

                appGridView.clearChoices();

                AppList.this.startActivity(i);
            }

        });

    }

    private boolean catchItemClick(String id){
        if(id.equals(Constants.id_new_folder)){
            showNewFolderPop();
            return true;
        }
        if(appGridView.getChoiceMode() == AbsListView.CHOICE_MODE_MULTIPLE){
            return true;
        }
        return false;
    }

    Fragment newFolderFragment;
    private void showNewFolderPop(){
        newFolderFragment = new NewFolderFragment();
        getFragmentManager().beginTransaction().add(R.id.applist_layout, newFolderFragment).commit();

        FrameLayout.LayoutParams parms = (FrameLayout.LayoutParams)appGridView.getLayoutParams();
        parms.setMargins(0,dip2px(70),0,0);
        appGridView.setLayoutParams(parms);

        appGridView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
    }

    private void hideNewFolderPop(){
        getFragmentManager().beginTransaction().remove(newFolderFragment).commit();

        FrameLayout.LayoutParams parms = (FrameLayout.LayoutParams)appGridView.getLayoutParams();
        parms.setMargins(0,0,0,0);
        appGridView.setLayoutParams(parms);

        appGridView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        appGridView.clearChoices();
    }

    private int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void newFolderPop_buttonCancel_Click(View v){
        hideNewFolderPop();
    }

    public void newFolderPop_buttonOk_Click(View v){

        EditText text = (EditText)findViewById(R.id.editText_name);
        String name = text.getText().toString();

        if(name.isEmpty() || name.trim().isEmpty()){
            return;
        }


        hideNewFolderPop();
    }

    public void closeAppList(View v){
        this.finish();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
