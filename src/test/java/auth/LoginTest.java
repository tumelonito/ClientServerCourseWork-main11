package auth;

import controllers.auth.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import services.BaseIntegrationTest;
import services.LoginService;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Authentication Logic Integration Tests")
class LoginTest extends BaseIntegrationTest {

    private LoginService loginService;


    @BeforeEach
    void setUp() throws SQLException {
        cleanDatabase();
        this.loginService = new LoginService();
    }

    @Test
    @DisplayName("Should successfully sign up a new user")
    void testRegistration() {
        System.out.println("--- Running test: testRegistration ---");
        String uniqueLogin = "user_" + System.currentTimeMillis();
        LoginRequest request = new LoginRequest(uniqueLogin, "strong_password");

        boolean wasSuccessful = loginService.signUp(request);

        assertThat(wasSuccessful).isTrue();
    }

    @Test
    @DisplayName("Should successfully sign in an existing user")
    void testSuccessfulSignIn() {
        System.out.println("--- Running test: testSuccessfulSignIn ---");
        LoginRequest request = new LoginRequest("existingUser", "password123");
        loginService.signUp(request);

        boolean canSignIn = loginService.signIn(request);

        assertThat(canSignIn).isTrue();
    }

    @Test
    @DisplayName("Should fail to sign in with an incorrect password")
    void testFailedSignIn() {
        System.out.println("--- Running test: testFailedSignIn ---");
        // Arrange
        LoginRequest signUpRequest = new LoginRequest("anotherUser", "correct_password");
        loginService.signUp(signUpRequest);

        // Act
        LoginRequest signInRequest = new LoginRequest("anotherUser", "WRONG_password");
        boolean canSignIn = loginService.signIn(signInRequest);

        // Assert
        assertThat(canSignIn).isFalse();
    }

    @Test
    @DisplayName("Should fail to sign up a user with a duplicate login")
    void testDuplicateRegistration() {
        System.out.println("--- Running test: testDuplicateRegistration ---");
        LoginRequest request = new LoginRequest("duplicateUser", "password");
        boolean firstSignUp = loginService.signUp(request);

        boolean secondSignUp = loginService.signUp(request); // Try to sign up again with the same login

        assertThat(firstSignUp).isTrue();
        assertThat(secondSignUp).isFalse(); // Should fail because the login is unique
    }
}