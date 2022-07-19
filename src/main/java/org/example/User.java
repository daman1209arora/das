package org.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.mongodb.client.model.Filters.eq;

public class User {
    @BsonId
    private ObjectId id;

    @BsonProperty(value = "name")
    private String name;

    @BsonProperty(value = "email_id")
    private String emailId;

    @BsonProperty(value = "can_create_doc")
    private Boolean canCreateDoc;

    @BsonProperty(value = "role")
    private String role;

    public User() {}
    public User(String name, String emailId, String role, Boolean canCreateDoc) {
        this.name = name;
        this.emailId = emailId;
        this.role = role;
        this.canCreateDoc = canCreateDoc;
    }

    public ObjectId getId() { return id; }
    public String getName() { return name; }
    public String getEmailId() { return emailId; }
    public String getRole() { return role;}
    public Boolean getCanCreateDoc() { return canCreateDoc; }

    public void setId(ObjectId id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmailId(String emailId) { this.emailId = emailId; }
    public void setRole(String role) { this.role = role; }
    public void setCanCreateDoc(Boolean canCreateDoc) { this.canCreateDoc = canCreateDoc; }

    @Override
    public String toString() {
        return "ID:" + id.toString() + " Name: " + name + " Email: " + emailId + " Role: " + role;
    }
    public static void createUser(String name, String emailId, Boolean canCreateDoc, String role, @NotNull MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("das");
        MongoCollection<User> collection = database.getCollection("users", User.class);
        User user = new User(name, emailId, role, canCreateDoc);
        InsertOneResult result = collection.insertOne(user);
        System.out.println("Inserted a document with the following id: "
                + Objects.requireNonNull(result.getInsertedId()).asObjectId().getValue());
    }

    public static void searchUserById(String userId, @NotNull MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("das");
        MongoCollection<User> usersCollection = database.getCollection("users", User.class);
        User user = usersCollection.find(eq("_id", new ObjectId(userId))).first();
        System.out.println(user);
    }
}
