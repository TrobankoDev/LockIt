package me.trobanko.lockit.listeners;

import me.trobanko.lockit.utils.Format;
import me.trobanko.lockit.utils.LockUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        if(!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if(!LockUtils.getContainers().contains(e.getClickedBlock().getType())) return;
        if(!LockUtils.isLocked(e.getClickedBlock())) return;

        Player player = e.getPlayer();

        if(LockUtils.getWhoLocked(e.getClickedBlock()).equals(player)){
            player.sendMessage(Format.c("&aOpening your locked " + e.getClickedBlock().getType().toString().toLowerCase() + "..."));
            return;
        }

        else if(LockUtils.getWhoHasAccess(e.getClickedBlock()).contains(e.getPlayer().getUniqueId().toString())){
            player.sendMessage(Format.c("&aOpening " + LockUtils.getWhoLocked(e.getClickedBlock()).getName() + "'s locked " + e.getClickedBlock().getType().toString().toLowerCase() + "..."));
            if(LockUtils.getWhoLocked(e.getClickedBlock()).isOnline()){
                LockUtils.getWhoLocked(e.getClickedBlock()).getPlayer().sendMessage(Format.c("&a" + player.getDisplayName() + " has opened one of your locked containers."));
            }
            return;
        }

        else {
            e.setCancelled(true);
            player.sendMessage(Format.c("&cThis " + e.getClickedBlock().getType().toString().toLowerCase() + " is locked by " + LockUtils.getWhoLocked(e.getClickedBlock()).getName()));
        }
    }

}
