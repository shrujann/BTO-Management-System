package util;

import constants.SystemConstants;
import entity.user.User;

public class PasswordUtil {
	public static boolean isValidPassword(String password) {
    // Password must be at least 8 characters long
    return password != null && password.length() >= 8;
  }

  public static boolean checkDefaultPassword(String password) {
    // Check if password is the default password
    return SystemConstants.DEFAULT_PASSWORD.equals(password);
  }

  public static boolean changePassword(User user, String currentPassword, String newPassword) {
    if (!verifyPassword(currentPassword, user.getPassword())) {
      System.out.println("Current password is incorrect.");
      return false;
    }
    
		// Check that new password is valid
    if (!isValidPassword(newPassword)) {
      System.out.println("New password is not valid. Password must be at least 8 characters long.");
      return false;
    }
    
		// Update password if valid
    user.setPassword(hashPassword(newPassword)); // Hash the new password before setting
    // System.out.println("Password changed successfully."); // Uncomment if not shown
    return true;
  }

  // Simple password hashing (for demonstration purposes only)
  public static String hashPassword(String plainPassword) {
    // This is a very basic hashing function.  Do not use this in a real-world application!
    StringBuilder hash = new StringBuilder();
    for (char c : plainPassword.toCharArray()) {
      hash.append(Integer.toHexString(c + 10)); // Adding 10 to the ASCII value
    }
    return hash.toString();
  }

	// Simple password verification (for demonstration purposes only)
  public static boolean verifyPassword(String plainPassword, String hashedPassword) {
    // This is a very basic verification function.  Do not use this in a real-world application!
    String hashedPlainPassword = hashPassword(plainPassword);
    return hashedPlainPassword.equals(hashedPassword);
  }

  public static boolean isLikelyHashed(String password){
    return password.length() >= 16 && password.matches("[0-9a-fA-F]+");
  }
}
/*
  public static void main(String[] args) {
    // Example usage
    User testUser = new User("Test Name", "testUser", 25, "single", "initialPassword");;
    String initialHashedPassword = hashPassword("initialPassword");
    testUser.setPassword(initialHashedPassword);

    System.out.println("Initial hashed password: " + testUser.getPassword());
		
		// Try to change password
    boolean changed = changePassword(testUser, "initialPassword", "newPassword123");
    if (changed) {
      System.out.println("New hashed password: " + testUser.getPassword());
    } else {
      System.out.println("Password change failed.");
    }

    // Verify password
    System.out.println("Is newPassword123 valid? " + verifyPassword("newPassword123", testUser.getPassword()));
    System.out.println("Is initialPassword valid? " + verifyPassword("initialPassword", testUser.getPassword()));
  }
}
*/
