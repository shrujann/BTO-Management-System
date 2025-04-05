public class User{
    private String NRIC;
    private String password;
    private int age;
    private String maritalStatus;

    public User(String NRIC, int age, String maritalStatus){
        this.NRIC = NRIC;
        this.password = "password";
        this.age = age;
        this.maritalStatus = maritalStatus;
    }

    public void setPassword(String newPassword){
        this.password = newPassword;
    }

    public void setNRIC(String NRIC){
        this.NRIC = NRIC;
    }

    public void setAge(int age){
        this.age = age;
    }

    public void setMaritalStatus(String maritalStatus){
        this.maritalStatus = maritalStatus;
    }

    public String getNRIC(){
        return this.NRIC;
    }

    public int getAge(){
        return this.age;
    }

    public String getMaritalStatus(){
        return this.maritalStatus;
    }

    public String getPassword(){
        return this.password;
    }
}