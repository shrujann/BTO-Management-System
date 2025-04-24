package data;

import entity.user.User;
import entity.user.HDBOfficer; 
import entity.user.HDBManager; 
import java.util.List;
import java.util.ArrayList;

// This class will handle the in-memory data management for users in the BTO Management System
public class UserDataManager {
	private List<User> users;

	public UserDataManager() {
		this.users = new ArrayList<>();
		// Add default user for testing purposes
		// User defaultUser = new User("S1234567A", "password", 30, "married");
		// createUser(defaultUser);
	}

	/**
	 * Creates a new user. (CHECKED)
	 * @param user The User object to create.
	 * @return True if the user creation is successful, false otherwise.
	 */
	public boolean createUser(User user) {
		if (user != null) {
			users.add(user);
			return true;
		}
		return false;
	}

	/**
	 * Retrieves a user by their NRIC. (CHECKED)
	 * @param NRIC The NRIC of the user to retrieve.
	 * @return The User object if found, null otherwise.
	 */
	public User getUserByNRIC(String NRIC) {
		for (User user : users) {
			if (user.getUserID().equals(NRIC)) {
				return user;
			}
		}
		return null;
	}

	/**
	 * Updates an existing user.(CHECKED)
	 * @param user The User object with updated details.
	 * @return True if the user update is successful, false otherwise.
	 */
	public boolean updateUser(User user) {
		for (int i = 0; i < users.size(); i++) {
			if (users.get(i).getUserID().equals(user.getUserID())) {
				users.set(i, user);
				return true;
			}
		}
		return false;
	}

	/**
	 * Deletes a user by their NRIC.
	 * @param nric The NRIC of the user to delete.
	 * @return True if the user deletion is successful, false otherwise.
	 */
	public boolean deleteUser(String nric) {
		for (int i = 0; i < users.size(); i++) {
			if (users.get(i).getUserID().equals(nric)) {
				users.remove(i);
				return true;
			}
		}
		return false;
	}

	public void setUsers(List<User> users){
		this.users = users;
	}

	public List<User> getUsers(){
		return this.users;
	}

	/**
	 * Retrieves all users.
	 * @return A list of all User objects.
	 */
	public List<User> getAllUsers() {
		return new ArrayList<>(users); // Return a copy to avoid external modification
	}
	
	// Add this method to retrieve an HDBOfficer by NRIC
	public HDBOfficer getHDBOfficer(String officerNRIC) {
		for (User user : users) {
			if (user instanceof HDBOfficer && user.getUserID().equals(officerNRIC)) {
				return (HDBOfficer) user; // Cast to HDBOfficer
			}
		}
		return null;
	}
	
	// Add this method to retrieve an HDBManager by NRIC
	public HDBManager getHDBManager(String managerNRIC) {
		for (User user : users) {
			if (user instanceof HDBManager && user.getUserID().equals(managerNRIC)) {
				return (HDBManager) user; // Cast to HDBManager
			}
		}
		return null;
	}	
}