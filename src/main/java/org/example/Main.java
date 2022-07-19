package org.example;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.log4j.BasicConfigurator;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import org.example.User;

public class Main {
    public void disapproveDocument(String documentId, String userId, String comment, @NotNull MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("das");
        MongoCollection<Document> documentsCollection = database.getCollection("documents");
        MongoCollection<Document> workflowsCollection = database.getCollection("workflows");
        MongoCollection<Document> usersCollection = database.getCollection("users");

        Document document = documentsCollection.find(eq("_id", new ObjectId(documentId))).first();
        String workflowId = document.getString("workflow_id");
        Document workflow = workflowsCollection.find(eq("_id", new ObjectId(workflowId))).first();

        List<String> approvedStatus = document.getList("approved_status", String.class);
        List<String> workflowApprovers = workflow.getList("members", String.class);
        if (workflowApprovers.size() <= approvedStatus.size())
            return;
        System.out.println("workflowApprovers.size():" + workflowApprovers.size());
        System.out.println("approvedStatus.size():" + approvedStatus.size());
        String nextUserId = workflowApprovers.get(approvedStatus.size());
        if (nextUserId.equals(userId)) {
            List<String> newApprovedStatus = new ArrayList<>(); // set to empty
            documentsCollection.updateOne(
                    eq("_id", new ObjectId(documentId)),
                    combine(
                            set("approved_status", newApprovedStatus),
                            set("comments", comment)
                    )
            );

            // Fetch nextUserId's email id
            String notifyUserId = document.getString("creator_id");
            Document user = usersCollection.find(eq("_id", new ObjectId(notifyUserId))).first();
            String destEmailId = user.getString("email_id");
            System.out.println("Destination Email ID:" + destEmailId);
            String senderEmailId = "daman.arora@sprinklr.com";

            final String uname = "daman.arora@sprinklr.com";
            final String pwd = "";

            //Set properties and their values
            Properties props = new Properties();
            props.put("mail.smtp.auth", true);
            props.put("mail.smtp.starttls.enable", true);
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");


            //Create a Session object & authenticate uid and pwd
            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(uname, pwd);
                        }
                    });

            try {
                //Create MimeMessage object & set values
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(senderEmailId));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destEmailId));
                message.setSubject("This is test Subject");
                message.setText("Document disapproved. Make changes.");
                //Now send the message
                Transport.send(message);
                System.out.println("Your email sent successfully....");
            } catch (MessagingException exp) {
                throw new RuntimeException(exp);
            }
        }
    }

    public void approveDocument(String documentId, String userId, @NotNull MongoClient mongoClient) {
        // Fetch document. Check if userId is next in order for approval
        // If yes, approve, and send email to user next in order.
        MongoDatabase database = mongoClient.getDatabase("das");
        MongoCollection<Document> documentsCollection = database.getCollection("documents");
        MongoCollection<Document> workflowsCollection = database.getCollection("workflows");
        MongoCollection<Document> usersCollection = database.getCollection("users");

        Document document = documentsCollection.find(eq("_id", new ObjectId(documentId))).first();
        String workflowId = document.getString("workflow_id");
        Document workflow = workflowsCollection.find(eq("_id", new ObjectId(workflowId))).first();

        List<String> approvedStatus = document.getList("approved_status", String.class);
        List<String> workflowApprovers = workflow.getList("members", String.class);

        if(workflowApprovers.size() <= approvedStatus.size())
            return;

        String nextUserId = workflowApprovers.get(approvedStatus.size());
        if(nextUserId.equals(userId)) {
            List<String> newApprovedStatus = new ArrayList<>(approvedStatus);
            newApprovedStatus.add(nextUserId);
            documentsCollection.updateOne(
                    eq("_id", new ObjectId(documentId)),
                    set("approved_status", newApprovedStatus)
            );

            if(workflowApprovers.size() <= 1+approvedStatus.size()) // no need to notify next user
                return;

            // Fetch nextUserId's email id
            String notifyUserId = workflowApprovers.get(approvedStatus.size()+1);
            Document user = usersCollection.find(eq("_id", new ObjectId(notifyUserId))).first();
            String destEmailId = user.getString("email_id");
            System.out.println("Destination Email ID:" + destEmailId);
            String senderEmailId = "daman.arora@sprinklr.com";

            final String uname = "daman.arora@sprinklr.com";
            final String pwd = "";

            //Set properties and their values
            Properties props = new Properties();
            props.put("mail.smtp.auth", true);
            props.put("mail.smtp.starttls.enable", true);
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");


            //Create a Session object & authenticate uid and pwd
            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(uname, pwd);
                        }
                    });

            try {
                //Create MimeMessage object & set values
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(senderEmailId));
                message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(destEmailId));
                message.setSubject("This is test Subject");
                message.setText(document.getString("document_content"));
                //Now send the message
                Transport.send(message);
                System.out.println("Your email sent successfully....");
            } catch (MessagingException exp) {
                throw new RuntimeException(exp);
            }

            System.out.println("Finished");
        }
        else {
            System.out.println("Can't approve! Not in order");
        }
    }

    public static void main(String[] args) {
        BasicConfigurator.configure();
        ConnectionString connectionString = new ConnectionString("mongodb+srv://m001-student:m001-mongodb-basics@cluster0.lunvrpk.mongodb.net/?retryWrites=true&w=majority");
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                .serverApi(ServerApi.builder().version(ServerApiVersion.V1).build())
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
//                ----------------CRUD Testing---------------------
//        Main mainObj = new Main();
//        mainObj.approveDocument("62cc257bcbf513050d2e457f", "62c7c4d371978b345496b606", mongoClient);
//        mainObj.disapproveDocument("62cc257bcbf513050d2e457f", "62c7c6f7d5e862781fe1b0ed", "Software version needs to be updated.", mongoClient);
//
//        User.createUser("Dummy User", "dummy@gmail.com", false, "Intern", mongoClient);
//        User.searchUserById("62d6542bb021ff679953b25c", mongoClient);
//        Article.createArticle("62c7c4d371978b345496b606", "Rejection flow check", "62c7d90e1e1c9636c29a8ef6", mongoClient);
//        Article.searchArticleById("62d6550acb48a44509e52824", mongoClient);
//        List<String> userIds = new ArrayList<>();
//        userIds.add("62c7c6f7d5e862781fe1b0ed");
//        userIds.add("62c7cdb5f744d8b235264ff0");
//        Workflow.createWorkflow("Software Dev", userIds, mongoClient);
//        Workflow.searchWorkflowById("62c7d90e1e1c9636c29a8ef6", mongoClient);
//        Task.createTask("62d6550acb48a44509e52824", "62c7c4d371978b345496b606", "62c7c6f7d5e862781fe1b0ed", "", mongoClient);
//        Task.searchTaskById("62d65c0e79150f2642e21070", mongoClient);


        // 62c7c4d371978b345496b606 - Daman
        // 62c7c6f7d5e862781fe1b0ed - Nitya
        // 62c7cdb5f744d8b235264ff0 - Vishwa
        // 62c7d7c4dcdd7042b8a2f92a - Ragy
//        String taskId = Approval.initiateArticleApproval("62c7c4d371978b345496b606", "62d6550acb48a44509e52824", mongoClient);
//        taskId = Approval.finishTask("62c7c6f7d5e862781fe1b0ed", taskId, "APPROVE", "", mongoClient);
//        taskId = Approval.finishTask("62c7cdb5f744d8b235264ff0", taskId, "APPROVE", "", mongoClient);


        String taskId = Approval.initiateArticleApproval("62c7c4d371978b345496b606", "62d67a8c3285646616677324", mongoClient);
        taskId = Approval.finishTask("62c7c6f7d5e862781fe1b0ed", taskId, "APPROVE", "", mongoClient);
        taskId = Approval.finishTask("62c7cdb5f744d8b235264ff0", taskId, "REJECT", "", mongoClient);
        taskId = Approval.initiateArticleApproval("62c7c4d371978b345496b606", "62d67a8c3285646616677324", mongoClient);
        taskId = Approval.finishTask("62c7c6f7d5e862781fe1b0ed", taskId, "APPROVE", "", mongoClient);
        taskId = Approval.finishTask("62c7cdb5f744d8b235264ff0", taskId, "APPROVE", "", mongoClient);

        System.out.println("Done");
        mongoClient.close();
    }
}