package de.exoworld.customtablist.Listener;

import de.exoworld.customtablist.Main;
import de.exoworld.customtablist.Manager.TablistManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.group.GroupCreateEvent;
import net.luckperms.api.event.group.GroupDeleteEvent;

public class RankCreateListener {
    public RankCreateListener(LuckPerms API) {
        API.getEventBus().subscribe(Main.getInstance(), GroupCreateEvent.class, e -> {
            TablistManager.getInstance().refreshTeams(true);
            TablistManager.getInstance().refreshAllPlayerTeams();
            TablistManager.getInstance().calculateRankPriority();
        });

        API.getEventBus().subscribe(Main.getInstance(), GroupDeleteEvent.class, e -> {
            TablistManager.getInstance().refreshTeams(true);
            TablistManager.getInstance().refreshAllPlayerTeams();
            TablistManager.getInstance().calculateRankPriority();
        });
    }
}
