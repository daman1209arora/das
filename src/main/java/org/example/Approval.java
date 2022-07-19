package org.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class Approval {
    public static String initiateArticleApproval(String userId, String articleId, MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("das");
        MongoCollection<Article> articlesCollection = database.getCollection("articles", Article.class);
        Article article = articlesCollection.find(eq("_id", new ObjectId(articleId))).first();
        String articleCreator = article.getCreatorId();
        // If no task has been assigned, or rejection has been sent to creatorId
        if(userId.equals(articleCreator)) {
            String latestTaskId = article.getLatestTaskId();
            Boolean noTask = latestTaskId.equals("");
            if(noTask) {
                Workflow workflow = Workflow.searchWorkflowById(article.getWorkflowId(), mongoClient);
                String firstApprover = workflow.getMembers().get(0);
                String taskId = Task.createTask(articleId, articleCreator, firstApprover, "", "PENDING", mongoClient);
                Article.updateArticleTask(articleId, taskId, mongoClient);
                return taskId;
            }
            else {
                Task latestTask = Task.searchTaskById(latestTaskId, mongoClient);
                if (latestTask.getSentToId().equals(articleCreator) && latestTask.getStatus().equals("PENDING")) {
                    // Change status of latestTask
                    Task.updateTaskStatus(latestTaskId, "APPROVED", mongoClient);
                    Workflow workflow = Workflow.searchWorkflowById(article.getWorkflowId(), mongoClient);
                    String firstApprover = workflow.getMembers().get(0);
                    String newTaskId = Task.createTask(articleId, articleCreator, firstApprover, "", "PENDING", mongoClient);
                    Article.updateArticleTask(articleId, newTaskId, mongoClient);
                    return newTaskId;
                }
                else {
                    System.err.println("You have not been assigned this task yet!");
                }
            }
        }
        else {
            System.err.println("You are not the creator!");
        }
        return null;
    }

    public static String finishTask(String userId, String taskId, String action, String comment, MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("das");
        MongoCollection<Task> taskCollection = database.getCollection("tasks", Task.class);
        MongoCollection<Article> articleCollection = database.getCollection("articles", Article.class);
        MongoCollection<Workflow> workflowsCollection = database.getCollection("workflows", Workflow.class);
        Task task = taskCollection.find(eq("_id", new ObjectId(taskId))).first();
        String articleId = task.getArticleId();
        Article article = articleCollection.find(eq("_id", new ObjectId(articleId))).first();
        String workflowId = article.getWorkflowId();
        String creatorId = article.getCreatorId();
        System.out.println(task);
        if(article.getLatestTaskId().equals(taskId)) {
            if(task.getSentToId().equals(userId) && task.getStatus().equals("PENDING")) {
                Workflow worflow = workflowsCollection.find(eq("_id", new ObjectId(workflowId))).first();
                if(action.equals("REJECT")) {
                    // Create new task for creator
                    String newTaskId = Task.createTask(articleId, userId, creatorId, comment, "PENDING", mongoClient);
                    // Change the latest task of article
                    Article.updateArticleTask(articleId, newTaskId, mongoClient);
                    // Update old task
                    Task.updateTaskStatus(taskId, "REJECTED", mongoClient);
                    return newTaskId;
                }
                else {
                    // Check if someone is next in line
                    List<String> members = worflow.getMembers();
                    int index = members.indexOf(userId);
                    if(index == (members.size() - 1)) {
                        // No new task to be created
                        Article.updateArticleTask(articleId, "", mongoClient);
                        // Final approval done
                        Article.updateArticleStatus(articleId, "APPROVED", mongoClient);
                        // Update old task
                        Task.updateTaskStatus(taskId, "APPROVED", mongoClient);
                        return "";
                    }
                    else {
                        String nextUserId = members.get(index+1);
                        // Create new task for next approver
                        String newTaskId = Task.createTask(articleId, userId, nextUserId, comment, "PENDING", mongoClient);
                        // Change the latest task of article
                        Article.updateArticleTask(articleId, newTaskId, mongoClient);
                        // Update old task
                        Task.updateTaskStatus(taskId, "APPROVED", mongoClient);
                        return newTaskId;
                    }
                }
            }
            else {
                System.out.println("This task has not been assigned to you");
            }
        }
        else {
            System.err.println("This is not the current task");
        }
        return null;
    }

}
