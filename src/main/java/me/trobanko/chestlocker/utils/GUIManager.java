package me.trobanko.chestlocker.utils;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;


public class GUIManager {

    public void openConfirmLockGui(Player player){
        Gui gui = Gui.gui().title(Component.text(Format.c("&dLock Chest?"))).rows(1).create();
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        gui.setItem(3, ItemBuilder.from(Material.TOTEM_OF_UNDYING).name(Component.text(Format.c("&aConfirm"))).asGuiItem(e -> {
            // Lock chest for player
            LockUtils.createNewLock(player, LockUtils.getPlayerByBlock().get(player));
        }));
        gui.setItem(5, ItemBuilder.from(Material.BARRIER).name(Component.text(Format.c("&cCancel"))).asGuiItem(e -> {
            gui.close(player);
        }));

        gui.getFiller().fill(ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).name(Component.text(" ")).asGuiItem());

        gui.open(player);
    }

    public void openLockedContainersList(Player player){
        PaginatedGui gui = Gui.paginated().title(Component.text(Format.c("&4Your locked containers"))).rows(6).create();
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        Document filter = new Document("uuid", player.getUniqueId().toString());


        // Previous item
        gui.setItem(6, 3, ItemBuilder.from(Material.PAPER).name(Component.text(Format.c("&cPrevious"))).asGuiItem(event -> gui.previous()));
        // Next item
        gui.setItem(6, 7, ItemBuilder.from(Material.PAPER).name(Component.text(Format.c("&cNext"))).asGuiItem(event -> gui.next()));


        gui.getFiller().fillBottom(ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).name(Component.text(" ")).asGuiItem());

        MongoUtils.getCollection().find(filter).forEach(document -> {

            DateFormat DFormat = DateFormat.getDateInstance(DateFormat.LONG);

            Document location = (Document) document.get("location");
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text(Format.c("&6-------------------")));
            lore.add(Component.text(Format.c("&3Type: &e" + document.getString("type")))); // Material type
            lore.add(Component.text(Format.c("&3Location:")));
            lore.add(Component.text(Format.c("&3  x: &b" + location.getInteger("x")))); // x
            lore.add(Component.text(Format.c("&3  y: &b" + location.getInteger("y")))); // y
            lore.add(Component.text(Format.c("&3  z: &b" + location.getInteger("z")))); // z
            lore.add(Component.text(Format.c("&3Date Created: &d" + DFormat.format(document.get("creation-date"))))); // Date created
            lore.add(Component.text(Format.c("&6-------------------")));

            GuiItem guiItem = ItemBuilder.from(Material.getMaterial(document.getString("type")))
                    .name(Component.text(Format.c("&cLocked " + document.getString("type").toLowerCase()))).lore(lore).asGuiItem();

            gui.addItem(guiItem);
        });

        gui.open(player);
    }

}
