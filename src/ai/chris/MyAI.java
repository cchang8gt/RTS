package ai.chris;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import ai.AI;
import rts.GameState;
import rts.units.Unit;
import rts.units.UnitAction;
import rts.units.UnitStats;
import ai.chris.MyUnits;
import java.util.Random;
/********************************************************************
 * TODO:
 * Create Maps and Nodes
 * Ensure map updates properly
 * Ensure Nodes update properly
 *
 */
public class MyAI extends AI {
	private boolean init;
	public int current_turn;
	private long turn_start;
	private long turn_limit;
	public int player_id;
	public int last_unit;
	private Random rand;
	
	public GameState state;
	public MyMap map;
	int[] map_array;
	
	//states
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
	public static final int STATE_ALL_RESOURCES_CLAIMED = 10;
	public static final int STATE_DEDICATED_EXPLORER = 11;
	public static final int STATE_BUILDING_BARRACKS = 12;
	public static final int STATE_BUILDING_BASE = 13;
	
	//units
	public static final int UNIT_WORKER = 1;
	public static final int UNIT_LIGHT = 0;
	public static final int UNIT_HEAVY = 2;
	public static final int UNIT_ARCHER = 3;
	public static final int UNIT_BIRD = 4;
	public static final int UNIT_SKY = 5;
	
	//buildings
	public static final int BUILDING_BASE = 0;
	public static final int BUILDING_BARRACKS = 1;
	public static final int BUILDING_AIRPORT = 2;

	
	//mic
	public int num_workers = 15;	//zero point in having more workers than resource nodes
	public int dedicated_worker;
	public int num_workers_fog;
	public int worker_count;
	public int fighter_count;
	public int resource_count;
	public int base_count;
	public int barracks_count;
	public static final int max_barracks = 6;
	//TEST
	private ArrayList<Buildings> buildings_list;
	private ArrayList<MyUnits> units_list;
	private ArrayList<Resource> resources_list;
	private ArrayList<Integer> money;
	private ArrayList<Unit> enemy_list;
	private boolean temp = false;
	private boolean loaded = false;
	private Resource r;
	//

	public MyAI() {
		super();
		init = false;
		current_turn = 0;
		last_unit = 0;
		player_id = -1;
		rand = new Random();
	
		buildings_list = new ArrayList<Buildings>();
		units_list = new ArrayList<MyUnits>();
		resources_list = new ArrayList<Resource>();
		money = new ArrayList<Integer>();
		enemy_list = new ArrayList<Unit>();
	}

	public void initialize() {
		player_id = state.getMyUnits().get(0).getPlayer();
		map_array = state.getMap();
		player_id = state.getMyUnits().get(0).getPlayer();
		map = new MyMap(state, map_array);
		
		for(int i = 0; i < state.getResourceTypes(); i++) {
			money.add(state.getResources(i));
		}
		init = true;
		
	}
	
	public void u_resource_no_fog() {
		ResourceComparator c = new ResourceComparator();
		update_resource_list();
		for(int i = 0; i < resources_list.size(); i++) {
			for(int j = 0; j < buildings_list.size(); j++) {
				if(buildings_list.get(j).stats != null && buildings_list.get(j).stats.isStockpile() && buildings_list.isEmpty() == false) {
					resources_list.get(i).distance = map.getManhattanDistance(
							resources_list.get(i).stats.getX()+resources_list.get(i).stats.getY()*state.getMapWidth(), 
								state.getMyUnits().get(0).getX()+state.getMyUnits().get(0).getY()*state.getMapWidth());
				}
			}

		}
		Collections.sort(resources_list, c);
	}
	
	public void getAction(GameState gs, int time_limit) {
		turn_start = System.currentTimeMillis();
		turn_limit = time_limit;
		worker_count = 0;
		barracks_count = 0;
		state = gs;
		current_turn++;
		if(!init) {
			initialize();
		}
		//if not fog then we just need to update resources once
		map.updateMap();
		if(!gs.isFog()) {
			//if(!loaded) {
			u_resource_no_fog();
			//}
		}
		if(gs.isFog()) {
			update_resource_list();
		}
		update_unit_list();
		update_building_list();
		
		for(int i = 0; i < units_list.size(); i++) {
			if(units_list.get(i).stats.isWorker()) {
				worker_count++;
			}
		}
		num_workers_fog = resources_list.size()+1;
		num_workers = 15;
		
		
		
		/*
		System.out.println("RESOURCES LIST: " + resources_list.size());
		for(int i = 0; i < resources_list.size(); i++) {
			//System.out.println(resources_list.get(i));
		}
	
		System.out.println("UNITS: ");
		for(int i = 0; i < units_list.size(); i++) {
			System.out.println(units_list.get(i).stats.getID());
		}
		System.out.println();
	
		System.out.println("BUILDINGS: ");
		for(int i = 0; i < buildings_list.size(); i++) {
			System.out.println(buildings_list.get(i).stats.getLabel());
		}
		System.out.println();
		
		System.out.print("MONEY: ");
		for(int i = 0; i < money.size(); i++) {
			System.out.print(money.get(i) + " ");
		}
		System.out.println();
		*/
		/*
		if(!temp) {
			r = new Resource(state.getNeutralUnits().get(0), map);
			units_list.get(0).resource_to_mine = r;
			units_list.get(0).resource_to_mine.claimed = 4124;
			temp = true;
		}
		*/
		//int path = map.get_path(state.getMyUnits().get(1).getX()+state.getMyUnits().get(1).getY()*state.getMapWidth(), 3+3*state.getMapWidth());
		//state.getMyUnits().get(1).setAction(new UnitAction(state.getMyUnits().get(1), 1, path%state.getMapWidth(), path/state.getMapWidth(), 0));
		
		//System.out.println("RESOURCE: " + r.claimed);
		//System.out.println("RESOURCES:");
		//for(int i = 0; i < state.getNeutralUnits().size(); i++) {
		//	System.out.println(state.getNeutralResources().get)
		//}
		
		//Manage Workers Here
		//Manage Fighters Here
		//Manage Buildings Here
		manage_buildings();
		manage_workers();
		manage_fighters();
		//System.out.println(dedicated_worker);
		//System.out.println("LEGAL ACTIONS: ");
		
		/*
		for(int i = 0; i < buildings_list.size(); i++) {
			if(buildings_list.get(i).stats.getType() == MyAI.BUILDING_BASE) {
				buildings_list.get(i).buildWorker();
			}
		}
		*/
		for(int i = 0; i < money.size(); i++) {
			money.set(i, state.getResources(i));
		}
		//System.out.print("Delta time: ");
		//System.out.println(System.currentTimeMillis()-turn_start);
	}
	
	public void update_unit_list() {
		for(int i = 0; i < units_list.size(); i++) {
			units_list.get(i).exists = false;	
		}
		for(int i = 0; i < state.getMyUnits().size(); i++) {
			if(state.getMyUnits().get(i).isBuilding() == false) {
				boolean found = false;
				for(int j = 0; j < units_list.size(); j++) {
					if(units_list.get(j).equals(state.getMyUnits().get(i))) {
						units_list.get(j).exists = true;
						found = true;
						break;
					}
				}
				if(!found && state.getMyUnits().get(i).isBuilding() == false) {
					MyUnits u = new MyUnits(state.getMyUnits().get(i), map);
					units_list.add(u);
				}
			}
		}
		/*
		 * On death:
		 * Resets resource's claimed variable to -1
		 */
		for(int i = 0; i < units_list.size(); i++) {
			if(units_list.get(i).exists == false) {
				if(units_list.get(i).resource_to_mine != null) {
					units_list.get(i).resource_location = -1;
					units_list.get(i).resource_to_mine.claimed = -1;
					
				}
				/*
				if(units_list.get(i).resource_to_mine.claimed == units_list.get(i).getID()) {
					units_list.get(i).resource_location = -1;
					units_list.get(i).resource_to_mine.claimed = -1;
					System.out.println("KILLED!");
				}
				*/
				units_list.remove(i);
			}
		}
		
		for(int i = 0; i < units_list.size(); i++) {
			if(units_list.get(i).exists == true) {
				if(units_list.get(i).stats.isWorker() && (units_list.get(i).resource_to_mine == null || units_list.get(i).resource_to_mine.stats.getResources() == 0)) {
					//System.out.println("Unit: [RESOURCE_ASSIGNMENT]: " + units_list.get(i).stats.getLabel());
					for(int j = 0; j < resources_list.size(); j++) {
						units_list.get(i).resource_location = resources_list.get(j).getAnyOpening();
						units_list.get(i).resource_to_mine = resources_list.get(j);
					}
				}
			}
		}

	}
	

	public void update_building_list() {
		for(int i = 0; i < buildings_list.size(); i++) {
			if(buildings_list.get(i).stats.getType() == BUILDING_BARRACKS) {
				barracks_count++;
			}
		}
		for(int i = 0; i < buildings_list.size(); i++) {
			buildings_list.get(i).exists = false;
		}
		for(int i = 0; i < state.getMyUnits().size(); i++) {
			if(state.getMyUnits().get(i).isBuilding() == true) {
				boolean found = false;
				for(int j = 0; j < buildings_list.size(); j++) {
					if(buildings_list.get(j).equals(state.getMyUnits().get(i))) {
						buildings_list.get(j).exists = true;
						found = true;
						break;
					}
				}
				if(!found && state.getMyUnits().get(i).isBuilding() == true) {
					Buildings b = new Buildings(state.getMyUnits().get(i), map);
					buildings_list.add(b);
				}
			}
		}
		for(int i = 0; i < buildings_list.size(); i++) {
			if(buildings_list.get(i).exists == false) {
				buildings_list.remove(i);
			}
		}
		
	}
	
	public void update_resource_list() {
		for(int i = 0; i < resources_list.size(); i++) {
			resources_list.get(i).exists = false;
		}
		//resources_list.clear();
		for(int i = 0; i < state.getNeutralUnits().size(); i++) {
			if(state.getNeutralUnits().get(i).isResources() == true) {
				boolean found = false;
				for(int j = 0; j < resources_list.size(); j++) {
					if(resources_list.get(j).equals(state.getNeutralUnits().get(i))) {
						resources_list.get(j).exists = true;
						resources_list.get(j).updateOpenings();
						found = true;
						break;
					}
				}
				if(!found && state.getNeutralUnits().get(i).isResources() == true) {
					Resource r = new Resource(state.getNeutralUnits().get(i), map);
					resources_list.add(r);
				}
			}
		}
		for(int i = 0; i < resources_list.size(); i++) {
			if(resources_list.get(i).exists == false) {
				resources_list.get(i).claimed = -1;
				//System.out.println("CHECK TIME!: " + resources_list.get(i).claimedWorker);
				if(resources_list.get(i).claimedWorker != null) {
					resources_list.get(i).claimedWorker.resource_location = -1;
					resources_list.get(i).claimedWorker.resource_to_mine = null;
				}
				resources_list.remove(i);
			}
		}
		
	}
	
	/*TODO:
	 * fix fog shit
	 */
	public void manage_fighters() {
		
		for(int i = 0; i < units_list.size(); i++) {
			int u = 0;
			if(units_list.get(i).stats != null) {
				//Look for a unit to attack
				ArrayList<UnitAction> ua = new ArrayList<UnitAction>();
				ArrayList<UnitAction> mov = new ArrayList<UnitAction>();
				if(!units_list.get(i).stats.isWorker()) {
					//loop actions
					if(current_turn%5 == 2 || current_turn%5 == 3 || current_turn%5 == 4) {
						units_list.get(i).state = STATE_ATTACK; 
					}
					else if(current_turn%5 == 1 || current_turn%5==0) {
						units_list.get(i).state = STATE_MOVE;
					}
					for(int j = 0; j < units_list.get(i).stats.getActions().size(); j++) {
						if(units_list.get(i).stats.getActions().get(j).getType() == UnitAction.ATTACK) {
							ua.add(units_list.get(i).stats.getActions().get(j));
						}
						else if(units_list.get(i).stats.getActions().get(j).getType() == UnitAction.MOVE) {
							mov.add(units_list.get(i).stats.getActions().get(j));
						}
					}
					for(int j = 0; j < ua.size(); j++) {
						if(units_list.get(i).state == STATE_ATTACK) {
							int tarx = ua.get(j).getTargetX();
							int tary = ua.get(j).getTargetY();
							if(map.getNode(tarx, tary).getNodeType() == 16) {
								if(units_list.get(i).stats.hasAction() == false) {
									units_list.get(i).stats.setAction(ua.get(j));
									
								}
							}
						}
					}
					
					for(int j = 0; j < mov.size(); j++) {
						if(units_list.get(i).state == STATE_MOVE) {
							for(int k = 0; k < mov.size(); k++) {
								if(!mov.isEmpty()) {
									UnitAction a = mov.get(rand.nextInt(mov.size()));
									if(units_list.get(i).stats.hasAction() == false) {
										//units_list.get(i).stats.setAction(new UnitAction(units_list.get(i).stats, UnitAction.MOVE, 1, 1, 0));
										/*
										int paff = map.get_path(units_list.get(i).stats.getX()+units_list.get(i).stats.getY()*state.getMapWidth(), 2+2*state.getMapWidth());
										if(paff != -1) {
											units_list.get(i).stats.setAction(new UnitAction(units_list.get(i).stats, UnitAction.MOVE, paff%state.getMapWidth(), paff/state.getMapWidth(), 0));
										}
										*/
										units_list.get(i).stats.setAction(a);
									}
								}
							}
						}
					}
					
				}
				//System.out.println("X: " + u%state.getMapWidth() + " Y: " + u/state.getMapWidth());
				
			}
		}
	}
	
	
	public void manage_workers() {
		dedicated_worker = 0;
		/*
		 * If there is a dedicated worker, mark it as such
		 */
		for(int i = 0; i < units_list.size(); i++) {
			if(units_list.get(i).stats.isWorker()) {
				if(units_list.get(i).state == STATE_DEDICATED_EXPLORER) {
					dedicated_worker++;
				}
			}
		}
		
		/*
		 * If there are no dedicated workers, create a dedicated worker
		 */

		for(int i = 0; i < units_list.size(); i++) {
			if(dedicated_worker == 0) {
				if(units_list.get(i).stats.isWorker()) {
					units_list.get(i).state = STATE_DEDICATED_EXPLORER;
					dedicated_worker++;
				}			
			}

		}
		
		for(int i = 0; i < units_list.size(); i++) {
			if(units_list.get(i).state == STATE_DEDICATED_EXPLORER && units_list.get(i).stats.isWorker()) {
				if(units_list.get(i).stats != null) {
					ArrayList<UnitAction> ua = new ArrayList<UnitAction>();
					for(int j = 0; j < units_list.get(i).stats.getActions().size(); j++) {
						if(units_list.get(i).stats.getActions().get(j).getType() == UnitAction.MOVE){
							ua.add(units_list.get(i).stats.getActions().get(j));
						}
					}
					if(ua.size() != 0) {
						units_list.get(i).stats.setAction(ua.get(rand.nextInt(ua.size())));
					}
				}
			}
		}
		int to_x = state.getMapWidth()/2;
		int to_y = state.getMapHeight()/2;
		for(int i = 0; i < units_list.size(); i++) {
			if(units_list.get(i).stats != null) {
				if(money.get(0) > 15 && barracks_count < max_barracks && units_list.get(i).state != STATE_DEDICATED_EXPLORER) {
					for(int j = 0; j < buildings_list.size(); j++) {
						if(buildings_list.get(j).stats.isStockpile()) {
							if(map.getManhattanDistance(units_list.get(i).stats.getX()+units_list.get(i).stats.getY()*state.getMapWidth(), 
									buildings_list.get(j).stats.getX()+buildings_list.get(j).stats.getY()*state.getMapWidth()) > 15) {
								//System.out.println("BUILDING BARRACKS");
								units_list.get(i).state = STATE_BUILDING_BARRACKS;
							}
						}
					}
				}
				if(units_list.get(i).state == STATE_BUILDING_BARRACKS && units_list.get(i).stats.isWorker()) {
					if(money.get(0) > 30) {
						int t_x = 0;
						int t_y = 0;
						for(int j = 0; j < units_list.get(i).stats.getActions().size(); j++) {
							//System.out.println(units_list.get(i).stats.getActions().get(j));
							if(units_list.get(i).stats.getActions().get(j).getType() == UnitAction.BUILD) {
								t_x = units_list.get(i).stats.getActions().get(j).getTargetX();
								t_y = units_list.get(i).stats.getActions().get(j).getTargetY();
							}
							if(units_list.get(i).stats.hasAction() == false) {
								//System.out.println("BUILDING BARRACKS NOW");
								units_list.get(i).stats.setAction(new UnitAction(units_list.get(i).stats, UnitAction.BUILD, t_x, t_y, BUILDING_BARRACKS));
								//resources_list.clear();
							}
						}
					}
					u_resource_no_fog();
					update_building_list();
					update_unit_list();
					if(units_list.get(i).stats.getResources() != 0) {
						units_list.get(i).state = STATE_MINE;
					}
					else {
						units_list.get(i).state = STATE_IDLE;
					}
				}
				if(units_list.get(i).stats.isWorker()) {
					units_list.get(i).updatePosition();
					if(units_list.get(i).get_resource_location() == -1) {
						for(int j = 0; j < resources_list.size(); j++) {
							/*
							 * If resource claimed is -1 <-- supported by dead worker
							 * If fresh unit's resource location is -1 <-- supported although should encapsulate this block inside a state
							 * If the resource has an opening
							 */
							if(resources_list.get(j).claimed == -1 && units_list.get(i).get_resource_location() == -1 && resources_list.get(j).hasOpening()) {
								resources_list.get(j).claimed = units_list.get(i).stats.getID();
								units_list.get(i).resource_to_mine = resources_list.get(j);
								units_list.get(i).resource_to_mine.claimedWorker = units_list.get(i);
								units_list.get(i).resource_location = resources_list.get(j).getAnyOpening();
								
							}
						}
						//If the unit STILL doesn't have a resource..
						//System.out.println("TRYING TO FIND A RESOURCE. LAST POSSIBLE CHANCE!!!!! " + units_list.get(i).resource_location);
						if(units_list.get(i).resource_location == -1) {
							//System.out.println("ERROR: ALL RESOURCES CLAIMED");
							units_list.get(i).state = STATE_ALL_RESOURCES_CLAIMED;
						}
					}
				}
			}
			//units_list.get(i).work();
		}
		int path;
		int base_loc;
		for(int i = 0; i < units_list.size(); i++) {
			if(units_list.get(i).stats != null) {
				if(units_list.get(i).state == STATE_ALL_RESOURCES_CLAIMED) {
					if(units_list.size() < resources_list.size() -1) {
						resources_list.clear();
					}
				}
				if(units_list.get(i).stats.getResources() == 0) {
					//System.out.println(units_list.get(i).stats.lastActionSucceeded());

					/*
					 * If the worker is IDLE or has RESOURCSE FOUND
					 * Then move towards resource
					 */
					if(units_list.get(i).state == STATE_IDLE || units_list.get(i).state == STATE_FOUND_RESOURCE) {
						if(units_list.get(i).resource_to_mine != null) {
							if(units_list.get(i).stats.hasAction() == false&& units_list.get(i).stats != null && units_list.get(i).resource_location != -1) {
								//System.out.println(units_list.get(i).resource_location);
								path = map.get_path(units_list.get(i).stats.getX()+units_list.get(i).stats.getY()*state.getMapWidth(), units_list.get(i).resource_location);
								if(path != -1) {
									units_list.get(i).stats.setAction(new UnitAction(units_list.get(i).stats, UnitAction.MOVE, path%map.state.getMapWidth(), path/map.state.getMapWidth(), 0));
								}
							}
						}
					}
					/*
					 * If the worker is next to the resource
					 * Then harvest it 
					 */
					if(units_list.get(i).stats.isWorker()) { 
						for(int j = 0; j < units_list.get(i).stats.getActions().size(); j++) {
							if(units_list.get(i).stats.getActions().get(j).getType() == UnitAction.HARVEST && units_list.get(i).stats.getResources() == 0) {
								units_list.get(i).stats.setAction(units_list.get(i).stats.getActions().get(j));
								units_list.get(i).state = STATE_MINE;
							}
						}
					}
					
					/*
					if(units_list.get(i).stats.lastActionSucceeded() == false) {
						units_list.get(i).state = STATE_STUCK;
						for(int j = 0; j < buildings_list.size(); j++) {
							base_loc = buildings_list.get(j).getAnyOpening();
							buildings_list.get(j).updateOpenings();
							path = map.get_path(units_list.get(i).stats.getX()+units_list.get(i).stats.getY()*state.getMapWidth(), 
									base_loc%state.getMapWidth()+base_loc/state.getMapWidth()*state.getMapWidth());
							units_list.get(i).stats.setAction(new UnitAction(units_list.get(i).stats, UnitAction.MOVE, path%map.state.getMapWidth(), path/map.state.getMapWidth(), 0));
						}
					}
					*/
				}
				else if (units_list.get(i).stats.getResources() != 0) {
					/*
					 * If the worker has resources and is in state STATE_MINE
					 * Return it to base
					 */
					//System.out.println("RESOURCES!!!!: " + units_list.get(i).stats.getResources());
					if(units_list.get(i).state == STATE_MINE) {
						for(int j = 0; j < buildings_list.size(); j++) {
							if(buildings_list.get(j).stats.isStockpile()) {
								base_loc = buildings_list.get(j).getAnyOpening();
								buildings_list.get(j).updateOpenings();
								if(base_loc != -1) {
									if(units_list.get(i).stats.hasAction() == false) {
										path = map.get_path(units_list.get(i).stats.getX()+units_list.get(i).stats.getY()*state.getMapWidth(), 
											base_loc%state.getMapWidth()+base_loc/state.getMapWidth()*state.getMapWidth());
										units_list.get(i).stats.setAction(new UnitAction(units_list.get(i).stats, UnitAction.MOVE, path%map.state.getMapWidth(), path/map.state.getMapWidth(), 0));
									
									}
								}
							}
						}
					}
					for(int j = 0; j < units_list.get(i).stats.getActions().size(); j++) {
						if(units_list.get(i).stats.getActions().get(j).getType() == UnitAction.RETURN){
							units_list.get(i).stats.setAction(units_list.get(i).stats.getActions().get(j));
							if(units_list.get(i).resource_location != -1) {
								units_list.get(i).state = STATE_FOUND_RESOURCE;
							}
							else {
								units_list.get(i).state = STATE_ALL_RESOURCES_CLAIMED;
							}

						}
					}
					/*
					 * If the worker is at the base and has resources and is at STATE_RETURN
					 * Deposit the resource and set state to STATE_FOUND_RESOURCE
					 */
				}
			}
		}
	}
	
	public void resetWorkersResources(MyUnits u) {
		if(u.exists){
			u.resource_location = -1;
			u.resource_to_mine.claimed = -1;
			u.resource_to_mine = null; //be careful of this,l
		}
	}
	
//	state.getMyUnits().get(0).setAction(new UnitAction(state.getMyUnits().get(0), 5, state.getMyUnits().get(0).getX()-1, state.getMyUnits().get(0).getY(), 1));
	public void manage_buildings() {
		//workers
		for(int i = 0; i < buildings_list.size(); i++) {
			buildings_list.get(i).updateOpenings();
			if(buildings_list.get(i).stats != null && buildings_list.get(i).stats.isStockpile()) {
				int loc = buildings_list.get(i).getAnyOpening();
				if(loc != -1) {
					if(state.isFog()) {
						if(worker_count < num_workers_fog) {
							buildings_list.get(i).buildWorker();
						}
					}
					else {
						if(worker_count < num_workers) {
							buildings_list.get(i).buildWorker();
						}
					}
				}
			}
		}
		
		//barracks
		for(int i = 0; i < buildings_list.size(); i++) {
			if(buildings_list.get(i).stats != null && buildings_list.get(i).stats.isStockpile() == false) {
				int loc = buildings_list.get(i).getAnyOpening();
				if(loc != -1) {
					buildings_list.get(i).buildSoldierUnit(UNIT_HEAVY);
				}
			}
		}
	}
	public Buildings getABuilding(int n) {
		for(int i = 0; i < buildings_list.size(); i++) {
			if(buildings_list.get(i).stats.getType() == n) {
				return buildings_list.get(i);
			}
		}
		return null;
	}
}
