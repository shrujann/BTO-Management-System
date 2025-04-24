package entity.project;

public class FlatType {
	private String typeName;
	private int numberOfUnits;
	private double sellingPrice;

	public FlatType(String typeName, int numberOfUnits, double sellingPrice) {
		this.typeName = typeName;
		this.numberOfUnits = numberOfUnits;
		this.sellingPrice = sellingPrice;
	}

	// Getters and setters
	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public int getNumberOfUnits() {
		return numberOfUnits;
	}

	public void setNumberOfUnits(int numberOfUnits) {
		this.numberOfUnits = numberOfUnits;
	}

	public double getSellingPrice() {
		return sellingPrice;
	}

	public void setSellingPrice(double sellingPrice) {
		this.sellingPrice = sellingPrice;
	}

	public void decreaseUnits() {
		if (this.numberOfUnits > 0) {
			this.numberOfUnits--;
		}
	}

	@Override
	public String toString() {
		return "FlatType{" + "typeName='" + typeName + '\'' + ", numberOfUnits=" + numberOfUnits + ", sellingPrice="
				+ sellingPrice + '}';
	}
}