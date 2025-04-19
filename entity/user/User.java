package entity.user;
public class User {
    private String name;
    private String userID;
    private int age;
    private String maritalStatus;
    private String password;

    public User(String name, String userID, int age, String maritalStatus, String password){
        this.name = name;
        this.userID = userID;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.password = password;
    }

    public String getName(){
        return name;
    }
    
    public String getUserID(){
        return userID;
    }

    public int getAge(){
        return age;
    }

    public String getMaritalStatus(){
        return maritalStatus;
    }

    public String getPassword(){
        return password;
    }

    @Override
    public String toString(){
        return "User{name='" + name + "', userID='" + userID + "', age=" + age +
               ", maritalStatus='" + maritalStatus + "', password='" + password + "'}";
    }
}
