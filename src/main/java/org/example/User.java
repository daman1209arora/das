package org.example;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

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

    public String toString() {
        return "ID:" + id.toString() + " Name: " + name + " Email: " + emailId + " Role: " + role;
    }
}
