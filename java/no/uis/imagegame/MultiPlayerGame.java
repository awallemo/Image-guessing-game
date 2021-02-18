package no.uis.imagegame;

import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class MultiPlayerGame extends Game {

    private MultiplayerStatus guesserStatus;
    private MultiplayerStatus hostStatus;

    private ArrayList<Picture> sentPictures;
    private ArrayList<Integer> sentPicturesNumber;
    private HashMap<Integer, Picture> avaliableImages;
    private String gameHostMessage;
    private String gameGuesserMessage;

    @Override
    public int getGuesses() {
        return super.getGuesses();
    }

    public Boolean HostInGame;
    public Boolean GuesserInGame;
    private Random RoleGiver;
    private String hostUsername;


    public MultiPlayerGame(ImageLabelReader labelReader, Resource[] resources) {
        super(labelReader, resources);
        gameHostMessage = "Host, it is your turn!";
        gameGuesserMessage = new String("Wait for the Host!");
        avaliableImages = new HashMap<>();
        sentPicturesNumber = new ArrayList<>();
        Integer id = 0;
        for (int i = 0; i < 49; i++) {
            String stringTall = Integer.toString(i);
            avaliableImages.put(i, new Picture("images/scattered_images/" + this.getImageFolderName() + "/" + stringTall + ".png", 0, id));
            id++;
        }
        sentPictures = new ArrayList<>();
        guesserStatus = MultiplayerStatus.WAITING_HOST;
        hostStatus = MultiplayerStatus.HOST_TURN;
        HostInGame = false;
        GuesserInGame = false;
        RoleGiver = new Random();
    }

    public String getHostMessage() {
        return gameHostMessage;
    }

    public String getGuesserMessage() {
        return gameGuesserMessage;
    }

    public MultiplayerStatus getHostStatus() {
        return hostStatus;
    }

    public MultiplayerStatus getGuesserStatus() {
        return guesserStatus;
    }


    public ArrayList<Picture> getSentPictures() {
        return sentPictures;
    }

    public HashMap<Integer, Picture> get_ImagesMap() {
        return avaliableImages;
    }

    public Boolean updatePictureStatus(Integer Index) {
        Picture tmpPicture = avaliableImages.get(Index);
        if (tmpPicture == null) {
            return false;
        }
        tmpPicture.setStatus(1);
        avaliableImages.replace(Index, tmpPicture);
        return true;
    }

    public Boolean sendPicture(Integer Index) {
        Picture tmPicture = avaliableImages.get(Index);
        if (tmPicture == null) {
            return false;
        }
        sentPictures.add(tmPicture);
        hostStatus = MultiplayerStatus.WAITING_HOST;
        guesserStatus = MultiplayerStatus.GUESSING;
        gameHostMessage = "You gotta wait for the guesser!";
        gameGuesserMessage = "It is now your turn!";
        return true;
    }

    public Boolean Guess(String Guess) {
        Guess = Guess.trim();
        if (Guess.equals(this.getCorrectLabel())) {
            guesserStatus = MultiplayerStatus.WONGAME;
            hostStatus = MultiplayerStatus.WONGAME;
            gameGuesserMessage = "You have won!";
            gameHostMessage = "You have won!";
            return true;
        } else {
            guesserStatus = MultiplayerStatus.WAITING_HOST;
            hostStatus = MultiplayerStatus.HOST_TURN;
            gameGuesserMessage = "Unlucky! try one more time next turn! Wait for the Host!";
            gameHostMessage = "It is your turn again!";
            return false;
        }
    }

    public void defineHostUsername(String username) {
        hostUsername = username;
    }

    public String getHostUsername() {
        return hostUsername;
    }

    public Boolean addTheGuesser() {
        if (!GuesserInGame) {
            GuesserInGame = true;
            return true;
        } else {
            return false;
        }
    }
    
    public Boolean addTheHost() {
        if (!HostInGame) {
            HostInGame = true;
            return true;
        } else {
            return false;
        }
    }

    public String giveMeARole() {
        int Role = RoleGiver.nextInt(2);
        if (Role == 0) {
            if (this.addTheHost()) {
                return "Host";
            }
            return "Guesser";
        } else {
            if (this.addTheGuesser()) {
                return "Guesser";
            }
            return "Host";
        }
    }

    public Boolean joinable(){
        if(!HostInGame || !GuesserInGame){
            return true;
        }
        return false;
    }

    public MultiPlayerGame resetGame(String Role) {
        MultiPlayerGame newGame = new MultiPlayerGame(this.labelReader, this.resources);
        String hostusername = getHostUsername();
        if(Role == "Host"){
            newGame.HostInGame = true;
            if (hostusername != null) {
                newGame.defineHostUsername(hostusername);
            }
        }else {
            newGame.GuesserInGame = true;
        }
        return newGame;
    }

    public void quittingGame(String Role) {
        if (Role.equals("Host")) {
            HostInGame = false;
            hostUsername = null;
        } else {
            GuesserInGame = false;
        }
    }

    public boolean readyForDeletion() {
        if (!HostInGame && !GuesserInGame) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<Integer> getSentPicturesNumber() {
        for (Picture picture : sentPictures) {
            Integer number = picture.getId();
            sentPicturesNumber.add(number);
        }
        return sentPicturesNumber;
    }

    public Integer getTotalNumberOfPictures() {
        return avaliableImages.size();
    }
}