package entity.user;

import entity.project.BTOProject;
import java.util.List;
import java.util.ArrayList;

public class HDBOfficer extends Applicant {
	private boolean isRegisteredForProject;
	private String registrationStatus; // Pending, Approved, Rejected
	private BTOProject projectAssigned;
	private List<Integer> enquiriesHandled; // List of enquiry IDs handled by the officer
	private List<BTOProject> futureProjects;
	
	/**
	 * Constructor for the HDBOfficer class.
	 * @param userID        The officer's NRIC.
	 * @param password      The officer's password.
	 * @param age           The officer's age.
	 * @param maritalStatus The officer's marital status.
	 */
	public HDBOfficer(String name, String userID, int age, String maritalStatus, String password) {
		super(name, userID, age, maritalStatus, password);
		this.isRegisteredForProject = false;
		this.registrationStatus = "Pending"; // Default status
		this.projectAssigned = null;
		this.enquiriesHandled = new ArrayList<>();
		this.futureProjects = new ArrayList<>();
	}

	// Getters and setters for attributes
	public String getRegistrationStatus() {return registrationStatus;}
	public void setRegistrationStatus(String registrationStatus) {this.registrationStatus = registrationStatus;}

	public boolean isRegisteredForProject() {return isRegisteredForProject;}
	public void setRegisteredForProject(boolean registeredForProject) {isRegisteredForProject = registeredForProject;}

	public BTOProject getProjectAssigned() {return projectAssigned;}
	public void setProjectAssigned(BTOProject projectAssigned) {this.projectAssigned = projectAssigned;}

	public List<Integer> getEnquiriesHandled() {return enquiriesHandled;}
	public void setEnquiriesHandled(List<Integer> enquiriesHandled) {this.enquiriesHandled = enquiriesHandled;}

	// Add and remove enquiry IDs
	public void addEnquiryHandled(Integer enquiryId) {this.enquiriesHandled.add(enquiryId);}
	public void removeEnquiryHandled(Integer enquiryId) {this.enquiriesHandled.remove(enquiryId);}

	// Get and set future HDBProjects
	public List<BTOProject> getFutureProjects(){
		return this.futureProjects;
	} 

	public void addFutureProject(BTOProject project){
		futureProjects.add(project);
	}

	@Override
	public String toString() {
			return "HDBOfficer{" +
							"name='" + getName() + '\'' +
							", userID='" + getUserID() + '\'' +
							", age=" + getAge() +
							", maritalStatus='" + getMaritalStatus() + '\'' +
							", isRegisteredForProject=" + isRegisteredForProject +
							", registrationStatus='" + registrationStatus + '\'' +
							", projectAssigned=" + projectAssigned +
							", enquiriesHandled=" + enquiriesHandled +
							'}';
	}
}