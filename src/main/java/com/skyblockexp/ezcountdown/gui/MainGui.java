package com.skyblockexp.ezcountdown.gui;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public final class MainGui {
    private static final String TITLE = ChatColor.BLUE + "EzCountdowns";
    private final CountdownManager manager;

    public MainGui(CountdownManager manager) {
        this.manager = manager;
    }

    public void openMain(Player player) {
        List<Countdown> list = new ArrayList<>(manager.getCountdowns());
        int size = Math.max(9, ((list.size() + 8) / 9) * 9);
        Inventory inv = Bukkit.createInventory((InventoryHolder) null, size, TITLE);
        for (int i = 0; i < list.size(); i++) {
            Countdown cd = list.get(i);
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + cd.getName());
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.YELLOW + "Status: " + (cd.isRunning() ? "Running" : "Stopped"));
            lore.add(ChatColor.GRAY + "Left-click: Edit");
            lore.add(ChatColor.GRAY + "Right-click: Preview");
            lore.add(ChatColor.RED + "Shift-right: Delete");
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
        player.openInventory(inv);
    }

    public static String getTitle() { return TITLE; }
}
