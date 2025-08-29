package me.namioll.stunPlugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class StunStick {

    private final FileConfiguration config;
    private String name;
    public StunStick(StunPlugin plugin) {
        this.config = plugin.getConfig();
        this.name = ChatColor.translateAlternateColorCodes('&', config.getString("StunStick.name", "&9&lStun Stick"));
    }

    public ItemStack createStunStick() {
        ItemStack palochka = new ItemStack(Material.STICK, 1);
        ItemMeta meta = palochka.getItemMeta();
        meta.setDisplayName(name);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addEnchant(Enchantment.VANISHING_CURSE, 1, false);
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("");
        lore.add(ChatColor.translateAlternateColorCodes('&',config.getString("StunStick.lore1", "&7Эта палка позволяет задержать игрока.")));
        lore.add(ChatColor.translateAlternateColorCodes('&',config.getString("StunStick.lore2", "&7Использование: &6&l[ПКМ]&7.")));
        meta.setLore(lore);
        palochka.setItemMeta(meta);
        return palochka;
    }
}
