package me.trobanko.chestlocker.commands;

import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;
import me.trobanko.chestlocker.utils.Format;
import me.trobanko.chestlocker.utils.GUIManager;
import me.trobanko.chestlocker.utils.LockUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@Command("locker")
public class LockCommand extends CommandBase {

    GUIManager guiManager = new GUIManager();

    @SubCommand("lock")
    public void lockSubCommand(final Player player){
        Block target;

        if(player.getTargetBlockExact(5) == null) return;

        target = player.getTargetBlockExact(5);

        if(target.getType().equals(Material.CHEST)){

            if(LockUtils.isLocked(target)) {
                player.sendMessage(Format.c("&cThis chest has already been locked!"));
                return;
            }

            // Confirm chest locking
            LockUtils.getPlayerByBlock().put(player, target);
            guiManager.openConfirmLockGui(player);

        }

    }

    @SubCommand("list")
    public void listSubCommand(final Player player){
        guiManager.openLockedContainersList(player);
    }

}
