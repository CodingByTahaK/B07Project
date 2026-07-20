package com.cscb07.museum;

/**
 * Presenter class for the Login/Signup screen.
 * Implements the business logic and coordinates communication between the View and Model.
 */
public class LoginPresenter implements LoginContract.Presenter {
    private LoginContract.View view;
    private LoginContract.Model model;

    /**
     * Initializes the presenter with a view and a model.
     * @param view The view interface to update.
     * @param model The model interface for data operations.
     */
    public LoginPresenter(LoginContract.View view, LoginContract.Model model) {
        this.view = view;
        this.model = model;
    }

    /**
     * Attempts to log in a user. Validates input before calling the model.
     * @param email User's email string.
     * @param password User's password string.
     */
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

    /**
     * Attempts to register a new user. Validates all inputs before calling the model.
     * @param username Desired username.
     * @param email User's email.
     * @param password User's password.
     * @param confirmPassword User re-entering the password
     */
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

    /**
     * Validates the email format using a regular expression.
     * @param email The email to validate.
     * @return true if valid, false otherwise.
     */
    @Override
    public boolean checkEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        if (email != null && email.matches(emailPattern)) {
            view.clearEmailError();
            return true;
        }
        view.showInvalidEmail();
        return false;
    }

    /**
     * Validates the password (must be at least 6 characters).
     * @param password The password to validate.
     * @return true if valid, false otherwise.
     */
    @Override
    public boolean checkPassword(String password) {
        if (password != null && password.length() >= 6) {
            view.clearPasswordError();
            return true;
        }
        view.showInvalidPassword();
        return false;
    }

    /**
     * Validates that the username is not null or empty.
     * @param username The username to validate.
     * @return true if valid, false otherwise.
     */
    @Override
    public boolean checkUsername(String username) {
        if (username != null && !username.isEmpty()) {
            view.clearUsernameError();
            return true;
        }
        view.showInvalidUsername();
        return false;
    }

    /**
     * Validates that the password and its confirmation match.
     * @param password The original password.
     * @param password_confirm The confirmed password.
     * @return true if they match, false otherwise.
     */
    @Override
    public boolean checkConfirmPassword(String password, String password_confirm) {
        if (password != null && password.equals(password_confirm)) {
            view.clearConfirmPasswordError();
            return true;
        }
        view.showPasswordsDoNotMatch();
        return false;
    }

    /**
     * Checks if a user session already exists.
     * If so, instructs the view to skip login and go to home.
     */
    @Override
    public void checkIsLoggedIn() {
        if (model.getCurrentUser() != null) {
            view.navigateToHome();
        }
    }
}
