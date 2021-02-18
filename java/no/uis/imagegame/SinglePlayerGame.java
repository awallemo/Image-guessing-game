package no.uis.imagegame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.springframework.core.io.Resource;

public class SinglePlayerGame extends Game {
	private String player;
	private ArrayList<Integer> avaliableImages;
	private String gameMessage;
	
	public SinglePlayerGame(ImageLabelReader labelReader, Resource[] resources, boolean randommode) {
		super(labelReader, resources);
		gameMessage = "Singleplayer. Begin guessing!";
		avaliableImages = new ArrayList<Integer>();
		
		if(randommode) {
			initrandommode();
		}else {
			initnormalmode();
		}
		this.addImg();
		
	}
	
	private void initrandommode() {
		for(int i=0;i<49;i++) {
			avaliableImages.add(i);
		}
		Collections.shuffle(avaliableImages, new Random()); 
	}

	private void initnormalmode() {
		Moves movesa = new Moves();
		System.out.println("||"+this.getCorrectLabel());
		try {
			avaliableImages=movesa.get_moves(this.getCorrectLabel());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	public boolean gameIsOver() {
		return avaliableImages.isEmpty();
	}
	
	
	public void addImg() {
		this.addImg("images/scattered_images/" + this.getImageFolderName() + "/" + this.getNextProposal() + ".png");
	}
	
	public String getGameMessage() {
		return gameMessage;
	}
	
	public boolean performGuess(String guess) {
		if(guess.equals(this.getCorrectLabel())) {
			this.incGuesses();
			gameMessage = "Correct! You used " + this.getGuesses() + " guesses";
			return true;
		}
		if(!guess.equals("")) {
			this.addGuess(guess);
		}
		this.incGuesses();
		gameMessage = "Wrong! You've used " + this.getGuesses() + " guesses";
		this.addImg();
		return false;
	}
	
	public String getPlayer() {
		return player;
	}


	public int getNextProposal() {
		return avaliableImages.remove(0);
	}
	
	

}
