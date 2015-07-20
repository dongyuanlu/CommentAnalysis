package uibe.ldy.network;

import uibe.ldy.reddit.RedditConfig;
import util.SQLUtil;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

public class CommenterGraph {
	
	private UndirectedSparseGraph<CmmNode, CmmEdge> graph;
	
	private SQLUtil sql;
	
	
	public CommenterGraph(){
		graph = new UndirectedSparseGraph<CmmNode, CmmEdge>();
		
		sql = new SQLUtil(RedditConfig.database);
	}
	
	
	public void generateGraphFromDB(){
		
	}
	
	
	class CmmNode {
		int id;
		String commenterId;
		
	}
	
	class CmmEdge {
		private double weight;
		private int polarity;
		
		int id;
		
		public CmmEdge(double weight, int polarity){
			this.weight = weight;
			this.polarity = polarity;
		}

		public double getWeight() {
			return weight;
		}

		public int getPolarity() {
			return polarity;
		}
		
		
	}
	
	
	

}
