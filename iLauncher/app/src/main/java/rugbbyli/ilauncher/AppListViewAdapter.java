package rugbbyli.ilauncher;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by yangg on 2015/7/4.
 */
public class AppListViewAdapter extends BaseAdapter {

    private List<AppListItem> m_items;
    private Activity m_context;
    private FolderItem m_openedFolder = null;
    private int m_openFolderPosition = -1;
    private GridView m_gridView;

    public AppListViewAdapter(Activity context, GridView gridView){
        m_items = AppHelper.getCurrent().getInstallApps();
        m_context = context;
        m_gridView = gridView;
    }

    public void openFolder(int position){
        FolderItem item = (FolderItem)m_items.get(position);

        if(item == m_openedFolder) return;

        closeFolder();

        m_openedFolder = item;
        m_openedFolder.setIsOpen(true);
        m_openFolderPosition = position;
    }

    public void closeFolder(){
        if(m_openedFolder != null){
            m_openedFolder.setIsOpen(false);
            m_openFolderPosition = -1;
            m_openedFolder = null;
        }
    }

    public void toggleFolder(int position){



        if(m_openFolderPosition == position){
            closeFolder();
        }
        else {
            openFolder(position);
        }
    }

    @Override
    public int getCount() {
        return m_items.size();
    }

    @Override
    public Object getItem(int position) {
        return m_items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        AppListItem item = m_items.get(position);

        if(item.type == AppListItemType.App || item.type == AppListItemType.Folder || item.type == AppListItemType.AddFolder){
            if(convertView == null){
                convertView = m_context.getLayoutInflater().inflate(R.layout.sample_app_item, null);
            }
            ImageView appIcon = (ImageView)convertView.findViewById(R.id.icon);
            appIcon.setImageDrawable(item.icon);
            TextView appName = (TextView)convertView.findViewById(R.id.app_label);
            appName.setText(item.name);
        }
        else if(item.type == AppListItemType.FolderApp){
            if(convertView == null){
                convertView = m_context.getLayoutInflater().inflate(R.layout.list_folder_apps, null);
            }

        }

        //if(appGridViewState == AppListState.CreatingFolder){
        convertView.setBackgroundResource(m_gridView.isItemChecked(position) ? R.drawable.item_select: 0);
        //convertView.setPadding(0,0,0,0);
        //}

        //if(appGridView.getSelectedItemPosition())

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
//        if(m_openedFolder == null){
//            return AppListItemType.App.ordinal();
//        }
//        else{
//            if(position == (m_openFolderPosition / m_gridView.getNumColumns() + 1)*m_gridView.getNumColumns()){
//                return AppListItemType.FolderApp.ordinal();
//            }
//            else{
//                return AppListItemType.App.ordinal();
//            }
//        }
        if(m_items.get(position).type == AppListItemType.FolderApp){
            return AppListItemType.FolderApp.ordinal();
        }
        else {
            return AppListItemType.App.ordinal();
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

}
