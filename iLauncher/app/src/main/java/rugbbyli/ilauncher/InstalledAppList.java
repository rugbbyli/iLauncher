package rugbbyli.ilauncher;

import java.util.Collections;
import java.util.List;

/**
 * Created by zxq on 2015/7/10.
 */
public class InstalledAppList {
    private List<AppListItem> m_apps;

    public InstalledAppList(List<AppListItem> items){
        m_apps = items;
        Collections.sort(m_apps);
    }

    public List<AppListItem> all(){
        return m_apps;
    }

    public int insert(AppListItem item){
        int pos = 0;
        for(;pos < m_apps.size();pos++){
            if(item.compareTo(m_apps.get(pos)) < 0) break;
        }
        m_apps.add(pos, item);
        return pos;
    }

    public int remove(AppListItem item){
        return -1;
    }

    /**
     * find item from installed applist. if item is in a folder, return folder's location.
     */
    public int find(AppListItem item){
        AppListItem appListItem = null;
        for(int i = 0;i<m_apps.size();i++){
            appListItem = m_apps.get(i);
            if(appListItem instanceof FolderItem){
                FolderItem folderItem = (FolderItem)appListItem;
                for(int j = 0;j<folderItem.getItems().size();j++){
                    if(isItemEqual(folderItem.getItems().get(j), item)){
                        return i;
                    }
                }
            }
            if(isItemEqual(appListItem, item)) return i;
        }

        return -1;
    }

    private boolean isItemEqual(AppListItem item1, AppListItem item2){
        if(item1 == item2) return true;

        if(item1.type != item2.type) return false;
        if(item1.type == AppListItemType.App){
            return ((AppItem)item1).id == ((AppItem)item2).id;
        } else{
            return item1.name == item2.name;
        }
    }
}
