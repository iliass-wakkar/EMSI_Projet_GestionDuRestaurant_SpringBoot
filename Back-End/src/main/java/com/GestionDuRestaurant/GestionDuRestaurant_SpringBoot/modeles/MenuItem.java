package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "menu_items")
@IdClass(MenuItemId.class)
public class MenuItem {

    @Id
    @ManyToOne
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Id
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "price_override")
    private Double priceOverride;

    // Constructors
    public MenuItem() {
    }

    public MenuItem(Menu menu, Item item, Integer displayOrder) {
        this.menu = menu;
        this.item = item;
        this.displayOrder = displayOrder;
    }

    // Getters and setters
    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Double getPriceOverride() {
        return priceOverride;
    }

    public void setPriceOverride(Double priceOverride) {
        this.priceOverride = priceOverride;
    }
}