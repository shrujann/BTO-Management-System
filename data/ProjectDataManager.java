package data;

import entity.project.BTOProject;
import java.util.List;
import java.util.ArrayList;

// This class will manage the data for BTO projects in memory
public class ProjectDataManager {
	private List<BTOProject> projects;

	public ProjectDataManager() {
		this.projects = new ArrayList<>();
	}

	/**
	 * Creates a new BTO project.
	 * 
	 * @param project The BTOProject object to create.
	 * @return True if the project creation is successful, false otherwise.
	 */
	public boolean createProject(BTOProject project) {
		if (project != null) {
			projects.add(project);
			return true;
		}
		return false;
	}

	/**
	 * Retrieves a project by its name.
	 * 
	 * @param projectName The name of the project to retrieve.
	 * @return The BTOProject object if found, null otherwise.
	 */
	public BTOProject getProject(String projectName) {
		for (BTOProject project : projects) {
			if (project.getProjectName().equals(projectName)) {
				return project;
			}
		}
		return null;
	}

	/**
	 * Updates an existing BTO project.
	 * 
	 * @param project The BTOProject object with updated details.
	 * @return True if the project update is successful, false otherwise.
	 */
	public boolean updateProject(BTOProject project) {
		for (int i = 0; i < projects.size(); i++) {
			if (projects.get(i).getProjectName().equals(project.getProjectName())) {
				projects.set(i, project);
				return true;
			}
		}
		return false;
	}

	/**
	 * Deletes a BTO project.
	 * 
	 * @param projectName The name of the project to delete.
	 * @return True if the project deletion is successful, false otherwise.
	 */
	public boolean deleteProject(String projectName) {
		for (int i = 0; i < projects.size(); i++) {
			if (projects.get(i).getProjectName().equals(projectName)) {
				projects.remove(i);
				return true;
			}
		}
		return false;
	}

	public List<BTOProject> getProjects(){
		return this.projects;
	}

	/**
	 * Retrieves all projects.
	 * 
	 * @return A list of all BTOProject objects.
	 */
	public List<BTOProject> getAllProjects() {
		return new ArrayList<>(projects); // Return a copy to avoid external modification
	}
}
