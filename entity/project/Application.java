package entity.project;

import constants.ApplicationStatus;

/*
 * Represents an application for a BTO project.
 * Stores the applicant ID, project name, status, and other relevant details.
 */
public class Application {
	private String applicantID;
	private String projectName;
	private ApplicationStatus status;
	private boolean isWithdrawn; // Whether the application has been withdrawn by the applicant.
	private String flatType; // 2-Room or 3-Room (only applicable after booking)
	//flatBooked

	/**
	 * Constructor for the Application class.
	 * 
	 * @param applicantID The ID of the applicant.
	 * @param projectName The name of the BTO project.
	 * @param status      The status of the application.
	 */
	public Application(String applicantID, String projectName, ApplicationStatus status) {
		this.applicantID = applicantID;
		this.projectName = projectName;
		this.status = status;
		this.isWithdrawn = false; // Default value
		this.flatType = null; // Initially, no flat type is selected
	}

	// Getters and setters for attributes
	public String getApplicantID() {return applicantID;}
	public void setApplicantID(String applicantID) {this.applicantID = applicantID;}

	public String getProjectName() {return projectName;}
	public void setProjectName(String projectName) {this.projectName = projectName;}

	public ApplicationStatus getStatus() {return status;}
	public void setStatus(ApplicationStatus status) {this.status = status;}

	public boolean isWithdrawn() {return isWithdrawn;}
	public void setWithdrawn(boolean withdrawn) {isWithdrawn = withdrawn;}

	public String getFlatType() {return flatType;}
	public void setFlatType(String flatType) {this.flatType = flatType;}

	@Override
	public String toString() {
			return "Application{" +
							"applicantID='" + applicantID + '\'' +
							", projectName='" + projectName + '\'' +
							", status=" + status +
							", isWithdrawn=" + isWithdrawn +
							", flatType='" + flatType + '\'' +
							'}';
	}
}
