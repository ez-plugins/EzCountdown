package com.skyblockexp.ezcountdown.gui;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import com.skyblockexp.ezcountdown.command.CountdownPermissions;
import com.skyblockexp.ezcountdown.listener.AnvilClickListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.skyblockexp.ezcountdown.util.MaterialCompat;

import java.util.ArrayList;
import java.util.List;

public final class CommandsEditor {
    private static final String PREFIX = ChatColor.DARK_PURPLE + "Commands: ";
    private final CountdownManager manager;
    private final AnvilClickListener anvilHandler;
    private final MessageManager messageManager;
    private final CountdownPermissions permissions;

    public CommandsEditor(CountdownManager manager, AnvilClickListener anvilHandler, MessageManager messageManager, CountdownPermissions permissions) {
        this.manager = manager;
        this.anvilHandler = anvilHandler;
        this.messageManager = messageManager;
        this.permissions = permissions;
    }

    public void openCommandsEditor(Player player, Countdown countdown) {
        String title = PREFIX + countdown.getName();
        List<String> cmds = new ArrayList<>(countdown.getEndCommands());
        int size = Math.max(9, ((cmds.size() + 1 + 8) / 9) * 9);
        Inventory inv = Bukkit.createInventory(null, size, title);
        for (int i = 0; i < cmds.size(); i++) {
            String c = cmds.get(i);
            ItemStack it = new ItemStack(Material.PAPER);
            ItemMeta meta = it.getItemMeta();
            meta.setDisplayName(ChatColor.WHITE + c);
            meta.setLore(List.of(ChatColor.GRAY + "Left-click to edit"));
            it.setItemMeta(meta);
            inv.setItem(i, it);
        }
        Material addMat = MaterialCompat.resolve("GREEN_WOOL", "LIME_WOOL", "WOOL", "PAPER");
        ItemStack add = new ItemStack(addMat);
        ItemMeta am = add.getItemMeta();
        am.setDisplayName(ChatColor.GREEN + "Add Command");
        am.setLore(List.of(ChatColor.GRAY + "Click to add a command via text input"));
        add.setItemMeta(am);
        inv.setItem(size - 1, add);
        player.openInventory(inv);
    }
    

    public static String getPrefix() { return PREFIX; }
}
