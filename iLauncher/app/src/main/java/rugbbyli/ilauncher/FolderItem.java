package rugbbyli.ilauncher;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zxq on 2015/6/29.
 */
public class FolderItem extends AppListItem {

    private List<AppListItem> items;

    public FolderItem(CharSequence name, Drawable icon){
        super(name, icon);
        type = AppListItemType.Folder;
        items = new ArrayList<>();
    }

    public List<AppListItem> getItems(){
        return items;
    }
}
