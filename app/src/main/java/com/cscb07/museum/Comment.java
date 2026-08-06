package com.cscb07.museum;

/**
 * Represents a comment on an artifact
 * It stores the comment's unique ID, the user ID of the
 * user who posted it, the username of the user, and the 
 * text of the comment
 */

public class Comment {
    private String commentID;
    private String userID;
    private String username;
    private String comment;

    /**
     * Creates an empty comment object
     */
    public Comment() {
    }

    /**
     * Creates an comment object with the specified fields
     * @param commentID - the unique ID of each comment
     * @param userID - the unique ID of the user who posted the comment
     * @param username - the username of the user who posted the comment
     * @param comment - the text of the comment
     * 
     */
    public Comment (String commentID, String userID, String username, String comment) {
        this.commentID = commentID;
        this.userID = userID;
        this.username = username;
        this.comment = comment;
    }

    /**
     * Returns the unique ID of the comment
     * @return commentID - the unique ID of the comment
     */
    public String getCommentID() {
        return commentID;
    }

    /**
     * Sets the unique ID of the comment
     * @param commentID - string of the new commentID
     */
    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    /**
     * Returns the unique ID of the user
     * @return userID - the unique ID of the user
     */
    public String getUserID() {
        return userID;
    }
    
    /**
     * Sets the unique ID of the user
     * @param userID - string of the new userID
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * Returns the username of the user
     * @return username- the username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user
     * @param username - the username of the user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the comment text
     * @return comment - the comment text
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the text of the comment
     * @param comment - the new text for the comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }
}