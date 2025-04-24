package boundary.view;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import data.DataManager;
import control.auth.AuthenticationService;
import csv.csvWrite;
import csv.projectReader;
import csv.userReader;
import entity.project.BTOProject;
import entity.user.Applicant;
import entity.user.HDBManager;
import entity.user.HDBOfficer;
import entity.user.User;
import util.ValidationUtil;
import util.PasswordUtil;
import constants.SystemConstants;

public class MainMenu {
    private Scanner scanner;
    private DataManager dataManager;
    private AuthenticationService authService;

    public MainMenu() {
        scanner = new Scanner(System.in);
        dataManager = new DataManager(); // Instantiates UserDataManager, ProjectDataManager, etc.

        userReader reader = new userReader();
        projectReader projectReader = new projectReader();

        List<User> usersList = dataManager.getUserDataManager().getUsers();
        List<BTOProject> projectsList = dataManager.getProjectDataManager().getProjects();

        // Load users in order: officers, managers, applicants
        reader.readUsers(SystemConstants.officerPath,   "officer",   usersList);
        reader.readUsers(SystemConstants.managerPath,   "manager",   usersList);
        reader.readUsers(SystemConstants.applicantPath, "applicant", usersList);

        // Load projects from CSV file
        projectReader.readProject(SystemConstants.projectPath, projectsList, usersList);

        // Initialize AuthenticationService with UserDataManager
        authService = new AuthenticationService(dataManager.getUserDataManager());
    }

    public void display() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n=== Main Menu ===");
            System.out.println("1. Login");
            System.out.println("2. Register as New User");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1": login();  break;
                case "2": register(); break;
                case "3": exit = true; System.out.println("Exiting. Goodbye!"); break;
                default:  System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void login() {
        System.out.println("\n--- Login ---");
        System.out.println("Login as:");
        System.out.println("1. Applicant");
        System.out.println("2. HDB Officer");
        System.out.println("3. HDB Manager");
        System.out.print("Enter your choice: ");
        String typeChoice = scanner.nextLine().trim();

        System.out.print("Enter your NRIC: ");
        String nric = scanner.nextLine().trim();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        User user = authService.authenticate(nric, password);
        if (user == null) {
            System.out.println("Authentication failed. Returning to main menu.");
            return;
        }

        switch (typeChoice) {
            case "1":
                if (user instanceof Applicant) {
                    new ApplicantMenu((Applicant) user, scanner, dataManager).display();
                } else {
                    System.out.println("User type mismatch. Returning to main menu.");
                }
                break;
            case "2":
                if (user instanceof HDBOfficer) {
                    new OfficerMenu((HDBOfficer) user, scanner, dataManager).display();
                } else {
                    System.out.println("User type mismatch. Returning to main menu.");
                }
                break;
            case "3":
                if (user instanceof HDBManager) {
                    new ManagerMenu((HDBManager) user, scanner, dataManager).display();
                } else {
                    System.out.println("User type mismatch. Returning to main menu.");
                }
                break;
            default:
                System.out.println("Invalid login type selected. Returning to main menu.");
        }
    }

    private void register() {
        System.out.println("\n--- Register New User ---");

        System.out.print("Enter Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter NRIC: ");
        String nric = scanner.nextLine().trim();
        if (!ValidationUtil.isValidNRIC(nric)) {
            System.out.println("Invalid NRIC format. Registration cancelled.");
            return;
        }
        if (dataManager.getUserDataManager().getUserByNRIC(nric) != null) {
            System.out.println("User with NRIC " + nric + " already exists. Registration cancelled.");
            return;
        }

        System.out.print("Enter password (at least 8 characters): ");
        String password = scanner.nextLine();
        if (!PasswordUtil.isValidPassword(password)) {
            System.out.println("Password is not valid. Registration cancelled.");
            return;
        }
        password = PasswordUtil.hashPassword(password);

        System.out.print("Enter age: ");
        int age;
        try {
            age = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid age input. Registration cancelled.");
            return;
        }

        System.out.print("Enter marital status (single/married): ");
        String maritalStatus = scanner.nextLine().trim();
        if (!ValidationUtil.isValidAge(age, maritalStatus.equalsIgnoreCase("single"))) {
            System.out.println("Age does not meet requirements. Registration cancelled.");
            return;
        }

        System.out.println("Register as:");
        System.out.println("1. Applicant");
        System.out.println("2. HDB Officer");
        System.out.println("3. HDB Manager");
        System.out.print("Enter your choice: ");
        String typeChoice = scanner.nextLine().trim();

        User newUser = switch (typeChoice) {
            case "1" -> new Applicant(name, nric, age, maritalStatus, password);
            case "2" -> new HDBOfficer(name, nric, age, maritalStatus, password);
            case "3" -> new HDBManager(name, nric, age, maritalStatus, password);
            default  -> null;
        };
        if (newUser == null) {
            System.out.println("Invalid choice. Registration cancelled.");
            return;
        }

        boolean created = dataManager.getUserDataManager().createUser(newUser);
        System.out.println(created
            ? "Registration successful! Please login to continue."
            : "Registration failed. Please try again.");

        // Immediately save user CSVs with proper filtering
        List<User> allUsers   = dataManager.getUserDataManager().getUsers();
        List<User> applicants = allUsers.stream()
            .filter(u -> u.getClass().getSimpleName().equals("Applicant"))
            .collect(Collectors.toList());
        List<User> officers   = allUsers.stream()
            .filter(u -> u instanceof HDBOfficer)
            .collect(Collectors.toList());
        List<User> managers   = allUsers.stream()
            .filter(u -> u instanceof HDBManager)
            .collect(Collectors.toList());

        csvWrite writer = new csvWrite(dataManager);
        writer.csvWrite(SystemConstants.applicantPath, "applicant", applicants);
        writer.csvWrite(SystemConstants.officerPath,  "officer",   officers);
        writer.csvWrite(SystemConstants.managerPath,  "manager",   managers);
    }

    public void exitAndSave() {
        List<User> allUsers   = dataManager.getUserDataManager().getUsers();
        List<User> applicants = allUsers.stream()
            .filter(u -> u.getClass().getSimpleName().equals("Applicant"))
            .collect(Collectors.toList());
        List<User> officers   = allUsers.stream()
            .filter(u -> u instanceof HDBOfficer)
            .collect(Collectors.toList());
        List<User> managers   = allUsers.stream()
            .filter(u -> u instanceof HDBManager)
            .collect(Collectors.toList());

        List<BTOProject> projectsList = dataManager.getProjectDataManager().getAllProjects();

        csvWrite writer = new csvWrite(dataManager);
        writer.csvWrite(SystemConstants.applicantPath, "applicant", applicants);
        writer.csvWrite(SystemConstants.officerPath,  "officer",   officers);
        writer.csvWrite(SystemConstants.managerPath,  "manager",   managers);
        writer.projectWrite(SystemConstants.projectPath, projectsList);

    }

    public static void main(String[] args) {
        MainMenu menu = new MainMenu();
        menu.display();
        menu.exitAndSave();
    }
}
