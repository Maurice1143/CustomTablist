package de.exoworld.customtablist;

import de.exoworld.customtablist.Manager.TablistManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class Settings {
    private FileConfiguration config;
    private static List<String> headerList = new ArrayList<>();
    private static List<String> footerList = new ArrayList<>();
    private static int refreshRate;
    public Settings() {
        Main.getInstance().saveDefaultConfig();
        config = Main.getInstance().getConfig();
        headerList = config.getStringList("Tablist.Header");
        footerList = config.getStringList("Tablist.Footer");
        refreshRate = config.getInt("Tablist.RefreshRate");
    }


    public static List<String> getHeaderList() {
        return headerList;
    }

    public static List<String> getFooterList() {
        return footerList;
    }

    public static int getRefreshRate() {
        return refreshRate;
    }

    public void reloadSettings() {
        Main.getInstance().reloadConfig();
        config = Main.getInstance().getConfig();

        headerList = config.getStringList("Tablist.Header");
        footerList = config.getStringList("Tablist.Footer");
        refreshRate = config.getInt("Tablist.RefreshRate", 10);

        TablistManager.getInstance().refreshAll();
        TablistManager.getInstance().restartTimer();
    }
}
