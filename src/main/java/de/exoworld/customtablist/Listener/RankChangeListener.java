package de.exoworld.customtablist.Listener;

import de.exoworld.customtablist.Main;
import de.exoworld.customtablist.Manager.LuckPermsManager;
import de.exoworld.customtablist.Manager.TablistManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public class RankChangeListener {
    public RankChangeListener(LuckPerms API) {
        API.getEventBus().subscribe(Main.getInstance(), UserDataRecalculateEvent.class, e -> {
            Player player = Bukkit.getPlayer(e.getUser().getUniqueId());
            Map<Player, String> rankMap = LuckPermsManager.getInstance().getRankMap();
            if (player != null && player.isOnline()) {
                if (rankMap.containsKey(player) && !rankMap.get(player).equals(e.getUser().getPrimaryGroup())) {
                    rankMap.replace(player, e.getUser().getPrimaryGroup());
                    TablistManager.getInstance().refreshAllPlayerTeams();
                }
            }
        });
    }
}
