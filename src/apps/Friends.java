package apps;

/*
 * Friendship Graph Algorithms
 * Fall 2013
 * 
 * Richard Gerdes and Harry Stern
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Scanner;

import structures.PeopleGraph;
import structures.Person;

public class Friends {
	
	PeopleGraph people = new PeopleGraph();
	
	/*
	 * Constructor
	 * 
	 * @param path Path to input file containing graph data.
	 * 
	 */
	
	public Friends(String path) {
		try {
			Scanner sc = new Scanner(new File(path));
			
			int numPeople = 0;
			if(sc.hasNext()){
			numPeople = Integer.parseInt(sc.nextLine());
			}else{
				System.out.println("File is empty");
				System.exit(-1);
			}
			int i = 0;
			while(sc.hasNext() && i < numPeople){
				
				String[] arr = sc.nextLine().split("\\|");
				// System.out.println(arr[0] + " | " + arr[1]);
				arr[0] = arr[0].toLowerCase();
				arr[1] = arr[1].toLowerCase();
				
				if(arr[1].equals("y")){
					arr[2] = arr[2].toLowerCase();
					people.put(arr[0], new Person(arr[0], arr[2]));
				}else{
					people.put(arr[0], new Person(arr[0], ""));
				}
				
				i++;
			}
			
			while(sc.hasNext()){
				String[] arr = sc.nextLine().split("\\|");
				// System.out.println(arr[0] + " | " + arr[1]);
				
				people.addEdge(arr[0].toLowerCase(), arr[1].toLowerCase());
				
			}
			
			sc.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}

	
	/*
	 * Main
	 * 
	 * Runs the menu of options and operations on the Friend graph
	 * 
	 */
	
	public static void main(String[] args){
		
		String path = "";
		File f = new File(path);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		

		try {
			
			while(true){
				System.out.print("What is the name of the input file:");
				
				path = br.readLine();
				
				f = new File(path);
				if(f.exists()){
					break;
				}else{
					System.out.println("That file does not exist! Please try another");
				}
			}
			
			Friends friends = new Friends(path);
			
			while(true){
				System.out.println("Select an option:");
				System.out.println("1) Subgraph of School");
				System.out.println("2) Introduction Chain");
				System.out.println("3) Cliques at school");
				System.out.println("4) Show Connectors");
				System.out.println("5) Quit");
				
				String read = br.readLine();
				if(isNumber(read)){
					int selection = Integer.parseInt(read);
					if(selection == 1){
						//TODO Graph of school
					}else if(selection == 2){
						//TODO Intro Chain
					}else if(selection == 3){
						//TODO Cliques
					}else if(selection == 4){
						//TODO Connectors
					}else if(selection == 5){
						System.out.println("Exiting");
						System.exit(0);
					}else{
						System.out.println("Bad input please try agian.");
					}
					
				}else{
					System.out.println("Bad input please try agian.");
				}
				
				
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	/*
	 * isNumber
	 * 
	 * Determines if an input string is an integer or not. checks each char in the string to see if they are digits
	 * 
	 */
	
	public static boolean isNumber(String str)
	{
	    for (int i = 0; i < str.length(); i++)
	    {
	        if (!Character.isDigit(str.charAt(i)))
	        	return false;
	    }
	    return true;
	}

}