package entity.user;

import java.util.ArrayList;
import java.util.List;

public class Applicant extends User {
	private String flatBooked; // The flat booked by the applicant (e.g., "ProjectA, Type2")
  private String flatType; // The type of flat booked (e.g., "2-Room", "3-Room")
  private List<String> pendingApplications; // List of project names for pending applications
  private List<Integer> enquiries; // List of enquiry IDs submitted by the applicant

	/**
	 * Constructor for the Applicant class.
	 * @param userID        The applicant's NRIC.
	 * @param password      The applicant's password.
	 * @param age           The applicant's age.
	 * @param maritalStatus The applicant's marital status.
	 */
	public Applicant(String name, String userID, int age, String maritalStatus, String password) {
		super(name, userID, age, maritalStatus, password);
		this.flatBooked = null; // Initially, no flat is booked
    this.flatType = null;
    this.pendingApplications = new ArrayList<>();
    this.enquiries = new ArrayList<>();
	}

	// Getters and setters for attributes
	public String getFlatBooked() {return flatBooked;}
	public void setFlatBooked(String flatBooked) {this.flatBooked = flatBooked;}
	


	public String getFlatType() {return flatType;}
	public void setFlatType(String flatType) {this.flatType = flatType;}
	
	public List<String> getPendingApplications() {return pendingApplications;}
	public void setPendingApplications(List<String> pendingApplications) {this.pendingApplications = pendingApplications;}

  public List<Integer> getEnquiries() {return enquiries;}
	public void setEnquiries(List<Integer> enquiries) {this.enquiries = enquiries;}

  // Add and remove pending applications
	public void addPendingApplication(String projectName) {this.pendingApplications.add(projectName);}
	public void removePendingApplication(String projectName) {this.pendingApplications.remove(projectName);}

  // Add and remove enquiry IDs
  public void addEnquiry(Integer enquiryId) {this.enquiries.add(enquiryId);}
	public void removeEnquiry(Integer enquiryId) {this.enquiries.remove(enquiryId);}

	@Override
	public String toString() {
			return "Applicant{" +
							"name: '" + getName() + '\'' +
							", userID: '" + getUserID() + '\'' +
							", age: " + getAge() +
							", maritalStatus: '" + getMaritalStatus() + '\'' +
							", flatBooked: '" + flatBooked + '\'' +
							", flatType: '" + flatType + '\'' +
							", pendingApplications: " + pendingApplications +
							", enquiries: " + enquiries +
							'}';
	}
}








/* 
	@Override
	public String toString() {
		return "Applicant{" +
				"name: '" + getName() + '\'' +
				", userID: '" + getUserID() + '\'' +
				", age: " + getAge() +
				", maritalStatus: '" + getMaritalStatus() + '\'' +
				'}';
	}

	*/
