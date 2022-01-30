package me.trobanko.chestlocker.listeners;

import me.trobanko.chestlocker.utils.Format;
import me.trobanko.chestlocker.utils.LockUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        if(!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if(!e.getClickedBlock().getType().equals(Material.CHEST)) return;
        if(!LockUtils.isLocked(e.getClickedBlock())) return;

        Player player = e.getPlayer();

        if(LockUtils.getWhoLocked(e.getClickedBlock()).equals(player)){
            player.sendMessage(Format.c("&aOpening your locked chest..."));
            return;
        } else {
            e.setCancelled(true);
            player.sendMessage(Format.c("&cThis chest is locked by " + LockUtils.getWhoLocked(e.getClickedBlock()).getDisplayName()));
        }
    }

}
