package control.report;

import entity.project.Application;
import data.UserDataManager;
import java.util.List;

public class ReportGenerator {
	public static String generateApplicantListReport(List<Application> applications, UserDataManager userDataManager) {
		StringBuilder report = new StringBuilder();
		report.append("Applicant List Report\n");
		report.append("----------------------\n");

		for (Application application : applications) {
			String applicantID = application.getApplicantID();
			report.append("Applicant NRIC: ").append(applicantID).append("\n");
			report.append("Flat Type Booked: ").append(application.getFlatType()).append("\n");
			report.append("Application Status: ").append(application.getStatus()).append("\n");
			report.append("----------------------\n");
		}
		return report.toString();
	}
}
