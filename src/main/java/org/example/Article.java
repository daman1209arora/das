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

public class Article {
    @BsonId
    private ObjectId id;

    @BsonProperty(value = "creator_id")
    private String creatorId;

    @BsonProperty(value = "workflow_id")
    private String workflowId;

    @BsonProperty(value = "document_content")
    private String documentContent;

    @BsonProperty(value = "latest_task_id")
    private String latestTaskId;

    @BsonProperty(value = "article_status")
    private String articleStatus;

    public Article() {}
    public Article(String creatorId, String workflowId, String documentContent, String latestTaskId, String articleStatus) {
        this.creatorId = creatorId;
        this.workflowId = workflowId;
        this.documentContent = documentContent;
        this.latestTaskId = latestTaskId;
        this.articleStatus = articleStatus;
    }

    public ObjectId getId() { return id; }
    public String getCreatorId() { return creatorId; }
    public String getWorkflowId() { return workflowId; }
    public String getDocumentContent() { return documentContent;}
    public String getLatestTaskId() { return latestTaskId;}
    public String getArticleStatus() { return articleStatus;}

    public void setId(ObjectId id) { this.id = id; }
    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }
    public void setWorkflowId(String workflowId) { this.workflowId = workflowId; }
    public void setDocumentContent(String documentContent) { this.documentContent = documentContent; }
    public void setLatestTaskId(String latestTaskId) { this.latestTaskId = latestTaskId; }
    public void setArticleStatus(String articleStatus) { this.articleStatus = articleStatus; }
    @Override
    public String toString() {
        return "ID:" + id.toString() + " CreatorId: " + creatorId + " WorkflowId: "
                + workflowId + " DocumentContent: " + documentContent + " LatestTaskId: " + latestTaskId
                + " ArticleStatus: " + articleStatus;
    }
    public static String createArticle(String creatorId, String documentContent, String workflowId, @NotNull MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("das");
        MongoCollection<Article> collection = database.getCollection("articles", Article.class);
        Article article = new Article(creatorId, workflowId, documentContent, "", "PENDING");
        InsertOneResult result = collection.insertOne(article);
        System.out.println("Inserted a document with the following id: "
                + Objects.requireNonNull(result.getInsertedId()).asObjectId().getValue());
        return Objects.requireNonNull(result.getInsertedId()).asObjectId().getValue().toString();
    }

    public static Article searchArticleById(String articleId, @NotNull MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("das");
        MongoCollection<Article> articlesCollection = database.getCollection("articles", Article.class);
        Article article = articlesCollection.find(eq("_id", new ObjectId(articleId))).first();
        System.out.println(article);
        return article;
    }

    public static void updateArticleTask(String articleId, String taskId, MongoClient mongoClient) {
        System.out.println("ArticleId: " + articleId + " updating taskId to " + taskId);
        MongoDatabase database = mongoClient.getDatabase("das");
        MongoCollection<Article> articlesCollection = database.getCollection("articles", Article.class);
        articlesCollection.findOneAndUpdate(eq("_id", new ObjectId(articleId)), combine(set("latest_task_id", taskId)));
    }

    public static void updateArticleStatus(String articleId, String articleStatus, MongoClient mongoClient) {
        System.out.println("ArticleId: " + articleId + " updating status to " + articleStatus);
        MongoDatabase database = mongoClient.getDatabase("das");
        MongoCollection<Article> articlesCollection = database.getCollection("articles", Article.class);
        articlesCollection.findOneAndUpdate(eq("_id", new ObjectId(articleId)), combine(set("article_status", articleStatus)));
    }
}
