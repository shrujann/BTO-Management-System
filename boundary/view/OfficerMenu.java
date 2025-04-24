package boundary.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import constants.ApplicationStatus;
import data.DataManager;
import data.UserDataManager;
import entity.communication.Enquiry;
import entity.communication.Receipt;
import entity.project.Application;
import entity.project.BTOProject;
import entity.user.Applicant;
import entity.user.HDBOfficer;
import entity.user.User;
import control.project.OfficerRegistrationManager;
import control.project.ApplicationManager;
import control.enquiry.EnquiryManager;
import util.DateUtil;
import util.PasswordUtil;
import util.ValidationUtil;

public class OfficerMenu {
	private HDBOfficer officer;
	private Scanner scanner;
	private DataManager dataManager;

	// Controllers / Managers
	private OfficerRegistrationManager officerRegistrationManager;
	private ApplicationManager applicationManager;
	private EnquiryManager enquiryManager;

	public OfficerMenu(HDBOfficer officer, Scanner scanner, DataManager dataManager) {
		this.officer = officer;
		this.scanner = scanner;
		this.dataManager = dataManager;

		officerRegistrationManager = new OfficerRegistrationManager(dataManager.getUserDataManager(),
				dataManager.getProjectDataManager(), dataManager.getApplicationDataManager());
		applicationManager = new ApplicationManager(dataManager.getApplicationDataManager(),
				dataManager.getProjectDataManager());
		enquiryManager = new EnquiryManager(dataManager.getEnquiryDataManager());
	}

	public void display() {
		boolean logout = false;
		List<BTOProject> projects1 = dataManager.getProjectDataManager().getAllProjects();

		for (BTOProject proj1 : projects1) {
			// String projectName=proj1.getProjectName();
			List<User> officers = proj1.getOfficers();
			for (User u : officers) {
				// String name = u.getName(); // assuming User has a getName() method
				if (u.getName().equals(officer.getName())) {
					officer.setProjectAssigned(proj1);
				}
			}
		}
		while (!logout) {
			System.out.println("\nWelcome HDB Officer: " + officer.getUserID());
			System.out.println("1. View Projects As Applicant");
			System.out.println("2. Apply for Project As Applicant");
			System.out.println("3. View Application Status As Applicant");
			System.out.println("4. Request Withdrawal As Applicant");
			System.out.println("5. Submit Enquiry As Applicant");
			System.out.println("6. View/Edit/Delete Enquiries As Applicant");
			System.out.println("7. Register to Join Project Team As HDB Officer");
			System.out.println("8. View Team Project Details As HDB Officer");
			System.out.println("9. Reply to Inquiries As HDB Officer");
			System.out.println("10. Update Flat Availability As HDB Officer");
			System.out.println("11. Update Applicant Status As HDB Officer");
			System.out.println("12. Generate Receipt As HDB Officer");
			System.out.println("13. Change Password As HDB Officer");
			System.out.println("14. View Applicant's Applications As HDB Officer");
			System.out.println("15. View Officer Registration Status As HDB Officer");
			System.out.println("16. Logout");
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
					registerToJoinProjectTeam();
					break;
				case "8":
					viewProjectDetails();
					break;
				case "9":
					replyToInquiries();
					break;
				case "10":
					updateFlatAvailability();
					break;
				case "11":
					updateApplicantStatus();
					break;
				case "12":
					generateReceipt();
					break;
				case "13":
					changePassword();
					break;
				case "14":
					viewApplicantApplications();
					break;
				case "15":
					viewOfficerRegistrationStatus();
					break;
				case "16":
					logout = true;
					System.out.println("Logging out. Returning to Main Menu.");
					break;
				default:
					System.out.println("Invalid choice. Please try again.");
			}
		}
	}

	// 1. View Projects: List all visible projects.
	// 1. View Projects: List all visible + eligible projects, mark
	// pending/approved, and show unit counts.
	// private void viewProjects() {
	// System.out.println("\n--- Available BTO Projects ---");
	// List<BTOProject> allProjects =
	// dataManager.getProjectDataManager().getAllProjects();
	// List<BTOProject> eligible = filterProjects(allProjects);

	// if (eligible.isEmpty()) {
	// System.out.println("No projects available for your user group at the
	// moment.");
	// return;
	// }

	// int idx = 1;
	// for (BTOProject p : eligible) {
	// boolean approved =
	// p.getApprovedOfficerRegistrations().contains(officer.getUserID());
	// boolean pending =
	// p.getPendingOfficerRegistrations().contains(officer.getUserID());
	// String status;
	// if (pending) {
	// status = " (Registration Pending)";
	// } else if (approved) {
	// status = " (Registered)";
	// } else {
	// status = "";
	// }
	// System.out.printf(
	// "%d. %s%s - %s%n 2-Room Units: %d, 3-Room Units: %d%n",
	// idx++,
	// p.getProjectName(),
	// status,
	// p.getNeighbourhood(),
	// p.getNum2RoomUnits(),
	// p.getNum3RoomUnits());
	// }
	// }

	// // Helper: only visible projects for which this officer meets the HDB
	// // age/marital + unit‑availability rules
	// private List<BTOProject> filterProjects(List<BTOProject> projects) {
	// int age = officer.getAge();
	// boolean married = officer.getMaritalStatus().equalsIgnoreCase("Married");

	// return projects.stream()
	// .filter(BTOProject::isVisible)
	// .filter(p -> {
	// // single ≥35 → only 2‑Room; married ≥21 → either type
	// if (!married && age >= 35) {
	// return p.getNum2RoomUnits() > 0;
	// } else if (married && age >= 21) {
	// return p.getNum2RoomUnits() > 0 || p.getNum3RoomUnits() > 0;
	// }
	// return false;
	// })
	// .collect(Collectors.toList());
	// }

	// 1. View Projects: List only the visible + eligible projects, marking
	// Pending/Registered/Rejected
	// private void viewProjects() {
	// System.out.println("\n--- Available BTO Projects ---");

	// // 1) filter by visibility + your eligibility rules
	// List<BTOProject> eligible = dataManager.getProjectDataManager()
	// .getAllProjects()
	// .stream()
	// .filter(BTOProject::isVisible)
	// .filter(p -> {
	// int age = officer.getAge();
	// boolean married = officer.getMaritalStatus().equalsIgnoreCase("Married");
	// if (!married && age >= 35) {
	// return p.getNum2RoomUnits() > 0;
	// } else if (married && age >= 21) {
	// return p.getNum2RoomUnits() > 0 || p.getNum3RoomUnits() > 0;
	// }
	// return false;
	// })
	// .collect(Collectors.toList());

	// if (eligible.isEmpty()) {
	// System.out.println("No projects available for your user group at the
	// moment.");
	// return;
	// }

	// // 2) print each with its status and both unit counts
	// int idx = 1;
	// for (BTOProject p : eligible) {
	// boolean approved =
	// p.getApprovedOfficerRegistrations().contains(officer.getUserID());
	// boolean pending =
	// p.getPendingOfficerRegistrations().contains(officer.getUserID());
	// boolean rejected =
	// p.getRejectedOfficerRegistrations().contains(officer.getUserID());

	// String status = pending ? " (Registration Pending)"
	// : approved ? " (Registered)"
	// : rejected ? " (Registration Rejected)"
	// : "";

	// System.out.printf(
	// "%d. %s%s - %s%n 2-Room Units: %d, 3-Room Units: %d%n",
	// idx++,
	// p.getProjectName(),
	// status,
	// p.getNeighbourhood(),
	// p.getNum2RoomUnits(),
	// p.getNum3RoomUnits());
	// }
	// }

	// in boundary.view.OfficerMenu

	/**
	 * 1. View Projects: list every visible project,
	 * mark pending / approved / rejected registrations.
	 */
	private void viewProjects() {
		System.out.println("\n--- Available BTO Projects ---");

		// 1) filter by visibility + your eligibility rules
		List<BTOProject> eligibleProjects = dataManager.getProjectDataManager()
      .getAllProjects()
      .stream()
      .filter(BTOProject::isVisible)  // Ensure project is visible
      .filter(p -> isEligible(officer, p)) // Use helper method to check eligibility
      .collect(Collectors.toList());

    if (eligibleProjects.isEmpty()) {
      System.out.println("No projects available for your user group at the moment.");
      return;
    }

		// 2) print each with its status and both unit counts
		int idx = 1;
		for (BTOProject p : eligibleProjects) {
			boolean approved = p.getApprovedOfficerRegistrations().contains(officer.getUserID());
			boolean pending = p.getPendingOfficerRegistrations().contains(officer.getUserID());
			boolean rejected = p.getRejectedOfficerRegistrations().contains(officer.getUserID());
		
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

		private boolean isEligible(HDBOfficer officer, BTOProject project) {
			int age = officer.getAge();
			boolean married = officer.getMaritalStatus().equalsIgnoreCase("Married");

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

	// 2. Apply for Project: Allow the officer to apply.
	private void applyForProject() {
		Date today = DateUtil.systemDate();
		BTOProject current = officer.getProjectAssigned();
		Date endDate = current.getApplicationEndDate();

		List<BTOProject> visibleProjects = new ArrayList<>();
		for (BTOProject proj : dataManager.getProjectDataManager().getAllProjects()) {
			if (proj.isVisible() && DateUtil.isWithinRange(today, proj.getApplicationStartDate(), proj.getApplicationEndDate()) || (!proj.getOfficers().contains(officer)) && DateUtil.isAfterEnd(endDate, proj.getApplicationStartDate()) ) {
				visibleProjects.add(proj);
			}
		}
		if (visibleProjects.isEmpty()) {
			return;
		} else {
				int idx = 1;
				for (BTOProject p : visibleProjects){
						boolean approved = p.getApprovedOfficerRegistrations().contains(officer.getUserID());
						boolean pending = p.getPendingOfficerRegistrations().contains(officer.getUserID());
						boolean rejected = p.getRejectedOfficerRegistrations().contains(officer.getUserID());
			
						String status;
						if (pending) {
							status = " (Registration Pending)";
						} else if (approved) {
							status = " (Registered)";
						} else if (rejected) {
							status = " (Registration Rejected)";
						} else {
							status = "";
						}
			
						System.out.printf(
								"%d. %s%s - %s%n" +
										"    2-Room Units: %d, 3-Room Units: %d%n",
								idx++,
								p.getProjectName(),
								status,
								p.getNeighbourhood(),
								p.getNum2RoomUnits(),
								p.getNum3RoomUnits());
			
					if (idx == 1) {
						System.out.println("No projects available.");
					}
		}
	}
		System.out.print("Enter the number corresponding to the project you want to apply for: ");
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
		
		List<User> projectOfficers=selectedProject.getOfficers();
		
		for (User u: projectOfficers) {
			if (u.getUserID().equals(officer.getUserID())) {
				System.out.println("You are already an officer for the project. No registration as applicant allowed.");
		return;	}
			}
		
		// No additional date validation needed here,
		// but you can use DateUtil if you require a more refined check.
		Application newApp = new Application(officer.getUserID(), selectedProject.getProjectName(),
				ApplicationStatus.PENDING);
		boolean added = dataManager.getApplicationDataManager().createApplication(newApp);
		if (added) {
			System.out.println(
					"Application for project \"" + selectedProject.getProjectName() + "\" submitted successfully.");
		} else {
			System.out.println("Failed to submit application.");
		}
	}

	// 3. View Application Status: List applications for the officer.
	private void viewApplicationStatus() {
		System.out.println("\n--- Your Application Status ---");
		List<Application> allApps = dataManager.getApplicationDataManager().getAllApplications();
		boolean found = false;
		for (Application app : allApps) {
			if (app.getApplicantID().equalsIgnoreCase(officer.getUserID())) {
				System.out.println("Project: " + app.getProjectName() + " | Status: " + app.getStatus()
						+ (app.isWithdrawn() ? " (Withdrawn)" : ""));
				found = true;
			}
		}
		if (!found) {
			System.out.println("You have not applied for any projects.");
		}
	}

	// 4. Request Withdrawal: Allow the officer to withdraw an active application.
	private void requestWithdrawal() {
		System.out.println("\n--- Request Withdrawal ---");
		List<Application> allApps = dataManager.getApplicationDataManager().getAllApplications();
		List<Application> activeApps = new ArrayList<>();
		int index = 1;
		for (Application app : allApps) {
			if (app.getApplicantID().equalsIgnoreCase(officer.getUserID()) && !app.isWithdrawn()) {
				System.out.println(index + ". " + app.getProjectName() + " | Status: " + app.getStatus());
				activeApps.add(app);
				index++;
			}
		}
		if (activeApps.isEmpty()) {
			System.out.println("No active applications found for withdrawal.");
			return;
		}
		System.out.print("Select the application number to withdraw: ");
		int sel;
		try {
			sel = Integer.parseInt(scanner.nextLine().trim());
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Withdrawal cancelled.");
			return;
		}
		if (sel < 1 || sel > activeApps.size()) {
			System.out.println("Invalid selection. Withdrawal cancelled.");
			return;
		}
		Application appToWithdraw = activeApps.get(sel - 1);
		appToWithdraw.setStatus(ApplicationStatus.WITHDRAWN);
		appToWithdraw.setWithdrawn(true);
		boolean updated = dataManager.getApplicationDataManager().updateApplication(appToWithdraw);
		if (updated) {
			System.out
					.println("Application for project \"" + appToWithdraw.getProjectName() + "\" has been withdrawn.");
		} else {
			System.out.println("Failed to withdraw application.");
		}
	}

	// 5. Submit Enquiry: Use EnquiryManager (trim inputs and check empty).
	private void submitEnquiry() {
		System.out.println("\n--- Submit Enquiry ---");
		List<BTOProject> projects = dataManager.getProjectDataManager().getAllProjects();
		List<BTOProject> visibleProjects = new ArrayList<>();
		int index = 1;
		for (BTOProject proj : projects) {
			if (proj.isVisible()) {
				System.out.println(index + ". " + proj.getProjectName() + " - " + proj.getNeighbourhood());
				visibleProjects.add(proj);
				index++;
			}
		}
		if (visibleProjects.isEmpty()) {
			System.out.println("No projects available for enquiry.");
			return;
		}
		System.out.print("Enter the number corresponding to the project for your enquiry: ");
		int projIndex;
		try {
			projIndex = Integer.parseInt(scanner.nextLine().trim());
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Enquiry cancelled.");
			return;
		}
		if (projIndex < 1 || projIndex > visibleProjects.size()) {
			System.out.println("Invalid project selection. Enquiry cancelled.");
			return;
		}
		BTOProject selectedProject = visibleProjects.get(projIndex - 1);
		System.out.print("Enter your enquiry text: ");
		String enquiryText = scanner.nextLine().trim();
		if (!ValidationUtil.isValidNRIC(enquiryText) && enquiryText.isEmpty()) {
			// Alternatively, we just check that it’s not empty.
			System.out.println("Enquiry text cannot be empty. Submission cancelled.");
			return;
		}
		enquiryManager.submitEnquiry(officer, selectedProject.getProjectName(), enquiryText);
	}

	// 6. View/Edit/Delete Enquiries: Manage own enquiries.
	private void manageEnquiries() {
		System.out.println("\n--- Manage Your Enquiries ---");
		List<Enquiry> myEnquiries = enquiryManager.viewEnquiries(officer);
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
		System.out.print("Select an enquiry number to manage (or enter 0 to cancel): ");
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
			System.out.print("Enter new enquiry text: ");
			String newText = scanner.nextLine().trim();
			if (newText.isEmpty()) {
				System.out.println("Enquiry text cannot be empty. Edit cancelled.");
			} else {
				enquiryManager.editEnquiry(officer, selectedEnquiry.getEnquiryID(), newText);
			}
		} else if (op.equals("2")) {
			enquiryManager.deleteEnquiry(officer, selectedEnquiry.getEnquiryID());
		} else {
			System.out.println("Invalid operation. Returning to menu.");
		}
	}

	// 7. Register to Join Project Team: Use OfficerRegistrationManager.
	private void registerToJoinProjectTeam() {
		System.out.println("\n--- Register to Join Project Team ---");
		System.out.print("Enter the project name to join: ");
		String projectName = scanner.nextLine().trim();

		BTOProject project = dataManager.getProjectDataManager().getProject(projectName);
		if (project == null) {
			System.out.println("Project not found.");
			return;
		}

		// Reject guard:
		if (project.getRejectedOfficerRegistrations().contains(officer.getUserID())) {
			System.out.println("Your previous registration was rejected; you cannot apply again.");
			return;
		}
		// Already pending?
		if (project.getPendingOfficerRegistrations().contains(officer.getUserID())) {
			System.out.println("You already have a pending registration for this project.");
			return;
		}
		// Already approved?
		if (project.getApprovedOfficerRegistrations().contains(officer.getUserID())) {
			System.out.println("You are already registered for this project.");
			return;
		}

		boolean registered = officerRegistrationManager
				.registerOfficerForProject(officer.getUserID(), projectName);
		System.out.println(registered
				? "Registration request submitted successfully."
				: "Failed to register for the project.");
	}

	// 8. View Project Details: Show details of the assigned project.
	private void viewProjectDetails() {
		System.out.println("\n--- View Project Details ---");
		BTOProject assignedProject = officer.getProjectAssigned();
		if (assignedProject == null) {
			System.out.println("You are not assigned to any project.");
			return;
		}
		System.out.println("Project Name: " + assignedProject.getProjectName());
		System.out.println("Neighbourhood: " + assignedProject.getNeighbourhood());
		System.out.println("Visibility: " + (assignedProject.isVisible() ? "Visible" : "Hidden"));
		System.out.println("2-Room Units: " + assignedProject.getNum2RoomUnits());
		System.out.println("3-Room Units: " + assignedProject.getNum3RoomUnits());
		System.out.println("Application Period: " + DateUtil.formatDate(assignedProject.getApplicationStartDate())
				+ " to " + DateUtil.formatDate(assignedProject.getApplicationEndDate()));
	}

	// 9. Reply to Inquiries: Allow the officer to reply to any enquiry.
	private void replyToInquiries() {
		System.out.println("\n--- Reply to Inquiries ---");
		List<Enquiry> allEnquiries = dataManager.getEnquiryDataManager().getAllEnquiries();
		if (allEnquiries.isEmpty()) {
			System.out.println("No enquiries available.");
			return;
		}
		int index = 1;
		for (Enquiry enq : allEnquiries) {
			System.out.println(index + ". ID: " + enq.getEnquiryID() + ", Applicant: " + enq.getApplicantID()
					+ ", Project: " + enq.getProjectName() + ", Enquiry: " + enq.getEnquiryText() + ", Reply: "
					+ (enq.getReplyText() == null ? "No reply" : enq.getReplyText()));
			index++;
		}
		System.out.print("Enter the ID of the enquiry to reply to: ");
		int replyID;
		try {
			replyID = Integer.parseInt(scanner.nextLine().trim());
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Operation cancelled.");
			return;
		}
		Enquiry enquiryToReply = null;
		for (Enquiry enq : allEnquiries) {
			if (enq.getEnquiryID() == replyID) {
				enquiryToReply = enq;
				break;
			}
		}
		if (enquiryToReply == null) {
			System.out.println("Enquiry not found.");
			return;
		}
		System.out.print("Enter your reply: ");
		String replyText = scanner.nextLine().trim();
		if (replyText.isEmpty()) {
			System.out.println("Reply cannot be empty.");
			return;
		}
		enquiryToReply.setReplyText(replyText);
		boolean updated = dataManager.getEnquiryDataManager().updateEnquiry(enquiryToReply);
		if (updated) {
			System.out.println("Reply sent successfully.");
		} else {
			System.out.println("Failed to send reply.");
		}
	}

	// 10. Update Flat Availability: Allow officer to decrease unit count with
	// validation.
	private void updateFlatAvailability() {
		System.out.println("\n--- Update Flat Availability ---");
		BTOProject assignedProject = officer.getProjectAssigned();
		if (assignedProject == null) {
			System.out.println("You are not assigned to any project to update flat availability.");
			return;
		}
		System.out.println("Current availability:");
		System.out.println("2-Room Units: " + assignedProject.getNum2RoomUnits());
		System.out.println("3-Room Units: " + assignedProject.getNum3RoomUnits());
		System.out.print("Enter flat type to update (2-Room/3-Room): ");
		String flatType = scanner.nextLine().trim();
		if (flatType.isEmpty() || (!flatType.equalsIgnoreCase("2-Room") && !flatType.equalsIgnoreCase("3-Room"))) {
			System.out.println("Invalid flat type. Update cancelled.");
			return;
		}
		System.out.print("Enter number of units to decrease: ");
		int units;
		try {
			units = Integer.parseInt(scanner.nextLine().trim());
			if (units <= 0) {
				System.out.println("Units must be a positive number. Update cancelled.");
				return;
			}
		} catch (NumberFormatException e) {
			System.out.println("Invalid number. Update cancelled.");
			return;
		}
		if (flatType.equalsIgnoreCase("2-Room")) {
			int current = assignedProject.getNum2RoomUnits();
			if (units > current) {
				System.out.println("Cannot decrease more units than available. Update cancelled.");
				return;
			}
			assignedProject.setNum2RoomUnits(current - units);
		} else {
			int current = assignedProject.getNum3RoomUnits();
			if (units > current) {
				System.out.println("Cannot decrease more units than available. Update cancelled.");
				return;
			}
			assignedProject.setNum3RoomUnits(current - units);
		}
		boolean updated = dataManager.getProjectDataManager().updateProject(assignedProject);
		if (updated) {
			System.out.println("Flat availability updated successfully.");
		} else {
			System.out.println("Failed to update flat availability.");
		}

	}

	// 11. Update Applicant Status: Allow officer to update application status.
	private void updateApplicantStatus() {
		System.out.println("\n--- Update Applicant Status and Book Flat for Successful Applicants ---");
		System.out.print("Enter the project name to view applications: ");
		String projectName = scanner.nextLine().trim();
		if (projectName.isEmpty()) {
			System.out.println("Project name cannot be empty. Operation cancelled.");
			return;
		}
		if (!projectName.equals(officer.getProjectAssigned().getProjectName())) {
		    System.out.println("You are not an officer for this project. Operation cancelled.");
		    return;
		}
		List<Application> projectApps = applicationManager.getApplicationsByProject(projectName);
		if (projectApps.isEmpty()) {
			System.out.println("No applications found for this project.");
			return;
		}

		// ADDED: Print list of applications
		int index = 0;
		List<Application> newprojectApps= new ArrayList<>();
		for (Application app : projectApps) {
			
			if (app.getStatus()!=ApplicationStatus.SUCCESSFUL) {
				
				continue;}
			index++;
			System.out.println(index + ". Applicant: " + app.getApplicantID() +
					" | Current Status: " + app.getStatus() +
					" | Flat Type: " + (app.getFlatType() != null ? app.getFlatType() : "Not Selected"));
			newprojectApps.add(app);
		}
		
		if (index==0) {
			System.out.println("No Valid Applications You Can Update.");
			return;
		}

		System.out.print("Select an application to update (number): ");
		int sel;
		try {
			sel = Integer.parseInt(scanner.nextLine().trim());
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Operation cancelled.");
			return;
		}
		if (sel < 1 || sel > projectApps.size()) {
			System.out.println("Invalid selection. Operation cancelled.");
			return;
		}

		Application selectedApp = newprojectApps.get(sel - 1);
		BTOProject project = dataManager.getProjectDataManager().getProject(projectName);

		if (project == null) {
			System.out.println("Project not found. Operation cancelled.");
			return;
		}

		// Check if the applicant has already booked a flat
		if (project.getBookedApplicants().contains(selectedApp.getApplicantID())) {
			System.out.println("Applicant has already booked a flat. Operation cancelled.");
			return;
		}

		System.out.print("Enter new status (BOOKED): ");
		String statusInput = scanner.nextLine().trim().toUpperCase();
		ApplicationStatus newStatus;
		try {
			newStatus = ApplicationStatus.valueOf(statusInput);
		} catch (IllegalArgumentException e) {
			System.out.println("Invalid status. Operation cancelled.");
			return;
		}

		// If setting status to BOOKED, ask the HDB officer to set the flat type.
		String flatType = null;
		if (newStatus == ApplicationStatus.BOOKED) {
			System.out.print("Enter flat type (2-Room/3-Room): ");
			flatType = scanner.nextLine().trim();
			if (!flatType.equalsIgnoreCase("2-Room") && !flatType.equalsIgnoreCase("3-Room")) {
				System.out.println("Invalid flat type. Operation cancelled.");
				return;
			}
		}

		// Update the application status
		selectedApp.setStatus(newStatus);
		selectedApp.setFlatType(flatType);

		// If application is successful and the application status has been updated to
		// booked, update booked applicant to the project.
		if (newStatus == ApplicationStatus.BOOKED) {
			project.getBookedApplicants().add(selectedApp.getApplicantID());
		}

		// Update database
		boolean projectUpdated = dataManager.getProjectDataManager().updateProject(project);
		boolean appUpdated = dataManager.getApplicationDataManager().updateApplication(selectedApp);

		if (projectUpdated && appUpdated) {
			System.out.println("Applicant status updated successfully.");
		} else {
			System.out.println("Failed to update applicant status.");
		}
	}

	/*
	 * boolean updated =
	 * applicationManager.updateApplicationStatus(selectedApp.getApplicantID(),
	 * newStatus);
	 * if (updated) {
	 * System.out.println("Applicant status updated successfully.");
	 * } else {
	 * System.out.println("Failed to update applicant status.");
	 * }
	 * }
	 */

	private void viewOfficerRegistrationStatus() {
		System.out.println("\n--- Officer Registration Status ---");
		List<BTOProject> allProjects = dataManager.getProjectDataManager().getAllProjects();
		boolean found = false;

		for (BTOProject p : allProjects) {
			String name = p.getProjectName();
			if (p.getApprovedOfficerRegistrations().contains(officer.getUserID())) {
				System.out.println("Project: " + name + " | Status: APPROVED");
				found = true;
			}
			if (p.getPendingOfficerRegistrations().contains(officer.getUserID())) {
				System.out.println("Project: " + name + " | Status: PENDING APPROVAL");
				found = true;
			}
			if (p.getRejectedOfficerRegistrations().contains(officer.getUserID())) {
				System.out.println("Project: " + name + " | Status: REJECTED");
				found = true;
			}
		}

		if (!found) {
			System.out.println("You have not registered for any projects as an officer.");
		}
	}

	// 12. Generate Receipt: Simulate receipt generation.
	private void generateReceipt() {
		System.out.println("\n--- Generate Receipt ---");
		List<Application> allApps = dataManager.getApplicationDataManager().getAllApplications();
		List<Application> successfulApps = new ArrayList<>();
		int index = 1;
		for (Application app : allApps) {
			
			List<BTOProject> officerFutureProjects = officer.getFutureProjects();
			List<String> projectNames= new ArrayList<>();
			for (BTOProject p1: officerFutureProjects) {
				projectNames.add(p1.getProjectName());
			}
			projectNames.add(officer.getProjectAssigned().getProjectName());
			
			String appProjectName=app.getProjectName();
			
			if (!projectNames.contains(appProjectName)) {
			    continue;
			}
			
			if (app.getStatus() == ApplicationStatus.BOOKED) {
				System.out
						.println(index + ". Applicant: " + app.getApplicantID() + ", Project: " + app.getProjectName());
				successfulApps.add(app);
				index++;
			}
		}
		if (successfulApps.isEmpty()) {
			System.out.println("No successful applications available for receipt generation.");
			return;
		}
		System.out.print("Select the application number for which to generate a receipt: ");
		int sel;
		try {
			sel = Integer.parseInt(scanner.nextLine().trim());
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Operation cancelled.");
			return;
		}
		if (sel < 1 || sel > successfulApps.size()) {
			System.out.println("Invalid selection. Operation cancelled.");
			return;
		}
		Application selectedApp = successfulApps.get(sel - 1);
		BTOProject project = dataManager.getProjectDataManager().getProject(selectedApp.getProjectName());
		if (project == null) {
			System.out.println("Project not found. Operation cancelled.");
			return;
		}
		int receiptID = (int) (Math.random() * 10000);
		String applicantNRIC=selectedApp.getApplicantID();
		
		List<User> users= dataManager.getUserDataManager().getAllUsers();
		
		Applicant a = null;
		for (User u : users) {
		    if (u.getUserID().equals(applicantNRIC)) {
		        a = (Applicant) u;
		        break;
		    }
		}
		
		
		Receipt receipt = new Receipt(receiptID, a, project, selectedApp.getFlatType(), new Date());
		System.out.println("Receipt generated successfully:");
		System.out.println("Applicant Name: "+receipt.getApplicant().getName());
		System.out.println("Applicant NRIC: "+receipt.getApplicant().getUserID());
		System.out.println("Applicant Age: "+receipt.getApplicant().getAge());
		System.out.println("Applicant Marital Status: "+receipt.getApplicant().getMaritalStatus());
		System.out.println("Applicant Flat Type Booked: "+receipt.getFlatType());
		System.out.println("Applicant Project: "+receipt.getProject().getProjectName());




	}

	// 13. Change Password
	private void changePassword() {
		System.out.print("Enter your current password: ");
		String currentPassword = scanner.nextLine().trim();

		System.out.print("Enter your new password: ");
		String newPassword = scanner.nextLine().trim();

		boolean changed = PasswordUtil.changePassword(officer, currentPassword, newPassword);
		if (changed) {
			System.out.println("Password changed successfully.");
		} else {
			System.out.println("Password change failed.");
		}
	}

	// 14. View all applications for a given applicant NRIC.
	private void viewApplicantApplications() {
		System.out.println("\n--- View Applicant’s Applications ---");
		System.out.print("Enter applicant NRIC: ");
		String nric = scanner.nextLine().trim();
		if (nric.isEmpty()) {
			System.out.println("NRIC cannot be empty. Operation cancelled.");
			return;
		}
		List<Application> apps = applicationManager.getApplicationsByApplicant(nric);
		
		for (Application a : apps) {
			if (!a.getProjectName().equals(officer.getProjectAssigned().getProjectName())) {
				System.out.println("You cannot view applications of this applicant. The applicant has not applied for your project.");
				return;
			}
		}
		
	    
		if (apps.isEmpty()) {
			System.out.println("No applications found for NRIC " + nric + ".");
		} else {
			System.out.println("Applications for " + nric + ":");
			for (Application app : apps) {
				System.out.printf("  • Project: %s | Status: %s%n",
						app.getProjectName(), app.getStatus());
			}
		}
	}
}
