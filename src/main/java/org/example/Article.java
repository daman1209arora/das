package org.example;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

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

    public String toString() {
        return "ID:" + id.toString() + " CreatorId: " + creatorId + " WorkflowId: " + workflowId + " DocumentContent: " + documentContent;
    }
}
