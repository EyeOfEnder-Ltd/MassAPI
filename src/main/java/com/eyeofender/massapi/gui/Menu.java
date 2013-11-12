package com.eyeofender.massapi.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public abstract class Menu implements Listener {

    private String title;
    private ItemStack menuItem;
    private ItemStack[] icons;
    private boolean iconSpacing;

    private Inventory inventory;

    public Menu(Plugin plugin, String title, ItemStack menuItem, ItemStack[] icons, boolean iconSpacing) {
        this.title = title;
        this.menuItem = menuItem;
        this.icons = icons;
        this.iconSpacing = iconSpacing;

        createInventory();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private void createInventory() {
        int slots = iconSpacing ? icons.length * 2 - 1 : icons.length;
        inventory = Bukkit.getServer().createInventory(null, (int) (Math.ceil(slots / 9.0) * 9.0), title);

        for (int i = 0; i < slots; i++) {
            if (iconSpacing) {
                if (i % 2 == 0) inventory.setItem(i, icons[i / 2]);
            } else {
                inventory.setItem(i, icons[i]);
            }
        }
    }

    public void display(Player player) {
        player.getOpenInventory().close();
        player.openInventory(inventory);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() == null || menuItem == null) return;

        Player player = event.getPlayer();
        if (event.getItem().isSimilar(menuItem)) {
            display(player);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getInventory().getTitle().equals(inventory.getTitle())) return;
        event.setResult(Result.DENY);

        ItemStack clicked = event.getCurrentItem();
        if (clicked != null) onIconClick(clicked);
    }

    public abstract void onIconClick(ItemStack icon);

}
