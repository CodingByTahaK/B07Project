package com.cscb07.museum;

public class LoginPresenter implements LoginContract.Presenter {
    private LoginContract.View view;
    private LoginContract.Model model;

    public LoginPresenter(LoginContract.View view, LoginContract.Model model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void onDestroy() {
        this.view = null;
    }

    @Override
    public void onLoginClicked(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            view.showErrorMessage("Email and password cannot be empty");
            return;
        }

        model.login(email, password, new LoginContract.Model.LoginListener() {
            @Override
            public void onSuccess() {
                view.navigateToMain();
            }

            @Override
            public void onFailure(String message) {
                view.showErrorMessage(message);
            }
        });
    }

    @Override
    public void onChangeToSignupClicked() {
        view.navigateToSignup();
    }

    @Override
    public void onChangeToLoginClicked() {
        view.navigateToLogin();
    }

    @Override
    public void onSignupClicked(String email, String password, String username) {
        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            view.showErrorMessage("All fields are required");
            return;
        }

        model.signup(email, password, username, new LoginContract.Model.LoginListener() {
            @Override
            public void onSuccess() {
                view.navigateToMain();
            }

            @Override
            public void onFailure(String message) {
                view.showErrorMessage(message);
            }
        });
    }

    @Override
    public void onLogoutClicked() {
        model.logout();
        view.navigateToLogin();
    }
}
