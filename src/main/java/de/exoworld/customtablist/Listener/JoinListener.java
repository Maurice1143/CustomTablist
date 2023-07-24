package de.exoworld.customtablist.Listener;

import de.exoworld.customtablist.Manager.LuckPermsManager;
import de.exoworld.customtablist.Manager.TablistManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin (PlayerJoinEvent e) {
        LuckPermsManager.getInstance().getRankMap().put(e.getPlayer(), LuckPermsManager.getInstance().getPrimaryGroup(e.getPlayer()));

        TablistManager.getInstance().createTeams(e.getPlayer());
        TablistManager.getInstance().setTablist(e.getPlayer(), true);
        TablistManager.getInstance().refreshAllPlayerTeams();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        LuckPermsManager.getInstance().getRankMap().remove((e.getPlayer()));
    }
}
