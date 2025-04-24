package data;

import entity.communication.Enquiry;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import constants.SystemConstants;

// This class will handle in-memory data management for enquiries related to BTO projects.
public class EnquiryDataManager {
	private List<Enquiry> enquiries;
	private int nextEnquiryID;

	public EnquiryDataManager() {
		this.enquiries = new ArrayList<>();
		this.nextEnquiryID = 1;
		loadEnquiriesFromCSV(SystemConstants.ENQUIRY_FILE_PATH); // Load data on initialization
	}

	/**
	 * Creates a new enquiry. Called by controller to save a new enquiry
	 * @param enquiry The Enquiry object to create.
	 * @return True if the enquiry creation is successful, false otherwise.
	 */
	public void saveEnquiry(Enquiry enquiry) {
		if (enquiry != null) {
			enquiry.setEnquiryID(nextEnquiryID++); // Assign auto-increment ID
			enquiries.add(enquiry);
			saveEnquiriesToCSV(enquiries, SystemConstants.ENQUIRY_FILE_PATH); // Save to CSV after adding
		}
	}

	/**
	 * Retrieves an enquiry by its ID.
	 * @param enquiryID The ID of the enquiry to retrieve.
	 * @return The Enquiry object if found, null otherwise.
	 */
	public Enquiry getEnquiryByID(int enquiryID) {
		for (Enquiry enquiry : enquiries) {
			if (enquiry.getEnquiryID() == enquiryID) {
				return enquiry;
			}
		}
		return null;
	}

	/**
	 * Updates an existing enquiry.
	 * @param enquiry The Enquiry object with updated details.
	 * @return True if the enquiry update is successful, false otherwise.
	 */
	public boolean updateEnquiry(Enquiry enquiry) {
		for (int i = 0; i < enquiries.size(); i++) {
			if (enquiries.get(i).getEnquiryID() == enquiry.getEnquiryID()) {
				enquiries.set(i, enquiry);
				saveEnquiriesToCSV(enquiries, SystemConstants.ENQUIRY_FILE_PATH); // Save to CSV after updating
				return true;
			}
		}
		return false;
	}

	/**
	 * Deletes an enquiry by its ID.
	 * @param enquiryID The ID of the enquiry to delete.
	 * @return True if the enquiry deletion is successful, false otherwise.
	 */
	public boolean deleteEnquiry(int enquiryID) {
		for (int i = 0; i < enquiries.size(); i++) {
			if (enquiries.get(i).getEnquiryID() == enquiryID) {
				enquiries.remove(i);
				saveEnquiriesToCSV(enquiries, SystemConstants.ENQUIRY_FILE_PATH); // Save to CSV after deleting
				return true;
			}
		}
		return false;
	}

	/**
	 * Retrieves all enquiries.
	 * @return A list of all Enquiry objects.
	 */
	public List<Enquiry> getAllEnquiries() {
		return new ArrayList<>(enquiries); // Return a copy to avoid external modification
	}

	/**
	 * Retrieves enquiries by project name.
	 * @param projectName The name of the project to retrieve enquiries for.
	 * @return A list of Enquiry objects for the specified project.
	 */
	public List<Enquiry> getEnquiriesByProject(String projectName) {
		List<Enquiry> projectEnquiries = new ArrayList<>();
		for (Enquiry enquiry : enquiries) {
			if (enquiry.getProjectName().equals(projectName)) {
				projectEnquiries.add(enquiry);
			}
		}
		return projectEnquiries;
	}

	/**
	 * Retrieves enquiries by applicant NRIC. 
	 * @param applicantNRIC The NRIC of the applicant to retrieve enquiries for.
	 * @return A list of Enquiry objects for the specified applicant.
	 */
	 public List<Enquiry> getEnquiriesByApplicantID(String applicantNRIC) {
		List<Enquiry> applicantEnquiries = new ArrayList<>();
		for (Enquiry enquiry : enquiries) {
			if (enquiry.getApplicantID().equals(applicantNRIC)) {
				applicantEnquiries.add(enquiry);
			}
		}
		return applicantEnquiries;
	}

  /**
   * Loads enquiries from the CSV file.
   * @param filePath The path to the CSV file.
   */
	 private void loadEnquiriesFromCSV(String filePath) {
		    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
		      String line = reader.readLine(); // Skip header
		  
		      while ((line = reader.readLine()) != null) {
		        String[] fields = line.split(",", -1); // include empty fields
		        if (fields.length < 5) continue;
		  
		        int enquiryID = Integer.parseInt(fields[0].trim());
		        String applicantID = fields[1].trim();
		        String projectName = fields[2].trim();
		        String enquiryText = fields[3].trim();
		        String replyText = fields[4].trim();
		  
		        Enquiry enquiry = new Enquiry(applicantID, projectName, enquiryText);
		        enquiry.setEnquiryID(enquiryID);
		        enquiry.setReplyText(replyText);
		  
		        enquiries.add(enquiry);
		  
		        // Ensure next ID is always higher than max
		        if (enquiryID >= nextEnquiryID) {
		          nextEnquiryID = enquiryID + 1;
		        }
		      }
		  
		      System.out.println("Enquiries loaded successfully from CSV.");
		    } catch (IOException e) {
		      System.out.println("Failed to load enquiries: " + e.getMessage());
		    } catch (Exception e) {
		      System.out.println("Error reading data from CSV: " + e.getMessage());
		    }
		  }
  /**
   * Saves enquiries to the CSV file.
   * @param enquiries The list of enquiries to save.
   * @param filePath The path to the CSV file.
   */
	public void saveEnquiriesToCSV(List<Enquiry> enquiries, String filePath) {
    try (FileWriter writer = new FileWriter(filePath)) {
      // Write the header
      writer.append("enquiryID,applicantID,projectName,enquiryText,replyText\n");

      // Write data rows
      for (Enquiry enquiry : enquiries) {
        writer.append(String.format("%d,%s,%s,%s,%s\n",
          enquiry.getEnquiryID(), enquiry.getApplicantID(), enquiry.getProjectName(),
					enquiry.getEnquiryText(), enquiry.getReplyText()));
      }
    } catch (IOException e) {
      System.err.println("Error writing to CSV file: " + e.getMessage());
    }
  }
}
