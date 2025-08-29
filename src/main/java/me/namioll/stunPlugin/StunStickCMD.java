package me.namioll.stunPlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class StunStickCMD implements CommandExecutor {

    private final FileConfiguration config;
    private final StunStick stunStick;
    public StunStickCMD(StunPlugin plugin) {
        this.config = plugin.getConfig();
        this.stunStick = new StunStick(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            return true;
        }

        if (p.hasPermission("StunPlugin.stick")) {
            p.getInventory().addItem(stunStick.createStunStick());
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("StunStick.success-message", "&aУспешно!")));
        }
        else {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("StunStick.no-permissions-message", "&cУ вас нет прав!")));
        }
        return true;
    }
}
