package com.cscb07.museum;

public class LoginPresenter implements LoginContract.Presenter {
    private LoginContract.View view;
    private LoginContract.Model model;

    public LoginPresenter(LoginContract.View view, LoginContract.Model model) {
        this.view = view;
        this.model = model;
    }


    @Override
    public void login(String email, String password) {
        if (!checkEmail(email) || !checkPassword(password)) {
            view.showErrorMessage("Some fields are invalid");
            return;
        }

        model.login(email, password, new LoginContract.Model.LoginListener() {
            @Override
            public void onSuccess() {
                view.navigateToHome();
            }

            @Override
            public void onFailure(String message) {
                view.showErrorMessage(message);
            }
        });
    }

    @Override
    public void signup(String username, String email, String password, String confirmPassword) {
        if (!checkUsername(username) || !checkEmail(email) || !checkPassword(password) || !checkConfirmPassword(password, confirmPassword)) {
            view.showErrorMessage("Some fields are invalid");
            return;
        }

        model.signup(email, password, username, new LoginContract.Model.LoginListener() {
            @Override
            public void onSuccess() {
                view.navigateToHome();
            }

            @Override
            public void onFailure(String message) {
                view.showErrorMessage(message);
            }
        });
    }

    public boolean checkEmail(String email) {
        if (email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view.clearEmailError();
            return true;
        }
        view.showInvalidEmail();
        return false;
    }

    public boolean checkPassword(String password) {
        if (password != null && password.length() >= 6) {
            view.clearPasswordError();
            return true;
        }
        view.showInvalidPassword();
        return false;
    }

    public boolean checkUsername(String username) {
        if (username != null && !username.isEmpty()) {
            view.clearUsernameError();
            return true;
        }
        view.showInvalidUsername();
        return false;
    }

    public boolean checkConfirmPassword(String password, String password_confirm) {
        if (password != null && password.equals(password_confirm)) {
            view.clearConfirmPasswordError();
            return true;
        }
        view.showPasswordsDoNotMatch();
        return false;
    }

    @Override
    public void checkIsLogined() {
        if (model.getCurrentUser() != null) {
            view.navigateToHome();
        }
    }


}
