package org.example;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;
import org.apache.log4j.BasicConfigurator;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {
    public void createWorkflow(String workflowName, List<String> userIds, @NotNull MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("das");
        MongoCollection<Document> collection = database.getCollection("workflows");
        Document workflow = new Document()
                .append("workflow_name", workflowName)
                .append("members", userIds);
        InsertOneResult result = collection.insertOne(workflow);
        System.out.println("Inserted a document with the following id: "
                + Objects.requireNonNull(result.getInsertedId()).asObjectId().getValue());
    }

    public void createUser(String name, String emailId, Boolean canCreateDoc, String role, @NotNull MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("das");
        MongoCollection<Document> collection = database.getCollection("users");

        Document user = new Document()
                .append("name", name)
                .append("email_id", emailId)
                .append("can_create_doc", canCreateDoc)
                .append("role", role);

        InsertOneResult result = collection.insertOne(user);
        System.out.println("Inserted a document with the following id: "
                + Objects.requireNonNull(result.getInsertedId()).asObjectId().getValue());
    }


    public void createDocument(String creatorId, String documentContent, String workflowId, @NotNull MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("das");
        MongoCollection<Document> collection = database.getCollection("documents");

        Document doc =new Document()
                .append("creator_id", creatorId)
                .append("workflow_id", workflowId)
                .append("document_content", documentContent)
                .append("approved_status", new ArrayList<String>());

        InsertOneResult result = collection.insertOne(doc);
        System.out.println("Inserted a document with the following id: "
                + Objects.requireNonNull(result.getInsertedId()).asObjectId().getValue());
    }
    public static void main(String[] args) {
        BasicConfigurator.configure();
        ConnectionString connectionString = new ConnectionString("mongodb+srv://m001-student:m001-mongodb-basics@cluster0.lunvrpk.mongodb.net/?retryWrites=true&w=majority");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        Main mainObj = new Main();
        mainObj.createUser(
                "Ragy Thomas",
                "ragy@gmail.com",
                true,
                "CEO",
                mongoClient
        );

        List<String> userIds = new ArrayList<>();
        userIds.add("62c7c6f7d5e862781fe1b0ed");
        userIds.add("62c7cdb5f744d8b235264ff0");
        mainObj.createWorkflow(
                "Care Cloud",
                userIds,
                mongoClient
        );

        mainObj.createDocument(
                "62c7c4d371978b345496b606",
                "This is a dummy document",
                "62c7d90e1e1c9636c29a8ef6",
                mongoClient
        );
        System.out.println("Done");
        mongoClient.close();
    }
}