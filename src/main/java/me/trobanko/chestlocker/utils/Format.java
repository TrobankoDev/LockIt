package me.trobanko.chestlocker.utils;

import org.bukkit.ChatColor;

public class Format {
    public static String c(String string){
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
