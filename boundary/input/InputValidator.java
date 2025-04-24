package boundary.input;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputValidator {
	private static final int MIN_PASSWORD_LENGTH = 8;
	private static final int MIN_AGE = 18;
	private static final int MAX_AGE = 120;

	/**
	 * Validates if the input is a valid NRIC format.
	 * 
	 * @param NRIC The NRIC to validate.
	 * @return True if the NRIC is valid, false otherwise.
	 */
	public static boolean isValidNRIC(String NRIC) {
		// NRIC starts with S or T, followed by 7 digits and ends with a letter
		Pattern pattern = Pattern.compile("^[ST]\\d{7}[A-Z]$");
		Matcher matcher = pattern.matcher(NRIC);
		return matcher.matches();
	}

	// Validates if the input is a valid password format.
	public static boolean isValidPassword(String password) {
		// Password should be at least 8 characters long
		return password != null && password.length() >= MIN_PASSWORD_LENGTH;
	}

	// Validates if the input is a valid age.
	public static boolean isValidAge(int age) {
		return age >= MIN_AGE && age <= MAX_AGE; // Unlikely to live over 120 years old
	}

	/**
	 * Validates if the input is a non-empty string.
	 * 
	 * @param input The string to validate.
	 * @return True if the string is non-empty, false otherwise.
	 */
	public static boolean isNonEmptyString(String input) {
		return input != null && !input.trim().isEmpty();
	}

	public static boolean isValidMaritalStatus(String status) {
		return status != null && (status.equalsIgnoreCase("single") || status.equalsIgnoreCase("married"));
	}

	// Additional validation methods can be added here, such as validating marital
	// status,
	// flat types, etc.
}