package ToolItems;

import android.graphics.Color;

import com.chinalwb.are.Constants;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Italic;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_UpdaterDefault;
import com.chinalwb.are.styles.toolitems.IARE_ToolItem_Updater;

import daksh.docs.R;

public class ItalicToolItem extends ARE_ToolItem_Italic {

    @Override
    public IARE_ToolItem_Updater getToolItemUpdater() {
        if (mToolItemUpdater == null) {
            mToolItemUpdater = new ARE_ToolItem_UpdaterDefault(this, R.color.colorAccent, Constants.UNCHECKED_COLOR);
            setToolItemUpdater(mToolItemUpdater);
        }
        return mToolItemUpdater;
    }
}
