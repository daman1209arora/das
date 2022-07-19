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
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

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
    @BsonProperty(value = "status")
    private String status;


    public Task(String articleId, String sentFromId, String sentToId, String comment, String status) {
        this.articleId = articleId;
        this.sentFromId = sentFromId;
        this.sentToId = sentToId;
        this.comment = comment;
        this.status = status;
    }
    public Task() {}

    public ObjectId getId() { return id; }
    public String getArticleId() { return articleId; }
    public String getSentFromId() { return sentFromId; }
    public String getSentToId() { return sentToId;}
    public String getComment() { return comment; }
    public String getStatus() { return status; }

    public void setId(ObjectId id) { this.id = id; }
    public void setArticleId(String articleId) { this.articleId = articleId; }
    public void setSentFromId(String sentFromId) { this.sentFromId = sentFromId; }
    public void setSentToId(String sentToId) { this.sentToId = sentToId; }
    public void setComment(String comment) { this.comment = comment; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "ID:" + id.toString() + " ArticleId: " + articleId + " SentFromId: " + sentFromId + " SentToId: " + sentToId + " Comment: " + comment + " Status: " + status;
    }
    public static String createTask(String articleId, String sentFromId, String sentToId, String comment, String status, @NotNull MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("das");
        MongoCollection<Task> collection = database.getCollection("tasks", Task.class);
        Task task = new Task(articleId, sentFromId, sentToId, comment, status);
        InsertOneResult result = collection.insertOne(task);
        System.out.println("Inserted a document with the following id: "
                + Objects.requireNonNull(result.getInsertedId()).asObjectId().getValue());
        return Objects.requireNonNull(result.getInsertedId()).asObjectId().getValue().toString();
    }

    public static Task searchTaskById(String taskId, @NotNull MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("das");
        MongoCollection<Task> tasksCollection = database.getCollection("tasks", Task.class);
        Task task = tasksCollection.find(eq("_id", new ObjectId(taskId))).first();
        System.out.println(task);
        return task;
    }

    public static void updateTaskStatus(String taskId, String status, @NotNull MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("das");
        MongoCollection<Task> tasksCollection = database.getCollection("tasks", Task.class);
        tasksCollection.findOneAndUpdate(eq("_id", new ObjectId(taskId)), combine(set("status", status)));
    }
}
