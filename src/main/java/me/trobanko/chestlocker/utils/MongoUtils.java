package me.trobanko.chestlocker.utils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoUtils {

    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static MongoCollection<Document> collection;

    public static void setupDatabase(){
        mongoClient = MongoClients.create("mongodb+srv://trobanko:dodo12345@spigot.aaxt0.mongodb.net/myFirstDatabase?retryWrites=true&w=majority");
        database = mongoClient.getDatabase("Locker");
        collection = database.getCollection("locks");
    }

    public static MongoCollection<Document> getCollection(){
        return collection;
    }

}
