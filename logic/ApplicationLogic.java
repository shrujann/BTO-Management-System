package logic;

import entity.project.Application;
import entity.project.BTOProject;
import entity.user.Applicant;
import constants.ApplicationStatus;
import java.util.List;

public class ApplicationLogic {

	// Check if the application is valid based on applicant eligibility and project
	// offerings.
	public static boolean isValidApplication(Applicant applicant, BTOProject project) {
		if (applicant == null || project == null) {
			System.out.println("Applicant or Project cannot be null.");
			return false;
		}

		// Check if applicant is eligible based on age and marital status
		boolean isSingle = applicant.getMaritalStatus().equalsIgnoreCase("single");
		if (isSingle && !project.has2RoomFlats()) {
			System.out.println("Singles can only apply for projects with 2-Room flats.");
			return false;
		}
		// Additional validations can be added here
		return true;
	}

	public static boolean canApply(Applicant applicant, List<Application> existingApplications) {
		for (Application app : existingApplications) {
			if (app.getApplicantID().equals(applicant.getUserID())) {
				System.out.println("Applicant has already applied for a project.");
				return false; // Applicant has already applied
			}
		}
		return true; // Applicant can apply
	}

	public static void withdrawApplication(Application application) {
		// Change application status to "Withdrawn"
		application.setStatus(ApplicationStatus.WITHDRAWN);
		System.out.println("Application withdrawn successfully.");
	}
}
