package me.trobanko.chestlocker;

import me.mattstudios.mf.base.CommandManager;
import me.trobanko.chestlocker.commands.LockCommand;
import me.trobanko.chestlocker.listeners.BlockBreakListener;
import me.trobanko.chestlocker.listeners.PlayerInteractListener;
import me.trobanko.chestlocker.utils.MongoUtils;
import org.bukkit.plugin.java.JavaPlugin;



public final class ChestLocker extends JavaPlugin {

    @Override
    public void onEnable() {

        // Connect to mongodb
        MongoUtils.setupDatabase();

        // Command manager
        CommandManager commandManager = new CommandManager(this);
        commandManager.register(new LockCommand());

        // Register Listener
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
