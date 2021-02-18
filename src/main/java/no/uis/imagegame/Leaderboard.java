package no.uis.imagegame;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import no.uis.players.Player;
import no.uis.players.PlayerRepository;


class playerSinglePcomp implements Comparator<Player> {
	@Override
    public int compare(Player p1, Player p2) 
    {
		if (p1.getSingleplayerscore() == p2.getSingleplayerscore()) {
			if (p1.getSp_score_time_stamp() > p2.getSp_score_time_stamp()) {
				return 1;
			}
			return -1;
		}
		return p1.getSingleplayerscore() - p2.getSingleplayerscore();
    }
}

class playerMultiPcomp implements Comparator<Player> {
	@Override
    public int compare(Player p1, Player p2) 
    {
		if (p1.getMultiplayerscore() == p2.getMultiplayerscore()) {
				return 1;
		}
		return p1.getMultiplayerscore() - p2.getMultiplayerscore();
    }
}

public class Leaderboard {
    ArrayList<Player> sortedSinglePlayer = new ArrayList<Player>();
    ArrayList<Player> sortedMultiPlayer = new ArrayList<Player>();
	ArrayList<Player> base = new ArrayList<Player>();
	PlayerRepository entryRepository;
	
	public Leaderboard(PlayerRepository entryRepository) {
		this.entryRepository = entryRepository;
	}
	
	public Boolean playersExist() {
		if(entryRepository == null) {
			return false;
		}
		return true;	
	}
	
	public ArrayList<Player> getAllplayers(){
		ArrayList<Player> allPlayers = new ArrayList<Player>();
		Iterable<Player> p = entryRepository.findAll();
		Iterator<Player> test = p.iterator();
		while (test.hasNext()) {
        	Player curr_p =(Player) test.next();
        	allPlayers.add(curr_p);
		}
		return allPlayers;
	}
	
	public ArrayList<String> stringForHTML(ArrayList<Player> players, String scoretype) {
		ArrayList<String> isString = new ArrayList<String>();
		int length = 10;
		if(players.size() < 10) {
			length = players.size();
		}
		
		if (scoretype.contentEquals("SP")) {
			for (int i=0; i<length; i++) {
				String inputString = new String();
				inputString =String.valueOf(players.get(i).getSingleplayerscore()  + ": " + players.get(i).getUsername());
				isString.add(inputString);
			}
			return isString;
		}
		if (scoretype.contentEquals("MP")) {
			for (int i=0; i<length; i++) {
				String inputString = new String();
				inputString = String.valueOf(players.get(i).getMultiplayerscore() + ": " + players.get(i).getUsername());
				isString.add(inputString);
			}
			return isString;
		}
		return null;
	}

	
	public ArrayList<String> singlePlayerArray() {
	    ArrayList<Player> base = getAllplayers();
		TreeSet<Player> fromDatabase_SP = new TreeSet<Player>(new playerSinglePcomp());
		for (int i=0; i < base.size(); i++) {
			if (!(base.get(i).getSingleplayerscore() == 0)) {
				fromDatabase_SP.add(base.get(i));
			}
		}
		
		ArrayList<Player> singleplayerNotSorted = new ArrayList<Player>();
        Iterator<Player> intoList_SP = fromDatabase_SP.iterator();
        while (intoList_SP.hasNext()) {
        	Player currentUser =(Player) intoList_SP.next();
        	singleplayerNotSorted.add(currentUser);
        }
        
        ArrayList<String> sortedSinglePlayer = stringForHTML(singleplayerNotSorted, "SP");
		return sortedSinglePlayer;
	}
	

	public ArrayList<String> multiPlayerArray() {
	    ArrayList<Player> base = getAllplayers();
		TreeSet<Player> fromDatabase_MP = new TreeSet<Player>(new playerMultiPcomp());
		for (int i=0; i < base.size(); i++) {
			if (!(base.get(i).getMultiplayerscore() == 0)) {
				fromDatabase_MP.add(base.get(i));
			}
		}
	    
		ArrayList<Player> multiplayerNotSorted = new ArrayList<Player>();
        Iterator<Player> intoList_MP = fromDatabase_MP.iterator();
        while (intoList_MP.hasNext()) {
        	Player currentUser =(Player) intoList_MP.next();
        	multiplayerNotSorted.add(currentUser);
        }
        
        ArrayList<String> sortedMultiPlayer = stringForHTML(multiplayerNotSorted, "MP");
		return sortedMultiPlayer;
	}

}
