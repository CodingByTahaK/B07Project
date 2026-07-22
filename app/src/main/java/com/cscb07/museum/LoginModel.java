package com.cscb07.museum;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Model class handling Firebase Authentication and Database operations for Login/Signup.
 */
public class LoginModel implements LoginContract.Model {
    private final FirebaseAuth mAuth;

    public LoginModel() {
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * @return The currently logged-in Firebase user, or null if session is expired.
     */
    @Override
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    /**
     * Authenticates a user with Firebase Auth.
     * @param email User email.
     * @param password User password.
     * @param listener Callback to notify the presenter of the result.
     */
    @Override
    public void login(String email, String password, LoginContract.Model.LoginListener listener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onSuccess();
                    } else {
                        listener.onFailure(task.getException() != null ? task.getException().getMessage() : "Login failed");
                    }
                });
    }

    /**
     * Creates a new user in Firebase Auth and saves user profile data in Realtime Database.
     * @param email User email.
     * @param password User password.
     * @param username User display name.
     * @param listener Callback to notify the presenter of the result.
     */
    @Override
    public void signup(String email, String password, String username, LoginContract.Model.LoginListener listener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user == null) throw new NullPointerException("Cannot obtain registered user");

                        // Save additional user info (username, uid, type) to the 'users' node
                        DatabaseReference database = FirebaseDatabase.getInstance().getReference("users");
                        User newUser = new User(user.getUid(), user.getEmail(), username, "user");
                        database.child(user.getUid())
                            .setValue(newUser)
                            .addOnSuccessListener(new OnSuccessListener<Void>(){
                                @Override
                                public void onSuccess(Void unused) {
                                    listener.onSuccess();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener(){
                                @Override
                                public void onFailure(Exception exception) {
                                    listener.onFailure(exception.getMessage());
                                }
                            });

                    } else {
                        listener.onFailure(task.getException() != null ? task.getException().getMessage() : "Registration failed");
                    }
                });
    }

    /**
     * Logs the current user out of Firebase.
     */
    @Override
    public void logout() {
        mAuth.signOut();
    }
}
