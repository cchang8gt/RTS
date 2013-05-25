package ai.chris;
import java.util.ArrayList;

import rts.GameState;
import rts.units.Unit;
import rts.units.UnitAction;
import rts.units.UnitDefinition;
import ai.chris.MyMap;
import ai.chris.MyAI;
/*
 * need to create an abstraction layer for units to queue orders
 * notes about actions:
 * setting the action doesn't mean it'll be performed. make sure that each action is performed 
 */
public class MyUnits {
	public static final int STATE_IDLE = 0;
	public static final int STATE_ATTACK = 1;
	public static final int STATE_MOVE = 2;
	public static final int STATE_MINE = 3;
	public static final int STATE_RETURN = 4;
	public static final int STATE_DEPOSIT = 5;
	public static final int STATE_ALERT = 6;
	public static final int STATE_EXPLORE = 7;
	public static final int STATE_FIND_RESOURCE = 8;
	public static final int STATE_FOUND_RESOURCE = 9;
	
	//values
	public static final int KILL_SKY_ARCHER = 1;
	public static final int KILL_BIRD = 2;
	public static final int KILL_LIGHT = 3;
	public static final int KILL_ARCHER = 4;
	public static final int KILL_HEAVY = 5;
	
	public static final int KILL_AIRPORT = 1;
	public static final int KILL_BARRACKS = 2;
	public static final int KILL_BASE = 3;
	
	public Unit stats;
	public MyMap map;
	
	public int state;
	public int resource_location; //for workers only
	public Resource resource_to_mine;
	public boolean exists;
	public long id;
	
	public boolean nearby[];
	ArrayList<Node> possible_nearby_list;
	private Node unit_node;
	
	/*
	 * If worker finds an empty resource, set it to its
	 */
	private int my_resource;
	
	public MyUnits(Unit u, MyMap map) {
		this.stats = u;
		this.map = map;
		nearby = new boolean[4];
		id = u.getID();
		possible_nearby_list = new ArrayList<Node>();
		unit_node = map.getNode(stats.getX(), stats.getY());
		possible_nearby_list = unit_node.getNeighbors();
		//state = 0;
		resource_location = -1;
		exists = true;
		
		if(u.isWorker() == false) {
			state = STATE_MOVE;
			updateNearbyEnemies();
		}
		if(u.isWorker()) {
			state = 0;
			updateNearbySpace();
		}
	}
	
	public MyUnits(Unit u) {
		this.stats = u;
	}

	public void updateNearbyUnits() {
		updatePosition();
		for(int i = 0; i < possible_nearby_list.size(); i++) {
			if(possible_nearby_list.get(i).getNodeType() == 0) {
				if(possible_nearby_list.get(i).getX() < stats.getX() && possible_nearby_list.get(i).getY() == stats.getY()) {
					nearby[0] = true;
				}
				//right
				else if(possible_nearby_list.get(i).getX() > stats.getX() && possible_nearby_list.get(i).getY() == stats.getY()) {
					nearby[1] = true;
				}
				//top
				else if(possible_nearby_list.get(i).getX() == stats.getX() && possible_nearby_list.get(i).getY() < stats.getY()) {
					nearby[2] = true;
				}
				//bottom
				else if(possible_nearby_list.get(i).getX() == stats.getX() && possible_nearby_list.get(i).getY() > stats.getY()) {
					nearby[3] = true;
				}
			}
			else {
				if(possible_nearby_list.get(i).getX() < stats.getX() && possible_nearby_list.get(i).getY() == stats.getY()) {
					nearby[0] = false;
				}
				//right
				else if(possible_nearby_list.get(i).getX() > stats.getX() && possible_nearby_list.get(i).getY() == stats.getY()) {
					nearby[1] = false;
				}
				//top
				else if(possible_nearby_list.get(i).getX() == stats.getX() && possible_nearby_list.get(i).getY() < stats.getY()) {
					nearby[2] = false;
				}
				//bottom
				else if(possible_nearby_list.get(i).getX() == stats.getX() && possible_nearby_list.get(i).getY() > stats.getY()) {
					nearby[3] = false;
				}
			}
		}
	}
	
	public void updateNearbyEnemies() {
		updatePosition();
		for(int i = 0; i < possible_nearby_list.size(); i++) {
			if(possible_nearby_list.get(i).getNodeType() == 16) {
				if(possible_nearby_list.get(i).getX() < stats.getX() && possible_nearby_list.get(i).getY() == stats.getY()) {
					nearby[0] = true;
				}
				//right
				else if(possible_nearby_list.get(i).getX() > stats.getX() && possible_nearby_list.get(i).getY() == stats.getY()) {
					nearby[1] = true;
				}
				//top
				else if(possible_nearby_list.get(i).getX() == stats.getX() && possible_nearby_list.get(i).getY() < stats.getY()) {
					nearby[2] = true;
				}
				//bottom
				else if(possible_nearby_list.get(i).getX() == stats.getX() && possible_nearby_list.get(i).getY() > stats.getY()) {
					nearby[3] = true;
				}
			}
			else {
				if(possible_nearby_list.get(i).getX() < stats.getX() && possible_nearby_list.get(i).getY() == stats.getY()) {
					nearby[0] = false;
				}
				//right
				else if(possible_nearby_list.get(i).getX() > stats.getX() && possible_nearby_list.get(i).getY() == stats.getY()) {
					nearby[1] = false;
				}
				//top
				else if(possible_nearby_list.get(i).getX() == stats.getX() && possible_nearby_list.get(i).getY() < stats.getY()) {
					nearby[2] = false;
				}
				//bottom
				else if(possible_nearby_list.get(i).getX() == stats.getX() && possible_nearby_list.get(i).getY() > stats.getY()) {
					nearby[3] = false;
				}
			}
		}
	}
	public void updateNearbySpace() {
		updatePosition();
		for(int i = 0; i < possible_nearby_list.size(); i++) {
			if(possible_nearby_list.get(i).getNodeType() == 0) {
				if(possible_nearby_list.get(i).getX() < stats.getX() && possible_nearby_list.get(i).getY() == stats.getY()) {
					nearby[0] = true;
				}
				//right
				else if(possible_nearby_list.get(i).getX() > stats.getX() && possible_nearby_list.get(i).getY() == stats.getY()) {
					nearby[1] = true;
				}
				//top
				else if(possible_nearby_list.get(i).getX() == stats.getX() && possible_nearby_list.get(i).getY() < stats.getY()) {
					nearby[2] = true;
				}
				//bottom
				else if(possible_nearby_list.get(i).getX() == stats.getX() && possible_nearby_list.get(i).getY() > stats.getY()) {
					nearby[3] = true;
				}
			}
			else {
				if(possible_nearby_list.get(i).getX() < stats.getX() && possible_nearby_list.get(i).getY() == stats.getY()) {
					nearby[0] = false;
				}
				//right
				else if(possible_nearby_list.get(i).getX() > stats.getX() && possible_nearby_list.get(i).getY() == stats.getY()) {
					nearby[1] = false;
				}
				//top
				else if(possible_nearby_list.get(i).getX() == stats.getX() && possible_nearby_list.get(i).getY() < stats.getY()) {
					nearby[2] = false;
				}
				//bottom
				else if(possible_nearby_list.get(i).getX() == stats.getX() && possible_nearby_list.get(i).getY() > stats.getY()) {
					nearby[3] = false;
				}
			}
		}
	}
	public boolean leftOpen() {
		return nearby[0];
	}
	public boolean rightOpen() {
		return nearby[1];
	}
	public boolean topOpen() {
		return nearby[2];
	}
	public boolean botOpen() {
		return nearby[3];
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
		
		if(nearby[0]) {
			x--;
			return x+y*map.state.getMapWidth();
		}
		else if(nearby[1]) {
			x++;
			return x+y*map.state.getMapWidth();
		}
		else if(nearby[2]) {
			y--;
			return x+y*map.state.getMapWidth();
		}
		else if(nearby[3]) {
			y++;
			return x+y*map.state.getMapWidth();
		}
		else {
			return -1;
		}
	}
	
	public void updatePosition() {
		unit_node = map.getNode(stats.getX(), stats.getY());
		possible_nearby_list = unit_node.getNeighbors();
	}
	
	public void update_worker(ArrayList<Resource> r) {

		if(state == STATE_IDLE) {
			state = STATE_FIND_RESOURCE;
		}
		if(state == STATE_FIND_RESOURCE) {
			
		}
		if(state == STATE_FOUND_RESOURCE) {
			
		}
		if(state == STATE_MOVE) {
			
		}
		
	}
	
	public void work() {
		if(this.resource_to_mine != null) {
			System.out.print("LOC: ");
			System.out.println(resource_location%map.map_width + " " + resource_location/map.map_width);
			int path = map.get_path(stats.getX()+stats.getY()*map.map_width, resource_location);
			stats.setAction(new UnitAction(stats, UnitAction.MOVE, path%map.state.getMapWidth(), path/map.state.getMapWidth(), 0));
			
		}
	}
	//in form of x+y*width
	public void set_resource_location(int n) {
		this.resource_location = n;
	}
	
	public int get_resource_location() {
		return this.resource_location;
	}
	
	public void update_fighter() {
		
	}
	
	public void addAction() {
		
	}
	public void attack(int x, int y) {
		
	}
	
	public ArrayList<Unit> nearby_units() {
		if(stats.getType() == MyAI.UNIT_ARCHER) {
			
		}
		else if(stats.getType() == MyAI.UNIT_LIGHT || stats.getType() == MyAI.UNIT_HEAVY) {
			
		}
		return null;
	}
	public void move_single(int x, int y) {
		int loc = x+y*map.state.getMapWidth();
		int path;
		System.out.println("MOVING!!");
		if(stats.hasAction() == false && stats.getX() != x && stats.getY() != y) {
			path = map.get_path(stats.getX()+stats.getY()*map.state.getMapWidth(), loc);
			stats.setAction(new UnitAction(stats, UnitAction.MOVE, path%map.state.getMapWidth(), path/map.state.getMapWidth(), 0));
		}
	}
	
	public void move_complete(int x, int y) {
		int loc = x+y*map.state.getMapWidth();
		ArrayList<Node> list = new ArrayList<Node>();
		if(stats.hasAction() == false && stats.getX() != x && stats.getY() != y) {
			list = map.get_path_list(stats.getX()+stats.getY()*map.state.getMapWidth(), loc);
			if(list != null && list.isEmpty() == false) {
				for(int i = 0; i < list.size(); i++) {
					//list code
				}
			}
		}
	}
	
	public long getID() {
		return this.id;
	}
	public boolean equals(Object o) {
		return(stats.getID() == ((MyUnits)o).stats.getID());
	}
	public boolean equals(Unit u) {
		return(u.getID() == stats.getID());
	}
}
