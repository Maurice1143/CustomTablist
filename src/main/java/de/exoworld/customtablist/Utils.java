package de.exoworld.customtablist;

import de.exoworld.customtablist.Manager.TablistManager;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {

    private static final Map<String, String> colors = new HashMap<>() {{
        put("&0", "black");
        put("&1", "dark_blue");
        put("&2", "dark_green");
        put("&3", "dark_aqua");
        put("&4", "dark_red");
        put("&5", "dark_purple");
        put("&6", "gold");
        put("&7", "gray");
        put("&8", "dark_gray");
        put("&9", "blue");
        put("&a", "green");
        put("&b", "aqua");
        put("&c", "red");
        put("&d", "light_purple");
        put("&e", "yellow");
        put("&f", "white");
    }};

    public static String convertListToStringWithNewLine(List<String> stringList) {
        StringBuilder listString = new StringBuilder();
        int size = stringList.size();
        for (int i = 0; i < size; i++) {
            listString.append(ChatColor.WHITE).append(stringList.get(i)).append(i + 1 != size ? "\n" : "");
        }
        return listString.toString();
    }

    public static String checkListForVariables(String string) {
        String checkedString = string;
        String[] words = string.split(" ");
        for (String word : words) {
            if (word.contains("%TPS%")) {
                StringBuilder newString = new StringBuilder();
                double[] currentTPS = Bukkit.getTPS();
                for (int i = 0; i < currentTPS.length; i++) {
                    double tps = currentTPS[i];

                    newString.append(String.format("%.2f", tps));
                    newString.append(i != currentTPS.length - 1 ? " " : "");
                }
                checkedString = checkedString.replaceAll("%TPS%", newString.toString());
                //Bukkit.broadcast(Component.text(checkedString));
            }
        }
        return checkedString;
    }

    /*public static String convertColorString(String colorString) {
        String color = "§f" + colorString;
        if (colorString != null && colorString.contains("&")) {
            color = colorString.replaceAll("&", "§");
            return color;

        }
        TODO Add ability to use Hexcolors
        else if (colorString.startsWith("#")){

        }
        return color;
    }*/

    public static NamedTextColor convertColorToNamedTextColor(String color) {
        NamedTextColor convertedColor = NamedTextColor.NAMES.value((colors.get(color) != null) ? colors.get(color) : "white");
        if (convertedColor == null) {
            convertedColor = NamedTextColor.WHITE;
        }
        return convertedColor;
    }

    public static void checkForMultipleString(List<String> stringList, String type) {
        List<String> tempList = new ArrayList<>();
        //List<String> tempListAll = new ArrayList<>();
        Map<Integer, List<String>> tempMap = new HashMap<>();
        for (int i = 0; i < stringList.size(); i++) {
            String s = stringList.get(i);
            /*if (s.equals("")) {
                String space = "";
                while (tempListAll.contains(s + space)) {
                    space = space + " ";
                }
                Bukkit.broadcast(Component.text("Spaces: " + space.length()));
                tempList.add(space);
                tempListAll.add(space);
            } else {*/
                String[] splitStrings = s.split("::");
                for (String splitString : splitStrings) {
                    tempList.add(splitString);
                    //tempListAll.add(splitString);
                }

                if (tempList.size() == 0) {
                    tempList.add(s);
                    //tempListAll.add(s);
                }
            //}

            tempMap.put(i, List.copyOf(tempList.stream().toList()));
            tempList.clear();
        }
        TablistManager.getInstance().getMultiTable().put(type, tempMap);
    }
}
