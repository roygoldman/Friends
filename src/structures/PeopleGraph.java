package structures;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Stack;

import structures.AdjacencyLinkedList;


public class PeopleGraph {
	HashMap<String, Integer> nameToIndex = new HashMap<String, Integer>();
	ArrayList<Person> nodes;
	AdjacencyLinkedList adjLL; 
	
	public PeopleGraph() {
		this.nodes = new ArrayList<Person>();
		this.adjLL = new AdjacencyLinkedList();
	}
	
	public Person get(int index) {
		return this.nodes.get(index);
	}
	
	public Person get(String name) {
		Integer index = this.nameToIndex.get(name);
		if (index == null) {
			return null;
		}
		else {
			return this.get(index);
		}
	}
	
	public void put(Person person) {
		int index = nodes.size();
		this.nameToIndex.put(person.getName(), index); 
		this.nodes.add(person);
	}
	
	public PersonNode getNeighbor(String name) {
		return this.adjLL.get(this.get(name));
	}
	
	public PersonNode getNeighbor(Person person) {
		return this.getNeighbor(person.getName());
	}
	
	public void addEdge(String name1, String name2) {
		Person p1 = this.get(name1);
		Person p2 = this.get(name2);


		this.adjLL.addEdge(p1, p2);
		this.adjLL.addEdge(p2, p1);
				
	}
	
	// for convenience
	public void addEdge(Person p1, Person p2) {
		this.addEdge(p1.getName(), p2.getName());
	}
	
	public String toString() {
		String ret = "";
		ret += this.nodes.size();
		for (Person person: this.nodes) {
			ret += "\n" + person;
		}
		ArrayList<String> lines = new ArrayList<String>();
		for (Person person: this.nodes) {
			for (PersonNode neighbor = getNeighbor(person); neighbor != null; neighbor = neighbor.next) {
				String forwards = person.getName() + "|" + neighbor.person.getName();
				String backwards = neighbor.person.getName() + "|" +person.getName();
				if ((!lines.contains(backwards)) && (!lines.contains(forwards))) {
					lines.add(forwards);
				}
			}
		}
		for (String line: lines) {
			ret += "\n" + line;
		}
		return ret;
	}
	
	public PeopleGraph getSchoolSubgraph(String school) {
		PeopleGraph subgraph = new PeopleGraph();
		
		// it would probably be easier for this to be split into multiple functions
		for (Person p: this.nodes) {
			Queue<Person> q = new ArrayDeque<Person>();
			if ((subgraph.get(p.getName()) == null) && (p.getSchool().equals(school))) {
				q.add(p);
				subgraph.put(p);
				while (!q.isEmpty()) {					
					Person person = q.remove();
					PersonNode neighbor = this.getNeighbor(person);
					while (neighbor != null) {
						if (neighbor.person.getSchool().equals(school)) {
							if (subgraph.get(neighbor.person.getName()) == null) {
								q.add(neighbor.person);
								subgraph.put(neighbor.person);
							}
							subgraph.addEdge(person, neighbor.person);
						}
						neighbor = neighbor.next;
					}
				}
			}
		}
		
		return subgraph;
	}
	
	private Person selectSmallest(HashMap<Person, Integer> distances) {
		Person closest_person = null;
		Integer smallest_distance = null; // could use a double that has an infinity but whatever.
		for (Entry<Person, Integer> current : distances.entrySet()) {
			if (smallest_distance == null) {
				closest_person = current.getKey();
				smallest_distance = current.getValue();
			}
			else if (current.getValue() == null) {
				continue;
			}
			else if (smallest_distance > current.getValue()) {
				closest_person = current.getKey();
				smallest_distance = current.getValue();				
			}
		}
		return closest_person;
	}
	
	public HashMap<Person, Person> runDijkstra(Person initial, Person destination) {
		HashMap<Person, Person> prev = new HashMap<Person, Person>();

		ArrayList<Person> fringe = new ArrayList<Person>();
		
		HashMap<Person, Integer> distances = new HashMap<Person, Integer>();
		HashMap<Person, Boolean> done = new HashMap<Person, Boolean>();

		for (Person node: this.nodes) {
			distances.put(node, null); //infinity
			done.put(node, false);
		}		
		
		prev.put(initial, initial);
		done.put(initial, true);
		distances.remove(initial);
		
		PersonNode init_neighbor = this.getNeighbor(initial);
		while (init_neighbor != null) {
			distances.put(init_neighbor.person, 1);
			prev.put(init_neighbor.person, initial);
			fringe.add(init_neighbor.person);
			
			init_neighbor = init_neighbor.next;
		}
		
		while (!fringe.isEmpty()) {
			Person smallest = this.selectSmallest(distances);
			
			PersonNode neighborNode = this.getNeighbor(smallest);
			Person neighbor;
			while (neighborNode != null) {
				neighbor = neighborNode.person;
				
				if (done.get(neighbor)) {
					neighborNode = neighborNode.next;
					continue;
				}
				else if (distances.get(neighbor) == null) {
					distances.put(neighbor, distances.get(smallest)+1);
					prev.put(neighbor, smallest);
					fringe.add(neighbor);
				}
				else if (distances.get(neighbor) > distances.get(smallest)+1) {
					distances.put(neighbor, distances.get(smallest)+1);
					prev.put(neighbor, smallest);
				}
				
				neighborNode = neighborNode.next;
			}
			
			fringe.remove(smallest);
			distances.remove(smallest);
			done.put(smallest, true);
						
			if (smallest == destination) {
				return prev;
			}
			
		}
		
		
		return prev;
	}
	
	public ArrayList<Person> shortestPath(String name1, String name2) {
		Person p1 = this.get(name1);
		Person p2 = this.get(name2);
		
		if (p1 == null || p2 == null) {
			return null;
		}
		
		HashMap<Person, Person> dijkstraPrev = this.runDijkstra(p1, p2);
		
		Deque<Person> stack = new ArrayDeque<Person>();
		Person prev = dijkstraPrev.get(p2);
		
		if (prev == null) {
			return null;
		}
		
		stack.push(prev);
		while (prev != p1) {
			prev = dijkstraPrev.get(prev);
			stack.push(prev);
		}
		
		ArrayList<Person> shortestPath = new ArrayList<Person>();
		while (!stack.isEmpty()) {
			shortestPath.add(stack.pop());
		}
		shortestPath.add(p2);
		return shortestPath;
	}
	
	public ArrayList<PeopleGraph> getCliques() {
		ArrayList<PeopleGraph> cliques = new ArrayList<PeopleGraph>();
		for (Person p: this.nodes) {
			boolean skip = false;
			for(PeopleGraph pg : cliques){
				if (pg.get(p.getName()) != null){
					skip = true;
				}
			}
			if(skip){
				continue;
			}
			PeopleGraph clique = new PeopleGraph();
			Queue<Person> q = new ArrayDeque<Person>();
			if (clique.get(p.getName()) == null) {
				q.add(p);
				clique.put(p);
				while (!q.isEmpty()) {					
					Person person = q.remove();
					for (PersonNode neighbor = this.getNeighbor(person); neighbor != null; neighbor = neighbor.next) {
						if (clique.get(neighbor.person.getName()) == null) {
							q.add(neighbor.person);
							clique.put(neighbor.person);
						}
						clique.addEdge(person, neighbor.person);
					}
				}
			}
			cliques.add(clique);
		}
		return cliques;
	}
	
	public ArrayList<Person> getConnectors(){

		ArrayList<Person> connectors = new ArrayList<Person>();
		HashMap<Person, Integer> dfsnum = new HashMap<Person, Integer>();
		HashMap<Person, Integer> back = new HashMap<Person, Integer>();

		for(Person p : this.nodes){
			if(dfsnum.get(p) == null){
				dfs(p, dfsnum, back, connectors, 1);
			}
		}
		
		return connectors;
		
	}
	
	private int dfs(Person s, HashMap<Person, Integer> dfs, HashMap<Person, Integer> back, ArrayList<Person> connectors, Integer nextdfs){
		if(dfs.get(s) != null){
			return nextdfs;
		}
		
		dfs.put(s, nextdfs);
		back.put(s, nextdfs);
		nextdfs++;
		
		boolean sso = true;
		
		PersonNode n = this.getNeighbor(s);
		while(n != null){
			int lastdfs = nextdfs;
			nextdfs = dfs(n.person, dfs, back, connectors, nextdfs);
			if(lastdfs == nextdfs){
				if(back.get(s) > dfs.get(n.person)){
					back.put(s, dfs.get(n.person));
				}
			}else{
				if(dfs.get(s) <= back.get(n.person)){
					if(dfs.get(s) == 1){
						if(sso){
							sso = false;
						}else{
							if(!connectors.contains(s)){
								connectors.add(s);
							}
						}
					}else if(!connectors.contains(s)){
						connectors.add(s);
					}
				}else{
					if(back.get(s) > back.get(n.person)){
						back.put(s, back.get(n.person));
					}
				}
			}
			n = n.next;
		}
		return nextdfs;
	}
	
}
