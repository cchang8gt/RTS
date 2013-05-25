package ai.chris;
import java.util.ArrayList;
import rts.units.UnitAction;
import java.util.Comparator;
import java.util.PriorityQueue;
//public static final int MAP_FOG       = 1;  /**< (bitmask) the tile has fog of war flag */
//public static final int MAP_WALL      = 2;  /**< (bitmask) the tile has the wall flag */
//public static final int MAP_PLAYER    = 4;  /**< (bitmask) the tile has the current player's unit */
//public static final int MAP_NEUTRAL   = 8;  /**< (bitmask) the tile has the neutral player's unit */
//public static final int MAP_NONPLAYER = 16; /**< (bitmask) the tile has a unit that isn't the current or neutral player's */ 

/*
 * Map should know: 
 * List of all nodes
 * 
 * Map jobs:
 * Update nodes
 * 	- Assign new costs
 * 	- Assign new neighbors (resources disappears)
 */
import rts.GameState;
public class MyMap {
	public GameState state;
	public int map[][];
	public int map_width;
	public int map_height;
	//delete this
	public int map_array[];
	public ArrayList<Node> nodes;
	
	/*
	 * Create 2D Array of map
	 * Use 2D array to generate graph of nodes
	 */
	public MyMap(GameState g, int[] map_array) {
		state = g;
		nodes = new ArrayList<Node>();
		map_width = g.getMapWidth();
		map_height = g.getMapHeight();

		this.map_array = map_array;
		map = new int[g.getMapHeight()][g.getMapWidth()];
		
		int row_count = 0;
		int col_count = 0;
		for(int i = 0; i < map_array.length; i++) {
			map[row_count][col_count] = map_array[i];
			col_count++;
			if(col_count == map_width) {
				col_count = 0;
				row_count++;
			}
		}
		
		/*
		 * Generate the Node set
		 */
		for(int i = 0; i < map.length; i++) {
			for(int j = 0; j < map[i].length; j++) {
				nodes.add(new Node(j, i, map[i][j], this));
			}
		}
		for(int i = 0; i < nodes.size(); i++) {
			nodes.get(i).generateNeighbors();
		}
	}
	
	/*
	 * Update the 2D map array and then update changes
	 */
	public void updateMap() {
		map_array = state.getMap();
		int row_count = 0;
		int col_count = 0;
		for(int i = 0; i < map_array.length; i++) {
			map[row_count][col_count] = map_array[i];
			col_count++;
			if(col_count == map_width) {
				col_count = 0;
				row_count++;
			}
		}
		
		for(int i = 0; i < map.length; i++) {
			for(int j = 0; j < map[i].length; j++) {
				getNode(j, i).updateCost(map[i][j]);
			}
		}
	}
	

	public void dumpMap() {
		/*
		System.out.println(map.length);
		for(int i = 0; i < map.length; i++) {
			for(int j = 0; j < map[i].length; j++) {
				System.out.print(map[i][j]);
			}
			System.out.println();
		}
		*/
		System.out.println(map.length);
		for(int i = 0; i < nodes.size(); i++) {
			System.out.print(nodes.get(i).getNodeType());
			if(i%32 == 31 && i > 0) {
				System.out.println();
			}
		}
	}
	
	public void debugDump() {
		for(int i = 0; i < nodes.size(); i++) {
			System.out.println("X: " + nodes.get(i).getX() + " Y: " + nodes.get(i).getY() + " Type: " + nodes.get(i).getNodeType() + " Cost: " + nodes.get(i).getCost());
		}
	}
	
	public int getManhattanDistance(int start, int end) {
		int x1 = start%state.getMapWidth();
		int y1 = start/state.getMapWidth();
		int x2 = end%state.getMapWidth();
		int y2 = end/state.getMapWidth();
		
		return(Math.abs(x2-x1)+Math.abs(y2-y1));
	}
	
	public int getManhattanDistance(int x1, int y1, int x2, int y2) {	
		return(Math.abs(x2-x1)+Math.abs(y2-y1));
	}
	
	public Node getNode(int x, int y) {
		for(int i = 0; i < nodes.size(); i++) {
			if(nodes.get(i).getX() == x && nodes.get(i).getY() == y) {
				return nodes.get(i);
			}
		}
		return null;
	}
	
	public boolean test() {
		Node p = this.getNode(15, 15);
		for(int i = 0; i < nodes.size(); i++) {
			if(nodes.get(i).equals(p)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean contains(int loc) {
		int x = loc%state.getMapWidth();
		int y = loc/state.getMapWidth();
		for(Node node : nodes) {
			if(node.getX() == x && node.getY() == y) {
				return true;
			}
		}
		return false;
	}
	
	public int get_path(int start, int goal) {
		int startx = start%state.getMapWidth();
		int starty = start/state.getMapWidth();
		
		int endx = goal%state.getMapWidth();
		int endy = goal/state.getMapWidth();
		
		Node start_node = this.getNode(startx, starty);
		Node goal_node = this.getNode(endx, endy);
		
		if(goal_node.getX() == start_node.getX() && goal_node.getY() == start_node.getY()) {
			return -1;
		}
		ArrayList<Node> temp = new ArrayList<Node>();
		Comparator<Node> c = new NodeComparator();
		start_node.setStart(true);
		goal_node.setGoal(true);
		Node current_node;
		//Node current_node = start_node;
		PriorityQueue<Node> open_set = new PriorityQueue<Node>(nodes.size(),c);
		//ArrayList<Node> open_set = new ArrayList<Node>();
		ArrayList<Node> closed_set = new ArrayList<Node>();
		//temp = current_node.getNeighbors();
		open_set.add(start_node);
		//MAKE SURE TO GET NODE BY (COL, ROW) 
		while(!open_set.isEmpty()) {
			current_node = open_set.remove();
			//System.out.println("Current Node: " + current_node);
			if(current_node.isGoal()) {
				//System.out.println("GOAL FOUND");
				//return reconstructPath(current_node);
				//System.out.println(reconstructPath(current_node));
				Node v = reconstructPath(current_node);
				if(v != null) {
					this.resetNodes();
					return v.getX()+v.getY()*map_width;
				}
			}
			else {
				closed_set.add(current_node);
				for(int i = 0; i < current_node.getNeighbors().size(); i++) {
					if(closed_set.contains(current_node.getNeighbors().get(i))) {
						
					}
					else if(open_set.contains(current_node.getNeighbors().get(i)) && current_node.getGValue()+current_node.getNeighbors().get(i).getCost() < current_node.getNeighbors().get(i).getGValue()) {
						current_node.getNeighbors().get(i).setParent(current_node);
						current_node.getNeighbors().get(i).setGValue(current_node.getGValue()+current_node.getNeighbors().get(i).getCost());
					}
					else if(!open_set.contains(current_node.getNeighbors().get(i))) {
						current_node.getNeighbors().get(i).setParent(current_node);
						current_node.getNeighbors().get(i).setGValue(current_node.getGValue() + current_node.getNeighbors().get(i).getCost());
						open_set.add(current_node.getNeighbors().get(i));
					}
				}
			}
		}
		
		//System.out.println();
		this.resetNodes();
		return -1;
		
		//return open_set.get(0).getLocation();
	}
	
	/**
	 * Returns list of paths
	 * @param start
	 * @param goal
	 * @return
	 */
	public ArrayList<Node> get_path_list(int start, int goal) {
		int startx = start%state.getMapWidth();
		int starty = start/state.getMapWidth();
		
		int endx = goal%state.getMapWidth();
		int endy = goal/state.getMapWidth();
		
		Node start_node = this.getNode(startx, starty);
		Node goal_node = this.getNode(endx, endy);
		
		if(goal_node.getX() == start_node.getX() && goal_node.getY() == start_node.getY()) {
			return null;
		}
		ArrayList<Node> temp = new ArrayList<Node>();
		Comparator<Node> c = new NodeComparator();
		start_node.setStart(true);
		goal_node.setGoal(true);
		Node current_node;
		//Node current_node = start_node;
		PriorityQueue<Node> open_set = new PriorityQueue<Node>(nodes.size(),c);
		//ArrayList<Node> open_set = new ArrayList<Node>();
		ArrayList<Node> closed_set = new ArrayList<Node>();
		//temp = current_node.getNeighbors();
		open_set.add(start_node);
		//MAKE SURE TO GET NODE BY (COL, ROW) 
		while(!open_set.isEmpty()) {
			current_node = open_set.remove();
			//System.out.println("Current Node: " + current_node);
			if(current_node.isGoal()) {
				System.out.println("GOAL FOUND");
				//return reconstructPath(current_node);
				//System.out.println(reconstructPath(current_node));
				ArrayList<Node> n = reconstructPath2(current_node);
				if(n != null) {
					return n;
				}
			}
			else {
				closed_set.add(current_node);
				for(int i = 0; i < current_node.getNeighbors().size(); i++) {
					if(closed_set.contains(current_node.getNeighbors().get(i))) {
						
					}
					else if(open_set.contains(current_node.getNeighbors().get(i)) && current_node.getGValue()+current_node.getNeighbors().get(i).getCost() < current_node.getNeighbors().get(i).getGValue()) {
						current_node.getNeighbors().get(i).setParent(current_node);
						current_node.getNeighbors().get(i).setGValue(current_node.getGValue()+current_node.getNeighbors().get(i).getCost());
					}
					else if(!open_set.contains(current_node.getNeighbors().get(i))) {
						current_node.getNeighbors().get(i).setParent(current_node);
						current_node.getNeighbors().get(i).setGValue(current_node.getGValue() + current_node.getNeighbors().get(i).getCost());
						open_set.add(current_node.getNeighbors().get(i));
					}
				}
			}
		}
		
		System.out.println();
		this.resetNodes();
		return null;
		
		//return open_set.get(0).getLocation();
	}
	
	public ArrayList<Node> reconstructPath2(Node goal) {
		ArrayList<Node> n = new ArrayList<Node>();
		ArrayList<Node> m = new ArrayList<Node>();
		n.add(goal);
		Node parent = goal;
		while(true) {
			parent = parent.getParent();
			if(parent != null) {
				n.add(parent);
			}
			else if(parent == null) {
				break;
			}
		}
		for(int i = n.size()-2; i >= 0; i--) {
			m.add(n.get(i));
		}
		return m;
	}
	
	/**
	 * Recursively traverses and finds second to last node
	 * @param goal
	 * @return
	 */
	public Node reconstructPath(Node goal) {
		/*
		ArrayList<Node> n = new ArrayList<Node>();
		n.add(reconstructPath(goal.getParent()));
		for(int i = 0; i < n.size(); i++) {
			System.out.println(n.get(i));
		}
		System.out.println();
		return null;
		*/
		Node n = goal;
		//System.out.println(n);
		if(n.getParent() != null) {
			if(n.getParent().isStart()) {
				return n;
			}
		}
		return reconstructPath(n.getParent());
		//return goal.getParent().getX()+goal.getParent().getY()*map_width;
	}
	
	/**
	 * Find locations around a point and return an array of free locations.
	 * Only returns traversable paths.
	 * @param x 
	 * @param y
	 * @param n
	 * @return
	 */
	/*
	 * NOTE UNNECESSARY!!! NOT USING!!!!
	 */
	public ArrayList<Integer> getAvailableLocationsAroundAPoint(int x, int y, int n) {
		Node current_node = this.getNode(x,  y);
		ArrayList<Integer> valid_locations = new ArrayList<Integer>();
		ArrayList<Node>	open_set = new ArrayList<Node>();
		ArrayList<Node> closed_set = new ArrayList<Node>();
		ArrayList<Node> validation_set = new ArrayList<Node>();
		
		int counter = 0;
		open_set.add(current_node);
		while(!open_set.isEmpty()) {
			current_node = open_set.remove(0);
		}
		//if(current_node.getCost() == 1 || current_node.getCost() == 2) {
		if(current_node.getCost() == 1) {
			valid_locations.add(current_node.getX()+current_node.getY()*map_width);
			counter++;
		}

		Node node = this.getNode(x, y);
		
		return null;
	}

	//resets all nodes
	public void resetNodes() {
		for(int i = 0; i < nodes.size(); i++) {
			nodes.get(i).setParent(null);
			nodes.get(i).setGValue(0);
			nodes.get(i).setHValue(0);
			nodes.get(i).setFValue(0);
			nodes.get(i).setGoal(false);
			nodes.get(i).setStart(false);
		}
	}
}
