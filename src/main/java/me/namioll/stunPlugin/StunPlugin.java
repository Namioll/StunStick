package me.namioll.stunPlugin;

import org.bukkit.plugin.java.JavaPlugin;

public final class StunPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        getCommand("stunstick").setExecutor(new StunStickCMD(this));
        getServer().getPluginManager().registerEvents(new StickListener(this), this);
    }
}
