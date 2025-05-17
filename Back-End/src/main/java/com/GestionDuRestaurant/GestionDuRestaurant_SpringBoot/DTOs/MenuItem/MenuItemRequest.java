package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.DTOs.MenuItem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MenuItemRequest {
    @NotNull(message = "Menu ID is required")
    private Long menuId;

    @NotNull(message = "Item ID is required")
    private Long itemId;

    private Integer displayOrder;

    private LocalDate startDate;

    private LocalDate endDate;

    @Positive(message = "Price override must be greater than zero")
    private BigDecimal priceOverride;

    // Getters and setters
    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
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

    public BigDecimal getPriceOverride() {
        return priceOverride;
    }

    public void setPriceOverride(BigDecimal priceOverride) {
        this.priceOverride = priceOverride;
    }
}