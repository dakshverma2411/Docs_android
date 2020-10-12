package ToolItems;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chinalwb.are.AREditText;
import com.chinalwb.are.styles.IARE_Style;
import com.chinalwb.are.styles.toolbar.IARE_Toolbar;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Link;
import com.chinalwb.are.styles.toolitems.IARE_ToolItem_Updater;

public class LinkToolItem extends ARE_ToolItem_Link {
    @Override
    public IARE_ToolItem_Updater getToolItemUpdater() {
        return super.getToolItemUpdater();
    }

    @Override
    public IARE_Style getStyle() {
        return super.getStyle();
    }

    @Override
    public View getView(Context context) {
        return super.getView(context);
    }

    @Override
    public void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
    }

    @Override
    public IARE_Toolbar getToolbar() {
        return super.getToolbar();
    }

    @Override
    public void setToolbar(IARE_Toolbar toolbar) {
        super.setToolbar(toolbar);
    }

    @Override
    public void setToolItemUpdater(IARE_ToolItem_Updater toolItemUpdater) {
        super.setToolItemUpdater(toolItemUpdater);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public AREditText getEditText() {
        return super.getEditText();
    }

    @Override
    protected <T> void printSpans(Class<T> clazz) {
        super.printSpans(clazz);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }

    @NonNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
