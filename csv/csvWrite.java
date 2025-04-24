package csv;

import data.DataManager;
import entity.project.BTOProject;
import entity.project.FlatType;
import entity.user.HDBManager;
import entity.user.HDBOfficer;
import entity.user.User;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

public class csvWrite {
    private final DataManager dataManager;

    /**
     * Accepts your DataManager so that if you ever need to pull
     * more data (e.g. look up users by NRIC) you have it.
     */
    public csvWrite(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    /**
     * Write a filtered user list to CSV.
     * @param filePath path to write to
     * @param userType "applicant", "officer", or "manager"
     * @param users    master list of users
     */
    public void csvWrite(String filePath, String userType, List<User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // header
            writer.write("Name,NRIC,Age,Marital Status,Password");
            writer.newLine();

            for (User user : users) {
                boolean shouldWrite;
                switch (userType.toLowerCase()) {
                    case "applicant":
                        shouldWrite = user.getClass().getSimpleName().equals("Applicant");
                        break;
                    case "officer":
                        shouldWrite = user instanceof HDBOfficer;
                        break;
                    case "manager":
                        shouldWrite = user instanceof HDBManager;
                        break;
                    default:
                        shouldWrite = false;
                }
                if (!shouldWrite) continue;

                String line = String.format("%s,%s,%d,%s,%s",
                        user.getName(),
                        user.getUserID(),
                        user.getAge(),
                        user.getMaritalStatus(),
                        user.getPassword());
                writer.write(line);
                writer.newLine();
            }

            System.out.println("Successfully wrote " + userType + "s to: " + filePath);

        } catch (IOException e) {
            System.out.println("Error writing " + userType + "s to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Write the full project list, including approved, pending, and rejected officers.
     */
    public void projectWrite(String filePath, List<BTOProject> projects) {
        SimpleDateFormat sdf = new SimpleDateFormat("M/d/yy");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // header
            writer.write(
                "Project Name,Neighborhood," +
                "Type 1,Number of units for Type 1,Selling price for Type 1," +
                "Type 2,Number of units for Type 2,Selling price for Type 2," +
                "Application opening date,Application closing date," +
                "Manager,Officer Slot,ApprovedOfficers,PendingOfficers,RejectedOfficers"
            );
            writer.newLine();

            for (BTOProject project : projects) {
                // prices from flat types
                List<FlatType> flats = project.getFlatTypes();
                double price2 = flats.get(0).getSellingPrice();
                double price3 = flats.get(1).getSellingPrice();

                // helper to format NRIC→"Name (NRIC)" or just NRIC if not found
                java.util.function.Function<String,String> fmt = nric -> {
                    User u = dataManager.getUserDataManager().getUserByNRIC(nric);
                    return (u != null)
                        ? u.getName() + " (" + nric + ")"
                        : nric;
                };

                String approved = project.getApprovedOfficerRegistrations().stream()
                    .map(fmt)
                    .collect(Collectors.joining(","));

                String pending = project.getPendingOfficerRegistrations().stream()
                    .map(fmt)
                    .collect(Collectors.joining(","));

                String rejected = project.getRejectedOfficerRegistrations().stream()
                    .map(fmt)
                    .collect(Collectors.joining(","));

                String row = String.format(
                    "%s,%s,%s,%d,%.2f,%s,%d,%.2f,%s,%s,%s,%d,\"%s\",\"%s\",\"%s\"",
                    project.getProjectName(),
                    project.getNeighbourhood(),
                    "2-Room", project.getNum2RoomUnits(), price2,
                    "3-Room", project.getNum3RoomUnits(), price3,
                    sdf.format(project.getApplicationStartDate()),
                    sdf.format(project.getApplicationEndDate()),
                    project.getHdbManagerInCharge(),
                    project.getAvailableHdbOfficerSlots(),
                    approved,
                    pending,
                    rejected
                );

                writer.write(row);
                writer.newLine();
            }

            System.out.println("Projects written to: " + filePath);

        } catch (IOException e) {
            System.out.println("Error writing projects to file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
