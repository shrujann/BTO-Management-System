import java.util.*;

public class userController{
    private User user;
    private List<User> userList;

    // constructor with new list created for the users.
    public userController(){
        this.userList = new ArrayList<>();
    }

    // using composition to set new user password.
    public void changeUserPassword(String newPassword){
        user.setPassword(newPassword);
        System.out.println("Changed user's password successfully.");
    }

    // validate the NRIC and check if it follows the format.
    public boolean validateNRIC(String NRIC){
        String pattern = "^[ST]\\d{7}[A-Z]$";

        return NRIC != null && NRIC.matches(pattern);
    }

    // change the NRIC of a user to a valid NRIC.
    public void changeNRIC(String NRIC){
        if (validateNRIC(NRIC)) {
            user.setNRIC(NRIC);
            System.out.println("Changed user's NRIC successfully.");
        }
        else {
            System.err.println("The provided NRIC does not follow the format.");
        }
    }

    // add another user to the user list.
    public void addUsers(User user){
        this.userList.add(user);
    }

    // set a user to be a copy of another.
    public void setUser(User user){
        this.user = user;
    }

    // return the user list.
    public List<User> getUserList(){
        return this.userList;
    }

}
