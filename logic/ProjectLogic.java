package logic;

import entity.project.BTOProject;
import entity.project.FlatType;
import java.util.Date;
import java.util.List;
import util.DateUtil;

public class ProjectLogic {
	public static boolean isValidProject(BTOProject project) {
		if (project == null) {
			return false;
		}

		if (project.getProjectName() == null || project.getProjectName().isEmpty()) {
			System.out.println("Project name cannot be empty.");
			return false;
		}

		if (project.getNeighbourhood() == null || project.getNeighbourhood().isEmpty()) {
			System.out.println("Neighbourhood cannot be empty.");
			return false;
		}

		if (project.getFlatTypes() == null || project.getFlatTypes().isEmpty()) {
			System.out.println("At least one flat type must be specified.");
			return false;
		}

		if (!isValidDates(project.getApplicationStartDate(), project.getApplicationEndDate())) {
			System.out.println("Invalid application dates.");
			return false;
		}
		// Additional validations can be added here
		return true;
	}

	public static boolean isValidDates(Date openingDate, Date closingDate) {
		if (openingDate == null || closingDate == null) {
			System.out.println("Opening and closing dates cannot be NULL.");
			return false;
		}

		if (openingDate.after(closingDate)) {
			System.out.println("Opening date cannot be after closing date.");
			return false;
		}
		return true;
	}

	public static boolean hasAvailableFlats(BTOProject project, String flatType) {
		for (FlatType ft : project.getFlatTypes()) {
			if (ft.getTypeName().equalsIgnoreCase(flatType) && ft.getNumberOfUnits() > 0) {
				return true;
			}
		}
		return false;
	}
}