package org.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;

import static com.mongodb.client.model.Filters.eq;

public class Workflow {
    @BsonId
    private ObjectId id;

    @BsonProperty(value = "workflow_name")
    private String workflowName;

    @BsonProperty(value = "members")
    private List<String> members;

    public Workflow() {}
    public Workflow(String workflowName, List<String> members) {
        this.workflowName = workflowName;
        this.members = members;
    }

    public ObjectId getId() { return id; }
    public String getWorkflowName() { return workflowName; }
    public List<String> getMembers() { return members; }

    public void setId(ObjectId id) { this.id = id; }
    public void setWorkflowName(String workflowName) { this.workflowName = workflowName; }
    public void setMembers(List<String> members) { this.members = members; }
    @Override
    public String toString() {
        return "ID: " + id.toString() + " Workflow Name: " + workflowName + " Members: [" + String.join(",",members) + "]";
    }
    public static String createWorkflow(String workflowName, List<String> userIds, @NotNull MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("das");
        MongoCollection<Workflow> collection = database.getCollection("workflows", Workflow.class);
        Workflow workflow = new Workflow(workflowName, userIds);
        InsertOneResult result = collection.insertOne(workflow);
        System.out.println("Inserted a document with the following id: "
                + Objects.requireNonNull(result.getInsertedId()).asObjectId().getValue());
        return Objects.requireNonNull(result.getInsertedId()).asObjectId().getValue().toString();
    }

    public static Workflow searchWorkflowById(String workflowId, @NotNull MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("das");
        MongoCollection<Workflow> workflowsCollection = database.getCollection("workflows", Workflow.class);
        Workflow workflow = workflowsCollection.find(eq("_id", new ObjectId(workflowId))).first();
        System.out.println(workflow);
        return workflow;
    }
}
