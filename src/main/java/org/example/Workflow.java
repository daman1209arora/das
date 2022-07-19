package org.example;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.List;

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

    public String toString() {
        return "ID: " + id.toString() + " Workflow Name: " + workflowName + " Members: [" + String.join(",",members) + "]";
    }
}
