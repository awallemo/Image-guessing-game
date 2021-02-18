package no.uis.imagegame;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class Moves {
	private HashMap<String, Move> records;

	public Moves() {
		records = new HashMap<>();
		loaddata();
	}

	class Move {
		int highscore;
		String labelID;
		String label;
		ArrayList<Integer> moveorder;

		public Move(int highscore, String labelID, String label, ArrayList<Integer> moveorder) {
			this.highscore = highscore;
			this.labelID = labelID;
			this.label = label;
			this.moveorder = moveorder;
		}
	}

	public ArrayList<Integer> get_moves(String label) throws IOException {
		if (records.containsKey(label)){
			return records.get(label).moveorder;
		}
		else{
			try {
				FileWriter addLine = new FileWriter(new File("src/main/resources/static/label/Image_order.csv"), true);
				ArrayList<Integer> createNewOrder = new ArrayList<>();
				for (int i = 1; i < 51; i++) {
					createNewOrder.add(i);
				}
				Collections.shuffle(createNewOrder);
				String stringCreateNewOrder = Arrays.toString(createNewOrder.toArray()).replace("[", "").replace("]", "").replace(" ", "");
				StringBuilder newLine = new StringBuilder();
				newLine.append("\nlabelID:" + label + ":" + stringCreateNewOrder + ":100");
				addLine.write(newLine.toString());
				addLine.close();
				Move value = new Move(100, "labelID", label, createNewOrder);
				records.put(label, value);
				addLine.close();
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			loaddata();
			return get_moves(label);
		}
	}

	private void loaddata(){
		try {
			Scanner imageScanner = new Scanner(new File("src/main/resources/static/label/Image_order.csv"));
			while(imageScanner.hasNextLine()) {
				String line = imageScanner.nextLine();
				String[] splittedLine = line.split(":", 4);
				String imageID = splittedLine[0];
				String imageLabel = splittedLine[1];
				String imageStringOrder = splittedLine[2];
				String imageStringScore = splittedLine[3];
				int imageScore = Integer.parseInt(imageStringScore);
				String[] arr = imageStringOrder.split(",");
				ArrayList<Integer> imageOrder = new ArrayList<>();
				for(String s: arr){
					imageOrder.add(Integer.parseInt(s));
				}
				Move create = new Move(imageScore, imageID, imageLabel, imageOrder);
				this.records.put(imageLabel, create);
			}
			imageScanner.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void update(String label, int score, ArrayList<Integer> newOrder) throws IOException {
		if (records.containsKey(label) == false){
				get_moves(label);
		}
		if (score < records.get(label).highscore){
			Collections.reverse(newOrder);
			records.get(label).highscore = score;
			for (Integer i: newOrder){
				records.get(label).moveorder.remove(records.get(label).moveorder.indexOf(i));
				records.get(label).moveorder.add(0, i);
			}
			try {
				FileWriter addLine = new FileWriter(new File("src/main/resources/static/label/Image_order.csv"), false);
				int counter = 0;
				for(String i : records.keySet()){
					counter++;
					String addID = records.get(i).labelID;
					String addLabel = records.get(i).label;
					String addOrder = Arrays.toString(records.get(i).moveorder.toArray()).replace("[", "").replace("]", "").replace(" ", "");
					int addHighscore = records.get(i).highscore;
					String newLine = new String(addID + ":" + addLabel + ":" + addOrder + ":" + addHighscore);
					if (counter < records.size()){
						newLine = newLine + "\n";
					}
					addLine.write(newLine);
				}
				addLine.close();
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		else if (score < 11){
			removeRandomMoves(label, score, newOrder);
		}	
	}

	public void removeRandomMoves(String label, int worseScore, ArrayList<Integer> worseOrder) throws IOException {
		int currentHighscore = records.get(label).highscore;
		ArrayList<Integer> bestOrder = new ArrayList<Integer>();
		for (int i = 0; i < currentHighscore; i++){
			bestOrder.add(records.get(label).moveorder.get(i));		}
		Collections.reverse(bestOrder);
		Collections.reverse(worseOrder);
		for (Integer i: worseOrder){
			records.get(label).moveorder.remove(records.get(label).moveorder.indexOf(i));
			records.get(label).moveorder.add(0, i);
		}
		for (Integer i: bestOrder){
			records.get(label).moveorder.remove(records.get(label).moveorder.indexOf(i));
			records.get(label).moveorder.add(0, i);
		}
		try {
			FileWriter addLine = new FileWriter(new File("src/main/resources/static/label/Image_order.csv"), false);
			int counter = 0;
			for(String i : records.keySet()){
				counter++;
				String addID = records.get(i).labelID;
				String addLabel = records.get(i).label;
				String addOrder = Arrays.toString(records.get(i).moveorder.toArray()).replace("[", "").replace("]", "").replace(" ", "");
				int addHighscore = records.get(i).highscore;
				String newLine = new String(addID + ":" + addLabel + ":" + addOrder + ":" + addHighscore);
				if (counter < records.size()){
					newLine = newLine + "\n";
				}
				addLine.write(newLine);
			}
			addLine.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}