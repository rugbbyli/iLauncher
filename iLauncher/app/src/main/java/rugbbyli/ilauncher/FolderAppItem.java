package rugbbyli.ilauncher;

import android.graphics.drawable.Drawable;

/**
 * Created by zxq on 2015/7/10.
 */
public class FolderAppItem extends AppItem {

    FolderItem father;

    public FolderAppItem(CharSequence id, CharSequence name, Drawable icon, FolderItem father) {
        super(id, name, icon);

        this.father = father;
    }

    public void changeFather(FolderItem father){
        if(this.father != null){
            this.father.getItems().remove(this);
            this.father.refreshIcon();
        }
        if(father != null){
            this.father = father;
            this.father.getItems().add(this);
            this.father.refreshIcon();
        }
    }
}
