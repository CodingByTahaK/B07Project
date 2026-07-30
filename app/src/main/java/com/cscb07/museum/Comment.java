package com.cscb07.museum;

public class Comment {
    private String commentID;
    private String userID;
    private String username;
    private String comment;

    public Comment() {
    }

    public Comment (String commentID, String userID, String username, String comment) {
        this.commentID = commentID;
        this.userID = userID;
        this.username = username;
        this.comment = comment;
    }

    public String getCommentID() {
        return commentID;
    }
    
    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getUserID() {
        return userID;
    }
    
    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}