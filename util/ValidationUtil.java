package util;

import constants.SystemConstants;

public class ValidationUtil {
	public static boolean isValidNRIC(String nric) {
		// NRIC should start with S or T, followed by 7 digits and end with a letter.
		return nric != null && nric.matches("[ST]\\d{7}[A-Z]");
	}

	public static boolean isValidAge(int age, boolean isSingle) {
		// Check if applicant is single
		if (isSingle) {
			return age >= SystemConstants.MIN_AGE_SINGLE;
		}
		// If applicant is married
		return age >= SystemConstants.MIN_AGE_MARRIED;
	}
}