package com.qr.hr.swipe;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    public int menuType;
    public List<MenuItem> menuItems = new ArrayList<>();

    public int getMenuType() {
        return menuType;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuType(int menuType) {
        this.menuType = menuType;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }
}
