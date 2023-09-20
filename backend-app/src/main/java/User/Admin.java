package User;

import org.springframework.stereotype.Service;

@Service
public class Admin extends User {
    public User createUser() {
        return new User();
    }

    public User editUser() {
        return new User();
    }

    public boolean deleteUser() {
        return true;
    }

    public User viewUser() {
        return new User();
    }
}
