package com.example.homework5;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class Forums implements Serializable {
    String forumDescription, forumName, forumTitle, forumId, documentId;
    Date forumTime;
    HashMap<String,Object> userLikes = new HashMap<>();

    public Forums(String forumDescription, String forumName, String forumTitle, String forumId, String documentId, Date forumTime, HashMap<String, Object> userLikes) {
        this.forumDescription = forumDescription;
        this.forumName = forumName;
        this.forumTitle = forumTitle;
        this.forumId = forumId;
        this.documentId = documentId;
        this.forumTime = forumTime;
        this.userLikes = userLikes;
    }

    public HashMap<String, Object> getHashMap() {
        return userLikes;
    }

    public void setHashMap(HashMap<String, Object> userLikes) {
        this.userLikes = userLikes;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getForumId() {
        return forumId;
    }

    public void setForumId(String forumId) {
        this.forumId = forumId;
    }

    public String getForumDescription() {
        return forumDescription;
    }

    public void setForumDescription(String forumDescription) {
        this.forumDescription = forumDescription;
    }

    public String getForumName() {
        return forumName;
    }

    public void setForumName(String forumName) {
        this.forumName = forumName;
    }

    public String getForumTitle() {
        return forumTitle;
    }

    public void setForumTitle(String forumTitle) {
        this.forumTitle = forumTitle;
    }

    public Date getForumTime() {
        return forumTime;
    }

    public void setForumTime(Date forumTime) {
        this.forumTime = forumTime;
    }

    @Override
    public String toString() {
        return "Forums{" +
                "forumDescription='" + forumDescription + '\'' +
                ", forumName='" + forumName + '\'' +
                ", forumTitle='" + forumTitle + '\'' +
                ", forumId='" + forumId + '\'' +
                ", documentId='" + documentId + '\'' +
                ", forumTime=" + forumTime +
                ", userLikes=" + userLikes +
                '}';
    }

}
