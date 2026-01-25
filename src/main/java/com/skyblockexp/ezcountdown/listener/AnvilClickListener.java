package com.skyblockexp.ezcountdown.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class AnvilClickListener implements Listener {
    private final Map<UUID, Consumer<String>> pending = new ConcurrentHashMap<>();

    public AnvilClickListener() {}

    public void request(Player player, Consumer<String> callback) {
        pending.put(player.getUniqueId(), callback);
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
                if (cb != null) Bukkit.getScheduler().runTask(null, () -> cb.accept(input));
            }
            player.closeInventory();
            event.setCancelled(true);
        }
    }
}
