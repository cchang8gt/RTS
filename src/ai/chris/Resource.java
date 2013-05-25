package ai.chris;
import java.util.ArrayList;

import rts.GameState;
import rts.units.Unit;
import rts.units.UnitAction;


public class Resource {
	private Node resource_node;
	private int resource_type;
	public Unit stats;
	public MyUnits claimedWorker;
	public long claimed;
	public MyMap map;
	public boolean exists;
	public int distance;
	//0 == left
	//1 == right
	//2 == top
	//3 == bottom 
	private boolean openings[];	
	
	private ArrayList<Node> possible_openings_list;

	public Resource(Unit resource, MyMap map) {
		this.map = map;
		openings = new boolean[4];
		stats = resource;
		possible_openings_list = new ArrayList<Node>();
		resource_node = map.getNode(stats.getX(), stats.getY());
		//updateWorker(ai);
		possible_openings_list = resource_node.getNeighbors();
		claimed = -1;
		exists = true;
		updateOpenings();
	}
	
	public Resource(Unit resource) {
		this.stats = resource;
		resource_type = resource.getType();
	}
	/*
	 * If possible.x < resource.x && possible.y == resource.y	then possible is left of resource
	 * If possible.x > resource.x && possible.y == resource.y	then possible is right of resource
	 * If possible.x == resource.x && possible.y < resource.y	then possible is below resource
	 * If possible.x == resource.x && possbile.y > resource.y	then possible is above resource
	 * 
	 * boolean: 
	 */
	public void updateOpenings() {
		//1 == nothing 
		for(int i = 0; i < possible_openings_list.size(); i++) {
			if(possible_openings_list.get(i).getNodeType() == 0) {
				//left
				if(possible_openings_list.get(i).getX() < stats.getX() && possible_openings_list.get(i).getY() == stats.getY()) {
					openings[0] = true;
				}
				//right
				else if(possible_openings_list.get(i).getX() > stats.getX() && possible_openings_list.get(i).getY() == stats.getY()) {
					openings[1] = true;
				}
				//top
				else if(possible_openings_list.get(i).getX() == stats.getX() && possible_openings_list.get(i).getY() < stats.getY()) {
					openings[2] = true;
				}
				//bottom
				else if(possible_openings_list.get(i).getX() == stats.getX() && possible_openings_list.get(i).getY() > stats.getY()) {
					openings[3] = true;
				}
			}
			else if(possible_openings_list.get(i).getNodeType() == GameState.MAP_NEUTRAL || 
					possible_openings_list.get(i).getNodeType() == GameState.MAP_WALL ||
					possible_openings_list.get(i).getNodeType() == GameState.MAP_PLAYER ||
					possible_openings_list.get(i).getNodeType() == GameState.MAP_NONPLAYER) {
				//left
				if(possible_openings_list.get(i).getX() < stats.getX() && possible_openings_list.get(i).getY() == stats.getY()) {
					openings[0] = false;
				}
				//right
				else if(possible_openings_list.get(i).getX() > stats.getX() && possible_openings_list.get(i).getY() == stats.getY()) {
					openings[1] = false;
				}
				//top
				else if(possible_openings_list.get(i).getX() == stats.getX() && possible_openings_list.get(i).getY() < stats.getY()) {
					openings[2] = false;
				}
				//bottom
				else if(possible_openings_list.get(i).getX() == stats.getX() && possible_openings_list.get(i).getY() > stats.getY()) {
					openings[3] = false;
				}
			}
			
		}
		//System.out.println("[Openings: " + "Left: " + l + " Right " + r + " Bottom " + b + " Top " + t + " X: " + resources.getX() + " Y: " + resources.getY() + "]");
	}
	
	//openings.add(resources.get(k).resources.getX()+resources.get(k).resources.getY()*state.getMapWidth() - state.getMapWidth());
	public boolean leftOpen() {
		return openings[0];
	}
	public boolean rightOpen() {
		return openings[1];
	}
	public boolean topOpen() {
		return openings[2];
	}
	public boolean botOpen() {
		return openings[3];
	} 
	
	public int getLeft() {
		int x = stats.getX()-1;
		int y = stats.getY();
		return x+y*map.state.getMapWidth();
	}
	public int getRight() {
		int x = stats.getX()+1;
		int y = stats.getY();
		return x+y*map.state.getMapWidth();
	}
	public int getTop() {
		int x = stats.getX();
		int y = stats.getY()-1;
		return x+y*map.state.getMapWidth();
	}
	public int getBot() {
		int x = stats.getX();
		int y = stats.getY()+1;
		return x+y*map.state.getMapWidth();
	}
	
	public int getAnyOpening() {
		int x = stats.getX();
		int y = stats.getY();
		
		if(openings[0]) {
			x--;
			return x+y*map.state.getMapWidth();
		}
		else if(openings[1]) {
			x++;
			return x+y*map.state.getMapWidth();
		}
		else if(openings[2]) {
			y--;
			return x+y*map.state.getMapWidth();
		}
		else if(openings[3]) {
			y++;
			return x+y*map.state.getMapWidth();
		}
		else {
			return -1;
		}
	}
	
	public void debug_dump() {
		
	}
	
	public boolean equals(Object n) {
		return(stats.getID() == ((Resource)n).stats.getID());
	}
	
	public boolean equals(Unit n) {
		return(stats.getID() == n.getID());
	}
	public boolean hasOpening() {
		return(openings[0]||openings[1]||openings[2]||openings[3]);
	}
	public String toString() {
		return("X: " + stats.getX() + " Y: " + stats.getY() + " TYPE: " + stats.getType() + " DISTANCE: " + 
	map.getManhattanDistance(stats.getX()+stats.getY()*map.map_width, 28+28*map.map_width) + " RESOURCE AMT: " + stats.getResources());
	}
	
}
