package data;

// This class will serve as a central point for managing various data managers in the BTO Management System, using in-memory storage
public class DataManager {
	private UserDataManager userDataManager;
	private ProjectDataManager projectDataManager;
	private ApplicationDataManager applicationDataManager;
	private EnquiryDataManager enquiryDataManager;
	

	public DataManager() {
		this.userDataManager = new UserDataManager();
		this.projectDataManager = new ProjectDataManager();
		this.applicationDataManager = new ApplicationDataManager();
		this.enquiryDataManager = new EnquiryDataManager();
	}

	// Getters
	public UserDataManager getUserDataManager() {
		return userDataManager;
	}

	public ProjectDataManager getProjectDataManager() {
		return projectDataManager;
	}

	public ApplicationDataManager getApplicationDataManager() {
		return applicationDataManager;
	}

	public EnquiryDataManager getEnquiryDataManager() {
		return enquiryDataManager;
	}
}