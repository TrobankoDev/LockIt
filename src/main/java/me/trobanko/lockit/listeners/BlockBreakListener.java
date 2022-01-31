package me.trobanko.lockit.listeners;

import me.trobanko.lockit.utils.Format;
import me.trobanko.lockit.utils.LockUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        if(!LockUtils.getContainers().contains(e.getBlock().getType())) return;
        if(!LockUtils.isLocked(e.getBlock())) return;
        Player player = e.getPlayer();
        if(!LockUtils.getWhoLocked(e.getBlock()).equals(e.getPlayer())){
            e.setCancelled(true);
            player.sendMessage(Format.c("&cYou cannot break this " + LockUtils.getWhoLocked(e.getBlock()).getName() + " because it is locked by " + LockUtils.getWhoLocked(e.getBlock()).getName()));
            return;
        }

        Block target = e.getBlock();

        player.sendMessage(Format.c("&cBreaking " + e.getBlock().getType().toString().toLowerCase() +" and removing lock..."));

        LockUtils.deleteContainer(target);
    }
}
