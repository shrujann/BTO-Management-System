package control.project;

import entity.user.HDBOfficer;
import entity.project.BTOProject;
import entity.project.Application;
import data.UserDataManager;
import data.ApplicationDataManager;
import data.ProjectDataManager;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import util.DateUtil;

// This class willhandle the registration of HDB Officers to specific BTO projects, 
// including submission, approval, and rejection of registrations.
public class OfficerRegistrationManager {
	private UserDataManager userDataManager;
	private ProjectDataManager projectDataManager;
	private ApplicationDataManager applicationDataManager;

	public OfficerRegistrationManager(UserDataManager userDataManager, ProjectDataManager projectDataManager,
			ApplicationDataManager applicationDataManager) {
		this.userDataManager = userDataManager;
		this.projectDataManager = projectDataManager;
		this.applicationDataManager = applicationDataManager;
	}

	/**
	 * Registers an HDB Officer for a specific BTO project.
	 * @param officerNRIC The NRIC of the HDB Officer.
	 * @param projectName The name of the BTO project.
	 * @return True if the registration is successful, false otherwise.
	 */
	public boolean registerOfficerForProject(String officerNRIC, String projectName) {
		HDBOfficer officer = (HDBOfficer) userDataManager.getUserByNRIC(officerNRIC);
		BTOProject project = projectDataManager.getProject(projectName);

		if (officer == null || project == null) {
			return false;
		}

		if (isOfficerAlreadyRegisteredForProject(officerNRIC, projectName)) {
			System.out.println("Officer is already registered for this project.");
			return false;
		}

		if (isOfficerApplyingForProject(officerNRIC, projectName)) {
			System.out.println("Officer cannot register for a project they have applied for.");
			return false;
		}

		List<String> pending = project.getPendingOfficerRegistrations();
		if (pending == null)
			pending = new ArrayList<>();

		if (getTotalOfficerRegistrations(project) >= project.getAvailableHdbOfficerSlots()) {
			System.out.println("No available officer slots.");
			return false;
		}

		if (isOfficerRegisteredForAnotherProject(officerNRIC, projectName)){
			System.out.println("Officer already registered in an overlapping project");
			return false;
		}

		pending.add(officerNRIC);
		project.setPendingOfficerRegistrations(pending);
		return projectDataManager.updateProject(project);
	}

	/**
	 * Approves the registration of an HDB Officer for a specific BTO project.
	 * @param officerNRIC The NRIC of the HDB Officer.
	 * @param projectName The name of the BTO project.
	 * @return True if the approval is successful, false otherwise.
	 */
	
	public boolean approveOfficerRegistration(String officerNRIC, String projectName) {
		BTOProject project = projectDataManager.getProject(projectName);
		if (project != null) {
			List<String> pendingRegistrations = project.getPendingOfficerRegistrations();
			if (pendingRegistrations != null && pendingRegistrations.contains(officerNRIC)) {
				pendingRegistrations.remove(officerNRIC);
				project.setPendingOfficerRegistrations(pendingRegistrations);
				List<String> approvedOfficers = project.getApprovedOfficerRegistrations();
				if (approvedOfficers == null) {
					approvedOfficers = new ArrayList<>();
				}
				approvedOfficers.add(officerNRIC);
				project.setApprovedOfficerRegistrations(approvedOfficers);
				return projectDataManager.updateProject(project);
			}
		}
		return false;
	}

	/**
	 * Rejects the registration of an HDB Officer for a specific BTO project.
	 * @param officerNRIC The NRIC of the HDB Officer.
	 * @param projectName The name of the BTO project.
	 * @return True if the rejection is successful, false otherwise.
	 */
	public boolean rejectOfficerRegistration(String officerNRIC, String projectName) {
		BTOProject project = projectDataManager.getProject(projectName);
		if (project != null) {
			List<String> pendingRegistrations = project.getPendingOfficerRegistrations();
			if (pendingRegistrations != null && pendingRegistrations.contains(officerNRIC)) {
				pendingRegistrations.remove(officerNRIC);
				project.setPendingOfficerRegistrations(pendingRegistrations);
				return projectDataManager.updateProject(project);
			}
		}
		return false;
	}

	/**
	 * Checks if an HDB Officer is already registered for a specific BTO project.
	 * @param officerNRIC The NRIC of the HDB Officer.
	 * @param projectName The name of the BTO project.
	 * @return True if the officer is already registered, false otherwise.
	 */
	public boolean isOfficerAlreadyRegisteredForProject(String officerNRIC, String projectName) {
		BTOProject project = projectDataManager.getProject(projectName);
		if (project != null) {
			List<String> registeredOfficers = project.getRegisteredOfficers();
			return registeredOfficers != null && registeredOfficers.contains(officerNRIC);
		}
		return false;
	}

	/**
	 * Checks if an HDB Officer is applying for the same project as an applicant.
	 * @param officerNRIC The NRIC of the HDB Officer.
	 * @param projectName The name of the BTO project.
	 * @return True if the officer has applied for the project, false otherwise.
	 */
	public boolean isOfficerApplyingForProject(String officerNRIC, String projectName) {
		List<Application> applications = applicationDataManager.getAllApplications();
		for (Application app : applications) {
			if (app.getApplicantID().equals(officerNRIC) && app.getProjectName().equals(projectName)) {
				return true;
			}
		}
		return false;
	}


	// ADDED THIS FUNCTION
	/**
   * Checks if an HDB Officer is already registered for another project within the application period. 
	 * @param officerNRIC The NRIC of the HDB Officer.
	 * @param projectName The name of the BTO project being registered for.
	 * @return True if the officer is registered for another project, false otherwise.
	 */
  public boolean isOfficerRegisteredForAnotherProject(String officerNRIC, String currentProjectName) {
	List<BTOProject> allProjects = projectDataManager.getAllProjects();

	//Current project StartDate:
	Date currentStartDate = null;
	for (BTOProject p : allProjects){
		if (p.getProjectName().equals(currentProjectName)){
			currentStartDate = p.getApplicationStartDate();
			break;
		}
	}

	if (currentStartDate == null){
		System.out.println("Project not found: " + currentProjectName);
		return true; // Default true to prevent registration
	}

	for (BTOProject p : allProjects){
		if (p.getProjectName().equals(currentProjectName)){
			continue;
		}

		List<String> approvedOfficers = p.getApprovedOfficerRegistrations();
		if (approvedOfficers != null && approvedOfficers.contains(officerNRIC)){
			Date otherEndDate = p.getApplicationEndDate();

			if (DateUtil.isAfterEnd(otherEndDate, currentStartDate)){
				System.out.printf("Checking: otherEndDate = %s, currentStartDate = %s%n", otherEndDate, currentStartDate);
				System.out.printf("Overlap detected: %s (ends %s) vs %s (starts %s)%n",
						p.getProjectName(), otherEndDate, currentProjectName, currentStartDate);
				return true;
			} 
		}
	}
	return false;
	}
	
	/**
	 * Retrieves a list of HDB Officers registered for a specific BTO project.
	 * @param projectName The name of the BTO project.
	 * @return A list of HDBOfficer objects registered for the specified project.
	 */
	public List<HDBOfficer> getRegisteredOfficersForProject(String projectName) {
		BTOProject project = projectDataManager.getProject(projectName);
		List<HDBOfficer> registeredOfficers = new ArrayList<>();
		if (project != null) {
			List<String> officerNRICs = project.getRegisteredOfficers();
			if (officerNRICs != null) {
				for (String officerNRIC : officerNRICs) {
					HDBOfficer officer = (HDBOfficer) userDataManager.getUserByNRIC(officerNRIC);
					if (officer != null) {
						registeredOfficers.add(officer);
					}
				}
			}
		}
		return registeredOfficers;
	}

	private int getTotalOfficerRegistrations(BTOProject project) {
		int pending = project.getPendingOfficerRegistrations() != null ? project.getPendingOfficerRegistrations().size()
				: 0;
		int approved = project.getApprovedOfficerRegistrations() != null
				? project.getApprovedOfficerRegistrations().size()
				: 0;
		return pending + approved;
	}
}
