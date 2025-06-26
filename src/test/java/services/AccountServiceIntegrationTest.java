package services;

import models.Account;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AccountService Integration Tests")
class AccountServiceIntegrationTest extends BaseIntegrationTest {

    private AccountService accountService;
    private LoginService loginService;

    @BeforeEach
    void setUp() throws SQLException {
        cleanDatabase();
        this.accountService = new AccountService();
        this.loginService = new LoginService();
        loginService.signUp(new controllers.auth.LoginRequest("testUser", "password"));
        loginService.signUp(new controllers.auth.LoginRequest("anotherUser", "password"));
    }

    @Nested
    @DisplayName("Account Creation")
    class AccountCreationTests {

        @Test
        @DisplayName("Should create a new account for an existing user")
        void shouldCreateAccountSuccessfully() {
            System.out.println("--- Running test: shouldCreateAccountSuccessfully ---");
            Account account = new Account("My Main Bank", "Primary bank account", "USD", 0);

            long newId = accountService.createAccount(account, "testUser");
            Optional<Account> savedAccount = accountService.getAccountById(newId, "testUser");

            assertThat(newId).isGreaterThan(0);
            assertThat(savedAccount).isPresent();
            assertThat(savedAccount.get().getName()).isEqualTo("My Main Bank");
        }
    }

    @Nested
    @DisplayName("Account Retrieval")
    class AccountRetrievalTests {

        @Test
        @DisplayName("Should retrieve a user's own account by ID")
        void shouldRetrieveOwnAccount() {
            System.out.println("--- Running test: shouldRetrieveOwnAccount ---");
            long accountId = accountService.createAccount(new Account("My Crypto", "", "USD", 0), "testUser");

            Optional<Account> foundAccount = accountService.getAccountById(accountId, "testUser");

            assertThat(foundAccount).isPresent();
            assertThat(foundAccount.get().getId()).isEqualTo(accountId);
        }

        @Test
        @DisplayName("Should NOT retrieve an account belonging to another user")
        void shouldNotRetrieveOthersAccount() {
            System.out.println("--- Running test: shouldNotRetrieveOthersAccount ---");
            long accountId = accountService.createAccount(new Account("Secret Account", "", "USD", 0), "anotherUser");

            Optional<Account> foundAccount = accountService.getAccountById(accountId, "testUser");

            assertThat(foundAccount).isNotPresent();
        }

        @Test
        @DisplayName("Should retrieve all accounts for a specific user")
        void shouldRetrieveAllUserAccounts() {
            System.out.println("--- Running test: shouldRetrieveAllUserAccounts ---");
            accountService.createAccount(new Account("Bank 1", "", "UAH", 0), "testUser");
            accountService.createAccount(new Account("Bank 2", "", "EUR", 0), "testUser");
            accountService.createAccount(new Account("Another User's Bank", "", "USD", 0), "anotherUser");

            List<Account> userAccounts = accountService.getAllAccounts("testUser");

            assertThat(userAccounts).hasSize(2);
        }
    }

    @Nested
    @DisplayName("Account Deletion")
    class AccountDeletionTests {

        @Test
        @DisplayName("Should delete an account successfully")
        void shouldDeleteAccount() {
            System.out.println("--- Running test: shouldDeleteAccount ---");
            long accountId = accountService.createAccount(new Account("Account to Delete", "", "USD", 0), "testUser");

            boolean isDeleted = accountService.deleteAccount(accountId, "testUser");
            Optional<Account> foundAccount = accountService.getAccountById(accountId, "testUser");

            assertThat(isDeleted).isTrue();
            assertThat(foundAccount).isNotPresent();
        }

        @Test
        @DisplayName("Should NOT delete an account belonging to another user")
        void shouldNotDeleteOthersAccount() {
            System.out.println("--- Running test: shouldNotDeleteOthersAccount ---");
            long accountId = accountService.createAccount(new Account("Protected Account", "", "USD", 0), "anotherUser");

            boolean isDeleted = accountService.deleteAccount(accountId, "testUser");

            assertThat(isDeleted).isFalse();
        }
    }
}