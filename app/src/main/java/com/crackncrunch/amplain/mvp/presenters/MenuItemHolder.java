package com.crackncrunch.amplain.mvp.presenters;

import android.view.MenuItem;
import android.view.View;

public class MenuItemHolder {
    private final CharSequence itemTitle;
    private final int iconResId;
    private MenuItem.OnMenuItemClickListener menuItemListener = null;
    private View.OnClickListener viewListener = null;

    public MenuItemHolder(CharSequence itemTitle, int iconResId, MenuItem.OnMenuItemClickListener listener) {
        this.itemTitle = itemTitle;
        this.iconResId = iconResId;
        this.menuItemListener = listener;
    }

    public MenuItemHolder(CharSequence itemTitle, int iconResId, View.OnClickListener listener) {
        this.itemTitle = itemTitle;
        this.iconResId = iconResId;
        this.viewListener = listener;
    }

    public CharSequence getTitle() {
        return itemTitle;
    }

    public int getIconResId() {
        return iconResId;
    }

    public MenuItem.OnMenuItemClickListener getMenuItemListener() {
        return menuItemListener;
    }

    public View.OnClickListener getViewListener() {
        return viewListener;
    }
}