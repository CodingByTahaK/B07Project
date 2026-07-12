package com.cscb07.museum;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class LoginFragment extends Fragment implements LoginContract.View {
    private TextView title;
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPassword;
    private Button loginButton;
    private Button switchButton;
    private LoginPresenter presenter;

    private boolean isLoginMode = true;

    public LoginFragment() {
        // Required empty public constructor
    }

    public abstract class AfterTextChangedWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
    }

    public class EmailWatcher extends AfterTextChangedWatcher {
        @Override
        public void afterTextChanged(Editable s) {
            presenter.checkEmail(s.toString());
        }
    }

    public class PasswordWatcher extends AfterTextChangedWatcher {
        @Override
        public void afterTextChanged(Editable s) {
            presenter.checkPassword(s.toString());
        }
    }

    public class UsernameWatcher extends AfterTextChangedWatcher {
        @Override
        public void afterTextChanged(Editable s) {
            presenter.checkUsername(s.toString());
        }
    }

    public class ConfirmPasswordWatcher extends AfterTextChangedWatcher {
        @Override
        public void afterTextChanged(Editable s) {
            presenter.checkConfirmPassword(passwordEditText.getText().toString(), s.toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_screen, container, false);

        title = view.findViewById(R.id.title);
        usernameEditText = view.findViewById(R.id.username_input);
        emailEditText = view.findViewById(R.id.email_input);
        passwordEditText = view.findViewById(R.id.password_input);
        loginButton = view.findViewById(R.id.login_button);
        switchButton = view.findViewById(R.id.switch_button);
        confirmPassword = view.findViewById(R.id.password_confirm);

        presenter = new LoginPresenter(this, new LoginModel());
        presenter.checkIsLogined();

        emailEditText.addTextChangedListener(new EmailWatcher());
        passwordEditText.addTextChangedListener(new PasswordWatcher());
        usernameEditText.addTextChangedListener(new UsernameWatcher());
        confirmPassword.addTextChangedListener(new ConfirmPasswordWatcher());

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            if (isLoginMode) {
                presenter.login(email, password);
            } else {
                String username = usernameEditText.getText().toString();
                String confirm = confirmPassword.getText().toString();
                presenter.signup(username, email, password, confirm);
            }
        });

        switchButton.setOnClickListener(v -> {
            if (isLoginMode) {
                switchToSignup();
            } else {
                switchToLogin();
            }
        });

        switchToLogin();

        return view;
    }

    @Override
    public void showErrorMessage(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSuccessMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showInvalidUsername() {
        usernameEditText.setError(getString(R.string.invalid_username));
    }

    @Override
    public void showInvalidEmail() {
        emailEditText.setError(getString(R.string.invalid_email));
    }

    @Override
    public void showInvalidPassword() {
        passwordEditText.setError(getString(R.string.invalid_password));
    }

    @Override
    public void showPasswordsDoNotMatch() {
        confirmPassword.setError(getString(R.string.password_not_same));
    }
    @Override
    public void clearUsernameError() {
        usernameEditText.setError(null);
    }

    @Override
    public void clearEmailError() {
        emailEditText.setError(null);
    }

    @Override
    public void clearPasswordError() {
        passwordEditText.setError(null);
    }

    @Override
    public void clearConfirmPasswordError() {
        confirmPassword.setError(null);
    }

    @Override
    public void switchToLogin() {
        isLoginMode = true;
        title.setText(R.string.login_title);
        loginButton.setText(R.string.login_button_label);
        switchButton.setText(R.string.switch_signup_label);
        usernameEditText.setVisibility(View.GONE);
        confirmPassword.setVisibility(View.GONE);
    }

    @Override
    public void switchToSignup() {
        isLoginMode = false;
        title.setText(R.string.signup_title);
        loginButton.setText(R.string.signup_button_label);
        switchButton.setText(R.string.switch_login_label);
        usernameEditText.setVisibility(View.VISIBLE);
        confirmPassword.setVisibility(View.VISIBLE);
    }
    @Override
    public void navigateToHome() {
        Fragment homeFragment = new HomeFragment();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, homeFragment);
        transaction.commit();
    }
}
