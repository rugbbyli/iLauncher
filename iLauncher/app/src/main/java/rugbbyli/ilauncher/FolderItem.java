package rugbbyli.ilauncher;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zxq on 2015/6/29.
 */
public class FolderItem extends AppListItem {

    private List<AppListItem> items;

    private boolean isOpen = false;
    public void setIsOpen(boolean isOpen){
        if(this.isOpen != isOpen) {
            this.isOpen = isOpen;
            refreshIcon();
        }
    }

    public void toggle(){
        setIsOpen(!isOpen);
    }

    public FolderItem(CharSequence name, Drawable icon){
        super(name, icon, AppListItemType.Folder);

        items = new ArrayList<>();
    }

    public List<AppListItem> getItems(){
        return items;
    }

    public void refreshIcon(){
        icon = AppHelper.getCurrent().getFolderIcon(isOpen, items);
    }
}
