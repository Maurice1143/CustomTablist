package de.exoworld.customtablist;

import de.exoworld.customtablist.Commands.Commands;
import de.exoworld.customtablist.Listener.JoinListener;
import de.exoworld.customtablist.Manager.LuckPermsManager;
import de.exoworld.customtablist.Manager.TablistManager;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;
public final class Main extends JavaPlugin {
    PluginManager pluginManager;
    private static final Logger log = Logger.getLogger("CustomTablist");
    private static Main mainInstance;
    private static Settings settings;
    @Override
    public void onEnable() {
        mainInstance = this;
        settings = new Settings();

        if (!setupLuckPerms() ) {
            log.severe(String.format("[%s] - LuckPerms API nicht gefunden!", getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        //tryToHookIntoPlugins();

        pluginManager = getServer().getPluginManager();
        createManager();
        createEvents();
        createCommands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private boolean setupLuckPerms() {
        if (getServer().getPluginManager().getPlugin("LuckPerms") == null) {
            return false;
        }
        RegisteredServiceProvider<LuckPerms> rsp = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (rsp == null) {
            return false;
        }
        new LuckPermsManager();
        return true;
    }


    public void createEvents() {
        pluginManager.registerEvents(new JoinListener(), this);
    }
    public void createManager() {
        new TablistManager();
    }

    private void createCommands() {
        getCommand("customtablist").setExecutor(new Commands());
    }
    public static Settings getSettings() {
        return settings;
    }

    public static Main getInstance() {
        return mainInstance;
    }
}
