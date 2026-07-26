package com.cscb07.museum;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.firebase.auth.FirebaseUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class LoginPresenterTest {

    @Mock
    private LoginContract.View view;

    @Mock
    private LoginContract.Model model;

    @Mock
    private FirebaseUser firebaseUser;

    private LoginPresenter presenter;

    @Before
    public void setUp() {
        presenter = new LoginPresenter(view, model);
    }

    @Test
    public void testLoginSuccess() {
        doAnswer((Answer<Void>) invocation -> {
            LoginContract.Model.LoginListener listener = invocation.getArgument(2);
            listener.onSuccess();
            return null;
        }).when(model).login(eq("test@example.com"), eq("password123"), any(LoginContract.Model.LoginListener.class));

        presenter.login("test@example.com", "password123");

        verify(view).navigateToHome();
    }

    @Test
    public void testLoginFailure() {
        doAnswer((Answer<Void>)invocation -> {
            LoginContract.Model.LoginListener listener = invocation.getArgument(2);
            listener.onFailure("Invalid credentials");
            return null;
        }).when(model).login(eq("test@example.com"), eq("wrongpassword"), any(LoginContract.Model.LoginListener.class));

        presenter.login("test@example.com", "wrongpassword");

        verify(view).showErrorMessage("Invalid credentials");
    }

    @Test
    public void testLoginInvalidEmail() {
        presenter.login("invalid-email", "password123");

        verify(view).showErrorMessage("Some fields are invalid");
    }

    @Test
    public void testSignupSuccess() {
        doAnswer((Answer<Void>) invocation -> {
            LoginContract.Model.LoginListener listener = invocation.getArgument(3);
            listener.onSuccess();
            return null;
        }).when(model).signup(eq("test@example.com"), eq("password123"), eq("user1"), any(LoginContract.Model.LoginListener.class));

        presenter.signup("user1", "test@example.com", "password123", "password123");

        verify(view).navigateToHome();
    }

    @Test
    public void testSignupFailure() {
        doAnswer((Answer<Void>) invocation -> {
            LoginContract.Model.LoginListener listener = invocation.getArgument(3);
            listener.onFailure("Email already in use");
            return null;
        }).when(model).signup(eq("test@example.com"), eq("password123"), eq("user1"), any(LoginContract.Model.LoginListener.class));

        presenter.signup("user1", "test@example.com", "password123", "password123");

        verify(view).showErrorMessage("Email already in use");
    }

    @Test
    public void testSignupInvalid() {
        presenter.signup("UwU", "valid-email@valid.com", "password", "mismatch");

        verify(view).showErrorMessage("Some fields are invalid");
    }

    @Test
    public void testCheckIsLoggedInTrue() {
        when(model.getCurrentUser()).thenReturn(firebaseUser);
        presenter.checkIsLoggedIn();
        verify(view).navigateToHome();
    }

    @Test
    public void testCheckIsLoggedInFalse() {
        when(model.getCurrentUser()).thenReturn(null);
        presenter.checkIsLoggedIn();
        verify(view, never()).navigateToHome();
    }

    @Test
    public void testCheckEmailValid() {
        presenter.checkEmail("test@example.com");
        verify(view).clearEmailError();
    }

    @Test
    public void testCheckEmailInvalid() {
        presenter.checkEmail("invalid-email");
        verify(view).showInvalidEmail();
    }

    @Test
    public void testCheckPasswordValid() {
        presenter.checkPassword("123456");
        verify(view).clearPasswordError();
    }

    @Test
    public void testCheckPasswordInvalid() {
        presenter.checkPassword("123");
        verify(view).showInvalidPassword();
    }

    @Test
    public void testCheckConfirmPasswordMatch() {
        presenter.checkConfirmPassword("password123", "password123");
        verify(view).clearConfirmPasswordError();
    }

    @Test
    public void testCheckConfirmPasswordMismatch() {
        presenter.checkConfirmPassword("password123", "different");
        verify(view).showPasswordsDoNotMatch();
    }

    @Test
    public void testCheckUsernameValid() {
        presenter.checkUsername("user123");
        verify(view).clearUsernameError();
    }

    @Test
    public void testCheckUsernameInvalid() {
        presenter.checkUsername("");
        verify(view).showInvalidUsername();
    }
}
