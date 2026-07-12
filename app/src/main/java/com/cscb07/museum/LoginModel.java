package com.cscb07.museum;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginModel implements LoginContract.Model {
    private final FirebaseAuth mAuth;
    public LoginModel() {
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

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

    @Override
    public void signup(String email, String password, String username, LoginContract.Model.LoginListener listener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user == null) throw new NullPointerException("Cannot obtain registered user");
                    /*
                        Store user details in firebase database: WIP
                        Need to implement the User class
                    */
                        listener.onSuccess();
                    } else {
                        listener.onFailure(task.getException() != null ? task.getException().getMessage() : "Registration failed");
                    }
                });
    }

    @Override
    public void logout() {
        mAuth.signOut();
    }
}
