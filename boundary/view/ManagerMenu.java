package boundary.view;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import constants.ApplicationStatus;
import constants.SystemConstants;
import data.DataManager;
import data.UserDataManager;
import entity.communication.Enquiry;
import entity.project.Application;
import entity.project.BTOProject;
import entity.project.FlatType;
import entity.user.HDBManager;
import entity.user.HDBOfficer;
import control.project.ProjectManager;
import control.project.OfficerRegistrationManager;
import control.report.ReportGenerator;
import csv.csvWrite;
import util.FilterUtil;
import util.PasswordUtil;
import util.DateUtil;

public class ManagerMenu {
	private HDBManager manager;
	private Scanner scanner;
	private DataManager dataManager;

	// Instantiate controllers using the DataManager's sub-managers.
	private ProjectManager projectManager;
	private OfficerRegistrationManager officerRegistrationManager;
	private UserDataManager userDataManager; // For report generation

	public ManagerMenu(HDBManager manager, Scanner scanner, DataManager dataManager) {
		this.manager = manager;
		this.scanner = scanner;
		this.dataManager = dataManager;

		projectManager = new ProjectManager(dataManager.getProjectDataManager());
		officerRegistrationManager = new OfficerRegistrationManager(dataManager.getUserDataManager(),
				dataManager.getProjectDataManager(), dataManager.getApplicationDataManager());
		userDataManager = dataManager.getUserDataManager();
	}

	public void display() {
		boolean logout = false;
		while (!logout) {
			System.out.println("\nWelcome HDB Manager: " + manager.getUserID());
			System.out.println("1. Create BTO Project");
			System.out.println("2. Edit BTO Project");
			System.out.println("3. Delete BTO Project");
			System.out.println("4. Toggle Project Visibility");
			System.out.println("5. View All Projects");
			System.out.println("6. Filter Projects");
			System.out.println("7. Approve/Reject Officer Registration");
			System.out.println("8. Approve/Reject Application Withdrawal");
			System.out.println("9. Review and Approve/Reject Applications"); // ADDED (CHECKED)
			System.out.println("10. Generate Reports");
			System.out.println("11. View Enquiries of ALL Projects");
			System.out.println("12. Reply to Enquiries");
			System.out.println("13. Change Password");
			System.out.println("14. Logout");
			System.out.print("Enter your choice: ");
			String choice = scanner.nextLine();

			switch (choice) {
				case "1":
					createBTOProject();
					break;
				case "2":
					editBTOProject();
					break;
				case "3":
					deleteBTOProject();
					break;
				case "4":
					toggleProjectVisibility();
					break;
				case "5":
					viewAllProjects();
					break;
				case "6":
					filterProjects();
					break;
				case "7":
					handleOfficerRegistration();
					break;
				case "8":
					handleApplicationWithdrawal();
					break;
				case "9":
					reviewAndApproveApplications();
					break;
				case "10":
					generateReports();
					break;
				case "11":
					viewAllEnquiries();
					break;
				case "12":
					replyToEnquiries();
					break;
				case "13":
					changePassword();
					break;
				case "14":
					logout = true;
					System.out.println("Logging out. Returning to Main Menu.");
					break;
				default:
					System.out.println("Invalid choice. Please try again.");
			}
		}
	}

	/**
	 * 1. Create BTO Project – prompts for units, prices, dates and immediately
	 * persists
	 */
	private void createBTOProject() {
		System.out.println("\n--- Create BTO Project ---");
		System.out.print("Enter project name: ");
		String projectName = scanner.nextLine().trim();
		if (projectName.isEmpty()) {
			System.out.println("Project name cannot be empty. Operation cancelled.");
			return;
		}

		System.out.print("Enter neighbourhood: ");
		String neighbourhood = scanner.nextLine().trim();
		if (neighbourhood.isEmpty()) {
			System.out.println("Neighbourhood cannot be empty. Operation cancelled.");
			return;
		}

		System.out.print("Is the project visible? (yes/no): ");
		boolean visible = scanner.nextLine().trim().equalsIgnoreCase("yes");

		// application dates
		System.out.print("Enter application opening date (M/DD/YY): ");
		Date startDate;
		try {
			startDate = DateUtil.parseDate(scanner.nextLine().trim());
		} catch (ParseException e) {
			System.out.println("Invalid date format. Operation cancelled.");
			return;
		}
		System.out.print("Enter application closing date (M/DD/YY): ");
		Date endDate;
		try {
			endDate = DateUtil.parseDate(scanner.nextLine().trim());
		} catch (ParseException e) {
			System.out.println("Invalid date format. Operation cancelled.");
			return;
		}
		if (endDate.before(startDate)) {
			System.out.println("Closing date must be after opening date. Operation cancelled.");
			return;
		}

		// overlap check
		if (hasOverlappingProject(manager.getUserID(), startDate, endDate)) {
			System.out.println("You already have a project in that period. Operation cancelled.");
			return;
		}

		// units & prices
		System.out.print("Enter number of 2-Room units: ");
		int num2 = readNonNegativeInt();
		if (num2 < 0)
			return;

		System.out.print("Enter selling price for 2-Room flats: ");
		double price2 = readNonNegativeDouble();
		if (price2 < 0)
			return;

		System.out.print("Enter number of 3-Room units: ");
		int num3 = readNonNegativeInt();
		if (num3 < 0)
			return;

		System.out.print("Enter selling price for 3-Room flats: ");
		double price3 = readNonNegativeDouble();
		if (price3 < 0)
			return;

		System.out.print("Enter available HDB Officer slots: ");
		int slots = readNonNegativeInt();
		if (slots < 0)
			return;

		// build project
		BTOProject project = new BTOProject(
				projectName,
				neighbourhood,
				visible,
				num2,
				num3,
				startDate,
				endDate,
				manager.getName(),
				slots,
				/* officers= */ null);

		// overwrite its FlatType list
		project.getFlatTypes().clear();
		project.getFlatTypes().add(new FlatType("2‑Room", num2, price2));
		project.getFlatTypes().add(new FlatType("3‑Room", num3, price3));

		// persist
		if (projectManager.createProject(project)) {
			System.out.println("Project created successfully!");
			persistProjects();
		} else {
			System.out.println("Failed to create project.");
		}
	}

	/** 2. Edit BTO Project – can adjust name, dates and per‑flat prices */
	private void editBTOProject() {
		System.out.println("\n--- Edit BTO Project ---");
		List<BTOProject> myProjects = projectManager.getProjectsByHDBManager(manager.getName());
		if (myProjects.isEmpty()) {
			System.out.println("You have not created any projects.");
			return;
		}

		for (int i = 0; i < myProjects.size(); i++) {
			System.out.printf("%d. %s%n", i + 1, myProjects.get(i).getProjectName());
		}
		System.out.print("Select a project to edit (number): ");
		int idx;
		try {
			idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
			if (idx < 0 || idx >= myProjects.size())
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			System.out.println("Invalid selection. Operation cancelled.");
			return;
		}
		BTOProject p = myProjects.get(idx);

		System.out.print("Enter new project name (leave blank to keep): ");
		String s = scanner.nextLine().trim();
		if (!s.isEmpty())
			p.setProjectName(s);

		System.out.print("Enter new neighbourhood (leave blank to keep): ");
		s = scanner.nextLine().trim();
		if (!s.isEmpty())
			p.setNeighbourhood(s);

		System.out.print("Enter new opening date (M/DD/YY, leave blank to keep): ");
		s = scanner.nextLine().trim();
		if (!s.isEmpty()) {
			try {
				p.setApplicationStartDate(DateUtil.parseDate(s));
			} catch (ParseException ex) {
				System.out.println("Bad date format. Operation cancelled.");
				return;
			}
		}

		System.out.print("Enter new closing date (M/DD/YY, leave blank to keep): ");
		s = scanner.nextLine().trim();
		if (!s.isEmpty()) {
			try {
				p.setApplicationEndDate(DateUtil.parseDate(s));
			} catch (ParseException ex) {
				System.out.println("Bad date format. Operation cancelled.");
				return;
			}
		}

		// 2‑Room price
		System.out.print("Enter new price for 2-Room flat (leave blank to keep): ");
		s = scanner.nextLine().trim();
		if (!s.isEmpty()) {
			try {
				double np2 = Double.parseDouble(s);
				if (np2 < 0)
					throw new NumberFormatException();
				p.getFlatTypes().stream()
						.filter(ft -> ft.getTypeName().equals("2-Room"))
						.findFirst()
						.ifPresent(ft -> ft.setSellingPrice(np2));
			} catch (NumberFormatException e) {
				System.out.println("Invalid price. Skipping 2-Room price update.");
			}
		}

		// 3-Room price
		System.out.print("Enter new price for 3-Room flat (leave blank to keep): ");
		s = scanner.nextLine().trim();
		if (!s.isEmpty()) {
			try {
				double np3 = Double.parseDouble(s);
				if (np3 < 0)
					throw new NumberFormatException();
				p.getFlatTypes().stream()
						.filter(ft -> ft.getTypeName().equals("3-Room"))
						.findFirst()
						.ifPresent(ft -> ft.setSellingPrice(np3));
			} catch (NumberFormatException e) {
				System.out.println("Invalid price. Skipping 3-Room price update.");
			}
		}
		
		
		System.out.print("Enter new 2-Room Flat Units (leave blank to keep): ");
		s = scanner.nextLine().trim();
		if (!s.isEmpty()) {
			try {
				int np4 = Integer.parseInt(s);
				if (np4 < 0)
					throw new NumberFormatException();
				p.setNum2RoomUnits(np4);
			} catch (NumberFormatException e) {
				System.out.println("Invalid price. Skipping 2-Room Unit update.");
			}
		}
		
		System.out.print("Enter new 3-Room Flat Units (leave blank to keep): ");
		s = scanner.nextLine().trim();
		if (!s.isEmpty()) {
			try {
				int np5 = Integer.parseInt(s);
				if (np5 < 0)
					throw new NumberFormatException();
				p.setNum3RoomUnits(np5);
			} catch (NumberFormatException e) {
				System.out.println("Invalid price. Skipping 3-Room Unit update.");
			}
		}

		if (projectManager.editProject(p)) {
			System.out.println("Project updated successfully.");
			persistProjects();
		} else {
			System.out.println("Failed to update project.");
		}
	}

	/** Helpers for reading non-negative numbers or cancelling on bad input */
	private int readNonNegativeInt() {
		try {
			int v = Integer.parseInt(scanner.nextLine().trim());
			if (v < 0)
				throw new NumberFormatException();
			return v;
		} catch (NumberFormatException e) {
			System.out.println("Invalid number. Operation cancelled.");
			return -1;
		}
	}

	private double readNonNegativeDouble() {
		try {
			double v = Double.parseDouble(scanner.nextLine().trim());
			if (v < 0)
				throw new NumberFormatException();
			return v;
		} catch (NumberFormatException e) {
			System.out.println("Invalid price. Operation cancelled.");
			return -1;
		}
	}

	// 3. Delete BTO Project – remains the same. (CHECKED)
	private void deleteBTOProject() {
		System.out.println("\n--- Delete BTO Project ---");
		List<BTOProject> myProjects1 = projectManager.getProjectsByHDBManager(manager.getName());
		if (myProjects1.isEmpty()) {
			System.out.println("You have no projects to delete.");
			return;
		}
		int index = 1;
		for (BTOProject p : myProjects1) {
			System.out.println(index + ". " + p.getProjectName());
			index++;
		}
		System.out.print("Select a project to delete (number): ");
		int projIndex;
		try {
			projIndex = Integer.parseInt(scanner.nextLine());
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Operation cancelled.");
			return;
		}
		if (projIndex < 1 || projIndex > myProjects1.size()) {
			System.out.println("Invalid selection.");
			return;
		}

		BTOProject selectedProject = myProjects1.get(projIndex - 1);
		if (!selectedProject.getBookedApplicants().isEmpty()) {
			System.out.println("Unable to delete project as applicants have booked flats already");
			return;
		}
		boolean deleted = projectManager.deleteProject(selectedProject.getProjectName());
		if (deleted) {
			System.out.println("Project deleted successfully.");
			persistProjects();
		} else {
			System.out.println("Failed to delete project.");
		}
	}

	// 4. Toggle Project Visibility – remains similar. (CHECKED)
	private void toggleProjectVisibility() {
		System.out.println("\n--- Toggle Project Visibility ---");
		List<BTOProject> myProjects = projectManager.getProjectsByHDBManager(manager.getName());
		if (myProjects.isEmpty()) {
			System.out.println("You have no projects.");
			return;
		}
		int index = 1;
		for (BTOProject p : myProjects) {
			System.out.println(
					index + ". " + p.getProjectName() + " (Currently " + (p.isVisible() ? "Visible" : "Hidden") + ")");
			index++;
		}
		System.out.print("Select a project to toggle visibility (number): ");
		int projIndex;
		try {
			projIndex = Integer.parseInt(scanner.nextLine());
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Operation cancelled.");
			return;
		}
		if (projIndex < 1 || projIndex > myProjects.size()) {
			System.out.println("Invalid selection.");
			return;
		}
		BTOProject selectedProject = myProjects.get(projIndex - 1);
		boolean newVisibility = !selectedProject.isVisible();
		boolean toggled = projectManager.toggleProjectVisibility(selectedProject.getProjectName(), newVisibility);
		if (toggled) {
			System.out.println(
					"Project visibility toggled successfully. New status: " + (newVisibility ? "Visible" : "Hidden"));
			persistProjects();
		} else {
			System.out.println("Failed to toggle project visibility.");
		}
	}

	// 5. View All Projects – no changes. (CHECKED)
	private void viewAllProjects() {
		System.out.println("\n--- All BTO Projects ---");
		List<BTOProject> allProjects = projectManager.getAllProjects();
		if (allProjects.isEmpty()) {
			System.out.println("No projects found.");
			return;
		}
		for (BTOProject proj : allProjects) {
			System.out.println("Project: " + proj.getProjectName() + ", Neighbourhood: " + proj.getNeighbourhood()
					+ ", Visible: " + proj.isVisible() + ", Manager: " + proj.getHdbManagerInCharge());
		}
	}

	// 6. Filter Projects – now using FilterUtil. (CHECKED)
	private void filterProjects() {
		System.out.println("\n--- Filter Projects ---");
		System.out.println("1. Filter by Neighbourhood");
		System.out.println("2. Filter by Flat Type");
		System.out.println("3. Show All of Your Projects"); // Option to remove filter
		System.out.print("Enter your choice (1-3): ");
		String choice = scanner.nextLine().trim();

		List<BTOProject> allProjects = dataManager.getProjectDataManager().getAllProjects();
		List<BTOProject> filteredProjects = new ArrayList<>(allProjects); // Start with all projects

		switch (choice) {
			case "1":
				// Display available neighborhoods
				System.out.println("Available Neighbourhoods:");
				Set<String> neighbourhoods = new HashSet<>();
				for (BTOProject project : allProjects) {
					neighbourhoods.add(project.getNeighbourhood());
				}
				int i = 1;
				for (String neighbourhood : neighbourhoods) {
					System.out.println(i + ". " + neighbourhood);
					i++;
				}
				System.out.print("Enter neighbourhood: ");
				String neighbourhood = scanner.nextLine().trim();
				filteredProjects = FilterUtil.filterByLocation(allProjects, neighbourhood);
				System.out.println("Filtered Projects by Neighbourhood: ");
				for (BTOProject p : filteredProjects) {
					System.out.println(p.getProjectName() + " in " + p.getNeighbourhood());
				}
				break;
			case "2":
				// Display available flat types
				System.out.println("Available Flat Types:");
				Set<String> flatTypes = new HashSet<>();
				for (BTOProject project : allProjects) {
					flatTypes.addAll(project.getTypesOfFlat());
				}
				i = 1;
				for (String flatType : flatTypes) {
					System.out.println(i + ". " + flatType);
					i++;
				}
				System.out.print("Enter flat type: ");
				String flatType = scanner.nextLine().trim();
				filteredProjects = FilterUtil.filterByFlatType(allProjects, flatType);
				System.out.println("Filtered Projects by Flat Type: ");
				for (BTOProject p : filteredProjects) {
					System.out.println(
							p.getProjectName() + " in " + p.getNeighbourhood() + " with types: " + p.getTypesOfFlat());
				}
				break;
			case "3":
				// Option 3: Show All Projects
				System.out.println("\n--- All of Your Projects ---");
				List<BTOProject> myProjects2 = projectManager.getProjectsByHDBManager(manager.getName());
				if (myProjects2.isEmpty()) {
					System.out.println("No projects found.");
					return;
				}
				for (BTOProject proj : myProjects2) {
					System.out.println("Project: " + proj.getProjectName() + ", Neighbourhood: " + proj.getNeighbourhood()
							+ ", Visible: " + proj.isVisible() + ", Manager: " + proj.getHdbManagerInCharge());
				}
				break;
			default:
				System.out.println("Invalid choice. Showing all projects.");
				break;
		}
		// Sort alphabetically
		filteredProjects = FilterUtil.sortByAlphabeticalOrder(filteredProjects);

		if (filteredProjects.isEmpty()) {
			System.out.println("No projects match the criteria.");
		}
	}
	/*
	 * private void filterProjects() {
	 * System.out.println("\n--- Filter Projects ---");
	 * System.out.print("Enter filter criteria (e.g., neighbourhood or flat type): "
	 * );
	 * String criteria = scanner.nextLine().trim();
	 * List<BTOProject> allProjects =
	 * dataManager.getProjectDataManager().getAllProjects();
	 * 
	 * // Use FilterUtil methods to filter by location and flat type.
	 * List<BTOProject> filteredByLocation =
	 * FilterUtil.filterByLocation(allProjects, criteria);
	 * List<BTOProject> filteredByFlatType =
	 * FilterUtil.filterByFlatType(allProjects, criteria);
	 * 
	 * // Combine results (removing duplicates may be added as needed)
	 * List<BTOProject> filtered = new ArrayList<>();
	 * filtered.addAll(filteredByLocation);
	 * filtered.addAll(filteredByFlatType);
	 * 
	 * // Sort alphabetically
	 * filtered = FilterUtil.sortByAlphabeticalOrder(filtered);
	 * 
	 * if (filtered.isEmpty()) {
	 * System.out.println("No projects match the criteria.");
	 * } else {
	 * for (BTOProject proj : filtered) {
	 * System.out.println("Project: " + proj.getProjectName() + ", Neighbourhood: "
	 * + proj.getNeighbourhood());
	 * }
	 * }
	 * }
	 */

	// 7. Approve/Reject Officer Registration – remains as before. (CHECKED)
	/**
	 * 7. Approve/Reject Officer Registration – now with immediate CSV persistence.
	 */
	private void handleOfficerRegistration() {
		System.out.println("\n--- Approve/Reject Officer Registration ---");
		System.out.print("Enter the project name to review registrations: ");
		String projectName = scanner.nextLine().trim();
		System.out.print("Enter the officer's NRIC: ");
		String officerNRIC = scanner.nextLine().trim();

		// Lookup objects
		HDBOfficer officer = dataManager.getUserDataManager().getHDBOfficer(officerNRIC);
		BTOProject project = dataManager.getProjectDataManager().getProject(projectName);

		if (officer == null) {
			System.out.println("Error: HDB Officer with NRIC " + officerNRIC + " not found.");
			return;
		}
		if (project == null) {
			System.out.println("Error: BTO Project with name '" + projectName + "' not found.");
			return;
		}

		// Validation checks
		if (dataManager.getApplicationDataManager().isApplicantForProject(officerNRIC, projectName)) {
			System.out.println("Error: This officer has applied for the same project as an applicant.");
			return;
		}
		if (officerRegistrationManager.isOfficerRegisteredForAnotherProject(officerNRIC, projectName)) {
			System.out.println("Error: This officer is already registered for another project.");
			return;
		}

		System.out.println("1. Approve Registration");
		System.out.println("2. Reject Registration");
		System.out.print("Enter your choice: ");
		String choice = scanner.nextLine();

		boolean result;
		switch (choice) {
			case "1":
				result = officerRegistrationManager.approveOfficerRegistration(officerNRIC, projectName);
				if (result) {
					System.out.println("Officer registration approved.");
					if (officer.getProjectAssigned() != null && !officer.getProjectAssigned().getProjectName().equals(projectName)){
						officer.addFutureProject(project);
						System.out.println("Due to this project being assigned after " + officerNRIC + "current project, it has been added into the FutureProjects:");
						System.out.println("Future Projects: ");
						
						for (BTOProject p : officer.getFutureProjects()){
							System.out.println("Project : " + p.getProjectName());
						}
					}
				} else {
					System.out.println("Failed to approve registration.");
					return;
				}
				break;
			case "2":
				result = officerRegistrationManager.rejectOfficerRegistration(officerNRIC, projectName);
				if (result) {
					project.getRejectedOfficerRegistrations().add(officerNRIC);
					project.getPendingOfficerRegistrations().remove(officerNRIC);
					System.out.println("Officer registration rejected.");
				} else {
					System.out.println("Failed to reject registration.");
					return;
				}
				break;
			default:
				System.out.println("Invalid choice.");
				return;
		}

		// 2) immediately persist the updated project list
		csvWrite writer = new csvWrite(dataManager);
		writer.projectWrite(
				SystemConstants.projectPath,
				dataManager.getProjectDataManager().getAllProjects());
		System.out.println("ProjectList.csv updated on disk.");
	}

	// 8. Approve/Reject Application Withdrawal (CHECKED)
	private void handleApplicationWithdrawal() {
		System.out.println("\n--- Approve/Reject Application Withdrawal ---");
		List<BTOProject> myProjects = projectManager.getProjectsByHDBManager(manager.getName());

		if (myProjects.isEmpty()) {
			System.out.println("You have not created any projects.");
			return;
		}

		// List projects managed by the HDB Manager
		System.out.println("Select a project to review withdrawal applications:");
		for (int i = 0; i < myProjects.size(); i++) {
			System.out.println((i + 1) + ". " + myProjects.get(i).getProjectName());
		}

		System.out.print("Enter the project number: ");
		int projectIndex;
		try {
			projectIndex = Integer.parseInt(scanner.nextLine());
			if (projectIndex < 1 || projectIndex > myProjects.size()) {
				System.out.println("Invalid selection.");
				return;
			}
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Operation cancelled.");
			return;
		}

		BTOProject selectedProject = myProjects.get(projectIndex - 1);
		List<Application> withdrawalApplications = dataManager.getApplicationDataManager()
				.getApplicationsByProjectName(selectedProject.getProjectName());

		// Filter only the withdrawal requested applications
		List<Application> withdrawalRequests = new ArrayList<>();
		int index = 1;
		for (Application app : withdrawalApplications) {
			if (app.getStatus() == ApplicationStatus.WITHDRAWAL_REQUESTED) {
				withdrawalRequests.add(app);
				System.out.println(index + ". Applicant: " + app.getApplicantID() + ", Status: " + app.getStatus());
				index++;
			}
		}

		if (withdrawalRequests.isEmpty()) {
			System.out.println("No withdrawal requests found for this project.");
			return;
		}

		System.out.print("Select a withdrawal request to review (number): ");
		int reqIndex;
		try {
			reqIndex = Integer.parseInt(scanner.nextLine());
			if (reqIndex < 1 || reqIndex > withdrawalRequests.size()) {
				System.out.println("Invalid selection.");
				return;
			}
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Operation cancelled.");
			return;
		}

		Application appToReview = withdrawalRequests.get(reqIndex - 1);

		System.out.println("1. Approve Withdrawal (confirm request)");
		System.out.println("2. Reject Withdrawal (revert request)");
		System.out.print("Enter your choice: ");
		String decision = scanner.nextLine();
		boolean updated = false;

		if (decision.equals("1")) {
			// Approve the withdrawal
			
				
				if ("2-Room".equals(appToReview.getFlatType())) {
			        List<BTOProject> projectsList = dataManager.getProjectDataManager().getAllProjects();
                    for (BTOProject p:projectsList) {
                    	if (p.getProjectName().equals(appToReview.getProjectName())) {
                    		p.setNum2RoomUnits(p.getNum2RoomUnits()+1);
                    	}
                    }
                    appToReview.setFlatType(null);
				}
				
				else if ("3-Room".equals(appToReview.getFlatType())) {
			        List<BTOProject> projectsList = dataManager.getProjectDataManager().getAllProjects();
                    for (BTOProject p:projectsList) {
                    	if (p.getProjectName().equals(appToReview.getProjectName())) {
                    		p.setNum3RoomUnits(p.getNum3RoomUnits()+1);
                    		
                    	}
                    }
                    appToReview.setFlatType(null);
				}
				
			
			appToReview.setStatus(ApplicationStatus.WITHDRAWN);
			appToReview.setWithdrawn(true);
			updated = dataManager.getApplicationDataManager().updateApplication(appToReview);
			if (updated) {
				System.out.println("Withdrawal approved.");
			} else {
				System.out.println("Failed to approve withdrawal.");
			}
		} else if (decision.equals("2")) {
			// Reject the withdrawal: revert the status back to PENDING
			appToReview.setStatus(ApplicationStatus.PENDING);
			appToReview.setWithdrawn(false);
			updated = dataManager.getApplicationDataManager().updateApplication(appToReview);
			if (updated) {
				System.out.println("Withdrawal rejected. Application status reverted to PENDING.");
			} else {
				System.out.println("Failed to reject withdrawal.");
			}
		} else {
			System.out.println("Invalid choice.");
		}
		
		
	}
	
	/*
	 * private void handleApplicationWithdrawal() {
	 * System.out.println("\n--- Approve/Reject Application Withdrawal ---");
	 * List<Application> allApps =
	 * dataManager.getApplicationDataManager().getAllApplications();
	 * List<Application> withdrawalApps = new ArrayList<>();
	 * int index = 1;
	 * for (Application app : allApps) {
	 * // Only show applications with status WITHDRAWAL_REQUESTED
	 * if (app.getStatus() == ApplicationStatus.WITHDRAWAL_REQUESTED) {
	 * BTOProject proj = projectManager.getProject(app.getProjectName());
	 * // Added check to make sure the project is under the logged in manager
	 * if (proj != null &&
	 * proj.getHdbManagerInCharge().equalsIgnoreCase(manager.getUserID())) {
	 * System.out.println("Application ID: " + app.getApplicantID() + ", Status: " +
	 * app.getStatus());
	 * System.out.println("Project: " + proj.getProjectName() +
	 * ", Manager in charge: "
	 * + proj.getHdbManagerInCharge());
	 * System.out.println("Adding application to withdrawal Applications: " +
	 * app.getApplicantID());
	 * System.out.println(index + ". Applicant: " + app.getApplicantID() +
	 * ", Project: " + app.getProjectName());
	 * withdrawalApps.add(app);
	 * index++;
	 * }
	 * }
	 * }
	 * if (withdrawalApps.isEmpty()) {
	 * System.out.println("No withdrawal requests found.");
	 * return;
	 * }
	 * System.out.print("Select a withdrawal request to review (number): ");
	 * int reqIndex;
	 * try {
	 * reqIndex = Integer.parseInt(scanner.nextLine());
	 * } catch (NumberFormatException e) {
	 * System.out.println("Invalid input. Operation cancelled.");
	 * return;
	 * }
	 * if (reqIndex < 1 || reqIndex > withdrawalApps.size()) {
	 * System.out.println("Invalid selection.");
	 * return;
	 * }
	 * 
	 * Application appToReview = withdrawalApps.get(reqIndex - 1);
	 * System.out.println("1. Approve Withdrawal (confirm request)");
	 * System.out.println("2. Reject Withdrawal (revert request)");
	 * System.out.print("Enter your choice: ");
	 * String decision = scanner.nextLine();
	 * boolean updated = false;
	 * if (decision.equals("1")) {
	 * // Approve the withdrawal
	 * appToReview.setStatus(ApplicationStatus.WITHDRAWN);
	 * appToReview.setWithdrawn(true);
	 * updated =
	 * dataManager.getApplicationDataManager().updateApplication(appToReview);
	 * if (updated) {
	 * System.out.println("Withdrawal approved.");
	 * } else {
	 * System.out.println("Failed to approve withdrawal.");
	 * }
	 * } else if (decision.equals("2")) {
	 * // Reject the withdrawal: revert the status back to PENDING
	 * appToReview.setStatus(ApplicationStatus.PENDING);
	 * appToReview.setWithdrawn(false);
	 * updated =
	 * dataManager.getApplicationDataManager().updateApplication(appToReview);
	 * if (updated) {
	 * System.out.
	 * println("Withdrawal rejected. Application status reverted to PENDING.");
	 * } else {
	 * System.out.println("Failed to reject withdrawal.");
	 * }
	 * } else {
	 * System.out.println("Invalid choice.");
	 * }
	 * }
	 */

	// 10. Generate Reports (CHECKED)
	private void generateReports() {
		System.out.println("\n--- Generate Reports ---");
		List<Application> allApps = dataManager.getApplicationDataManager().getAllApplications();
		String report = ReportGenerator.generateApplicantListReport(allApps, userDataManager);
		System.out.println(report);
	}

	// 11. View Enquiries of ALL Projects – unchanged. (CHECKED)
	private void viewAllEnquiries() {
		System.out.println("\n--- View All Enquiries ---");
		List<Enquiry> allEnquiries = dataManager.getEnquiryDataManager().getAllEnquiries();
		if (allEnquiries.isEmpty()) {
			System.out.println("No enquiries found.");
			return;
		}
		for (Enquiry enq : allEnquiries) {
			System.out.println("ID: " + enq.getEnquiryID() + ", Applicant: " + enq.getApplicantID() + ", Project: "
					+ enq.getProjectName() + ", Enquiry: " + enq.getEnquiryText() + ", Reply: "
					+ (enq.getReplyText() == null ? "No reply" : enq.getReplyText()));
		}
	}

	// 12. Reply to Enquiries – unchanged. (CHECKED)
	private void replyToEnquiries() {
		System.out.println("\n--- Reply to Enquiries ---");
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
			replyID = Integer.parseInt(scanner.nextLine());
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
		String replyText = scanner.nextLine();
		if (replyText == null || replyText.trim().isEmpty()) {
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

	// 13. Change Password (CHECKED)
	private void changePassword() {
		System.out.print("Enter your current password: ");
		String currentPassword = scanner.nextLine().trim();

		System.out.print("Enter your new password: ");
		String newPassword = scanner.nextLine().trim();

		boolean changed = PasswordUtil.changePassword(manager, currentPassword, newPassword);
		if (changed) {
			System.out.println("Password changed successfully.");
		} else {
			System.out.println("Password change failed.");
		}
	}

	// ADDED: Checking for overlapping projects
	private boolean hasOverlappingProject(String managerID, Date startDate, Date endDate) {
		List<BTOProject> allProjects = dataManager.getProjectDataManager().getAllProjects();
		for (BTOProject project : allProjects) {
			if (project.getHdbManagerInCharge().equals(managerID)) {
				// Check for overlap
				if (startDate.before(project.getApplicationEndDate())
						&& endDate.after(project.getApplicationStartDate())) {
					System.out.println("Overlap found!");
					System.out.println("New Start Date: " + startDate + ", New End Date: " + endDate);
					System.out.println("Existing Project: " + project.getProjectName() + ", Start Date: "
							+ project.getApplicationStartDate() + ", End Date: " + project.getApplicationEndDate());
					return true; // Overlap found
				}
			}
		}
		return false; // No overlap
	}

	// 9. CADDED: Review and Approve/Reject Applications
	private void reviewAndApproveApplications() {
		System.out.println("\n--- Review and Approve/Reject Applications ---");
		List<BTOProject> myProjects = projectManager.getProjectsByHDBManager(manager.getName());

		if (myProjects.isEmpty()) {
			System.out.println("You have not created any projects.");
			return;
		}

		// List projects managed by the HDB Manager
		System.out.println("Select a project to review applications:");
		for (int i = 0; i < myProjects.size(); i++) {
			System.out.println((i + 1) + ". " + myProjects.get(i).getProjectName());
		}

		System.out.print("Enter the project number: ");
		int projectIndex;
		try {
			projectIndex = Integer.parseInt(scanner.nextLine());
			if (projectIndex < 1 || projectIndex > myProjects.size()) {
				System.out.println("Invalid selection.");
				return;
			}
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Operation cancelled.");
			return;
		}

		BTOProject selectedProject = myProjects.get(projectIndex - 1);
		List<Application> pendingApplications = dataManager.getApplicationDataManager()
				.getApplicationsByProjectName(selectedProject.getProjectName());
		if (pendingApplications.isEmpty()) {
			System.out.println("No pending applications for this project.");
			return;
		}

		// List pending applications for the selected project
		System.out.println("Pending Applications:");
		for (int i = 0; i < pendingApplications.size(); i++) {
			Application app = pendingApplications.get(i);
			System.out.println((i + 1) + ". Applicant: " + app.getApplicantID() + ", Status: " + app.getStatus());
		}

		System.out.print("Enter the application number to review: ");
		int appIndex;
		try {
			appIndex = Integer.parseInt(scanner.nextLine());
			if (appIndex < 1 || appIndex > pendingApplications.size()) {
				System.out.println("Invalid selection.");
				return;
			}
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Operation cancelled.");
			return;
		}

		Application selectedApp = pendingApplications.get(appIndex - 1);

		System.out.println("1. Approve Application");
		System.out.println("2. Reject Application");
		System.out.print("Enter your choice: ");
		String choice = scanner.nextLine();

		switch (choice) {
			case "1":
				selectedApp.setStatus(ApplicationStatus.SUCCESSFUL);
				break;
			case "2":
				selectedApp.setStatus(ApplicationStatus.UNSUCCESSFUL);
				break;
			default:
				System.out.println("Invalid choice.");
				return;
		}

		boolean updated = dataManager.getApplicationDataManager().updateApplication(selectedApp);
		if (updated) {
			System.out.println("Application status updated successfully.");
			// To show on CLI
			// System.out.println("Application details to review: " +
			// selectedApp.getApplicantID() + ", Status: " + selectedApp.getStatus());
		} else {
			System.out.println("Failed to update application status.");
			// To show on CLI
			// System.out.println("Application details to review: " +
			// selectedApp.getApplicantID() + ", Status: " + selectedApp.getStatus());
		}
	}

	/**
	 * Immediately write out the full project list to CSV.
	 */
	private void persistProjects() {
		csvWrite writer = new csvWrite(dataManager);
		writer.projectWrite(
				SystemConstants.projectPath,
				dataManager.getProjectDataManager().getAllProjects());
		System.out.println("ProjectList.csv updated on disk.");
	}

}
