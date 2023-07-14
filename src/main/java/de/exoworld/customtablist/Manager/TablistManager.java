package de.exoworld.customtablist.Manager;

import de.exoworld.customtablist.Main;
import de.exoworld.customtablist.Settings;
import de.exoworld.customtablist.Utils;
import net.kyori.adventure.text.Component;
import net.luckperms.api.model.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;


public class TablistManager {
    private int infoTimerId = -1;
    private static TablistManager tablistManagerInstance;
    private final Map<String, String> rankDing = new HashMap<>();
    public TablistManager() {
        tablistManagerInstance = this;
        createTimer();

        refreshPlayerTablist();
    }

    public void setTablist(Player player) {

        String headerStringTemp = Utils.convertListToStringWithNewLine(Settings.getHeaderList());
        String footerStringTemp = Utils.convertListToStringWithNewLine(Settings.getFooterList());

        String headerString = Utils.checkListForVariables(headerStringTemp);
        String footerString = Utils.checkListForVariables(footerStringTemp);

        player.sendPlayerListHeader(Component.text(headerString));
        player.sendPlayerListFooter(Component.text(footerString));
    }

    public void refreshPlayerTablist() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            LuckPermsManager.getInstance().getRankMap().put(p, LuckPermsManager.getInstance().getPrimaryGroup(p));

            TablistManager.getInstance().createTeams(p);
            TablistManager.getInstance().setTablist(p);
        }
        TablistManager.getInstance().refreshAllPlayerTeams();
    }


    public void createTeams(Player player) {
        Scoreboard sb = player.getScoreboard();
        List<Group> allGroups = LuckPermsManager.getInstance().getGroupsSortedByWeight();
        rankDing.clear();
        int count = 0;

        for (Group group : allGroups) {
            count++;
            String name = group.getName();
            String priority = "0" + count;
            String playerColor = group.getCachedData().getMetaData().getMetaValue("CustomTablistPlayerColor");
            rankDing.put(name, priority);
            Team team = sb.getTeam("CustomTablist" + priority + name);

            if (team == null) {
                team = sb.registerNewTeam("CustomTablist" + priority + name);
            }
            String prefix = group.getCachedData().getMetaData().getPrefix();
            if (prefix == null || prefix.equals("")) {
                String displayName = group.getDisplayName();
                if (displayName == null || displayName.equals("") || displayName.equalsIgnoreCase("null")) {
                    displayName = name;
                }
                prefix = displayName + " | ";
            }
            prefix = Utils.convertColorString(prefix);
            team.prefix(Component.text(prefix));
            team.color(Utils.convertColorToNamedTextColor(playerColor));
        }
    }

    public void refreshAllPlayerTeams() {
        Bukkit.getOnlinePlayers().forEach(this::refreshPlayerTeams);
    }

    public void refreshPlayerTeams(Player player) {
        Scoreboard sb = player.getScoreboard();
        for (Player p : Bukkit.getOnlinePlayers()) {
            for (Team team : sb.getTeams()) {
                team.removePlayer(p);
            }
            String primary = LuckPermsManager.getInstance().getPrimaryGroup(p);
            if (sb.getTeam("CustomTablist" + rankDing.get(primary) + primary) == null) {
                createTeams(player);
            }
            sb.getTeam("CustomTablist" + rankDing.get(primary) + primary).addEntry(p.getName());

        }
    }

    public void refreshTeams(boolean delete) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (delete) {
                Scoreboard sb = p.getScoreboard();
                for (Team team : sb.getTeams()) {
                    if (team.getName().startsWith("CustomTablist")) {
                        team.unregister();
                    }
                }
            }
            createTeams(p);
        }

    }


    public void refreshAll() {
        Bukkit.getOnlinePlayers().forEach(this::setTablist);
        TablistManager.getInstance().refreshTeams(true);
        TablistManager.getInstance().refreshAllPlayerTeams();
    }

    public void createTimer() {
        infoTimerId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                setTablist(player);
            }
        }, Settings.getRefreshRate() * 20L, Settings.getRefreshRate() * 20L);
    }

    public void cancelTimer() {
        if (infoTimerId != -1) {
            Bukkit.getScheduler().cancelTask(infoTimerId);
            infoTimerId = -1;
        }

    }

    public void restartTimer() {
        cancelTimer();
        createTimer();
    }


    public static TablistManager getInstance() {
        return tablistManagerInstance;
    }


}
