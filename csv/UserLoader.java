package csv;

import entity.user.User;
import java.util.List;

public interface UserLoader {
    void readUsers(String filePath, String userType, List<User> users);
}