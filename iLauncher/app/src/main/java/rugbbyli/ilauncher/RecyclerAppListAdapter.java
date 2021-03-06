package rugbbyli.ilauncher;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zxq on 2015/7/7.
 */
public class RecyclerAppListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    interface ItemClickListener{
        void OnClick(View view, AppListItem item, int position);
    }

    class AppViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView icon;
        TextView name;
        int position;

        public AppViewHolder(View itemView) {
            super(itemView);

            icon = (ImageView)itemView.findViewById(R.id.icon);
            name = (TextView)itemView.findViewById(R.id.app_label);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            AppListItem item = m_items.get(position);
            onItemClick(itemView, item, position);
        }
    }

    class FolderAppViewHolder extends RecyclerView.ViewHolder {

        GridView apps;

        public FolderAppViewHolder(View itemView) {
            super(itemView);

            apps = (GridView)itemView.findViewById(R.id.folder_AppList);

            StaggeredGridLayoutManager.LayoutParams parms = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            parms.setFullSpan(true);
            parms.setMargins(10,0,10,10);

            itemView.setLayoutParams(parms);
        }
    }

    private List<AppListItem> m_items;
    private Activity m_context;
    private RecyclerView m_appListView;

    private ItemClickListener itemClickListener;

    private FolderItem m_openedFolder = null;
    private int m_openFolderPosition = -1;

    public RecyclerAppListAdapter(Activity context, RecyclerView view){
        this.m_items = AppHelper.getCurrent().getInstallApps();
        m_context = context;
        m_appListView = view;
    }

    public void setOnItemClickListener(ItemClickListener listener){
        itemClickListener = listener;
    }

    private void onItemClick(View view, AppListItem item, int position){
        if(itemClickListener != null){
            itemClickListener.OnClick(view, item, position);
        }
    }

    public void openFolder(int position){
        FolderItem item = (FolderItem)m_items.get(position);

        if(item == m_openedFolder) return;

        closeFolder();

        m_openedFolder = item;
        m_openedFolder.setIsOpen(true);
        m_openFolderPosition = position;
        notifyItemChanged(position);

        if(m_openedFolder.getItems().size() > 0) {
            int index = (m_openFolderPosition / 4 + 1) * 4;
            AppListItem folderApp = new AppListItem(m_openedFolder.name, null, AppListItemType.FolderAppList);
            m_items.add(index, folderApp);
            notifyItemInserted(index);
        }
    }

    public void closeFolder(){
        if(m_openedFolder != null){
            m_openedFolder.setIsOpen(false);
            notifyItemChanged(m_openFolderPosition);

            int index = (m_openFolderPosition / 4 + 1) * 4;
            m_openFolderPosition = -1;
            m_openedFolder = null;

            if(m_items.get(index).type == AppListItemType.FolderAppList) {
                m_items.remove(index);
                notifyItemRemoved(index);
            }
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;
        if(viewType == AppListItemType.FolderAppList.ordinal()){
            vh = new FolderAppViewHolder(m_context.getLayoutInflater().inflate(R.layout.list_folder_apps, null));
        }
        else {
            vh = new AppViewHolder(m_context.getLayoutInflater().inflate(R.layout.sample_app_item, null));
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AppListItem item = m_items.get(position);

        if(item.type == AppListItemType.FolderAppList && holder instanceof FolderAppViewHolder && m_openedFolder.getItems().size() > 0){
            Log.w("open folder:", m_openedFolder.name.toString());

            FolderAppViewHolder folderHolder = (FolderAppViewHolder)holder;

            initFolderAppList(folderHolder.apps, m_openedFolder);

            int minHeight = AppHelper.getCurrent().dip2px(100 * ((m_openedFolder.getItems().size() - 1) / 4 + 1));
            holder.itemView.setMinimumHeight(minHeight);


        }else if(item.type != AppListItemType.FolderAppList && holder instanceof  AppViewHolder){

            AppViewHolder appHolder = (AppViewHolder)holder;

            appHolder.name.setText(item.name);
            appHolder.icon.setImageDrawable(item.icon);
            appHolder.position = position;
        }else {
            //error..
            Log.e("error!!!", "viewHolder can not match item");
        }
    }

    @Override
    public int getItemCount() {
        return m_items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return m_items.get(position).type.ordinal();
    }

    private void initFolderAppList(GridView gridView, FolderItem folder){
        gridView.setAdapter(new ArrayAdapter<AppListItem>(m_context,R.layout.sample_app_item, folder.getItems()){
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

                RecyclerAppListAdapter.this.onItemClick(view, app, position);
            }
        });
    }
}
