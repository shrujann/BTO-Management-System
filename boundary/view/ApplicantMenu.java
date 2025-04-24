package boundary.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.Date;
import logic.ApplicationLogic;
import entity.user.Applicant;
import entity.user.HDBOfficer;
import util.DateUtil;
import util.PasswordUtil;
import entity.project.BTOProject;
import entity.project.FlatType;
import entity.project.Application;
import constants.ApplicationStatus;
import entity.communication.Enquiry;
import data.DataManager;
import control.enquiry.EnquiryManager;

public class ApplicantMenu {
	private Applicant applicant;
	private Scanner scanner;
	private DataManager dataManager;

	// Instantiate the EnquiryManager using the DataManager's EnquiryDataManager.
	private EnquiryManager enquiryManager;

	// Constructor now accepts the DataManager instance.
	public ApplicantMenu(Applicant applicant, Scanner scanner, DataManager dataManager) {
		this.applicant = applicant;
		this.scanner = scanner;
		this.dataManager = dataManager;
		this.enquiryManager = new EnquiryManager(dataManager.getEnquiryDataManager());
	}

	public void display() {
		boolean logout = false;
		while (!logout) {
			System.out.println("\nWelcome Applicant: " + applicant.getUserID());
			System.out.println("1. View Projects");
			System.out.println("2. Apply for Project");
			System.out.println("3. View Application Status");
			System.out.println("4. Request Withdrawal");
			System.out.println("5. Submit Enquiry");
			System.out.println("6. View/Edit/Delete Enquiries");
			System.out.println("7. Change Password");
			System.out.println("8. Logout");
			System.out.print("Enter your choice: ");
			String choice = scanner.nextLine().trim();

			switch (choice) {
			case "1":
				viewProjects();
				break;
			case "2":
				applyForProject();
				break;
			case "3":
				viewApplicationStatus();
				break;
			case "4":
				requestWithdrawal();
				break;
			case "5":
				submitEnquiry();
				break;
			case "6":
				manageEnquiries();
				break;
			case "7":
				changePassword();
				break;
			case "8":
				logout = true;
				System.out.println("Logging out. Returning to Main Menu.");
				break;
			default:
				System.out.println("Invalid choice. Please try again.");
			}
		}
	}

	// 1. Display available BTO projects (only those that are visible) and relevant to the applicant.
	private void viewProjects() {
		System.out.println("\n--- Available BTO Projects ---");

		// 1) filter by visibility + your eligibility rules
		List<BTOProject> eligibleProjects = dataManager.getProjectDataManager()
      .getAllProjects()
      .stream()
      .filter(BTOProject::isVisible)  // Ensure project is visible
      .filter(p -> isEligible(applicant, p)) // Use helper method to check eligibility
      .collect(Collectors.toList());

    if (eligibleProjects.isEmpty()) {
      System.out.println("No projects available for your user group at the moment.");
      return;
    }

		// 2) print each with its status and both unit counts
		int idx = 1;
		for (BTOProject p : eligibleProjects) {
			boolean approved = p.getApprovedOfficerRegistrations().contains(applicant.getUserID());
			boolean pending = p.getPendingOfficerRegistrations().contains(applicant.getUserID());
			boolean rejected = p.getRejectedOfficerRegistrations().contains(applicant.getUserID());
		
			String status = pending ? " (Registration Pending)"  
										:	approved ? " (Registered)"
										: rejected ? " (Registration Rejected)"
										: "";
		
			System.out.printf("%d. %s%s - %s%n 2-Room Units: %d, 3-Room Units: %d%n",
				idx++,
				p.getProjectName(),
				status,
				p.getNeighbourhood(),
				p.getNum2RoomUnits(),
				p.getNum3RoomUnits());
			}
		}

		private boolean isEligible(Applicant applicant, BTOProject project) {
			int age = applicant.getAge();
			boolean married = applicant.getMaritalStatus().equalsIgnoreCase("Married");

			// single ≥35 → only 2‑Room
			if (!married && age >= 35) {
					return project.getNum2RoomUnits() > 0;
			}
			// married ≥21 → either type
			else if (married && age >= 21) {
					return project.getNum2RoomUnits() > 0 || project.getNum3RoomUnits() > 0;
			}
			return false; // Not eligible
	}

	// 2. Apply for Project: List available projects and submit an application. (CHECKED)
	private void applyForProject() {
		viewProjects();
		List<BTOProject> visibleProjects = new ArrayList<>();
		for (BTOProject proj : dataManager.getProjectDataManager().getAllProjects()) {
			// ADDED: Check for visibility AND eligibility
			if (proj.isVisible() && isEligible(applicant, proj)) {
				visibleProjects.add(proj);
			}
		}
		if (visibleProjects.isEmpty()) {
			System.out.println("No eligible projects available at the moment.");
			return;
		}
		List<Application> existingApplications = dataManager.getApplicationDataManager().getAllApplications();
		boolean eligible = ApplicationLogic.canApply(applicant, existingApplications);
		if (!eligible) {
			System.out.println("You already have an active or approved application. Cannot apply for another project.");
			return;
		}
		System.out.print("\nEnter the number corresponding to the project you want to apply for: ");
		int projIndex;
		try {
			projIndex = Integer.parseInt(scanner.nextLine().trim());
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Application cancelled.");
			return;
		}
		if (projIndex < 1 || projIndex > visibleProjects.size()) {
			System.out.println("Invalid project selection. Application cancelled.");
			return;
		}
		BTOProject selectedProject = visibleProjects.get(projIndex - 1);
		
		// Create a new application.
		Application newApp = new Application(applicant.getUserID(), selectedProject.getProjectName(),
				ApplicationStatus.PENDING);
		boolean added = dataManager.getApplicationDataManager().createApplication(newApp);
		if (added) {
			System.out.println(
					"Application for project \"" + selectedProject.getProjectName() + "\" submitted successfully!");
		} else {
			System.out.println("There was an error submitting your application.");
		}
	}

	// 3. View Application Status: List all applications submitted by the applicant. (CHECKED)
	private void viewApplicationStatus() {
		System.out.println("\n--- Your Application Status ---");
		List<Application> allApps = dataManager.getApplicationDataManager().getAllApplications();
		boolean found = false;
		for (Application app : allApps) {
			if (app.getApplicantID().equalsIgnoreCase(applicant.getUserID())) {
				System.out.println("Project: " + app.getProjectName() + " | Status: " + app.getStatus()
						+ (app.isWithdrawn() ? " (Withdrawn)" : ""));
				found = true;
			}
		}
		if (!found) {
			System.out.println("You have not applied for any projects yet.");
		}
	}

	// 4. Request Withdrawal: Allow the applicant to withdraw an active application. (CHECKED)
	private void requestWithdrawal() {
		System.out.println("\n--- Request Withdrawal ---");
		List<Application> allApps = dataManager.getApplicationDataManager().getAllApplications();
		List<Application> activeApps = new ArrayList<>();
		int index = 1;
		for (Application app : allApps) {
			if (app.getApplicantID().equalsIgnoreCase(applicant.getUserID()) && !app.isWithdrawn()) {
				System.out.println(index + ". " + app.getProjectName() + " | Status: " + app.getStatus());
				activeApps.add(app);
				index++;
			}
		}
		if (activeApps.isEmpty()) {
			System.out.println("No active applications found for withdrawal.");
			return;
		}
		System.out.print("Enter the number of the application you want to withdraw: ");
		int appIndex;
		try {
			appIndex = Integer.parseInt(scanner.nextLine().trim());
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Withdrawal cancelled.");
			return;
		}
		if (appIndex < 1 || appIndex > activeApps.size()) {
			System.out.println("Invalid selection. Withdrawal cancelled.");
			return;
		}
		Application appToWithdraw = activeApps.get(appIndex - 1);
		// Changed status to WITHDRAWAL_REQUESTED.
		appToWithdraw.setStatus(ApplicationStatus.WITHDRAWAL_REQUESTED);
		boolean updated = dataManager.getApplicationDataManager().updateApplication(appToWithdraw);
		if (updated) {
			System.out.println("Withdrawal requested for project \"" + appToWithdraw.getProjectName() + "\".  Awaiting HDB Manager approval.");
		} else {
			System.out.println("Failed to request withdrawal. Please try again.");
		}
	}

	// 5. Submit Enquiry: Prompt for enquiry text and project selection. (CHECKED)
	private void submitEnquiry() {
		System.out.println("\n--- Submit Enquiry ---");
		List<BTOProject> allProjects = dataManager.getProjectDataManager().getAllProjects();
		List<BTOProject> visibleProjects = new ArrayList<>();
		int index = 1;
		for (BTOProject proj : allProjects) {
      // ADDED: Check if the project is visible and if the applicant is eligible
      if (proj.isVisible() && isEligible(applicant, proj)) {
				System.out.println(index + ". " + proj.getProjectName() + " in " + proj.getNeighbourhood());
				visibleProjects.add(proj);
				index++;
			}
		}
		if (visibleProjects.isEmpty()) {
			System.out.println("No projects available for enquiry.");
			return;
		}
		System.out.print("Enter the number corresponding to the project for which you want to submit an enquiry: ");
		int projIndex;
		try {
			projIndex = Integer.parseInt(scanner.nextLine().trim());
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Enquiry submission cancelled.");
			return;
		}
		if (projIndex < 1 || projIndex > visibleProjects.size()) {
			System.out.println("Invalid project selection. Enquiry submission cancelled.");
			return;
		}
		BTOProject selectedProject = visibleProjects.get(projIndex - 1);

		System.out.print("Enter your enquiry text: ");
		String enquiryText = scanner.nextLine().trim();
		if (enquiryText.isEmpty()) {
			System.out.println("Enquiry text cannot be empty. Submission cancelled.");
			return;
		}
		// Submit the enquiry using the EnquiryManager.
		enquiryManager.submitEnquiry(applicant, selectedProject.getProjectName(), enquiryText);
	}

	// 6. Manage Enquiries: Allow the applicant to view, edit, or delete their enquiries. (CHECKED)
	private void manageEnquiries() {
		System.out.println("\n--- Manage Your Enquiries ---");
		List<Enquiry> myEnquiries = enquiryManager.viewEnquiries(applicant);
		if (myEnquiries.isEmpty()) {
			System.out.println("You have no enquiries to manage.");
			return;
		}
		int index = 1;
		for (Enquiry enq : myEnquiries) {
			System.out.println(index + ". ID: " + enq.getEnquiryID() + ", Project: " + enq.getProjectName()
			+ ", Enquiry: " + enq.getEnquiryText());
			index++;
		}
		System.out.print("Select an enquiry by number to manage (or enter 0 to cancel): ");
		int choice;
		try {
			choice = Integer.parseInt(scanner.nextLine().trim());
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Operation cancelled.");
			return;
		}
		if (choice == 0) {
			return;
		}
		if (choice < 1 || choice > myEnquiries.size()) {
			System.out.println("Invalid selection. Operation cancelled.");
			return;
		}
		Enquiry selectedEnquiry = myEnquiries.get(choice - 1);
		System.out.println("1. Edit Enquiry");
		System.out.println("2. Delete Enquiry");
		System.out.print("Enter your choice: ");
		String op = scanner.nextLine().trim();
		if (op.equals("1")) {
			System.out.print("Enter the new enquiry text: ");
			String newText = scanner.nextLine().trim();
			if (newText.isEmpty()) {
				System.out.println("Enquiry text cannot be empty. Edit cancelled.");
			} else {
				enquiryManager.editEnquiry(applicant, selectedEnquiry.getEnquiryID(), newText);
			}
		} else if (op.equals("2")) {
			enquiryManager.deleteEnquiry(applicant, selectedEnquiry.getEnquiryID());
		} else {
			System.out.println("Invalid operation. Returning to menu.");
		}
	}
	
	// 7. Change Password 
  private void changePassword() {
		System.out.print("Enter your current password: ");
    String currentPassword = scanner.nextLine().trim();

    System.out.print("Enter your new password: ");
    String newPassword = scanner.nextLine().trim();

    boolean changed = PasswordUtil.changePassword(applicant, currentPassword, newPassword);
    if (changed) {
			System.out.println("Password changed successfully.");
    } else {
			System.out.println("Password change failed.");
    }
  }
}
