package util;

import entity.project.BTOProject;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class FilterUtil {

	// Filter by location (neighbourhood)
	public static List<BTOProject> filterByLocation(List<BTOProject> allProjects, String neighbourhood) {
		List<BTOProject> filtered = new ArrayList<>();
		for (BTOProject project : allProjects) {
			// Trim and ignore case for better matching
			if (project.getNeighbourhood() != null && 
					project.getNeighbourhood().trim().equalsIgnoreCase(neighbourhood.trim())) {
					filtered.add(project);
			}
		}
		return filtered;
	}

  // Filter by flat type
  public static List<BTOProject> filterByFlatType(List<BTOProject> projects, String flatType) {
		List<BTOProject> filteredProjects = new ArrayList<>();
		if (flatType == null || flatType.trim().isEmpty()) {
			return filteredProjects; 
		}
		for (BTOProject project : projects) {
			// Ensure the flat types are not null or empty
			if (project.getFlatTypes() != null && !project.getFlatTypes().isEmpty()) {
				// For each project, check if any flat type matches the given flatType (case insensitive)
				boolean matches = project.getFlatTypes().stream()
					.anyMatch(type -> type.getTypeName() != null && 
					type.getTypeName().trim().equalsIgnoreCase(flatType.trim()));
				if (matches) {
					filteredProjects.add(project);
				}
			}
		}
		return filteredProjects;
	}

  // Sort projects by alphabetical order
  public static List<BTOProject> sortByAlphabeticalOrder(List<BTOProject> projects) {
		projects.sort(Comparator.comparing(project -> project.getProjectName() != null ? project.getProjectName() : ""));
		return projects;
	}
}