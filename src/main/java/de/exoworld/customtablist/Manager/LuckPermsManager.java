package de.exoworld.customtablist.Manager;

import de.exoworld.customtablist.Listener.RankChangeListener;
import de.exoworld.customtablist.Listener.RankCreateListener;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.*;

public class LuckPermsManager {
    private static LuckPermsManager instance;
    private LuckPerms API;
    private final GroupManager groupManager;
    private final UserManager userManager;

    private final Map<Player, String> userRanks = new HashMap<>();

    public LuckPermsManager() {
        instance = this;
        setupLuckPerms();

        groupManager = API.getGroupManager();
        userManager = API.getUserManager();

        new RankChangeListener(API);
        new RankCreateListener(API);
    }

    private void setupLuckPerms() {
        RegisteredServiceProvider<LuckPerms> rsp = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        API = rsp.getProvider();
    }

    public LuckPerms getAPI() {
        return API;
    }

    public List<Group> getGroupsSortedByWeight() {
        Set<Group> allGroups = groupManager.getLoadedGroups();
        Map<Group, OptionalInt> unsortedGroups = new HashMap<>();
        List<Group> sortedGroups = new ArrayList<>();

        for (Group group : allGroups) {
            unsortedGroups.put(group, group.getWeight());
        }
        List<Map.Entry<Group, OptionalInt>> tempList = new ArrayList<>(unsortedGroups.entrySet());
        Comparator<Map.Entry<Group, OptionalInt>> comparator = (e1, e2) -> {

            OptionalInt optInt1 = e1.getValue();
            Integer int1 = 0;
            OptionalInt optInt2 = e2.getValue();
            Integer int2 = 0;
            if(optInt1.isPresent()) {
                int1 = optInt1.getAsInt();
            }
            if(optInt2.isPresent()) {
                int2 = optInt2.getAsInt();
            }
            return int1.compareTo(int2);
        };
        tempList.sort(comparator);

        for(Map.Entry<Group, OptionalInt> e: tempList) {
            sortedGroups.add(e.getKey());
        }
        Collections.reverse(sortedGroups);
        return sortedGroups;
    }

    public String getPrimaryGroup(Player player) {
        return userManager.getUser(player.getUniqueId()).getPrimaryGroup();
    }

    public static LuckPermsManager getInstance() {
        return instance;
    }

    public Map<Player, String> getRankMap() {
        return userRanks;
    }
}

