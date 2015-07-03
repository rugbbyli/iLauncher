package rugbbyli.ilauncher;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.StateSet;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import rugbbyli.ilauncher.sql.AppListDAO;


public class AppListActivity extends Activity implements NewFolderFragment.OnFragmentInteractionListener {

    public static boolean hasCreated = false;
    private AppListState appGridViewState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);
        //在虚拟按键上显示menu
        try {
            getWindow().addFlags(WindowManager.LayoutParams.class.getField("FLAG_NEEDS_MENU_KEY").getInt(null));
        }
        catch (NoSuchFieldException e) {

        }
        catch (IllegalAccessException e) {

        }
        updateView();
        addClickListener();


        hasCreated = true;
    }

    @Override
    protected void onDestroy() {
        hasCreated = false;
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            closeAppList(null);
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
        // automatically handle clicks on the HomeActivity/Up button, so long
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

        ArrayAdapter<AppListItem> adapter = new ArrayAdapter<AppListItem>(this,

                R.layout.sample_app_item,

                AppHelper.getCurrent().getInstallApps()) {

            @Override

            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.sample_app_item, null);
                }

                if(appGridView.getChoiceMode() == AbsListView.CHOICE_MODE_MULTIPLE){
                    if(appGridView.isItemChecked(position)){
                        //convertView.setBackgroundResource(R.drawable.item_select);
                    }
                    else{
                        //convertView.setBackgroundResource(0);
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

        this.appGridViewState = AppListState.Normal;
    }

    private void addClickListener(){

        appGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override

            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                AppListItem item = AppHelper.getCurrent().getInstallApps().get(pos);

                if(appGridViewState == AppListState.CreatingFolder){
                    //((AppListItemLayout)v).toggle();
                    //Log.w("item is checked:", Boolean.toString(appGridView.isItemChecked(pos)));
                    //Log.w("item is checked:", Boolean.toString(((AppListItemLayout)v).isChecked()));
                    //Log.w("change item state", "--------------------");
                    //appGridView.setItemChecked(pos, !appGridView.isItemChecked(pos));
                    v.setSelected(!v.isSelected());
                }

                else if(appGridViewState == AppListState.Normal) {
                    if (item.type == AppListItemType.App) {
                        String name = ((AppItem) (item)).id.toString();

                        if (catchItemClick(name)) return;


                        boolean isChecked = appGridView.isItemChecked(pos);

                        appGridView.clearChoices();

                        startActivity(((AppItem) item).intent);
                        //AppHelper.getCurrent().StartAppWithPackageName(name);
                    } else {

                    }
                }
            }

        });

    }

    private boolean catchItemClick(String id){
        if(id.equals(Constants.id_new_folder)){
            showNewFolderPop();
            return true;
        }
        if(appGridViewState == AppListState.CreatingFolder){
            return true;
        }
        return false;
    }

    Fragment newFolderFragment;
    private void showNewFolderPop(){
        newFolderFragment = new NewFolderFragment();
        getFragmentManager().beginTransaction().add(R.id.applist_layout, newFolderFragment).commit();

        FrameLayout.LayoutParams parms = (FrameLayout.LayoutParams)appGridView.getLayoutParams();
        parms.setMargins(0, AppHelper.getCurrent().dip2px(70), 0, 0);
        appGridView.setLayoutParams(parms);

        appGridView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        appGridViewState = AppListState.CreatingFolder;
    }

    private void hideNewFolderPop(){
        getFragmentManager().beginTransaction().remove(newFolderFragment).commit();

        FrameLayout.LayoutParams parms = (FrameLayout.LayoutParams)appGridView.getLayoutParams();
        parms.setMargins(0, 0, 0, 0);
        appGridView.setLayoutParams(parms);

        appGridView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        appGridView.clearChoices();
        appGridViewState = AppListState.Normal;
    }

    public void newFolderPop_buttonCancel_Click(View v){
        hideNewFolderPop();
    }

    public void newFolderPop_buttonOk_Click(View v){

        EditText text = (EditText)findViewById(R.id.editText_name);
        String name = text.getText().toString().trim();

        if(name.isEmpty()){
            return;
        }

        if(AppListDAO.ContainsFolder(name)) return;



        hideNewFolderPop();
    }

    public void closeAppList(View v){
        Intent i = new Intent(this, HomeActivity.class);

        startActivity(i);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
