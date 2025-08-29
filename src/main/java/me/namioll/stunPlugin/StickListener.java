package me.namioll.stunPlugin;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class StickListener implements Listener {

    private final FileConfiguration config;
    private final String stickName;

    private final Map<UUID, Long> stunnedUntil = new HashMap<>();
    private final Map<UUID, Long> cooldownUntil = new HashMap<>();

    public StickListener(StunPlugin plugin) {
        this.config = plugin.getConfig();
        this.stickName = ChatColor.translateAlternateColorCodes('&',
                config.getString("StunStick.name", "&9&lStun Stick"));
    }

    private long now() {
        return System.currentTimeMillis();
    }

    private boolean isStunned(Player p) {
        Long until = stunnedUntil.get(p.getUniqueId());
        if (until == null) return false;
        if (now() >= until) {
            stunnedUntil.remove(p.getUniqueId());
            return false;
        }
        return true;
    }

    private boolean isOnCooldown(Player p) {
        Long until = cooldownUntil.get(p.getUniqueId());
        if (until == null) return false;
        if (now() >= until) {
            cooldownUntil.remove(p.getUniqueId());
            return false;
        }
        return true;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEntityEvent e) {
        if (!(e.getRightClicked() instanceof Player target)) return;
        Player user = e.getPlayer();
        ItemStack item = user.getInventory().getItemInMainHand();
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;
        if (!stickName.equals(meta.getDisplayName())) return;
        if (!user.hasPermission("StunPlugin.stick")) {
            user.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    config.getString("StunStick.no-permissions-message", "&cУ вас нет прав!")));
            return;
        }
        if (isOnCooldown(user)) {
            long sec = Math.ceilDiv(cooldownUntil.get(user.getUniqueId()) - now(), 1000);
            user.sendMessage(ChatColor.RED + "Стой! Подожди ещё " + sec + " сек.");
            return;
        }
        long stunSec = (long) Math.max(0, config.getDouble("StunStick.cooldown-stun", 30.0));
        stunnedUntil.put(target.getUniqueId(), now() + stunSec * 1000);
        target.playSound(target.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
        target.sendMessage(ChatColor.RED + "Вы были застанены на " + stunSec + " сек!");
        long cdSec = (long) Math.max(0, config.getDouble("StunStick.cooldown-stick", 5.0));
        cooldownUntil.put(user.getUniqueId(), now() + cdSec * 1000);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (!isStunned(p)) return;

        // блокируем только если реально двигается (не просто голова)
        if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ()) {
            e.setTo(e.getFrom());
        }
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player p)) return;
        if (isStunned(p)) {
            e.setCancelled(true);
            p.sendMessage(ChatColor.RED + "Вы не можете атаковать в стане!");
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        if (isStunned(p)) {
            e.setCancelled(true);
            p.sendMessage(ChatColor.RED + "Вы не можете использовать команды в стане!");
        }
    }
}
