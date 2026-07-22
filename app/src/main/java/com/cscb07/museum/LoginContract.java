package com.cscb07.museum;

import com.google.firebase.auth.FirebaseUser;

/**
 * Defines the contract between the Login view, presenter, and model.
 * Following the Model-View-Presenter (MVP) architectural pattern.
 */
public interface LoginContract {

    /**
     * Interface for the Login View (implemented by Fragments or Activities).
     * Responsible for UI updates and navigation.
     */
    interface View {
        /** Navigates to the Home screen after successful authentication. */
        void navigateToHome();

        /** Updates the UI to display the login form. */
        void switchToLogin();

        /** Updates the UI to display the signup form. */
        void switchToSignup();

        /** Displays an error message to the user (e.g., via Toast). */
        void showErrorMessage(String message);

        /** Displays a success message to the user. */
        void showSuccessMessage(String message);

        /** Shows an error on the username input field. */
        void showInvalidUsername();

        /** Shows an error on the email input field. */
        void showInvalidEmail();

        /** Shows an error on the password input field. */
        void showInvalidPassword();

        /** Shows an error when password and confirmation do not match. */
        void showPasswordsDoNotMatch();

        /** Clears any error message from the username field. */
        void clearUsernameError();

        /** Clears any error message from the email field. */
        void clearEmailError();

        /** Clears any error message from the password field. */
        void clearPasswordError();

        /** Clears any error message from the confirm password field. */
        void clearConfirmPasswordError();
    }

    /**
     * Interface for the Login Presenter.
     * Acts as a mediator between View and Model, handling business logic.
     */
    interface Presenter {
        /** Checks if a user is already logged in and navigates to home if they are. */
        void checkIsLoggedIn();

        /** Initiates the login process with email and password. */
        void login(String email, String password);

        /** Initiates the signup process with user details. */
        void signup(String username, String email, String password, String confirmPassword);

        /** Validates email format and updates View accordingly. */
        boolean checkEmail(String email);

        /** Validates password criteria (e.g., length) and updates View accordingly. */
        boolean checkPassword(String password);

        /** Validates that username is not empty. */
        boolean checkUsername(String username);

        /** Validates that password and confirmPassword are identical. */
        boolean checkConfirmPassword(String password, String confirmPassword);
    }

    /**
     * Interface for the Login Model.
     * Handles data operations and authentication (e.g., via Firebase).
     */
    interface Model {
        /** Callback interface for authentication results. */
        interface LoginListener {
            /** Called when the authentication operation succeeds. */
            void onSuccess();

            /** Called when the authentication operation fails with an error message. */
            void onFailure(String message);
        }

        /** Returns the currently authenticated Firebase user, or null if none. */
        FirebaseUser getCurrentUser();

        /** Performs login using Firebase Authentication. */
        void login(String email, String password, LoginListener listener);

        /** Performs user registration and stores additional user data in the database. */
        void signup(String email, String password, String username, LoginListener listener);

        /** Signs out the current user. */
        void logout();
    }
}
