package ai.chris;
import java.util.ArrayList;

import rts.GameState;
import rts.units.Unit;
import rts.units.UnitAction;

/**
 * Building type:
 * 0 == Base
 * 1 == Soldier Office
 * 2 == Airport
 * @author qob
 *
 */
public class Buildings {
	private Node building_node;
	public Unit stats;
	public MyMap map;
	private boolean openings[];
	private int building_type;
	private static final boolean DEBUG = false;
	
	private ArrayList<Node> possible_openings_list;
	public boolean exists;
	public long id;
	
	public Buildings(Unit building, MyMap map) {
		this.map = map;
		openings = new boolean[4];
		stats = building;
		building_type = building.getType();
		possible_openings_list = new ArrayList<Node>();
		building_node = map.getNode(stats.getX(), stats.getY());
		exists = true;
		possible_openings_list = building_node.getNeighbors();
		updateOpenings();
	}
	
	public Buildings(Unit building) {
		stats = building;
		building_type = building.getType();
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
		for(int i = 0; i < possible_openings_list.size(); i++) {
			//free space
			if(possible_openings_list.get(i).getNodeType() == 0) {
				if(possible_openings_list.get(i).getX() < stats.getX() && possible_openings_list.get(i).getY() == stats.getY()) {
					openings[0] = true;
				}
				else if(possible_openings_list.get(i).getX() > stats.getX() && possible_openings_list.get(i).getY() == stats.getY()) {
					openings[1] = true; 
				}
				else if(possible_openings_list.get(i).getX() == stats.getX() && possible_openings_list.get(i).getY() < stats.getY()) {
					openings[2] = true;
				}
				else if(possible_openings_list.get(i).getX() == stats.getX() && possible_openings_list.get(i).getY() > stats.getY()) {
					openings[3] = true; 
				}
			}
			else if(possible_openings_list.get(i).getNodeType() == GameState.MAP_NEUTRAL ||
					possible_openings_list.get(i).getNodeType() == GameState.MAP_NONPLAYER ||
					possible_openings_list.get(i).getNodeType() == GameState.MAP_PLAYER ||
					possible_openings_list.get(i).getNodeType() == GameState.MAP_WALL) {
				if(possible_openings_list.get(i).getX() < stats.getX() && possible_openings_list.get(i).getY() == stats.getY()) {
					openings[0] = false;
				}
				else if(possible_openings_list.get(i).getX() > stats.getX() && possible_openings_list.get(i).getY() == stats.getY()) {
					openings[1] = false; 
				}
				else if(possible_openings_list.get(i).getX() == stats.getX() && possible_openings_list.get(i).getY() < stats.getY()) {
					openings[2] = false;;
				}
				else if(possible_openings_list.get(i).getX() == stats.getX() && possible_openings_list.get(i).getY() > stats.getY()) {
					openings[3] = false;; 
				}
			}
		}
	}
	
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
	
	/*
	 * 	public UnitAction(Unit unit, int action_type, int target_x, int target_y, int production) {
		this(unit.getID(), action_type, target_x, target_y, production);
		}
	 */
	public void buildWorker() {
		//if building and if base
		int location = getAnyOpening();
		int x = location%map.state.getMapWidth();
		int y = location/map.state.getMapWidth();
		if(location != -1) {
			if(DEBUG) {
				System.out.println("Building Type: BASE, Building Location X: " + stats.getX() + " Y: " + stats.getY() + " building a new WORKER at X: " + x + " Y: " + y);
			}
			if(stats.isBuilding() && stats.getType() == 0 && stats.hasAction() == false) {
				UnitAction action = new UnitAction(this.stats, UnitAction.BUILD, x, y, 1);
				this.stats.setAction(action);
			}
		}
		updateOpenings();
	}
	
	//0 == light
	//2 == heavy
	//3 == archer
	public void buildSoldierUnit(int type) {
		int t = type;
		int location = getAnyOpening();
		int x = location%map.state.getMapWidth();
		int y = location/map.state.getMapWidth();
		if(location != -1) {
			if(DEBUG) {
				System.out.println("Building Type: SOLDIER OFFICE,  Location X: " + stats.getX() + " Y: " + stats.getY() + " building a new FIGHTING UNIT at X: " + x + " Y: " + y);
			}
			if(stats.isBuilding() && stats.getType() == 1 && stats.hasAction() == false) {
				UnitAction action = new UnitAction(this.stats, UnitAction.BUILD, x, y, t);
				this.stats.setAction(action);
			}
		}
		updateOpenings();
	}
	
	public void buildAirUnit(int type) {
		int t = type;
		int location = getAnyOpening();
		int x = location%map.state.getMapWidth();
		int y = location/map.state.getMapWidth();
		if(location != -1) {
			if(DEBUG) {
				System.out.println("Building Type: AIRPORT, Location X: " + stats.getX() + " Y: " + stats.getY() + " building a new FLYING UNIT at X: " + x + " Y: " + y);
			}
			if(stats.isBuilding() && stats.getType() == 0 && stats.hasAction() == false) {
				UnitAction action = new UnitAction(this.stats, UnitAction.BUILD, x, y, t);
				this.stats.setAction(action);
			}
		}
		updateOpenings();
	}
	
	public boolean hasOpening() {
		return(openings[0]||openings[1]||openings[2]||openings[3]);
	}
	public boolean equals(Object n) {
		return true;
		//return stats.getID() == ((Buildings)n).stats.getID();
	}
	public boolean equals(Unit n) {
		return stats.getID() == n.getID();
	}
}
