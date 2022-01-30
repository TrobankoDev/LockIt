package me.trobanko.chestlocker.utils;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class LockUtils {

    private static HashMap<Player, Block> playerByBlock = new HashMap<>();

    public static HashMap<Player, Block> getPlayerByBlock(){
        return playerByBlock;
    }

    public static void createNewLock(Player player, Block target){

        Document lock = new Document("uuid", player.getUniqueId().toString())
                .append("type", target.getType().toString())
                .append("location", new Document("x", target.getX()).append("y", target.getY()).append("z", target.getZ()))
                .append("creation-date", new Date());
        MongoUtils.getCollection().insertOne(lock);

        playerByBlock.remove(player);
        player.closeInventory();
        player.sendMessage(Format.c("&aSuccessfully locked " + target.getType().toString().toLowerCase() +"."));

    }
    public static boolean isLocked(Block target){
        int x = target.getX();
        int y = target.getY();
        int z = target.getZ();

        Document filter = new Document("location", new Document("x", x).append("y", y).append("z", z));

        return (MongoUtils.getCollection().countDocuments(filter) > 0);
    }

    public static Player getWhoLocked(Block target){
        int x = target.getX();
        int y = target.getY();
        int z = target.getZ();

        Document filter = new Document("location", new Document("x", x).append("y", y).append("z", z));

        return Bukkit.getPlayer(UUID.fromString(MongoUtils.getCollection().find(filter).first().getString("uuid")));
    }

    public static void deleteContainer(Block target){
        int x = target.getX();
        int y = target.getY();
        int z = target.getZ();

        Document filter = new Document("location", new Document("x", x).append("y", y).append("z", z));

        try {
            MongoUtils.getCollection().deleteOne(filter);
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
