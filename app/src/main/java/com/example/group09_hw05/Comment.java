package com.example.group09_hw05;

import com.google.firebase.Timestamp;

public class Comment {
    String forumId, commentId, commentCreator, commentText, commentCreatorUid;
    Timestamp commentCreatedAt;

    public Comment() {

    }

    public Comment(String forumId, String commentId, String commentCreator, String commentText,String commentCreatorUid, Timestamp commentCreatedAt) {
        this.forumId = forumId;
        this.commentId = commentId;
        this.commentCreator = commentCreator;
        this.commentText = commentText;
        this.commentCreatedAt = commentCreatedAt;
        this.commentCreatorUid = commentCreatorUid;
    }

    public String getForumId() {
        return forumId;
    }

    public void setForumId(String forumId) {
        this.forumId = forumId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getCommentCreator() {
        return commentCreator;
    }

    public void setCommentCreator(String commentCreator) {
        this.commentCreator = commentCreator;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public Timestamp getCommentCreatedAt() {
        return commentCreatedAt;
    }

    public void setCommentCreatedAt(Timestamp commentCreatedAt) {
        this.commentCreatedAt = commentCreatedAt;
    }

    public String getCommentCreatorUid() {
        return commentCreatorUid;
    }

    public void setCommentCreatorUid(String commentCreatorUid) {
        this.commentCreatorUid = commentCreatorUid;
    }
}
