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

public class Task {
    @BsonId
    private ObjectId id;

    @BsonProperty(value = "article_id")
    private String articleId;

    @BsonProperty(value = "sent_from_id")
    private String sentFromId;

    @BsonProperty(value = "sent_to_id")
    private String sentToId;

    @BsonProperty(value = "comment")
    private String comment;

    public Task(String articleId, String sentFromId, String sentToId, String comment) {
        this.articleId = articleId;
        this.sentFromId = sentFromId;
        this.sentToId = sentToId;
        this.comment = comment;
    }
    public Task() {}

    public ObjectId getId() { return id; }
    public String getArticleId() { return articleId; }
    public String getSentFromId() { return sentFromId; }
    public String getSentToId() { return sentToId;}
    public String getComment() { return comment; }

    public void setId(ObjectId id) { this.id = id; }
    public void setArticleId(String articleId) { this.articleId = articleId; }
    public void setSentFromId(String sentFromId) { this.sentFromId = sentFromId; }
    public void setSentToId(String sentToId) { this.sentToId = sentToId; }
    public void setComment(String comment) { this.comment = comment; }

    @Override
    public String toString() {
        return "ID:" + id.toString() + " ArticleId: " + articleId + " SentFromId: " + sentFromId + " SentToId: " + sentToId;
    }
    public static void createTask(String articleId, String sentFromId, String sentToId, String comment, @NotNull MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("das");
        MongoCollection<Task> collection = database.getCollection("tasks", Task.class);
        Task task = new Task(articleId, sentFromId, sentToId, comment);
        InsertOneResult result = collection.insertOne(task);
        System.out.println("Inserted a document with the following id: "
                + Objects.requireNonNull(result.getInsertedId()).asObjectId().getValue());
    }

    public static void searchTaskById(String taskId, @NotNull MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("das");
        MongoCollection<Task> tasksCollection = database.getCollection("tasks", Task.class);
        Task task = tasksCollection.find(eq("_id", new ObjectId(taskId))).first();
        System.out.println(task);
    }
}
