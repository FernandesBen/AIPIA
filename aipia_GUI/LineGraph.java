package inventory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;


public class LineGraph {
	private ChartPanel graph;
	private String chartTitle;
	
	public LineGraph(String ct) {
		this.chartTitle = ct;
	}
	

	private DefaultCategoryDataset createDataset(SaleRecord[] sales) {
	      DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
	      SaleRecord previousSale = null;
	      for (SaleRecord sale : sales) {
	    	  if (previousSale != null && previousSale.getType() != sale.getType()) {
	    		  dataset.addValue(
	    				  previousSale.getAmtSold(), 
	    				  sale.getType(), 
	    				  previousSale.getDate());
	    	  }
	    	  dataset.addValue(
	    			  sale.getAmtSold(), 
	    			  sale.getType(), 
	    			  sale.getDate()); //this.xAxis(sales.length,  sale)
	    	  previousSale = sale;
	      }
	      return dataset;
	   }
	

	public void buildGraph(SaleRecord[] sales) {
		JFreeChart lineChart = ChartFactory.createLineChart(
				 this.chartTitle,
		         "Date of Sale","Quantity Sold",
		         createDataset(sales),
		         PlotOrientation.VERTICAL,
		         true,true,false);
		this.graph = new ChartPanel( lineChart );
		//this.graph.setBounds(500, 15, 400, 400);
	}
	
	public ChartPanel getGraph() {
		if (this.graph == null) {
			System.out.println("Graph cannot be displayed; it has not been built.");
		}
		return this.graph;
	}
	   
}
