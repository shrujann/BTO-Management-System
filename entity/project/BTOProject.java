package entity.project;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import entity.user.User;

/*
 * Represents a BTO project in the system.
 * Stores project details such as name, location, flat types, and application dates.
 */
public class BTOProject {
	private String projectName;
	private String neighbourhood;
	private boolean isVisible;
	private int num2RoomUnits;
	private int num3RoomUnits;
	private Date applicationStartDate;
	private Date applicationEndDate;
	private String hdbManagerInCharge;
	private int availableHdbOfficerSlots;
	private List<FlatType> flatTypes = new ArrayList<>();
	private List<String> pendingOfficerRegistrations = new ArrayList<>();
	private List<String> approvedOfficerRegistrations = new ArrayList<>();
	private List<String> rejectedOfficerRegistrations = new ArrayList<>();
	private List<User> officers = new ArrayList<>(); // List to store officers
	private List<String> bookedApplicants;

	public BTOProject(String projectName, String neighbourhood, boolean isVisible, int num2RoomUnits, int num3RoomUnits,
			Date applicationStartDate, Date applicationEndDate, String hdbManagerInCharge, int availableHdbOfficerSlots,
			List<User> officers) {
		this.projectName = projectName;
		this.neighbourhood = neighbourhood;
		this.isVisible = isVisible;
		this.num2RoomUnits = num2RoomUnits;
		this.num3RoomUnits = num3RoomUnits;
		this.applicationStartDate = applicationStartDate;
		this.applicationEndDate = applicationEndDate;
		this.hdbManagerInCharge = hdbManagerInCharge;
		this.availableHdbOfficerSlots = availableHdbOfficerSlots;
		this.officers = officers != null ? officers : new ArrayList<>();
		this.bookedApplicants = new ArrayList<>();
	}

	// Getters and setters for attributes
	public String getProjectName() {return projectName;}
	public void setProjectName(String projectName) {this.projectName = projectName;}

	public String getNeighbourhood() {return neighbourhood;}
	public void setNeighbourhood(String neighbourhood) {this.neighbourhood = neighbourhood;}

	public boolean isVisible() {return isVisible;}
	public void setVisible(boolean visible) {isVisible = visible;}

	public int getNum2RoomUnits() {return num2RoomUnits;}
	public void setNum2RoomUnits(int num2RoomUnits) {this.num2RoomUnits = num2RoomUnits;}

	public int getNum3RoomUnits() {return num3RoomUnits;}
	public void setNum3RoomUnits(int num3RoomUnits) {this.num3RoomUnits = num3RoomUnits;}

	public Date getApplicationStartDate() {return applicationStartDate;}
	public void setApplicationStartDate(Date applicationStartDate) {this.applicationStartDate = applicationStartDate;}

	public Date getApplicationEndDate() {return applicationEndDate;}
	public void setApplicationEndDate(Date applicationEndDate) {this.applicationEndDate = applicationEndDate;}

	public String getHdbManagerInCharge() {return hdbManagerInCharge;}
	public void setHdbManagerInCharge(String hdbManagerInCharge) {this.hdbManagerInCharge = hdbManagerInCharge;}

	public int getAvailableHdbOfficerSlots() {return availableHdbOfficerSlots;}
	public void setAvailableHdbOfficerSlots(int availableHdbOfficerSlots) {this.availableHdbOfficerSlots = availableHdbOfficerSlots;}

	public List<FlatType> getFlatTypes() {return flatTypes;}

	public List<String> getTypesOfFlat() {
		List<String> types = new ArrayList<>();
		for (FlatType flatType : flatTypes) {
			types.add(flatType.getTypeName());
		}
		return types;
	}

	public void setFlatTypes(List<FlatType> flatTypes) {
		this.flatTypes = (flatTypes != null) ? flatTypes : new ArrayList<>();
	}

	public List<User> getOfficers() {return officers;}
	public void setOfficers(List<User> officers) {this.officers = (officers != null) ? officers : new ArrayList<>();}

	public List<String> getPendingOfficerRegistrations() {return pendingOfficerRegistrations;}
	public void setPendingOfficerRegistrations(List<String> pendingOfficerRegistrations) {
		this.pendingOfficerRegistrations = pendingOfficerRegistrations;
	}

	public List<String> getApprovedOfficerRegistrations() {return approvedOfficerRegistrations;}
	public void setApprovedOfficerRegistrations(List<String> approvedOfficerRegistrations) {
		this.approvedOfficerRegistrations = approvedOfficerRegistrations;
	}

	public List<String> getRejectedOfficerRegistrations() {return rejectedOfficerRegistrations;}
	public void setRejectedOfficerRegistrations(List<String> rejectedOfficerRegistrations) {
		this.rejectedOfficerRegistrations = rejectedOfficerRegistrations;
	}

	public List<String> getBookedApplicants() {return bookedApplicants;}

	public boolean has2RoomFlats() {
		for (FlatType flat : flatTypes) {
			if (flat.getTypeName().equalsIgnoreCase("2-Room") && flat.getNumberOfUnits() > 0) {
				return true;
			}
		}
		return false;
	}

	public List<String> getRegisteredOfficers() {
		List<String> combined = new ArrayList<>();
		if (pendingOfficerRegistrations != null)
			combined.addAll(pendingOfficerRegistrations);
		if (approvedOfficerRegistrations != null)
			combined.addAll(approvedOfficerRegistrations);
		return combined;
	}

	@Override
	public String toString() {
		return "BTOProject{" + "projectName='" + projectName + '\'' + ", neighbourhood='" + neighbourhood + '\''
				+ ", hdbManagerInCharge=" + getHdbManagerInCharge() + ", officers="
				+ officers.stream().map(User::getName).collect(Collectors.joining(",")) + ", flatTypes=" + flatTypes
				+ '}';
	}
}
