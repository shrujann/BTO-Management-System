package data;

import entity.project.Application;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import constants.ApplicationStatus;
import constants.SystemConstants;

// This class will manage application data in memory for the BTO Management System
public class ApplicationDataManager {
	private List<Application> applications;

	public ApplicationDataManager() {
		this.applications = new ArrayList<>();
		loadApplicationsFromCSV(SystemConstants.APPLICATION_FILE_PATH); // Load data on initialization
	}

	/**
	 * Creates a new application.
	 * @param application The Application object to create.
	 * @return True if the application creation is successful, false otherwise.
	 */
	public boolean createApplication(Application application) {
		if (application != null) {
			applications.add(application);
			saveApplicationsToCSV(applications, SystemConstants.APPLICATION_FILE_PATH); // Save to CSV after adding
			return true;
		}
		return false;
	}

	/**
	 * Retrieves an application by its ID.
	 * @param applicationID The ID of the application to retrieve.
	 * @return The Application object if found, null otherwise.
	 */
	public Application getApplication(String applicationID) {
		for (Application application : applications) {
			if (application.getApplicantID().equals(applicationID)) {
				return application;
			}
		}
		return null;
	}

	/**
	 * Updates an existing application.
	 * @param application The Application object with updated details.
	 * @return True if the application update is successful, false otherwise.
	 */
	public boolean updateApplication(Application application) {
		for (int i = 0; i < applications.size(); i++) {
			if (applications.get(i).getApplicantID().equals(application.getApplicantID())) {
				applications.set(i, application);
				saveApplicationsToCSV(applications, SystemConstants.APPLICATION_FILE_PATH); // Save to CSV after updating
				return true;
			}
		}
		return false;
	}

	/**
	 * Deletes an application by its ID. 
	 * @param applicationID The ID of the application to delete.
	 * @return True if the application deletion is successful, false otherwise.
	 */
	public boolean deleteApplication(String applicationID) {
		for (int i = 0; i < applications.size(); i++) {
			if (applications.get(i).getApplicantID().equals(applicationID)) {
				applications.remove(i);
				saveApplicationsToCSV(applications, SystemConstants.APPLICATION_FILE_PATH); // Save to CSV after deleting
				return true;
			}
		}
		return false;
	}

	/**
	 * Retrieves all applications.
	 * @return A list of all Application objects.
	 */
	public List<Application> getAllApplications() {
		return new ArrayList<>(applications); // Return a copy to avoid external modification
	}

	/**
   * Retrieves applications by project name. (CHECKED)
   * @param projectName The name of the project.
   * @return A list of Application objects for the given project.
   */
  public List<Application> getApplicationsByProjectName(String projectName) {
		List<Application> projectApplications = new ArrayList<>();
		for (Application application : applications) {
			if (application.getProjectName().equals(projectName)) {
				projectApplications.add(application);
			}
		}
		return projectApplications;
	}



	
	/**
   * Loads applications from the CSV file. (CHEKED)
   * @param filePath The path to the CSV file.
   */
	public void loadApplicationsFromCSV(String filePath) {
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			reader.readLine(); // Skip the header

			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts.length == 5) { // Ensure all fields are present
					try {
						String applicantID = parts[0].trim();
						String projectName = parts[1].trim();
						ApplicationStatus status = ApplicationStatus.valueOf(parts[2].trim()); // Parse the status
						boolean isWithdrawn = Boolean.parseBoolean(parts[3].trim());
						String flatType = parts[4].trim();
						//String flatType = parts[4].trim(); has booked flat

						Application application = new Application(applicantID, projectName, status);
						application.setWithdrawn(isWithdrawn);
						application.setFlatType(flatType);
						applications.add(application);
					} catch (IllegalArgumentException e) {
						System.err.println("Invalid Application Status (skipping line): " + line);
					} catch (Exception e) {
						System.err.println("Error parsing Application data (skipping line): " + line);
						e.printStackTrace(); // Log the full exception for debugging
					}
				} else {
				System.err.println("Skipping invalid line: " + line);
				}
			}
		} catch (IOException e) {
			System.err.println("Error reading from CSV file: " + e.getMessage());
		}
	}

	/**
   * Saves applications to the CSV file. (CHECKED)
   * @param applications The list of applications to save.
   * @param filePath The path to the CSV file.
   */
	public void saveApplicationsToCSV(List<Application> applications, String filePath) {
		try (FileWriter writer = new FileWriter(filePath)) {
			// Write the header
			writer.append("applicantId,projectName,status,isWithdrawn,flatType\n");

			// Write data rows
			for (Application application : applications) {
				writer.append(String.format("%s,%s,%s,%b,%s\n",
				application.getApplicantID(), application.getProjectName(), application.getStatus(),
				application.isWithdrawn(), application.getFlatType()));
			}
		} catch (IOException e) {
			System.err.println("Error writing to CSV file: " + e.getMessage());
		}
	}

	/**
	 * Checks if an applicant has already applied for a specific project. (CHECKED)
	 * @param applicantID The ID of the applicant.
	 * @param projectName The name of the project.
	 * @return True if the applicant has applied for the project, false otherwise.
	 */
	public boolean isApplicantForProject(String applicantID, String projectName) {
		for (Application application : applications) {
			if (application.getApplicantID().equals(applicantID) && application.getProjectName().equals(projectName)) {
				return true; // Applicant has applied for this project
			}
		}
		return false; // Applicant has not applied for this project
	}
}
