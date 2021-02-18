package no.uis.imagegame;

import java.util.ArrayList;
import java.util.Random;

import org.springframework.core.io.Resource;


public class Game {
	
	private int guesses;
	private ArrayList<String> images;
	private Random rand;
	protected ImageLabelReader labelReader;
	protected Resource[] resources;
	private String correctLabel;
	private String[] files;
	private String imageFolderName;
	private ArrayList<String> imageLabels;
	private ArrayList<String> prevGuesses;

	public int getGuesses() {
		return guesses;
	}

	public ArrayList<String> getImages() {
		return images;
	}

	public String getCorrectLabel() {
		return correctLabel;
	}

	public String getImageFolderName() {
		return imageFolderName;
	}
		
	public Game(ImageLabelReader labelReader, Resource[] resources) {
		this.resources=resources;
		this.labelReader=labelReader;
		this.rand = new Random();
		this.guesses=0;
		this.images=new ArrayList<String>();
		
		imageLabels = getAllLabels(this.labelReader);
		
		prevGuesses = new ArrayList<String>();
		
		int randint = this.rand.nextInt(imageLabels.size()-1);
		this.correctLabel = imageLabels.get(randint);
		this.files = labelReader.getImageFiles(correctLabel);
		imageFolderName = getImageFolder(files);
		System.out.println("Correct: "+correctLabel); //just to cheat in cmd while developing
		
	}
	
	public void addGuess(String addedguess) {
		prevGuesses.add(addedguess);
	}

	public ArrayList<String> getPrevGuesses(){
		return prevGuesses;
	}

	public void incGuesses() {
		guesses++;
	}
	
	public void addImg(String imgpath) {
		images.add(imgpath);
	}
	
	private ArrayList<String> getAllLabels(ImageLabelReader ilr) {
		ArrayList<String> labels = new ArrayList<String>();
		for (Resource r : this.resources) {
			String fileName = r.getFilename();
			String fileNameCorrected = fileName.substring(0, fileName.lastIndexOf('_'));
			String label = ilr.getLabel(fileNameCorrected);
			labels.add(label);
		}
		return labels;
	}
	
	private String getImageFolder(String[] files) {
		String imageFolderName = "";
		for (String file : files) {
			String folderName = file + "_scattered";
			for (Resource r : resources) {

				if (folderName.equals(r.getFilename())) {
					imageFolderName = folderName;
					break;
				}
			}
		}
		return imageFolderName;
	}
}
