package entity.communication;

import entity.user.Applicant;
import entity.project.BTOProject;

import java.util.Date;

/*
 * Represents a receipt for a successful flat booking.
 * Stores the receipt details such as the receipt ID, applicant details, project details, and booking details.
 */
public class Receipt {
	private int receiptID;
	private Applicant applicant;
	private BTOProject project;
	private String flatType;
	private Date bookingDate;

	/**
	 * Constructor for the Receipt class.
	 * 
	 * @param receiptID   The unique ID of the receipt.
	 * @param applicant   The applicant who booked the flat.
	 * @param project     The BTO project the flat is in.
	 * @param flatType    The type of flat booked (2-Room or 3-Room).
	 * @param bookingDate The date the flat was booked.
	 */
	public Receipt(int receiptID, Applicant applicant, BTOProject project, String flatType, Date bookingDate) {
		this.receiptID = receiptID;
		this.applicant = applicant;
		this.project = project;
		this.flatType = flatType;
		this.bookingDate = bookingDate;
	}

	// Getters for attributes
	public int getReceiptID() {
		return receiptID;
	}

	public Applicant getApplicant() {
		return applicant;
	}

	public BTOProject getProject() {
		return project;
	}

	public String getFlatType() {
		return flatType;
	}

	public Date getBookingDate() {
		return bookingDate;
	}

	// Add a toString() method for easy printing of receipt details
	@Override
	public String toString() {
		return "Receipt{" + "receiptID: " + receiptID + ", applicant: " + applicant + ", project: " + project
				+ ", flatType: '" + flatType + '\'' + ", bookingDate: " + bookingDate + '}';
	}
}