package me.trobanko.lockit.utils;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GUIManager {

    public void openConfirmLockGui(Player player){
        Gui gui = Gui.gui().title(Component.text(Format.c("&dLock Container?"))).rows(1).create();
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        gui.setItem(3, ItemBuilder.from(Material.TOTEM_OF_UNDYING).name(Component.text(Format.c("&aConfirm"))).asGuiItem(e -> {
            // Lock container for player
            LockUtils.createNewLock(player, LockUtils.getPlayerByBlock().get(player));
        }));
        gui.setItem(5, ItemBuilder.from(Material.BARRIER).name(Component.text(Format.c("&cCancel"))).asGuiItem(e -> {
            gui.close(player);
        }));

        gui.getFiller().fill(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.text(" ")).asGuiItem());

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


        gui.getFiller().fillBottom(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.text(" ")).asGuiItem());

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
                    .name(Component.text(Format.c("&cLocked " + document.getString("type").toLowerCase()))).lore(lore).asGuiItem(e -> {
                        this.openLockMangerGui(player, LockUtils.getLock(document.getObjectId("_id").toString()));
                    });

            gui.addItem(guiItem);
        });

        gui.open(player);
    }

    public void openLockMangerGui(Player player, Document document){
        Gui gui = Gui.gui().title(Component.text(Format.c("&4Lock Manager"))).rows(1).create();
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        DateFormat DFormat = DateFormat.getDateInstance(DateFormat.LONG);
        Document location = (Document) document.get("location");
        List<Component> lock_info_lore = new ArrayList<>();
        lock_info_lore.add(Component.text(Format.c("&6-------------------")));
        lock_info_lore.add(Component.text(Format.c("&3Type: &e" + document.getString("type")))); // Material type
        lock_info_lore.add(Component.text(Format.c("&3Location:")));
        lock_info_lore.add(Component.text(Format.c("&3  x: &b" + location.getInteger("x")))); // x
        lock_info_lore.add(Component.text(Format.c("&3  y: &b" + location.getInteger("y")))); // y
        lock_info_lore.add(Component.text(Format.c("&3  z: &b" + location.getInteger("z")))); // z
        lock_info_lore.add(Component.text(Format.c("&3Date Created: &d" + DFormat.format(document.get("creation-date"))))); // Date created
        lock_info_lore.add(Component.text(Format.c("&6-------------------")));


        GuiItem access_manager = ItemBuilder.from(Material.ARMOR_STAND).name(Component.text(Format.c("&eAccess Manager"))).lore(Component.text(Format.c("&3Manage who can access this lock"))).asGuiItem(e -> {
            openAccessMenu((Player) e.getWhoClicked(), document);
        });

        GuiItem delete_lock = ItemBuilder.from(Material.WITHER_ROSE).name(Component.text(Format.c("&4Delete Lock"))).lore(Component.text(Format.c("&3Remove lock from this container"))).asGuiItem(e -> {
            openConfirmDelete((Player) e.getWhoClicked(), document);
        });

        GuiItem lock_info = ItemBuilder.from(Material.WRITABLE_BOOK).name(Component.text(Format.c("&bLock Information"))).lore(lock_info_lore).asGuiItem();

        GuiItem exit = ItemBuilder.from(Material.BARRIER).name(Component.text(Format.c("&cClose"))).lore(Component.text(Format.c("&3Go back to locks list"))).asGuiItem(e -> {
            this.openLockedContainersList((Player) e.getWhoClicked());
        });

        gui.setItem(0, access_manager);
        gui.setItem(1, delete_lock);
        gui.setItem(7, lock_info);
        gui.setItem(8, exit);
        gui.getFiller().fill(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.text(" ")).asGuiItem());

        gui.open(player);
    }

    public void openConfirmDelete(Player player, Document document){
        Gui gui = Gui.gui().title(Component.text(Format.c("&4Delete this lock?"))).rows(1).create();
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        gui.setItem(3, ItemBuilder.from(Material.EMERALD).name(Component.text(Format.c("&aConfirm"))).asGuiItem(e -> {
            MongoUtils.getCollection().deleteOne(document);
            this.openLockedContainersList((Player) e.getWhoClicked());
            player.sendMessage(Format.c("&aSuccessfully deleted lock."));
        }));
        gui.setItem(5, ItemBuilder.from(Material.BARRIER).name(Component.text(Format.c("&cCancel"))).asGuiItem(e -> {
            this.openLockMangerGui((Player) e.getWhoClicked(), document);
        }));

        gui.getFiller().fill(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.text(" ")).asGuiItem());

        gui.open(player);


    }

    public void openAccessMenu(Player player, Document document){
        Gui gui = Gui.gui().title(Component.text(Format.c("&4Access Menu"))).rows(5).create();
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        GuiItem remove_access = ItemBuilder.from(Material.REDSTONE_BLOCK).name(Component.text(Format.c("&4Remove Player")))
                .lore(Component.text(Format.c("&eRemove players from this lock"))).asGuiItem(e -> {
                    openRemovePlayerAccessMenu((Player) e.getWhoClicked(), document);
                });

        GuiItem view_players = ItemBuilder.from(Material.PLAYER_HEAD).name(Component.text(Format.c("&bView Players")))
                .lore(Component.text(Format.c("&aSee who can access this lock"))).asGuiItem(e -> {
                    showPlayersWithAccess((Player) e.getWhoClicked(), document);
                });

        GuiItem add_access  = ItemBuilder.from(Material.ENDER_EYE).name(Component.text(Format.c("&6Add Players to Lock"))).asGuiItem(e -> {
            openAddPlayerAccessMenu((Player) e.getWhoClicked(), document);
        });

        gui.setItem(44, ItemBuilder.from(Material.BARRIER).name(Component.text(Format.c("&cReturn"))).asGuiItem(e -> {
            this.openLockMangerGui((Player) e.getWhoClicked(), document);
        }));

        gui.setItem(13, remove_access);
        gui.setItem(22, view_players);
        gui.setItem(31, add_access);
        gui.getFiller().fill(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.text(" ")).asGuiItem());
        gui.open(player);
    }

    public void showPlayersWithAccess(Player player, Document document){
        PaginatedGui gui = Gui.paginated().title(Component.text(Format.c("&4Players with access"))).rows(6).create();
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        // Previous item
        gui.setItem(6, 3, ItemBuilder.from(Material.PAPER).name(Component.text(Format.c("&cPrevious"))).asGuiItem(event -> gui.previous()));
        // Next item
        gui.setItem(6, 7, ItemBuilder.from(Material.PAPER).name(Component.text(Format.c("&cNext"))).asGuiItem(event -> gui.next()));

        gui.setItem(6, 5, ItemBuilder.from(Material.BARRIER).name(Component.text(Format.c("&cReturn"))).asGuiItem(e -> {
            this.openAccessMenu((Player) e.getWhoClicked(), document);
        }));

        gui.getFiller().fillBottom(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.text(" ")).asGuiItem());


        @SuppressWarnings("unchecked")
        ArrayList<String> accessList = (ArrayList<String>) document.get("access");
        for(String uuid : accessList){
            GuiItem player_skull = ItemBuilder.skull().owner(Bukkit.getOfflinePlayer(UUID.fromString(uuid))).name(Component.text(Format.c("&e" + Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName()))).asGuiItem();
            gui.addItem(player_skull);
        }

        gui.open(player);
    }

    public void openAddPlayerAccessMenu(Player player, Document document){
        PaginatedGui gui = Gui.paginated().title(Component.text(Format.c("&4Add lock access"))).rows(6).create();
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        // Previous item
        gui.setItem(6, 3, ItemBuilder.from(Material.PAPER).name(Component.text(Format.c("&cPrevious"))).asGuiItem(event -> gui.previous()));
        // Next item
        gui.setItem(6, 7, ItemBuilder.from(Material.PAPER).name(Component.text(Format.c("&cNext"))).asGuiItem(event -> gui.next()));

        gui.setItem(6, 5, ItemBuilder.from(Material.BARRIER).name(Component.text(Format.c("&cReturn"))).asGuiItem(e -> {
            this.openAccessMenu((Player) e.getWhoClicked(), document);
        }));

        gui.getFiller().fillBottom(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.text(" ")).asGuiItem());

        @SuppressWarnings("unchecked")
        ArrayList<String> accessList = (ArrayList<String>) document.get("access");

        for(Player target : Bukkit.getOnlinePlayers()){
            if(target == player || accessList.contains(target.getUniqueId().toString())){
                continue;
            }
            GuiItem player_skull = ItemBuilder.skull().owner(target).name(Component.text(Format.c("&e" + target.getName()))).asGuiItem(e -> {
                openConfirmAddPlayerAccess(target, (Player) e.getWhoClicked(), document);
            });
            gui.addItem(player_skull);
        }

        gui.open(player);
    }

    public void openConfirmAddPlayerAccess(Player target, Player player, Document document){
        Gui gui = Gui.gui().title(Component.text(Format.c("&4Allow this player access?"))).rows(1).create();
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        gui.setItem(2, ItemBuilder.from(Material.EMERALD).name(Component.text(Format.c("&aConfirm"))).asGuiItem(e -> {
            LockUtils.addPlayerAccess(document, target.getUniqueId().toString());
            this.openLockedContainersList((Player) e.getWhoClicked());
            player.sendMessage(Format.c("&aSuccessfully added access for " + target.getDisplayName() + " to this lock."));
            target.sendMessage(Format.c("&a"+player.getDisplayName()+" gave you access to one of their locks."));
        }));

        gui.setItem(4, ItemBuilder.skull().owner(target).name(Component.text(Format.c("&e" + target.getName()))).asGuiItem());

        gui.setItem(6, ItemBuilder.from(Material.BARRIER).name(Component.text(Format.c("&cCancel"))).asGuiItem(e -> {
            this.openAccessMenu((Player) e.getWhoClicked(), document);
        }));

        gui.getFiller().fill(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.text(" ")).asGuiItem());

        gui.open(player);

    }

    public void openRemovePlayerAccessMenu(Player player, Document document){
        PaginatedGui gui = Gui.paginated().title(Component.text(Format.c("&4Remove lock access"))).rows(6).create();
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        // Previous item
        gui.setItem(6, 3, ItemBuilder.from(Material.PAPER).name(Component.text(Format.c("&cPrevious"))).asGuiItem(event -> gui.previous()));
        // Next item
        gui.setItem(6, 7, ItemBuilder.from(Material.PAPER).name(Component.text(Format.c("&cNext"))).asGuiItem(event -> gui.next()));

        gui.setItem(6, 5, ItemBuilder.from(Material.BARRIER).name(Component.text(Format.c("&cReturn"))).asGuiItem(e -> {
            this.openAccessMenu((Player) e.getWhoClicked(), document);
        }));

        gui.getFiller().fillBottom(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.text(" ")).asGuiItem());

        @SuppressWarnings("unchecked")
        ArrayList<String> accessList = (ArrayList<String>) document.get("access");
        for(String uuid : accessList){
            GuiItem player_skull = ItemBuilder.skull().owner(Bukkit.getOfflinePlayer(UUID.fromString(uuid))).name(Component.text(Format.c("&e" + Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName()))).asGuiItem(e -> {
                openConfirmRemovePlayerAccess(Bukkit.getOfflinePlayer(UUID.fromString(uuid)), (Player) e.getWhoClicked(), document);
            });
            gui.addItem(player_skull);
        }

        gui.open(player);
    }

    public void openConfirmRemovePlayerAccess(OfflinePlayer target, Player player, Document document){
        Gui gui = Gui.gui().title(Component.text(Format.c("&4Remove player's access?"))).rows(1).create();
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        gui.setItem(2, ItemBuilder.from(Material.EMERALD).name(Component.text(Format.c("&aConfirm"))).asGuiItem(e -> {
            LockUtils.removePlayerAccess(document, target.getUniqueId().toString());
            this.openLockedContainersList((Player) e.getWhoClicked());
            player.sendMessage(Format.c("&aSuccessfully removed " + target.getName() + "'s access to this lock."));
            if(target.isOnline()){
                target.getPlayer().sendMessage(Format.c("&c" + player.getDisplayName() + " has removed your access to one of their locks."));
            }
        }));

        gui.setItem(4, ItemBuilder.skull().owner(target).name(Component.text(Format.c("&e" + target.getName()))).asGuiItem());

        gui.setItem(6, ItemBuilder.from(Material.BARRIER).name(Component.text(Format.c("&cCancel"))).asGuiItem(e -> {
            this.openAccessMenu((Player) e.getWhoClicked(), document);
        }));

        gui.getFiller().fill(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.text(" ")).asGuiItem());

        gui.open(player);

    }

}
