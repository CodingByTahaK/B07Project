package com.cscb07.museum;

import java.util.ArrayList;

public class User {
	private String email;
	private String username;
	private String userID;
	private String userType;
	private ArrayList<String> savedArtifacts;
	
	public User() {
		savedArtifacts = new ArrayList<>();
	}
	
	public User(String userID, String email, String username, String userType) {
		this.email = email;
		this.username = username;
		this.userID = userID;
		this.userType = userType;
		this.savedArtifacts = new ArrayList<>();
	}
	
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String newEmail) {
		this.email = newEmail;
	}
	
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String newUsername) {
		this.username = newUsername;
	}
	
	
	public String getUid() {
		return userID;
	}

	public void setUid(String newUserID) {
		this.userID = newUserID;
	}
	
	
	public String getUserType() {
		return userType;
	}

	public void setUserType(String newUserType) {
		this.userType = newUserType;
	}
	
	
	public ArrayList<String> getSavedArtifacts(){
		if (savedArtifacts == null) {
			savedArtifacts = new ArrayList<>();
		}
		return savedArtifacts;
	}
	
	public void setSavedArtifacts(ArrayList<String> newSavedArtifacts) {
		this.savedArtifacts = newSavedArtifacts;
	}
	
	
	public boolean checkAdmin() {
		if (userType.equals("admin")) {
			return true;
		}
		return false;
	}
}