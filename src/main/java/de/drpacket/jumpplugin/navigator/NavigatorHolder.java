package de.drpacket.jumpplugin.navigator;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public final class NavigatorHolder implements InventoryHolder {
    private final NavigatorView view;
    private Inventory inventory;

    public NavigatorHolder(NavigatorView view) {
        this.view = view;
    }

    public NavigatorView view() {
        return view;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
