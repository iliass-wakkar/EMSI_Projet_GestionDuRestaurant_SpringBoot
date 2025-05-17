package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles;

import java.io.Serializable;
import java.util.Objects;

public class MenuItemId implements Serializable {
    private Long menu; // matches the menu field in MenuItem
    private Long item; // matches the item field in MenuItem

    public MenuItemId() {
    }

    public MenuItemId(Long menu, Long item) {
        this.menu = menu;
        this.item = item;
    }

    public Long getMenu() {
        return menu;
    }

    public void setMenu(Long menu) {
        this.menu = menu;
    }

    public Long getItem() {
        return item;
    }

    public void setItem(Long item) {
        this.item = item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MenuItemId that = (MenuItemId) o;
        return Objects.equals(menu, that.menu) && Objects.equals(item, that.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(menu, item);
    }
}