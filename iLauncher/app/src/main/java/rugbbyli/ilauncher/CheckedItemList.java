package rugbbyli.ilauncher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zxq on 2015/7/9.
 */
public class CheckedItemList {
    private Set<AppListItem> m_items;

    public CheckedItemList(){
        m_items = new HashSet<>();
    }

    public void clear(){
        m_items.clear();
    }

    public void toggle(AppListItem item){
        if(m_items.contains(item)){
            m_items.remove(item);

        }
        else {
            m_items.add(item);
        }
    }

    public boolean getChecked(AppListItem item){
        return m_items.contains(item);
    }

    public Set<AppListItem> getAllChecked(){
        return m_items;
    }
}
