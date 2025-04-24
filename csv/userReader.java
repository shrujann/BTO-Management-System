package csv;

import entity.user.Applicant;
import entity.user.HDBManager;
import entity.user.HDBOfficer;
import entity.user.User;
import util.PasswordUtil;

import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class userReader {

    public void readUsers(String filePath, String userType, List<User> users) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");

                if (tokens.length < 5) continue;

                String name = tokens[0].trim();
                String nric = tokens[1].trim();
                int age = Integer.parseInt(tokens[2].trim());
                String maritalStatus = tokens[3].trim();
                String password = tokens[4].trim();

                String finalPassword = PasswordUtil.isLikelyHashed(password) ? password : PasswordUtil.hashPassword(password);

                User user = null;

                switch (userType.toLowerCase()) {
                    case "applicant":
                        user = new Applicant(name, nric, age, maritalStatus, finalPassword);
                        break;
                    case "officer":
                        user = new HDBOfficer(name, nric, age, maritalStatus, finalPassword);
                        break;
                    case "manager":
                        user = new HDBManager(name, nric, age, maritalStatus, finalPassword);
                        break;
                    default:
                        System.out.println("Unknown user type: " + userType);
                        continue;
                }

                if (user != null) {
                    users.add(user); // Add to the provided list
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading user file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid age format in file: " + filePath);
        }
    }
}