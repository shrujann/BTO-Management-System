package logic;

import entity.user.Applicant;
import entity.user.HDBOfficer;
import entity.user.HDBManager;
import entity.user.User;
import util.ValidationUtil;

public class UserLogic {
	public static boolean isValidApplicant(Applicant applicant) {
		if (applicant == null) {
			return false;
		}
		// Validate NRIC
		if (!ValidationUtil.isValidNRIC(applicant.getUserID())) {
			System.out.println("Invalid NRIC format.");
			return false;
		}

		// Validate Age
		boolean isSingle = applicant.getMaritalStatus().equalsIgnoreCase("single");
		if (!ValidationUtil.isValidAge(applicant.getAge(), isSingle)) {
			System.out.println("Invalid age for the marital status.");
			return false;
		}
		// Additional validations can be added here
		return true;
	}

	public static boolean isValidHDBOfficer(HDBOfficer officer) {
		if (officer == null) {
			return false;
		}

		// Validate NRIC
		if (!ValidationUtil.isValidNRIC(officer.getUserID())) {
			System.out.println("Invalid NRIC format.");
			return false;
		}
		// Additional validations can be added here
		return true;
	}

	public static boolean isValidHDBManager(HDBManager manager) {
		if (manager == null) {
			return false;
		}

		// Validate NRIC
		if (!ValidationUtil.isValidNRIC(manager.getUserID())) {
			System.out.println("Invalid NRIC format.");
			return false;
		}
		// Additional validations can be added here
		return true;
	}

	public static boolean changePassword(User user, String currentPassword, String newPassword) {
		if (!user.getPassword().equals(currentPassword)) {
			System.out.println("Incorrect current password.");
			return false;
		}
		if (newPassword == null || newPassword.length() < 8) {
			System.out.println("New password must be at least 8 characters long.");
			return false;
		}

		user.setPassword(newPassword);
		System.out.println("Password changed successfully.");
		return true;
	}
}