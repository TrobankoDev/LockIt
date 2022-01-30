package me.trobanko.chestlocker.listeners;

import me.trobanko.chestlocker.utils.Format;
import me.trobanko.chestlocker.utils.LockUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        if(!e.getBlock().getType().equals(Material.CHEST)) return;
        if(!LockUtils.isLocked(e.getBlock())) return;
        Player player = e.getPlayer();
        if(!LockUtils.getWhoLocked(e.getBlock()).equals(e.getPlayer())){
            e.setCancelled(true);
            player.sendMessage(Format.c("&cYou cannot break this chest because it is locked by " + LockUtils.getWhoLocked(e.getBlock()).getDisplayName()));
            return;
        }

        Block target = e.getBlock();

        player.sendMessage(Format.c("&cBreaking chest and removing lock..."));

        LockUtils.deleteContainer(target);
    }
}
