package me.trobanko.lockit.commands;

import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;
import me.trobanko.lockit.utils.Format;
import me.trobanko.lockit.utils.GUIManager;
import me.trobanko.lockit.utils.LockUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;


@Command("lockit")
public class LockCommand extends CommandBase {

    @Default
    public void defaultResponse(final Player player){
        player.sendMessage(Format.c("&7========&b&lLock&e&lIt&7========="));
        player.sendMessage(Format.c("&6/lockit lock &7- &3Lock the container you're facing"));
        player.sendMessage(Format.c("&6/lockit list &7- &3View your locked containers"));
        player.sendMessage(Format.c("&7============================="));
    }

    GUIManager guiManager = new GUIManager();

    @SubCommand("lock")
    public void lockSubCommand(final Player player){
        Block target;

        if(player.getTargetBlockExact(5) == null) return;

        target = player.getTargetBlockExact(5);

        if(LockUtils.getContainers().contains(target.getType())){

            if(LockUtils.isLocked(target)) {
                player.sendMessage(Format.c("&cThis" + target.getType().toString().toLowerCase() + "has already been locked!"));
                return;
            }

            // Confirm chest locking
            LockUtils.getPlayerByBlock().put(player, target);
            guiManager.openConfirmLockGui(player);

        } else {
            player.sendMessage(Format.c("&cPlease face a container while executing this command to lock it!"));
        }

    }

    @SubCommand("list")
    public void listSubCommand(final Player player){
        guiManager.openLockedContainersList(player);
    }

}
