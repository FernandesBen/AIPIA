package inventory;

import java.awt.*;
import javax.swing.*;

import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InvGUI {
	
	private JFrame frame;
	private JLabel footer;
	private final static String IMPORT_FILE = "resources/import.csv";
	private ArrayList<InvButton> butList;
	private JInternalFrame graphSpace;
	private JInternalFrame aiButtonSpace;
	private JScrollPane scrollFrame;
	private JPanel scroller;
	private JButton uploadBut;
	private TutorialGUI tutorial;
	private JButton refreshBut;
	private JButton massScanBut;
	private JPanel demandSpace;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InvGUI window = new InvGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	public InvGUI() {
		this.butList = new ArrayList<InvButton>();
		initialize();
	}


	private void initialize() {
		this.tutorial = new TutorialGUI();
		graphSpace = new JInternalFrame();
		aiButtonSpace = new JInternalFrame();
		scroller = new JPanel ();
		
		frame = new JFrame("CSC 548 Inventory Demand Predictor");
		frame.setBounds(100, 100, 950, 550);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		graphSpace.setVisible(true);
		graphSpace.setBounds(415, 15, 500, 300);
		this.frame.add(graphSpace);
		
		aiButtonSpace.setVisible(true);
		aiButtonSpace.setBounds(415, 325, 90, 70);
		this.frame.add(aiButtonSpace);

		footer = new JLabel("CSC 548 Software Engineering. Inventory Demand Predictor. Made by Ryan King and Ben Fernandes. November 16, 2020.");
		footer.setBounds(110, 480, 750, 26);
		footer.setHorizontalAlignment(SwingConstants.CENTER);
		frame.add(footer);
		
		scroller.setLayout(new BoxLayout(scroller, BoxLayout.Y_AXIS));
		
		uploadBut = new JButton("Import");
		uploadBut.setBounds(70, 18, 80, 30);
		this.frame.add(uploadBut);
		uploadBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				uploadButtonAction();
			}
		});
		
		refreshBut = new JButton("Refresh");
		refreshBut.setBounds(160, 18, 80, 30);
		this.frame.add(refreshBut);
		refreshBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshButtonAction();
			}
		});
		
		massScanBut = new JButton("Scan All");
		massScanBut.setBounds(250, 18, 80, 30);
		this.frame.add(massScanBut);
		massScanBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				massScanButtonAction();
			}
		});
		
		scrollFrame = new JScrollPane (
				scroller, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollFrame.setBounds(35, 65, 350, 400);
		this.frame.add(scrollFrame);
		
		demandSpace = new JPanel();
		demandSpace.setBounds(540, 340, 355, 130);
		demandSpace.setLayout(new BoxLayout(demandSpace, BoxLayout.Y_AXIS));
		demandSpace.setVisible(true);
		//demandSpace.setBackground(new Color(1));
		this.frame.add(demandSpace);

		readUpload(IMPORT_FILE);
	}
	
	
	private void uploadButtonAction() {
		this.graphSpace.setContentPane(tutorial.getTutScroller());
	}
	
	private void refreshButtonAction() {
		clearAllData();
		readUpload(IMPORT_FILE);
		this.demandSpace.removeAll();
		demandSpace.revalidate();
		demandSpace.repaint();
	}
	
	private void massScanButtonAction() {
		for (InvButton button : butList) {
			button.aiButtonAction();
		}
	}
	
	public void clearAllData() {
		butList.clear();
		scroller.removeAll();
		scroller.revalidate();
		scroller.repaint();
	}
	
	public void readUpload(String filename) {
		try {
			Scanner sc = new Scanner(new File(filename));
			sc.useDelimiter("\n");
			sc.next();
			newButton(sc, null, "");
			sc.close();
		} catch (IOException ioe) {
	     	   ioe.printStackTrace();
	     	   System.out.println("No file available. New user.");
		}
	}

	public void newButton(Scanner sc, InvButton newBut, String previousWord) {
		if (sc.hasNext()) {
			String[] row = sc.next().split(",");
			if (!row[0].equals(previousWord)) {
				if (newBut != null) {
					newBut.drawGraph();
					this.butList.add(newBut);
				}
				newBut = new InvButton (scroller, row[0], this.graphSpace, this.aiButtonSpace, this.demandSpace);
			}
			if (newBut != null) {
				newBut.newSale(row[1], Double.parseDouble(row[2]), "Original Data");
			}
			this.newButton(sc, newBut, row[0]);
		}
		else {
			newBut.drawGraph();
			this.butList.add(newBut);
		}
	}

}
