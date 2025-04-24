package entity.user;

import java.util.ArrayList;
import java.util.List;
import entity.project.BTOProject;

/*
 * Represents an HDB Manager in the BTO Management System.
 * Extends the User class and includes specific attributes and methods for HDB managers.
 * An HDB Manager:
 *  - Can create, edit, and delete BTO project listings.
 *  - Can only handle one project within an application period.
 *  - Can toggle the visibility of the project to "on" or "off".
 *  - Can view all created projects, regardless of visibility setting. 
 *  - Can filter and view the list of projects they have created only.
 *  - Can view pending and approved HDB Officer registration.
 *  - Can approve or reject HDB Officer’s registration.
 *  - Can approve or reject Applicant’s BTO application, limited to the supply of flats.
 *  - Can approve or reject Applicant's request to withdraw the application.
 *  - Can generate a report of the list of applicants with their respective flat booking details.
 *  - Cannot apply for any BTO project as an Applicant.
 *  - Can view enquiries of ALL projects and reply to enquiries regarding the project they are handling.
 */

public class HDBManager extends User {
	private List<BTOProject> projectsManaged;

	/**
	 * Constructor for the HDBManager class.
	 * 
	 * @param userID        The manager's NRIC.
	 * @param password      The manager's password.
	 * @param age           The manager's age.
	 * @param maritalStatus The manager's marital status.
	 */
	public HDBManager(String name, String userID, int age, String maritalStatus, String password) {
		super(name, userID, age, maritalStatus, password);
		this.projectsManaged = new ArrayList<>();
	}

	// Getters and setters for attributes
	public List<BTOProject> getProjectsManaged() {
		return projectsManaged;
	}

	public void setProjectsManaged(List<BTOProject> projectsManaged) {
		this.projectsManaged = projectsManaged;
	}

	public void addProjectManaged(BTOProject project) {
		this.projectsManaged.add(project);
	}

	public void removeProjectManaged(BTOProject project) {
		this.projectsManaged.remove(project);
	}

	@Override
	public String toString() {
		return "HDBManager{name='" + getName() + "', userID='" + getUserID() +
				"', age=" + getAge() + ", maritalStatus='" + getMaritalStatus() + "'}";
}
}