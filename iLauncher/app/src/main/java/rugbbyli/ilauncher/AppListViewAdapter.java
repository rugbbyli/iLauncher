package rugbbyli.ilauncher;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
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
    private GridView m_openedFolderGridView;

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

        int index = (m_openFolderPosition / m_gridView.getNumColumns() + 1) * m_gridView.getNumColumns();
        AppListItem folderApp = new AppListItem(null, null, AppListItemType.FolderAppList);
        m_items.add(index, folderApp);
    }

    public void closeFolder(){
        if(m_openedFolder != null){
            m_openedFolder.setIsOpen(false);


            int index = (m_openFolderPosition / m_gridView.getNumColumns() + 1) * m_gridView.getNumColumns();
            m_items.remove(index);

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

        final AppListItem item = m_items.get(position);

        if(item.type == AppListItemType.App || item.type == AppListItemType.Folder || item.type == AppListItemType.AddFolder){
            if(convertView == null){
                convertView = m_context.getLayoutInflater().inflate(R.layout.sample_app_item, null);
            }
            ImageView appIcon = (ImageView)convertView.findViewById(R.id.icon);
            appIcon.setImageDrawable(item.icon);
            TextView appName = (TextView)convertView.findViewById(R.id.app_label);
            appName.setText(item.name);
        }
        else if(item.type == AppListItemType.FolderAppList){
            if(convertView == null){
                convertView = m_context.getLayoutInflater().inflate(R.layout.list_folder_apps, null);
                FrameLayout.LayoutParams parms = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                parms.width = m_context.getResources().getDisplayMetrics().widthPixels;
                parms.height = ActionBar.LayoutParams.WRAP_CONTENT;
                convertView.setLayoutParams(parms);
            }

            m_openedFolderGridView = (GridView)convertView;

            initFolderAppList(m_openedFolderGridView);
        }

        //if(appGridViewState == AppListState.CreatingFolder){
        //convertView.setBackgroundResource(m_gridView.isItemChecked(position) ? R.drawable.item_select: 0);
        convertView.setBackground(getItemBackground(m_gridView.isItemChecked(position)));
        //convertView.setPadding(0,0,0,0);
        //}

        //if(appGridView.getSelectedItemPosition())

        return convertView;
    }

    private Drawable[] m_itemBackgrounds = null;
    private Drawable getItemBackground(boolean isChecked){
        if(m_itemBackgrounds == null){
            Drawable d = m_context.getDrawable(R.drawable.item_select);

            Bitmap bmp = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);
            d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            d.draw(canvas);

            BitmapDrawable newBackground = new BitmapDrawable(m_context.getResources(), bmp) {
                @Override
                public int getMinimumWidth() {
                    return 0;
                }

                @Override
                public int getMinimumHeight() {
                    return 0;
                }
            };

            //newBackground.setTileModeXY(Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

            m_itemBackgrounds = new Drawable[2];
            m_itemBackgrounds[0] = newBackground;
            m_itemBackgrounds[1] = m_context.getDrawable(R.drawable.blank);
        }
        return m_itemBackgrounds[isChecked ? 0 : 1];
    }

    @Override
    public int getItemViewType(int position) {
//        if(m_openedFolder == null){
//            return AppListItemType.App.ordinal();
//        }
//        else{
//            if(position == (m_openFolderPosition / m_gridView.getNumColumns() + 1)*m_gridView.getNumColumns()){
//                return AppListItemType.FolderAppList.ordinal();
//            }
//            else{
//                return AppListItemType.App.ordinal();
//            }
//        }
        if(m_items.get(position).type == AppListItemType.FolderAppList){
            return 1;
        }
        else {
            return 0;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    private void initFolderAppList(GridView gridView){
        gridView.setAdapter(new ArrayAdapter<AppListItem>(m_context,R.layout.sample_app_item, m_openedFolder.getItems()){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(m_openedFolder == null) return convertView;

                if(convertView == null){
                    convertView = m_context.getLayoutInflater().inflate(R.layout.sample_app_item, null);
                }

                AppListItem app = m_openedFolder.getItems().get(position);
                ImageView appIcon = (ImageView)convertView.findViewById(R.id.icon);
                appIcon.setImageDrawable(app.icon);
                TextView appName = (TextView)convertView.findViewById(R.id.app_label);
                appName.setText(app.name);
                convertView.setTag(app);
                return convertView;
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppListItem app = (AppListItem)view.getTag();
                if (app != null && app.type == AppListItemType.App) {
                    String name = ((AppItem) (app)).id.toString();

                    m_context.startActivity(((AppItem) app).intent);
                }
            }
        });
    }
}
