package control.auth;

import entity.user.User;
import data.UserDataManager;
import util.PasswordUtil;

public class AuthenticationService {
	private UserDataManager userDataManager;

	public AuthenticationService(UserDataManager userDataManager) {
		this.userDataManager = userDataManager;
	}

	/**
	 * Authenticates a user based on the provided NRIC and password.
	 * 
	 * @param NRIC     The NRIC of the user.
	 * @param password The password of the user.
	 * @return The User object if authentication is successful, null otherwise.
	 */
	public User authenticate(String NRIC, String password) {
		User user = userDataManager.getUserByNRIC(NRIC);
		if (user == null) {
			System.out.println("User not found.");
		} else if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
			System.out.println("Incorrect password.");
		} else {
			return user;
		}
		return null;
	}

	/**
	 * Changes the password for a given user.
	 * 
	 * @param user        The user object for whom the password needs to be changed.
	 * @param newPassword The new password to set.
	 * @return True if the password change is successful, false otherwise.
	 */
	public boolean changePassword(User user, String newPassword) {
		String hashedPassword = PasswordUtil.hashPassword(newPassword);
		user.setPassword(hashedPassword);
		return userDataManager.updateUser(user);
	}
}