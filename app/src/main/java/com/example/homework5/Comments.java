package com.example.homework5;

import java.util.Date;

public class Comments {
    String commentUserName,commentDescription, commentUserId, documentId;
    Date commentTime;

    public Comments(String commentUserName, String commentDescription, String commentUserId, String documentId, Date commentTime) {
        this.commentUserName = commentUserName;
        this.commentDescription = commentDescription;
        this.commentUserId = commentUserId;
        this.documentId = documentId;
        this.commentTime = commentTime;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getCommentUserId() {
        return commentUserId;
    }

    public void setCommentUserId(String commentUserId) {
        this.commentUserId = commentUserId;
    }

    public Date getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(Date commentTime) {
        this.commentTime = commentTime;
    }

    public String getCommentDescription() {
        return commentDescription;
    }

    public void setCommentDescription(String commentDescription) {
        this.commentDescription = commentDescription;
    }

    public String getCommentUserName() {
        return commentUserName;
    }

    public void setCommentUserName(String commentUserName) {
        this.commentUserName = commentUserName;
    }

    @Override
    public String toString() {
        return "Comments{" +
                "commentUserName='" + commentUserName + '\'' +
                ", commentDescription='" + commentDescription + '\'' +
                ", commentUserId='" + commentUserId + '\'' +
                ", documentId='" + documentId + '\'' +
                ", commentTime=" + commentTime +
                '}';
    }
}
