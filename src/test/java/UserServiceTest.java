import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.revature.loandb.dao.UserDao;
import com.revature.loandb.model.User;
import com.revature.loandb.service.UserService;

public class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test if a user can be registered with valid data. This test should pass
    @Test
    public void testRegisterUserWithValidData() {
        when(userDao.getUserByUsername("newuser")).thenReturn(null);

        boolean result = userService.registerUser("newuser", "password", "user");
        assertTrue(result);
    }

    // Test if a user can be registered with existing data. This test should fail

    @Test
    public void testRegisterUserAlreadyExists() {
        User user = new User();
        user.setUsername("Scott Lang");
        when(userDao.getUserByUsername("Scott Lang")).thenReturn(user);

        boolean result = userService.registerUser("Scott Lang", "antman", "user");
        assertFalse(result);
    }

    // Test if a user can be logged in with invalid password. This test should fail

    @Test
    public void testLoginUserInvalidPassword() {
        User user = new User();
        user.setUsername("testuser");
        user.setPasswordHash(BCrypt.hashpw("password", BCrypt.gensalt()));
        when(userDao.getUserByUsername("testuser")).thenReturn(user);

        boolean result = userService.loginUser("testuser", "wrongpassword");
        assertFalse(result);
    }

}