package com.cscb07.museum;

import com.google.firebase.auth.FirebaseUser;

public interface LoginContract {
    interface View {
        void navigateToHome();
        void switchToLogin();
        void switchToSignup();
        void showErrorMessage(String message);
        void showSuccessMessage(String message);
        void showInvalidUsername();
        void showInvalidEmail();
        void showInvalidPassword();
        void showPasswordsDoNotMatch();
        void clearUsernameError();
        void clearEmailError();
        void clearPasswordError();
        void clearConfirmPasswordError();

    }

    interface Presenter {
        void checkIsLogined();
        void login(String email, String password);
        void signup(String username, String email, String password, String confirmPassword);
        boolean checkEmail(String email);
        boolean checkPassword(String password);
        boolean checkUsername(String username);
        boolean checkConfirmPassword(String password, String confirmPassword);
    }

    interface Model {
        interface LoginListener {
            void onSuccess();
            void onFailure(String message);
        }
        FirebaseUser getCurrentUser();
        void login(String email, String password, LoginListener listener);
        void signup(String email, String password, String username, LoginListener listener);
        void logout();
    }
}
