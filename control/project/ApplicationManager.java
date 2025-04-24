package control.project;

import entity.project.Application;
import entity.project.BTOProject;
import entity.project.FlatType;
import entity.user.Applicant;
import data.ApplicationDataManager;
import data.ProjectDataManager;
import constants.ApplicationStatus;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

public class ApplicationManager {
	private ApplicationDataManager applicationDataManager;
	private ProjectDataManager projectDataManager;

	// Constructor
	public ApplicationManager(ApplicationDataManager applicationDataManager, ProjectDataManager projectDataManager) {
		this.applicationDataManager = applicationDataManager;
		this.projectDataManager = projectDataManager;
	}

	/**
	 * Submits a new application for a BTO project.
	 * 
	 * @param application The Application object to submit.
	 * @return True if the application submission is successful, false otherwise.
	 */
	public boolean submitApplication(Application application) {
		// Additional logic here, such as checking if the applicant is eligible
		return applicationDataManager.createApplication(application);
	}

	/**
	 * Retrieves an application by its ID.
	 * 
	 * @param applicationID The ID of the application to retrieve.
	 * @return The Application object if found, null otherwise.
	 */
	public Application getApplication(String applicationID) {
		return applicationDataManager.getApplication(applicationID);
	}

	/**
	 * Withdraws an existing application.
	 * 
	 * @param applicationId The ID of the application to withdraw.
	 * @return True if the application withdrawal is successful, false otherwise.
	 */
	public boolean withdrawApplication(String applicationID) {
		// Additional logic here, such as checking if withdrawal is allowed
		Application application = applicationDataManager.getApplication(applicationID);
		if (application != null) {
			application.setStatus(ApplicationStatus.WITHDRAWN);
			return applicationDataManager.updateApplication(application);
		}
		return false;
	}

	/**
	 * Approves an application.
	 * 
	 * @param applicationID The ID of the application to approve.
	 * @return True if the application approval is successful, false otherwise.
	 */
	public boolean approveApplication(String applicationID) {
		Application application = applicationDataManager.getApplication(applicationID);
		if (application != null) {
			// Additional logic here, such as checking flat availability
			application.setStatus(ApplicationStatus.SUCCESSFUL);
			return applicationDataManager.updateApplication(application);
		}
		return false;
	}

	/**
	 * Rejects an application.
	 * 
	 * @param applicationID The ID of the application to reject.
	 * @return True if the application rejection is successful, false otherwise.
	 */
	public boolean rejectApplication(String applicationID) {
		Application application = applicationDataManager.getApplication(applicationID);
		if (application != null) {
			application.setStatus(ApplicationStatus.UNSUCCESSFUL);
			return applicationDataManager.updateApplication(application);
		}
		return false;
	}

	/**
	 * Retrieves all applications for a specific project.
	 * 
	 * @param projectName The name of the project to retrieve applications for.
	 * @return A list of Application objects for the specified project.
	 */
	public List<Application> getApplicationsByProject(String projectName) {
		List<Application> allApplications = applicationDataManager.getAllApplications();
		List<Application> projectApplications = new ArrayList<>();
		for (Application application : allApplications) {
			if (application.getProjectName().equals(projectName)) {
				projectApplications.add(application);
			}
		}
		return projectApplications;
	}

	/**
	 * Retrieves all applications submitted by a specific applicant.
	 * 
	 * @param applicantNRIC The NRIC of the applicant.
	 * @return A list of Application objects submitted by the specified applicant.
	 */
	public List<Application> getApplicationsByApplicant(String applicantNRIC) {
		List<Application> allApplications = applicationDataManager.getAllApplications();
		List<Application> applicantApplications = new ArrayList<>();
		for (Application application : allApplications) {
			if (application.getApplicantID().equals(applicantNRIC)) {
				applicantApplications.add(application);
			}
		}
		return applicantApplications;
	}

	/**
	 * Updates the status of an application.
	 * 
	 * @param applicationID The ID of the application to update.
	 * @param status        The new status of the application.
	 * @return True if the application status update is successful, false otherwise.
	 */
	public boolean updateApplicationStatus(String applicationID, ApplicationStatus status) {
		Application application = applicationDataManager.getApplication(applicationID);
		if (application != null) {
			application.setStatus(status);
			return applicationDataManager.updateApplication(application);
		}
		return false;
	}

	/**
	 * Checks if an applicant is eligible to apply for a specific project.
	 * 
	 * @param applicant The Applicant object.
	 * @param project   The BTOProject object.
	 * @return True if the applicant is eligible, false otherwise.
	 */
	public boolean isEligible(Applicant applicant, BTOProject project) {
		// Implement eligibility logic based on the notes
		// Singles (35+ years) can only apply for 2-Room flats.
		// Married individuals (21+ years) can apply for any flat type (2-Room or
		// 3-Room).
		int age = applicant.getAge();
		String maritalStatus = applicant.getMaritalStatus();
		// Extract flat type names
		List<String> flatTypeNames = project.getFlatTypes().stream().map(FlatType::getTypeName)
				.collect(Collectors.toList());

		if (maritalStatus.equalsIgnoreCase("single") && age >= 35) {
			return flatTypeNames.contains("2-Room");
		} else if (maritalStatus.equalsIgnoreCase("married") && age >= 21) {
			return true; // Married individuals can apply for any flat type
		}
		return false;
	}
}