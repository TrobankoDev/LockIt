package me.trobanko.lockit;

import me.mattstudios.mf.base.CommandManager;
import me.trobanko.lockit.commands.LockCommand;
import me.trobanko.lockit.listeners.BlockBreakListener;
import me.trobanko.lockit.listeners.PlayerInteractListener;
import me.trobanko.lockit.utils.MongoUtils;
import org.bukkit.plugin.java.JavaPlugin;



public final class LockIt extends JavaPlugin {

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
