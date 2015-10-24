package com.dots.focus.model;

import android.graphics.drawable.Drawable;

public class AppInfo extends Object {
    private String Name = "";
    private String packageName = "";
    private String Category = "Others";
    private Drawable icon = null;

    public AppInfo(String n, String p, Drawable i){
        Name = n;
        packageName = p;
        icon = i;
    }

    public AppInfo(String n, String p, String c){
        Name = n;
        packageName = p;
        Category = c;
    }

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
        if(category != null && !category.equals("unknown"))
            Category = category;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
