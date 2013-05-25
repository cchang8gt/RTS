package ai.chris;
import java.util.ArrayList;
import rts.GameState;
/*
 * Node should know its:
 * Cost, Location, Neighbors
 */
public class Node {
	private MyMap map;
	private int x;
	private int y;
	private int node_type;
	private int cost;
	private int g_value;
	private int h_value;
	private int f_value;
	private Node parent;
	private boolean obstacle;
	public boolean is_start;
	public boolean is_goal;
	public boolean is_path;
	private Node previousNode;
	float distanceFromStart;
	float heuristicDistanceFromGoal;
	private Node temp;
	public ArrayList<Node> neighbors;
	
	boolean visited;
	public Node(int x, int y, int loc, MyMap map) {
		this.map = map;
		this.x = x;
		this.y = y;
		this.node_type = loc;
		neighbors = new ArrayList<Node>();
		//generateNeighbors();
		updateCost(node_type);
		updateObstacle(node_type);
	}
	
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	
	public void updateCost(int node_type) {
		this.node_type = node_type;
		if(node_type == GameState.MAP_FOG) {
			cost = 1;
		}
		if(node_type == GameState.MAP_WALL){
			cost = 100;
		}
		if(node_type == GameState.MAP_PLAYER) {
			cost = 25;
		}
		if(node_type == GameState.MAP_NEUTRAL) {
			cost = 100;
		}
		if(node_type == GameState.MAP_NONPLAYER) {
			cost = 25;
		}
		if(node_type == 0) {
			cost = 1;
		}
	}
	
	//might not use this
	public void updateObstacle(int node_type) {
		this.node_type = node_type;
		//if(node_type == GameState.MAP_FOG) {
		//	obstacle 
		//}
		if(node_type == GameState.MAP_WALL){
			obstacle = true;
		}
		else if(node_type == GameState.MAP_PLAYER) {
			obstacle = true;
		}
		else if(node_type == GameState.MAP_NEUTRAL) {
			obstacle = true;
		}
		else if(node_type == GameState.MAP_NONPLAYER) {
			obstacle = true;
		}
		else if(node_type == 0) {
			obstacle = false;
		}
	}
	
	public boolean isObstacle() {
		return this.obstacle;
	}
	
	public void generateNeighbors() {
		//x-1 y
		//x+1 y
		//x y-1
		//x y+1
		temp = map.getNode(x-1, y);
		if(temp != null) {neighbors.add(temp);}
		temp = map.getNode(x+1, y);
		if(temp != null) {neighbors.add(temp);}
		temp = map.getNode(x, y-1);
		if(temp != null) {neighbors.add(temp);}
		temp = map.getNode(x, y+1);
		if(temp != null) {neighbors.add(temp);}
	}
	
	//usable by that func
	//don't think i need this
	public int getLocation() {
		return x+y*map.state.getMapWidth();
	}
	
	public int getNodeType() {
		return node_type;
	}
	
	public ArrayList<Node> getNeighbors() {
		return neighbors;
	}
	
	public void setCost(int cost) {
		this.cost = cost;
	}
	
	public int getCost() {
		return cost;
	}
	
	public void setStart(boolean s) {
		this.is_start = s;
	}
	
	public boolean isStart() {
		return this.is_start;
	}
	public void setGoal(boolean s) {
		this.is_goal = s;
	}
	
	public boolean isGoal() {
		return this.is_goal;
	}
	
	public void setVisited(boolean s) {
		this.visited = s;
	}
	
	public boolean isVisited() {
		return visited;
	}
	
	public void setParent(Node n) {
		this.parent = n;
	}
	
	public Node getParent() {
		return this.parent;
	}
	
	public int getFValue() {
		return this.f_value;
	}
	
	public void setFValue(int f) {
		this.f_value = f;
	}
	
	public int getGValue() {
		return this.g_value;
	}
	
	public void setGValue(int g) {
		this.g_value = g;
	}
	
	public void setHValue(int h) {
		this.h_value = h;
	}
	
	public int getHValue() {
		return this.h_value;
		
	}
	
	public String toString() {
		return "X: " + this.x + " Y: " + this.y + " Type: " + this.node_type + " Cost: " + this.cost;
	}
	
	public boolean equals(Object n) {
		return ((Node)n).getX() == this.x && ((Node)n).getY() == this.y;
	}
}
