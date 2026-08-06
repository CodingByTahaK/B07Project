package com.cscb07.museum;

import java.util.ArrayList;

/**
 * Represents a user 
 * Stores the user's unique ID, email, username, and userType (admin / regular)
 */
public class User {
	private String email;
	private String username;
	private String userID;
	private String userType = "user"; // avoid NullPointerException
	private ArrayList<String> savedArtifacts;
	
	/**
	 * Creates an empty User object
	 * Initializes saved artifact list
	 */
	public User() {
		savedArtifacts = new ArrayList<>();
	}
	
	/**
	 * Creates a user object with the specified fields
	 * @param userID - the unique ID of the user
	 * @param email - the email of the user
	 * @param username - the username of the user
	 * @param userType - the type of user (regular or admin)
	 */
	public User(String userID, String email, String username, String userType) {
		this.email = email;
		this.username = username;
		this.userID = userID;
		this.userType = userType;
		this.savedArtifacts = new ArrayList<>();
	}
	
	/**
	 * Returns the user's email
	 * @return email - the user's email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the email of the user
	 * @param newEmail - the email to assign the user
	 */
	public void setEmail(String newEmail) {
		this.email = newEmail;
	}
	
	/**
	 * Returns the user's username
	 * @return username - the user's username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the username of the user
	 * @param newUsername - the username to assign the user
	 */
	public void setUsername(String newUsername) {
		this.username = newUsername;
	}
	
	/** 
	 * Returns the user's user ID
	 * @return userID - the user ID
	 */
	public String getUid() {
		return userID;
	}

	/**
	 * Sets the user ID of the user
	 * @param newUserID - the user ID to assign the user
	 */
	public void setUid(String newUserID) {
		this.userID = newUserID;
	}
	
	/**
	 * Returns the user's type (regular or admin)
	 * @return userType - the user type (regular or admin)
	 */
	public String getUserType() {
		return userType;
	}

	/**
	 * Sets the user type of the user
	 * @param newUserType - the user type to assign the user
	 */
	public void setUserType(String newUserType) {
		this.userType = newUserType;
	}
	
	/**
	 * Returns the list of user's saved artifacts
	 * If the list is null, it is initialized
	 * @return savedArtifacts - the list of saved artifacts
	 */
	public ArrayList<String> getSavedArtifacts(){
		if (savedArtifacts == null) {
			savedArtifacts = new ArrayList<>();
		}
		return savedArtifacts;
	}

	/**
	 * Sets the user's saved artifacts list
	 * @param newSavedArtifacts - the list of saved artifacts to assign the user
	 */
	public void setSavedArtifacts(ArrayList<String> newSavedArtifacts) {
		this.savedArtifacts = newSavedArtifacts;
	}
	
	/**
	 * Checks whether the user is an admin user to allow for them
	 * to be granted admin privileges
	 * @return true if the user is an admin, and return false otherwise
	 */
	public boolean checkAdmin() {
		if (userType.equals("admin")) {
			return true;
		}
		return false;
	}
}