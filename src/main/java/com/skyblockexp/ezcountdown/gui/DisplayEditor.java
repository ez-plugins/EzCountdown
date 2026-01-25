package com.skyblockexp.ezcountdown.gui;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.display.DisplayType;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.Material;
import com.skyblockexp.ezcountdown.util.MaterialCompat;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.EnumSet;

public final class DisplayEditor {
    private static final String PREFIX = ChatColor.DARK_GREEN + "Displays: ";
    private final CountdownManager manager;
    private final MessageManager messageManager;

    public DisplayEditor(CountdownManager manager, MessageManager messageManager) {
        this.manager = manager;
        this.messageManager = messageManager;
    }

    public void openDisplayEditor(Player player, Countdown countdown) {
        String title = PREFIX + countdown.getName();
        Inventory inv = Bukkit.createInventory(null, 9, title);
        DisplayType[] values = DisplayType.values();
        EnumSet<DisplayType> current = countdown.getDisplayTypes();
        for (int i = 0; i < values.length && i < 9; i++) {
            DisplayType dt = values[i];
            Material mat = MaterialCompat.resolve("GRAY_CONCRETE", "GRAY_WOOL", "WOOL");
            if (current.contains(dt)) mat = MaterialCompat.resolve("LIME_CONCRETE", "LIME_WOOL", "WOOL");
            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.AQUA + dt.name());
            meta.setLore(java.util.List.of(ChatColor.GRAY + "Click to toggle"));
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
        player.openInventory(inv);
    }

    public static String getPrefix() { return PREFIX; }
}
