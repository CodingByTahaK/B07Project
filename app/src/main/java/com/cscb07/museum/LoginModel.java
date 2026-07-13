package com.cscb07.museum;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
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

    @Override
    public void logout() {
        mAuth.signOut();
    }
}
