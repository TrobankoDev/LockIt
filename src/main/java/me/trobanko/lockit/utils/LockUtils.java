package me.trobanko.lockit.utils;

import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

public class LockUtils {

    private static HashMap<Player, Block> playerByBlock = new HashMap<>();

    public static HashMap<Player, Block> getPlayerByBlock(){
        return playerByBlock;
    }

    public static ArrayList<Material> containers = new ArrayList<>(
            Arrays.asList(Material.CHEST, Material.TRAPPED_CHEST, Material.DISPENSER, Material.DROPPER, Material.HOPPER,
            Material.BARREL, Material.FURNACE, Material.SHULKER_BOX, Material.SMOKER));

    public static ArrayList<Material> getContainers() { return containers; }

    public static void createNewLock(Player player, Block target){

        Document lock = new Document("uuid", player.getUniqueId().toString())
                .append("type", target.getType().toString())
                .append("location", new Document("x", target.getX()).append("y", target.getY()).append("z", target.getZ()))
                .append("creation-date", new Date())
                .append("access", new ArrayList<String>());
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

    public static OfflinePlayer getWhoLocked(Block target){
        int x = target.getX();
        int y = target.getY();
        int z = target.getZ();

        Document filter = new Document("location", new Document("x", x).append("y", y).append("z", z));

        return Bukkit.getPlayer(UUID.fromString(MongoUtils.getCollection().find(filter).first().getString("uuid")));
    }

    public static ArrayList<String> getWhoHasAccess(Block target){
        int x = target.getX();
        int y = target.getY();
        int z = target.getZ();

        Document filter = new Document("location", new Document("x", x).append("y", y).append("z", z));

        @SuppressWarnings("unchecked")
        ArrayList<String> hasAccess = (ArrayList<String>) MongoUtils.getCollection().find(filter).first().get("access");

        return hasAccess;
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

    public static Document getLock(String id){
        Document filter = new Document(new Document("_id", new ObjectId(id)));
        return MongoUtils.getCollection().find(filter).first();
    }

    public static void addPlayerAccess(Document document, String uuid){
        MongoUtils.getCollection().updateOne(document, Updates.addToSet("access", uuid));
    }

    public static void removePlayerAccess(Document document, String uuid){
        MongoUtils.getCollection().updateOne(document, Updates.pull("access", uuid));
    }

}
