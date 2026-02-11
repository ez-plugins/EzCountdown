package com.skyblockexp.ezcountdown.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.bukkit.plugin.Plugin;

public final class AnvilClickListener implements Listener {
    private final Map<UUID, Consumer<String>> pending = new ConcurrentHashMap<>();

    private final Plugin plugin;

    public AnvilClickListener(Plugin plugin) {
        this.plugin = plugin;
    }

    public void request(Player player, Consumer<String> callback) {
        pending.put(player.getUniqueId(), callback);
        try {
            Inventory inv = Bukkit.createInventory(null, InventoryType.ANVIL, "Input");
            ItemStack paper = new ItemStack(Material.PAPER);
            ItemMeta meta = paper.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(" ");
                paper.setItemMeta(meta);
            }
            inv.setItem(0, paper);
            player.openInventory(inv);
        } catch (Throwable ignored) {
            // best-effort: if creating an anvil fails, still rely on external UI or prior opening
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.ANVIL) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!pending.containsKey(player.getUniqueId())) return;
        if (event.getRawSlot() == 2) {
            ItemStack result = event.getCurrentItem();
            if (result != null && result.hasItemMeta() && result.getItemMeta().hasDisplayName()) {
                String input = result.getItemMeta().getDisplayName();
                Consumer<String> cb = pending.remove(player.getUniqueId());
                if (cb != null) Bukkit.getScheduler().runTask(plugin, () -> cb.accept(input));
            }
            player.closeInventory();
            event.setCancelled(true);
        }
    }
}
