package inventory;

import javax.swing.*;

public class TutorialGUI {
	private JScrollPane scrollFrame;
	private JPanel tutFrame;
	private JLabel excelImg;
	private final static String IMAGE_FILE = "resources/excelScreenshot.png";
	
	public TutorialGUI() {
		tutFrame = new JPanel();
		tutFrame.setLayout(new BoxLayout(tutFrame, BoxLayout.Y_AXIS));
		tutFrame.setVisible(true);
		excelImg = new JLabel(new ImageIcon(IMAGE_FILE));
		contents();
		scrollFrame = new JScrollPane (
				tutFrame, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	}
	
	
	public JScrollPane getTutScroller() {
		return this.scrollFrame;
	}
	
	private void contents() {
		String st0 = " * Import Tutorial *";
		String st1 = " ";
		String st2 = " This brief tutorial will demonstrate how to upload the inventory data";
		String st3 = " from your small business to this app in five steps.";
		String st4 = " ";
		String st5 = " Step 1: Open Microsoft Excel Spreadsheet.";
		String st6 = " ";
		String st7 = " Step 2: Insert Data.";
		String st8 = " The first three columns has headers 'Item', 'Date', and 'Quantity' appropriately.";
		String st9 = " The first column are the names of your products.";
		String st10 = " The second column are the dates of your sales. MM/DD/YYYY";
		String st11 = " The third column is the amount sold on the entirety of that day.";
		String st12 = " Do not use commas ( , ) Follow the diagram below:";
		String st13 = " ";
		String st14 = " ";
		String st15 = " Step 3: Save the Spreadsheet";
		String st16 = " Save under the name 'import.csv' without the quotation marks.";
		String st17 = " Save the file as type CSV (Comma delimited). Ignore the warning.";
		String st18 = " ";
		String st19 = " Step 4: Place your 'import.csv' file into the folder of this project titled 'resources'.";
		String st20 = " If prompted with a warning, select 'Replace the file in the destination'.";
		String st21 = " Warning: The current CSV file contains data currently displayed on the app.";
		String st22 = " Replacing this file will remove the current sales data from this app.";
		String st23 = " ";
		String st24 = " Step 5: Refresh this app.";
		String st25 = " Either close and re-open this app, or click the 'Refresh' button.";
		String st26 = " ";
		

		tutFrame.add(new JLabel(st0));
		tutFrame.add(new JLabel(st1));
		tutFrame.add(new JLabel(st2));
		tutFrame.add(new JLabel(st3));
		tutFrame.add(new JLabel(st4));
		tutFrame.add(new JLabel(st5));
		tutFrame.add(new JLabel(st6));
		tutFrame.add(new JLabel(st7));
		tutFrame.add(new JLabel(st8));
		tutFrame.add(new JLabel(st9));
		tutFrame.add(new JLabel(st10));
		tutFrame.add(new JLabel(st11));
		tutFrame.add(new JLabel(st12));
		tutFrame.add(new JLabel(st13));
		tutFrame.add(excelImg);
		tutFrame.add(new JLabel(st14));
		tutFrame.add(new JLabel(st15));
		tutFrame.add(new JLabel(st16));
		tutFrame.add(new JLabel(st17));
		tutFrame.add(new JLabel(st18));
		tutFrame.add(new JLabel(st19));
		tutFrame.add(new JLabel(st20));
		tutFrame.add(new JLabel(st21));
		tutFrame.add(new JLabel(st22));
		tutFrame.add(new JLabel(st23));
		tutFrame.add(new JLabel(st24));
		tutFrame.add(new JLabel(st25));
		tutFrame.add(new JLabel(st26));
	}
}
