package inventory;

public class SaleRecord {
	private String date;
	private double amtSold;
	private String type;
	
	SaleRecord(String d, double s, String t) {
		this.date = d;
		this.amtSold = s;
		this.type = t;
	}
	
	public String getDate() {
		return this.date;
	}
	
	public double getAmtSold() {
		return this.amtSold;
	}
	
	public String getType() {
		return this.type;
	}
}
