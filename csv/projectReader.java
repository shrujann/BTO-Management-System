package csv;

import entity.project.BTOProject;
import entity.project.FlatType;
import entity.user.HDBManager;
import entity.user.HDBOfficer;
import entity.user.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class projectReader {

    public void readProject(String filePath, List<BTOProject> projects, List<User> users) {
        SimpleDateFormat sdf = new SimpleDateFormat("M/d/yy");

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine(); // skip header

            while ((line = reader.readLine()) != null) {
                String[] tokens = splitCsvLine(line);

                if (tokens.length < 13) continue;

                String projectName = tokens[0].trim();
                String neighbourhood = tokens[1].trim();

                int num2RoomUnits = Integer.parseInt(tokens[3].trim());
                double price2Room = Double.parseDouble(tokens[4].trim());

                int num3RoomUnits = Integer.parseInt(tokens[6].trim());
                double price3Room = Double.parseDouble(tokens[7].trim());

                Date startDate = sdf.parse(tokens[8].trim());
                Date endDate = sdf.parse(tokens[9].trim());

                String managerName = tokens[10].trim();
                int officerSlots = Integer.parseInt(tokens[11].trim());

                String officerNamesRaw = tokens[12].replaceAll("\"", "").trim();
                List<User> officerList = new ArrayList<>();
                List<String> approvedOfficerNRICs = new ArrayList<>();

                if (!officerNamesRaw.isEmpty()) {
                    String[] entries = officerNamesRaw.split(",");
                    for (String entry : entries) {
                        String trimmed = entry.trim();
                        if (trimmed.matches(".*\\(.*\\)")) {
                            String name = trimmed.substring(0, trimmed.indexOf('(')).trim();
                            String nric = trimmed.substring(trimmed.indexOf('(') + 1, trimmed.indexOf(')')).trim();
                            approvedOfficerNRICs.add(nric);
                            for (User u : users) {
                                if (u.getUserID().equals(nric)) {
                                    officerList.add(u);
                                    break;
                                }
                            }
                        } else {
                            for (User u : users) {
                                if (u.getName().equalsIgnoreCase(trimmed)) {
                                    officerList.add(u);
                                    if (u instanceof HDBOfficer) {
                                        approvedOfficerNRICs.add(u.getUserID());
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }

                String pendingOfficerRaw = tokens.length > 13 ? tokens[13].replaceAll("\"", "").trim() : "";
                List<String> pendingOfficerNRICs = new ArrayList<>();
                if (!pendingOfficerRaw.isEmpty()) {
                    String[] nricTokens = pendingOfficerRaw.split(",");
                    for (String nric : nricTokens) {
                        pendingOfficerNRICs.add(nric.trim());
                    }
                }

                String rejectedOfficerRaw = tokens.length > 14 ? tokens[14].replaceAll("\"", "").trim() : "";
                List<String> rejectedOfficerNRICs = new ArrayList<>();
                if (!rejectedOfficerRaw.isEmpty()) {
                    String[] nricTokens = rejectedOfficerRaw.split(",");
                    for (String nric : nricTokens) {
                        rejectedOfficerNRICs.add(nric.trim());
                    }
                }

                List<FlatType> flatList = new ArrayList<>();
                flatList.add(new FlatType("2-Room", num2RoomUnits, price2Room));
                flatList.add(new FlatType("3-Room", num3RoomUnits, price3Room));

                BTOProject project = new BTOProject(
                        projectName,
                        neighbourhood,
                        true,
                        num2RoomUnits,
                        num3RoomUnits,
                        startDate,
                        endDate,
                        managerName,
                        officerSlots,
                        officerList
                );

                project.setFlatTypes(flatList);
                project.setPendingOfficerRegistrations(pendingOfficerNRICs);
                project.setRejectedOfficerRegistrations(rejectedOfficerNRICs);
                // Filter officerList to only include users in approvedOfficerNRICs
                List<User> finalApprovedOfficers = users.stream()
                .filter(u -> approvedOfficerNRICs.contains(u.getUserID()))
                .collect(Collectors.toList());

                project.setApprovedOfficerRegistrations(approvedOfficerNRICs);
                project.setOfficers(finalApprovedOfficers);


                for (User u : officerList) {
                    if (u instanceof HDBOfficer) {
                        ((HDBOfficer) u).setProjectAssigned(project);
                    }
                }

                projects.add(project);
            }

        } catch (Exception e) {
            System.out.println("Error reading project file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String[] splitCsvLine(String line) {
        List<String> tokens = new ArrayList<>();
        boolean insideQuote = false;
        StringBuilder sb = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '"') {
                insideQuote = !insideQuote;
            } else if (c == ',' && !insideQuote) {
                tokens.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString().trim());
        return tokens.toArray(new String[0]);
    }
}
