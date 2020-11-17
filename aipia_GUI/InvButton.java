package inventory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import java.util.ArrayList;

import java.awt.*;
import javax.swing.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.IOException; 
import java.io.PrintWriter;

public class InvButton {
	private String label;
	private ArrayList<SaleRecord> sales;
	private LineGraph graph;
	private JInternalFrame graphSpace;
	private JInternalFrame aiButtonSpace;
	private JButton aiButton;
	private JPanel scroller; 
	private JButton itemButton;
	private boolean scannedByAI;
	private JPanel demandSpace;
	private double[] demandNums;
	private final static String TO_AI_FILE = "resources/aiInput.csv";
	private final static String FROM_AI_FILE = "resources/aiOutput.csv"; //could also use dummy data "resources/predictions.csv"
	private final static String AI_SCRIPT = "python /c start python resources/aipia.py"; //could also test with "resources/activationTest.bat"
	private final static String DEMAND_FILE = "resources/demand.txt";
	private final static String IMPORT_FILE = "resources/import.csv";
	
	

	public InvButton (JPanel s, String l, JInternalFrame in, JInternalFrame b, JPanel d){ 
		this.sales = new ArrayList<SaleRecord>();
		this.label = l;
		this.scroller = s;
		this.graph = new LineGraph(this.label);
		this.graphSpace = in;
		this.aiButtonSpace = b;
		this.scannedByAI = false;
		this.demandSpace = d;
		this.demandNums = null;
		//

		itemButton = new JButton(l);
		itemButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainButtonAction();
			}
		});
		itemButton.setMaximumSize(new Dimension(500, 50));
		itemButton.setMinimumSize(new Dimension(500, 50));
		this.scroller.add(itemButton);
		
		this.aiButton = new JButton("AI Scan");
		aiButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aiButtonAction();
			}
		});
	}

	public void newSale(String d, double s, String t) {
		this.sales.add(new SaleRecord(d, s, t));
	}
	
	public String displaySales() {
		String display = "";
		if (this.sales == null) {
			display = "No sales recorded.";
		}
		else {
			display += "\n" + this.label + "\n";
			for (SaleRecord sale : this.sales) {
				display += sale.getDate() + " ";
				display += sale.getAmtSold();
				display += ", ";
			}
		}
		return display;
	}
	
	public void mainButtonAction() {	
		this.graphSpace.setContentPane(this.graph.getGraph()); 
		graphSpace.revalidate();
		graphSpace.repaint();
		
		this.aiButtonSpace.setContentPane(this.aiButton);
		aiButtonSpace.revalidate();
		aiButtonSpace.repaint();
		
		this.updateDemandSpace();
	}
	
	public void drawGraph() {
		SaleRecord[] prim = new SaleRecord[this.sales.size()];
		for (int i=0; i<prim.length; i++) {
			prim[i] = this.sales.get(i);
		}
		this.graph.buildGraph(prim);
		this.graphSpace.setContentPane(this.graph.getGraph()); //< -- This is why the 'salami' button is displayed on startup
		graphSpace.revalidate();
		graphSpace.repaint();
	}
	
	public void aiButtonAction() {
		if (scannedByAI == false) {
			this.aiWriteOut(TO_AI_FILE, IMPORT_FILE);
			this.runScript(AI_SCRIPT);
			this.aiReadIn(FROM_AI_FILE);
			this.demandNums = this.readDemand(DEMAND_FILE);
			this.drawGraph();
			this.updateDemandSpace();
			this.scannedByAI = true;
		}
		else {
			System.out.println(this.label + " has already been scanned by the AI");
		}
	}
	
	public void aiWriteOut(java.lang.String toAIfile, java.lang.String importFile) {
		//Copy Header
		String[] header = {" ", " ", " "};
		try {
			Scanner sc = new Scanner(new File(importFile));
			sc.useDelimiter("\n");
			header = sc.next().split(",");
			sc.close();
		} catch (IOException ioe) {
	     	   ioe.printStackTrace();
	     	   System.out.println("Unable to read file "+importFile);
		}
		header[2] = header[2].substring(0,(header[2].length())-1); //Remove \n
		
		
		//Paste Header
		PrintWriter pw;
	    try {
	        pw = new PrintWriter(new File(toAIfile));
	        StringBuffer csvHeader = new StringBuffer("");
	        csvHeader.append("month,"+header[1]+","+header[2]+","+header[0]+","+this.label);
	        pw.write(csvHeader.toString());
	        pw.close();
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	        System.out.println("Unable to write out to file "+toAIfile);
	    }
	}
	
	public void aiReadIn(java.lang.String filename) {
		try {
			Scanner sc = new Scanner(new File(filename));
			String[] row;
			sc.useDelimiter("\n"); 
			sc.next();
			while (sc.hasNext()) {
				row = sc.next().split(",");
				//System.out.println(row[0]+" "+row[1]);
				this.newSale(row[0], Double.parseDouble(row[2]), "AI"); //row[2] could also be row[1] depending on file
			}
			sc.close();
		} catch (IOException ioe) {
	     	   ioe.printStackTrace();
	     	   System.out.println("AI read-in file not found.");
		}
	}
	
	public double[] readDemand(java.lang.String filename) {
		double demand = -1;
		double plusOrMinus = -1;
		try {
			Scanner sc = new Scanner(new File(filename));
			String[] row = sc.next().split(",");
			//System.out.println(row[0]+" "+row[1]);
			demand = Double.parseDouble(row[0]);
			plusOrMinus = Double.parseDouble(row[1]);
			sc.close();
		} catch (IOException ioe) {
	     	   ioe.printStackTrace();
	     	   System.out.println("Demand file not found.");
		}
		double[] arr = {demand, plusOrMinus};
		return arr;
	}
	
	private void updateDemandSpace() {
		this.demandSpace.removeAll();
		demandSpace.revalidate();
		demandSpace.repaint();
		if (demandNums != null) {
			String st0 = "The AI predicts that the total demand on this item over the next";
			String st1 = "sixty days will be:";
			String st2 = " ";
			String st3 = demandNums[0] + " units.";
			String st4 = " ";
			String st5 = "Plus or minus " + demandNums[1] + " units.";
			demandSpace.add(new JLabel(st0));
			demandSpace.add(new JLabel(st1));
			demandSpace.add(new JLabel(st2));
			demandSpace.add(new JLabel(st3));
			demandSpace.add(new JLabel(st4));
			demandSpace.add(new JLabel(st5));
		}
	}
	
	
	public void runScript (String script) {
		try {
			String command = script;
			Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			System.out.println("AI script failed to run. Missing "+script);
			e.printStackTrace();
		}
	}

}
