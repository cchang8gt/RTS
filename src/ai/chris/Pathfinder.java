package ai.chris;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class Pathfinder {
	public MyMap map;
	public Pathfinder(MyMap map) {
		this.map = map;
	}
	public Integer get_path(int start, int goal) {
		int startx = start%map.state.getMapWidth();
		int starty = start/map.state.getMapWidth();
		
		int endx = goal%map.state.getMapWidth();
		int endy = goal/map.state.getMapWidth();
		
		Node start_node = map.getNode(startx, starty);
		Node goal_node = map.getNode(endx, endy);
		ArrayList<Node> temp = new ArrayList<Node>();
		
		goal_node.setGoal(true);
		
		Node current_node = start_node;
		
		PriorityQueue<Node> open = new PriorityQueue<Node>();
		ArrayList<Node> open_set = new ArrayList<Node>();
		ArrayList<Node> closed_set = new ArrayList<Node>();
		temp = current_node.getNeighbors();
		
		//MAKE SURE TO GET NODE BY (COL, ROW) 
		for(int i = 0; i < 32; i++) {
			System.out.println(map.getNode(i, 1));
		}
		while(open_set.size() != 0) {
			if(current_node.isGoal()) {
				//return reconstruct_path(came_from, goal)
			}
		}
		
		System.out.println();
		return null;
		
		//return open_set.get(0).getLocation();
	}
	

	
	public void resetNodes() {
		
	}
}
