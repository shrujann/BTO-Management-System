package control.enquiry;

import entity.communication.Enquiry;
import entity.user.Applicant;
import data.EnquiryDataManager;

import java.util.List;

/**
 * Manages enquiries related to BTO projects. Handles submission, retrieval,
 * updating, and deletion of enquiries.
 */
public class EnquiryManager {
	private EnquiryDataManager enquiryDataManager;

	public EnquiryManager(EnquiryDataManager enquiryDataManager) {
		this.enquiryDataManager = enquiryDataManager;
	}

	/**
	 * Allows an applicant to submit an enquiry for a BTO project.
	 * 
	 * @param applicant   The applicant submitting the enquiry.
	 * @param project     The project the enquiry is regarding.
	 * @param enquiryText The text of the enquiry.
	 */
	public void submitEnquiry(Applicant applicant, String project, String enquiryText) {
		// Create a new enquiry
		Enquiry enquiry = new Enquiry(applicant.getUserID(), project, enquiryText);
		// Save the enquiry to the data store
		enquiryDataManager.saveEnquiry(enquiry);
		System.out.println("Enquiry submitted successfully.");
	}

	/**
	 * Allows an applicant to view their enquiries.
	 * 
	 * @param applicant The applicant viewing their enquiries.
	 * @return A list of enquiries for the applicant.
	 */
	public List<Enquiry> viewEnquiries(Applicant applicant) {
		// Retrieve the enquiries from the data store
		List<Enquiry> enquiries = enquiryDataManager.getEnquiriesByApplicantID(applicant.getUserID());
		return enquiries;
	}

	/**
	 * Allows an applicant to edit an existing enquiry.
	 * 
	 * @param applicant      The applicant editing the enquiry.
	 * @param enquiryID      The ID of the enquiry to edit.
	 * @param newEnquiryText The new text for the enquiry.
	 * @return true if the enquiry is successfully edited, false otherwise.
	 */
	public boolean editEnquiry(Applicant applicant, int enquiryID, String newEnquiryText) {
		// Retrieve the enquiry from the data store
		Enquiry enquiry = enquiryDataManager.getEnquiryByID(enquiryID);

		// Check if the enquiry exists and belongs to the applicant
		if (enquiry == null || !enquiry.getApplicantID().equals(applicant.getUserID())) {
			System.out.println("Enquiry not found or does not belong to the applicant.");
			return false;
		}
		// Update the enquiry text
		enquiry.setEnquiryText(newEnquiryText);

		// Update the enquiry in the data store
		enquiryDataManager.updateEnquiry(enquiry);

		System.out.println("Enquiry updated successfully.");
		return true;
	}

	/**
	 * Allows an applicant to delete an enquiry.
	 * 
	 * @param applicant The applicant deleting the enquiry.
	 * @param enquiryID The ID of the enquiry to delete.
	 * @return true if the enquiry is successfully deleted, false otherwise.
	 */
	public boolean deleteEnquiry(Applicant applicant, int enquiryID) {
		// Retrieve the enquiry from the data store
		Enquiry enquiry = enquiryDataManager.getEnquiryByID(enquiryID);

		// Check if the enquiry exists and belongs to the applicant
		if (enquiry == null || !enquiry.getApplicantID().equals(applicant.getUserID())) {
			System.out.println("Enquiry not found or does not belong to the applicant.");
			return false;
		}

		// Delete the enquiry from the data store
		enquiryDataManager.deleteEnquiry(enquiryID);

		System.out.println("Enquiry deleted successfully.");
		return true;
	}

	// Additional methods for retrieving, updating, and managing enquiries as needed
}