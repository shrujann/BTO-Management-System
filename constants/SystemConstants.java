package constants;

public class SystemConstants {
	// Default password for all users
	public static final String DEFAULT_PASSWORD = "password";

	// Age limit for singles to apply for 2-Room flats
	public static final int MIN_AGE_SINGLE = 35;

	// Age limit for married couples to apply for flats
	public static final int MIN_AGE_MARRIED = 21;

	// Maximum number of HDB Officers allowed for a project
	public static final int MAX_HDB_OFFICER_SLOTS = 10;

	public static final String applicantPath = "csv/ApplicantList.csv";
	public static final String officerPath = "csv/OfficerList.csv";
	public static final String managerPath = "csv/ManagerList.csv";
	public static final String projectPath = "csv/ProjectList.csv";
	public static final String ENQUIRY_FILE_PATH = "csv/EnquiryList.csv";
	public static final String APPLICATION_FILE_PATH = "csv/ApplicationList.csv";
}