package de.exoworld.customtablist.Manager;

import de.exoworld.customtablist.Main;
import de.exoworld.customtablist.Settings;
import de.exoworld.customtablist.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.luckperms.api.model.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;


public class TablistManager {
    private int infoTimerId = -1;
    private int switchTextsTimerId = -1;
    private static TablistManager tablistManagerInstance;

    private List<String> headerStringList = Settings.getHeaderList();
    private List<String> footerStringList = Settings.getFooterList();
    private String headerString = Utils.convertListToStringWithNewLine(headerStringList);
    private String footerString = Utils.convertListToStringWithNewLine(footerStringList);
    private final Map<String, Integer> rankDing = new HashMap<>();
    private final Map<String, Map<Integer, List<String>>> multiTexts = new HashMap<>();
    public TablistManager() {
        tablistManagerInstance = this;
        createTimer();
        calculateRankPriority();

        TablistManager.getInstance().getMultiTable().clear();
        refreshPlayerTablist();
    }
 //TODO Bei connect wird bei jedem Text refreshed
    public void setTablist(Player player, boolean refresh) {
        if (refresh) {
            headerStringList = switchStringsInTablist(getMultiTable().get("header"));
            footerStringList = switchStringsInTablist(getMultiTable().get("footer"));

            headerString = Utils.convertListToStringWithNewLine(headerStringList);
            footerString = Utils.convertListToStringWithNewLine(footerStringList);
        }
        String headerStringTemp = headerString;
        String footerStringTemp = footerString;

        headerStringTemp = Utils.checkListForVariables(headerStringTemp);
        footerStringTemp = Utils.checkListForVariables(footerStringTemp);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            headerStringTemp = PlaceholderAPI.setPlaceholders(player, headerStringTemp);
            footerStringTemp = PlaceholderAPI.setPlaceholders(player, footerStringTemp);
        }

        player.sendPlayerListHeader(Component.text(headerStringTemp));
        player.sendPlayerListFooter(Component.text(footerStringTemp));
    }

    private List<String> switchStringsInTablist(Map<Integer, List<String>> stringMap) {
        List<String> tempList = new ArrayList<>();
        Random r = new Random();

        //if (stringMap == null || stringMap.size() == 0) {}

        for (int i = 0; i < stringMap.size(); i++) {
            List<String> sListTemp = stringMap.get(i);

            //if (sListTemp != null && !sListTemp.isEmpty()) {
                if (sListTemp.size() == 1) {
                    tempList.add(sListTemp.get(0));
                } else {
                    tempList.add(sListTemp.get(r.nextInt(sListTemp.size())));
                }
            //} else {
            //tempList.add("");
            //}

        }
        return tempList;
    }

    public void refreshPlayerTablist() {
        Utils.checkForMultipleString(Settings.getHeaderList(), "header");
        Utils.checkForMultipleString(Settings.getFooterList(), "footer");

        LuckPermsManager.getInstance().getRankMap().clear();
        for (Player p : Bukkit.getOnlinePlayers()) {
            LuckPermsManager.getInstance().getRankMap().put(p, LuckPermsManager.getInstance().getPrimaryGroup(p));

            createTeams(p);
            setTablist(p, true);
        }
        TablistManager.getInstance().refreshAllPlayerTeams();
    }

    public void calculateRankPriority() {
        rankDing.clear();
        List<Group> allGroups = LuckPermsManager.getInstance().getGroupsSortedByWeight();
        int count = 0;

        for (Group group : allGroups) {
            count++;
            String name = group.getName();
            int priority = 10 + count;

            rankDing.put(name, priority);
        }
    }

    public void createTeams(Player player) {
        Scoreboard sb = player.getScoreboard();
        List<Group> allGroups = LuckPermsManager.getInstance().getGroupsSortedByWeight();
        for (Group group : allGroups) {
            String name = group.getName();
            String playerColor = group.getCachedData().getMetaData().getMetaValue("CustomTablistPlayerColor");
            Team team = sb.getTeam("CustomTablist" + rankDing.get(name) + name);

            if (team == null) {
                team = sb.registerNewTeam("CustomTablist" + rankDing.get(name) + name);
            }
            String prefix = group.getCachedData().getMetaData().getPrefix();
            if (prefix == null || prefix.equals("")) {
                String displayName = group.getDisplayName();
                if (displayName == null || displayName.equals("") || displayName.equalsIgnoreCase("null")) {
                    displayName = name;
                }
                prefix = displayName + " | ";
            }
            prefix = ChatColor.translateAlternateColorCodes('&', prefix);
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
        Utils.checkForMultipleString(Settings.getHeaderList(), "header");
        Utils.checkForMultipleString(Settings.getFooterList(), "footer");

        for (Player p : Bukkit.getOnlinePlayers()) {
            setTablist(p, true);
        }
        TablistManager.getInstance().refreshTeams(true);
        TablistManager.getInstance().refreshAllPlayerTeams();
    }

    public void createTimer() {
        infoTimerId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                setTablist(player, false);
            }
        }, Settings.getRefreshRate() * 20L, Settings.getRefreshRate() * 20L);

        switchTextsTimerId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                setTablist(player, true);
            }
        }, Settings.getSwitchTextsTime() * 20L, Settings.getSwitchTextsTime() * 20L);
    }

    public void cancelTimer() {
        if (infoTimerId != -1) {
            Bukkit.getScheduler().cancelTask(infoTimerId);
            infoTimerId = -1;
        }
        if (switchTextsTimerId != -1) {
            Bukkit.getScheduler().cancelTask(switchTextsTimerId);
            switchTextsTimerId = -1;
        }
    }

    public void restartTimer() {
        cancelTimer();
        createTimer();
    }


    public Map<String, Map<Integer, List<String>>> getMultiTable() {
        return multiTexts;
    }
    public static TablistManager getInstance() {
        return tablistManagerInstance;
    }


}
