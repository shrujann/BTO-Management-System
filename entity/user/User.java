package entity.user;

/*
 * Represents a user in the BTO Management System.
 * Each user has unique User ID (NRIC), password, age, and marital status.
 */
public class User {
	private String userID;
	private String password;
	private int age;
	private String maritalStatus;
	private String name;

	/**
	 * Constructor for the User class.
	 * 
	 * @param userID        The user's NRIC. Must start with S or T, followed by 7
	 *                      digits, and end with a letter.
	 * @param password      The user's password. The default password is "password".
	 * @param age           The user's age.
	 * @param maritalStatus The user's marital status.
	 * @param name          The user's name.
	 */
	public User(String name, String userID, int age, String maritalStatus, String password) {
		this.name = name;
		this.userID = userID;
		this.password = password;
		this.age = age;
		this.maritalStatus = maritalStatus;
	}

	// Getters and setters for all attributes
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public String getName() {
		return name;
	}

	public void setname(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "User{name='" + name + "', userID='" + userID + "', age=" + age + ", maritalStatus='" + maritalStatus
				+ "'}";
	}
}