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

public class Article {
    @BsonId
    private ObjectId id;

    @BsonProperty(value = "creator_id")
    private String creatorId;

    @BsonProperty(value = "workflow_id")
    private String workflowId;

    @BsonProperty(value = "document_content")
    private String documentContent;

    public Article() {}
    public Article(String creatorId, String workflowId, String documentContent) {
        this.creatorId = creatorId;
        this.workflowId = workflowId;
        this.documentContent = documentContent;
    }

    public ObjectId getId() { return id; }
    public String getCreatorId() { return creatorId; }
    public String getWorkflowId() { return workflowId; }
    public String getDocumentContent() { return documentContent;}

    public void setId(ObjectId id) { this.id = id; }
    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }
    public void setWorkflowId(String workflowId) { this.workflowId = workflowId; }
    public void setDocumentContent(String documentContent) { this.documentContent = documentContent; }
    @Override
    public String toString() {
        return "ID:" + id.toString() + " CreatorId: " + creatorId + " WorkflowId: " + workflowId + " DocumentContent: " + documentContent;
    }
    public static void createArticle(String creatorId, String documentContent, String workflowId, @NotNull MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("das");
        MongoCollection<Article> collection = database.getCollection("articles", Article.class);
        Article article = new Article(creatorId, workflowId, documentContent);
        InsertOneResult result = collection.insertOne(article);
        System.out.println("Inserted a document with the following id: "
                + Objects.requireNonNull(result.getInsertedId()).asObjectId().getValue());
    }

    public static void searchArticleById(String articleId, @NotNull MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("das");
        MongoCollection<Article> articlesCollection = database.getCollection("articles", Article.class);
        Article article = articlesCollection.find(eq("_id", new ObjectId(articleId))).first();
        System.out.println(article);
    }

}
