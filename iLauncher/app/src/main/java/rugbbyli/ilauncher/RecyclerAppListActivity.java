package rugbbyli.ilauncher;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import rugbbyli.ilauncher.R;
import rugbbyli.ilauncher.sql.AppListDAO;

public class RecyclerAppListActivity extends Activity implements RecyclerAppListAdapter.ItemClickListener, AppEventReceiver.AppEventListener {

    RecyclerView appListView;
    RecyclerAppListAdapter appListAdapter;

    AppListState appListState;

    CheckedItemList checkedAppList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_app_list);

        //在虚拟按键上显示menu
        try {
            getWindow().addFlags(WindowManager.LayoutParams.class.getField("FLAG_NEEDS_MENU_KEY").getInt(null));
        }
        catch (NoSuchFieldException e) {

        }
        catch (IllegalAccessException e) {

        }

        appListView = (RecyclerView)findViewById(R.id.gridViewAppList);
        StaggeredGridLayoutManager lm = new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL);
        appListView.setLayoutManager(lm);

        appListAdapter = new RecyclerAppListAdapter(this, appListView);
        appListAdapter.setOnItemClickListener(this);
        appListView.setAdapter(appListAdapter);

        appListView.setItemAnimator(new DefaultItemAnimator());

        checkedAppList = new CheckedItemList();

        appListState = AppListState.Normal;

        AppEventReceiver.getCurrent().setEventListener(this);
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
        else if(id == R.id.recycler_menu_add_folder){
            showNewFolderPop();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void hideSelf(View v){
        if(appListState == AppListState.CreatingFolder){
            if(v == null){
                hideNewFolderPop();
            }
            return;
        }

        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        overridePendingTransition(0, R.anim.zoomout);
    }

    Fragment newFolderFragment;
    private void showNewFolderPop(){
        newFolderFragment = new NewFolderFragment();
        getFragmentManager().beginTransaction().add(R.id.recycler_app_list, newFolderFragment).commit();

        RelativeLayout.LayoutParams parms = (RelativeLayout.LayoutParams)appListView.getLayoutParams();

        parms.setMargins(0, (int)getResources().getDimension(R.dimen.new_folder_fragment_height), 0, 0);
        appListView.setLayoutParams(parms);

        appListState = AppListState.CreatingFolder;
    }

    private void hideNewFolderPop(){
        getFragmentManager().beginTransaction().remove(newFolderFragment).commit();

        RelativeLayout.LayoutParams parms = (RelativeLayout.LayoutParams)appListView.getLayoutParams();
        parms.setMargins(0, 0, 0, 0);
        appListView.setLayoutParams(parms);

        checkedAppList.clear();

        newFolderFragment.getView().clearFocus();

        appListState = AppListState.Normal;
    }

    public void newFolderPop_buttonCancel_Click(View v){
        hideNewFolderPop();
    }

    public void newFolderPop_buttonOk_Click(View v){

        EditText text = (EditText)findViewById(R.id.frag_new_folder_edit_name);
        String name = text.getText().toString().trim();

        if(name.isEmpty()){
            Toast.makeText(this, Constants.str_text_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        if(AppListDAO.ContainsFolder(name)) {
            Toast.makeText(this, Constants.str_folder_exists, Toast.LENGTH_SHORT).show();
            return;
        }

        //get selected apps and make them into folder.

        appListAdapter.closeFolder();

        FolderItem folder = new FolderItem(name, null);

        for(AppListItem item : checkedAppList.getAllChecked()){
            folder.getItems().add(item);
        }

        for(AppListItem item : checkedAppList.getAllChecked()){
            AppHelper.getCurrent().getInstallApps().remove(item);
        }

        folder.refreshIcon();

        AppListDAO.AddFolder(folder);

        AppHelper.getCurrent().insertApp(folder);

        appListAdapter.notifyDataSetChanged();

        hideNewFolderPop();
    }

    @Override
    public void OnClick(View view, AppListItem item, int position) {
        switch (appListState) {
            case Normal:
            case OpenFolder:
                if (item.type == AppListItemType.App) {

                    startActivity(((AppItem) item).intent);

                } else if (item.type == AppListItemType.Folder) {

                    appListAdapter.toggleFolder(position);

                } else if (item.type == AppListItemType.AddFolder) {

                    showNewFolderPop();
                }
                break;
            case CreatingFolder:
                if (item.type == AppListItemType.App) {
                    AppListItemLayout layout = (AppListItemLayout)view;
                    if(layout != null){
                        layout.toggle();
                    }
                    else {
                        Log.e("error", "toggle item failed for layout cast error.");
                    }
                } else if (item.type == AppListItemType.Folder) {
                    appListAdapter.toggleFolder(position);
                }
                break;
        }
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
    public void install(String packageName) {

    }

    @Override
    public void uninstall(String packageName) {

    }
}
