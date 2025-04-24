package entity.communication;

/*
 * Represents an enquiry submitted by an applicant regarding a BTO project.
 * Stores the enquiry details such as the enquiry ID, applicant ID, project name, and enquiry text.
 */
public class Enquiry {
	private int enquiryID;
	private String applicantID;
	private String projectName;
	private String enquiryText;
	private String replyText;

	/**
	 * Constructor for the Enquiry class.
	 * 
	 * @param enquiryID   The unique ID of the enquiry.
	 * @param applicantID The ID of the applicant submitting the enquiry.
	 * @param projectName The name of the BTO project the enquiry is regarding.
	 * @param enquiryText The text of the enquiry.
	 */
	public Enquiry(int enquiryID, String applicantID, String projectName, String enquiryText) {
		this.enquiryID = enquiryID;
		this.applicantID = applicantID;
		this.projectName = projectName;
		this.enquiryText = enquiryText;
		this.replyText = null; // Initial value
	}

	/**
	 * Constructor for the Enquiry class without enquiryID.
	 * 
	 * @param applicantId The ID of the applicant submitting the enquiry.
	 * @param project     The name of the BTO project the enquiry is regarding.
	 * @param enquiryText The text of the enquiry.
	 */
	public Enquiry(String applicantID, String projectName, String enquiryText) {
		this.applicantID = applicantID;
		this.projectName = projectName;
		this.enquiryText = enquiryText;
		this.replyText = null; // Initial value
	}

	// Getters and setters for attributes
	public int getEnquiryID() {
		return enquiryID;
	}

	public void setEnquiryID(int enquiryID) {
		this.enquiryID = enquiryID;
	}

	public String getApplicantID() {
		return applicantID;
	}

	public void setApplicantID(String applicantID) {
		this.applicantID = applicantID;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setprojectName(String projectName) {
		this.projectName = projectName;
	}

	public String getEnquiryText() {
		return enquiryText;
	}

	public void setEnquiryText(String enquiryText) {
		this.enquiryText = enquiryText;
	}

	public String getReplyText() {
		return replyText;
	}

	public void setReplyText(String replyText) {
		this.replyText = replyText;
	}
}