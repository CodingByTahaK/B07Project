package com.cscb07.museum;

public interface LoginContract {
    interface View {
        void navigateToMain();
        void navigateToSignup();
        void navigateToLogin();
        void showEmailError();
        void showPasswordError();
        void showErrorMessage(String message);
    }

    interface Presenter {
        void onLoginClicked(String email, String password);
        void onSignupClicked(String email, String password, String username);
        void onDestroy();
        void onChangeToLoginClicked();
        void onChangeToSignupClicked();
        void onLogoutClicked();
    }

    interface Model {
        interface LoginListener {
            void onSuccess();
            void onFailure(String message);
        }
        void login(String email, String password, LoginListener listener);
        void signup(String email, String password, String username, LoginListener listener);
        void logout();
    }
}
