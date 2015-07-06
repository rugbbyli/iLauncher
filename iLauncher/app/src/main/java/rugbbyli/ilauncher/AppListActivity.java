package rugbbyli.ilauncher;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
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
import android.widget.Toast;

import rugbbyli.ilauncher.sql.AppListDAO;


public class AppListActivity extends Activity implements NewFolderFragment.OnFragmentInteractionListener {

    public static boolean hasCreated = false;
    private AppListState appGridViewState;
    private AppListViewAdapter m_adapter;
    private GridView appGridView;

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
            hideSelf(null);
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

    private void updateView(){
        appGridView = (GridView)findViewById(R.id.gridViewAppList);

        m_adapter = new AppListViewAdapter(this, appGridView);

        appGridView.setAdapter(m_adapter);

        this.appGridViewState = AppListState.Normal;
    }

    private void addClickListener(){

        appGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override

            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                AppListItem item = AppHelper.getCurrent().getInstallApps().get(pos);

                switch (appGridViewState){
                    case Normal:
                    case OpenFolder:
                        if (item.type == AppListItemType.App) {
                            String name = ((AppItem) (item)).id.toString();

                            boolean isChecked = appGridView.isItemChecked(pos);

                            appGridView.clearChoices();

                            startActivity(((AppItem) item).intent);

                        } else if(item.type == AppListItemType.Folder) {

                            m_adapter.toggleFolder(pos);
                            m_adapter.notifyDataSetChanged();
                        }
                        else if(item.type == AppListItemType.AddFolder){
                            showNewFolderPop();
                        }
                        break;
                    case CreatingFolder:
                        if(item.type == AppListItemType.App) {
                            Log.w("item is checked:", Boolean.toString(appGridView.isItemChecked(pos)));
                            m_adapter.notifyDataSetChanged();
                        }
                        else{
                            appGridView.setItemChecked(pos, false);
                        }
                        break;
                }

//                if(appGridViewState == AppListState.CreatingFolder){
//
//                    //((AppListItemLayout)v).toggle();
//                    Log.w("item is checked:", Boolean.toString(appGridView.isItemChecked(pos)));
//                    //Log.w("item is checked:", Boolean.toString(((AppListItemLayout)v).isChecked()));
//                    //Log.w("change item state", "--------------------");
//                    //appGridView.setItemChecked(pos, !appGridView.isItemChecked(pos));
//                    //v.setSelected(!v.isSelected());
//                    //appGridView.refreshDrawableState();
//                    m_adapter.notifyDataSetChanged();
//                }
//
//                else if(appGridViewState == AppListState.Normal) {
//                    if (item.type == AppListItemType.App) {
//                        String name = ((AppItem) (item)).id.toString();
//
//                        boolean isChecked = appGridView.isItemChecked(pos);
//
//                        appGridView.clearChoices();
//
//                        startActivity(((AppItem) item).intent);
//
//                    } else if(item.type == AppListItemType.Folder) {
//
//                        m_adapter.toggleFolder(pos);
//                        m_adapter.notifyDataSetChanged();
//                    }
//                }
//                else if(appGridViewState == AppListState.OpenFolder){
//                    showNewFolderPop();
//                }
            }
        });

    }

    Fragment newFolderFragment;
    private void showNewFolderPop(){
        newFolderFragment = new NewFolderFragment();
        getFragmentManager().beginTransaction().add(R.id.applist_layout, newFolderFragment).commit();

        FrameLayout.LayoutParams parms = (FrameLayout.LayoutParams)appGridView.getLayoutParams();
        parms.setMargins(0, AppHelper.getCurrent().dip2px(60), 0, 0);
        appGridView.setLayoutParams(parms);

        appGridView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        appGridView.clearChoices();

        appGridViewState = AppListState.CreatingFolder;
    }

    private void hideNewFolderPop(){
        getFragmentManager().beginTransaction().remove(newFolderFragment).commit();

        FrameLayout.LayoutParams parms = (FrameLayout.LayoutParams)appGridView.getLayoutParams();
        parms.setMargins(0, 0, 0, 0);
        appGridView.setLayoutParams(parms);

        appGridView.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
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
            Toast.makeText(this, Constants.str_text_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        if(AppListDAO.ContainsFolder(name)) {
            Toast.makeText(this, Constants.str_folder_exists, Toast.LENGTH_SHORT).show();
            return;
        }

        SparseBooleanArray positions = appGridView.getCheckedItemPositions();

        FolderItem folder = new FolderItem(name, null);

        for(int i = 0;i<positions.size();i++){
            int pos = positions.keyAt(i);
            AppListItem item = AppHelper.getCurrent().getInstallApps().get(pos);

            folder.getItems().add(item);
            AppHelper.getCurrent().getInstallApps().remove(pos);
        }

        folder.refreshIcon();

        AppListDAO.AddFolder(folder);

        AppHelper.getCurrent().getInstallApps().add(0, folder);

        m_adapter.notifyDataSetChanged();

        hideNewFolderPop();
    }

    public void hideSelf(View v){
        if(appGridViewState == AppListState.CreatingFolder){
            if(v == null){
                hideNewFolderPop();
            }
            return;
        }

        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        overridePendingTransition(0, R.anim.zoomout);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
