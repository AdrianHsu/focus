package com.dots.focus.model;

import android.graphics.drawable.Drawable;

/**
 * Created by Harvey Yang on 2015/9/30.
 */
public class AppInfo {
    private String Name = "";
    private String packageName = "";
    private String Category = "";
    private Drawable icon = null;

    public AppInfo(String n, String p, String c, Drawable i) {
        Name = n;
        packageName = p;
        Category = c;
        icon = i;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
