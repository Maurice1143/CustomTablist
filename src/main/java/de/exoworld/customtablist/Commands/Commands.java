package de.exoworld.customtablist.Commands;

import de.exoworld.customtablist.Main;
import de.exoworld.customtablist.Manager.TablistManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Commands implements CommandExecutor, TabCompleter {
    enum subCommands {
        reload,
        refresh,
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("customtablist")) {
            if (args[0].equalsIgnoreCase("reload")) {
                Main.getSettings().reloadSettings();
                sender.sendMessage("§7[§bCustomTablist§7] §2Config wurde erfolgreich reloaded.");
                return true;
            }
            if (args[0].equalsIgnoreCase("refresh")) {
                TablistManager.getInstance().refreshAll();
                sender.sendMessage("§7[§bCustomTablist§7] §2Tabliste wurde für alle Spieler neu geladen.");
                return true;
            }
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 0) return list;

        if (args.length == 1) {
            for  (subCommands s : subCommands.values()) {
                list.add(s.toString());
            }
        }

        List<String> completerList = new ArrayList<>();
        String currentArg = args[args.length-1].toLowerCase();
        for (String s : list) {
            String s1 = s.toLowerCase();
            if (s1.startsWith(currentArg)) {
                completerList.add(s);
            }
        }
        return completerList;
    }
}
