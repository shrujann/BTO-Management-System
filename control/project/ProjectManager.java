package control.project;

import entity.project.BTOProject;
import data.ProjectDataManager;
import java.util.List;
import java.util.ArrayList;
import util.DateUtil;

// This class will handle the creation, editing, deletion, and visibility of BTO projects.
public class ProjectManager {
	private ProjectDataManager projectDataManager;

	public ProjectManager(ProjectDataManager projectDataManager) {
		this.projectDataManager = projectDataManager;
	}

	/**
	 * Creates a new BTO project.
	 * Managers are allowed to create multiple projects even if their application
	 * dates overlap. This is an intentional design decision for flexibility in planning and scheduling.
	 * @param project The BTOProject object to create.
	 * @return True if the project creation is successful, false otherwise.
	 */
	//public boolean createProject(BTOProject project) {
	//	return projectDataManager.createProject(project);
	
	
	public boolean createProject(BTOProject newProject) {
		for (BTOProject existing : getAllProjects()) {
			if (existing.getHdbManagerInCharge().equalsIgnoreCase(newProject.getHdbManagerInCharge())) {
				boolean overlap = DateUtil.isWithinRange(existing.getApplicationStartDate(), newProject.getApplicationStartDate(), newProject.getApplicationEndDate()) ||
				DateUtil.isWithinRange(existing.getApplicationEndDate(), newProject.getApplicationStartDate(), newProject.getApplicationEndDate()) ||
				DateUtil.isWithinRange(newProject.getApplicationStartDate(), existing.getApplicationStartDate(), existing.getApplicationEndDate());
	
				if (overlap) {
					System.out.println("You already have a project during this application period. Cannot create another.");
					return false;
				}
			}
		}
		return projectDataManager.createProject(newProject);
	}

	/**
	 * Retrieves a project by its name.
	 * 
	 * @param projectName The name of the project to retrieve.
	 * @return The BTOProject object if found, null otherwise.
	 */
	public BTOProject getProject(String projectName) {
		return projectDataManager.getProject(projectName);
	}

	/**
	 * Edits an existing BTO project.
	 * 
	 * @param project The BTOProject object with updated details.
	 * @return True if the project update is successful, false otherwise.
	 */
	public boolean editProject(BTOProject project) {
		return projectDataManager.updateProject(project);
	}

	/**
	 * Deletes a BTO project.
	 * 
	 * @param projectName The name of the project to delete.
	 * @return True if the project deletion is successful, false otherwise.
	 */
	public boolean deleteProject(String projectName) {
		return projectDataManager.deleteProject(projectName);
	}

	/**
	 * Toggles the visibility of a BTO project.
	 * 
	 * @param projectName The name of the project to toggle visibility.
	 * @param visible     The new visibility status (true for visible, false for not
	 *                    visible).
	 * @return True if the visibility toggle is successful, false otherwise.
	 */
	public boolean toggleProjectVisibility(String projectName, boolean visible) {
		BTOProject project = projectDataManager.getProject(projectName);
		if (project != null) {
			project.setVisible(visible);
			return projectDataManager.updateProject(project);
		}
		return false;
	}

	/**
	 * Retrieves all projects, regardless of visibility.
	 * 
	 * @return A list of all BTOProject objects.
	 */
	public List<BTOProject> getAllProjects() {
		return projectDataManager.getAllProjects();
	}

	/**
	 * Filters projects based on the provided criteria (location, flat types, etc.).
	 * 
	 * @param filterCriteria The criteria to filter projects.
	 * @return A list of BTOProject objects that match the filter criteria.
	 */
	public List<BTOProject> filterProjects(String filterCriteria) {
		// Implement filtering logic here based on filterCriteria
		// This is a placeholder; actual implementation depends on how you store and
		// manage project data
		List<BTOProject> allProjects = projectDataManager.getAllProjects();
		List<BTOProject> filteredProjects = new ArrayList<>();
		for (BTOProject project : allProjects) {
			// Example: Filter by location
			if (project.getNeighbourhood().equalsIgnoreCase(filterCriteria)) {
				filteredProjects.add(project);
			}
		}
		return filteredProjects;
	}

	/**
	 * Retrieves a list of projects created by a specific HDB Manager.
	 * 
	 * @param hdbManagerId The ID of the HDB Manager.
	 * @return A list of BTOProject objects created by the specified HDB Manager.
	 */
	public List<BTOProject> getProjectsByHDBManager(String hdbManagerId) {
		List<BTOProject> allProjects = projectDataManager.getAllProjects();
		List<BTOProject> managerProjects = new ArrayList<>();
		for (BTOProject project : allProjects) {
			if (project.getHdbManagerInCharge().equals(hdbManagerId)) {
				managerProjects.add(project);
			}
		}
		return managerProjects;
	}
}