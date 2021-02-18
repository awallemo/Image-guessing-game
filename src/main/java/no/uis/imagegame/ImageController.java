package no.uis.imagegame;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import no.uis.players.Player;
import no.uis.players.PlayerRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class ImageController {
    @Autowired
    PlayerRepository entryRepository;

    @Value("classpath:/static/images/scattered_images/*")
    private Resource[] resources;

    private ArrayList<String> onlinePlayerList = new ArrayList<String>();
    private ArrayList<String> chatList = new ArrayList<String>();
    HashMap<Integer, MultiPlayerGame> multiPlayerGames = new HashMap<Integer, MultiPlayerGame>();
    Moves gameOrderMoves = new Moves();
    ImageLabelReader labelReader = new ImageLabelReader("src/main/resources/static/label/label_mapping.csv",
            "src/main/resources/static/label/image_mapping.csv");

    @RequestMapping("/")
    public String index(Model model, HttpSession session, HttpServletRequest request) {
        if (request.getSession().getAttribute("username") != null) {
            return ("redirect:" + "/loggedin");
        }
        return "index";
    }

    @RequestMapping("/registerNewUser")
    public String registerNewUser(Model model, HttpServletRequest request) {
        return "registerNewUser";
    }

    @RequestMapping("/addPlayer")
    public String addplayer(Model model, HttpServletRequest request, @RequestParam(value = "name") String name,
                            @RequestParam(value = "username") String username, @RequestParam(value = "password") String password,
                            @RequestParam(value = "confirmpassword") String confirmpassword) {

        username = username.trim();
        username = username.substring(0, 1).toUpperCase() + username.substring(1).toLowerCase();

        if (entryRepository.hentAlleBrukernavn().contains(username)) {
            System.out.println("username already exists");
            model.addAttribute("createmessage", "username already exists");
            return "registerNewUser";
        }

        if (!password.matches("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}")) {
            model.addAttribute("createmessage", "The password doesn't match the requirements.");
            return "registerNewUser";
        }

        if (password.equals(confirmpassword)) {
            Player p = new Player(name, username, password);
            entryRepository.save(p);
            System.out.println("player created");

            model.addAttribute("loginmessage", "New user: " + username + " created.");
            return "index";
        } else {
            System.out.println("password mismatch");
            model.addAttribute("createmessage", "password mismatch");
            return "registerNewUser";
        }
    }

    @PostMapping("/login")
    public String processLogin(Model model, HttpServletRequest request,
                               @RequestParam(value = "username") String formUsername,
                               @RequestParam(value = "password") String formPassword) {
        formUsername = formUsername.substring(0, 1).toUpperCase() + formUsername.substring(1).toLowerCase();
        Player p = entryRepository.findByUsername(formUsername);
        if (onlinePlayerList.contains(formUsername)) {
            model.addAttribute("loginmessage", "This user is already online");
            return "index";
        }
        if (p != null) {
            if (formPassword.equals(p.getPassword())) {
                onlinePlayerList.add(formUsername);
                request.getSession().setAttribute("username", formUsername);
                request.getSession().setAttribute("player", entryRepository.findByUsername(formUsername));
            } else {
                System.out.println("Wrong password");
                model.addAttribute("loginmessage", "Wrong password");
                return "index";
            }
        } else {
            model.addAttribute("loginmessage", "User dont exist");
            System.out.println("User dont exist");
            return "index";
        }
        return "redirect:/loggedin";
    }

    @RequestMapping("loggedin")
    public String chooseMode(Model model, HttpServletRequest request) {
        if (request.getSession().getAttribute("username") == null) {
            model.addAttribute("loginmessage", "No access! Log in to gain access to that page!");
            return "index";
        }
        model.addAttribute("players", onlinePlayerList);
        model.addAttribute("username", (String) request.getSession().getAttribute("username"));
        return "chooseMode";
    }

    @RequestMapping("madeby")
    public String madeby(Model model, HttpServletRequest request) {
        model.addAttribute("username", (String) request.getSession().getAttribute("username"));
        return "madeby";
    }

    @RequestMapping("/leaderboard")
    public String leaderboard(Model model, HttpServletRequest request) {
        Leaderboard leaderboard = new Leaderboard(entryRepository);

        // Boolean playersExist = leaderboard.playersExist();
        ArrayList<String> sortedSinglePlayer = new ArrayList<String>();
        ArrayList<String> sortedMultiPlayer = new ArrayList<String>();

        sortedSinglePlayer = leaderboard.singlePlayerArray();
        sortedMultiPlayer = leaderboard.multiPlayerArray();
        model.addAttribute("SingleP", sortedSinglePlayer);
        model.addAttribute("MultiP", sortedMultiPlayer);
        model.addAttribute("username", (String) request.getSession().getAttribute("username"));
        return "leaderboard";
    }


    @RequestMapping("user")
    public String user(Model model, HttpServletRequest request,
                       @RequestParam(value = "user", required = false, defaultValue = "") String user) {
        if (request.getSession().getAttribute("username") == null) {
            model.addAttribute("loginmessage", "No access! Log in to gain access to that page!");
            return "index";
        }
        Player p;
        if (user.equals("")) {
            p = entryRepository.findByUsername((String) request.getSession().getAttribute("username"));
        } else {
            p = entryRepository.findByUsername(user);
        }
        model.addAttribute("name", p.getName());
        model.addAttribute("userprofile", p.getUsername());
        model.addAttribute("singlescoor", p.getSingleplayerscore());
        model.addAttribute("doublescoor", p.getMultiplayerscore());
        model.addAttribute("SPgames", p.getGames_played());
        model.addAttribute("MPgames", p.getMp_games_played());
        model.addAttribute("SPaverage", p.getAverage_score());
        model.addAttribute("MPaverage", p.getMp_average_score());
        model.addAttribute("username", request.getSession().getAttribute("username"));
        return "me";
    }


    @RequestMapping("/chat")
    public String chat(Model model, HttpServletRequest request,
                       @RequestParam(value = "chatText", required = false, defaultValue = "") String chatText) {
        if (request.getSession().getAttribute("username") == null) {
            model.addAttribute("loginmessage", "No access! Log in to gain access to that page!");
            return "index";
        }
        if (!chatText.equals("")) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime now = LocalDateTime.now();
            chatList.add(
                    (String) request.getSession().getAttribute("username") + ": " + dtf.format(now) + ": " + chatText);
        }
        ArrayList<String> revChatList = new ArrayList<String>();
        revChatList.addAll(chatList);
        Collections.reverse(revChatList);
        model.addAttribute("chatList", revChatList);
        model.addAttribute("username", (String) request.getSession().getAttribute("username"));
        return "chat";
    }

    @RequestMapping("logout")
    public String logout(Model model, HttpServletRequest request) {
        onlinePlayerList.remove(request.getSession().getAttribute("username"));
        HttpSession session = request.getSession();
        if (session.getAttribute("multiplayerIndexGame") != null) {
            quitMultiplayerGame(session);
        }
        request.getSession().invalidate();
        return "redirect:/";
    }

    @RequestMapping("singleplayer")
    public String singleplayer(Model model, HttpServletRequest request,
                               @RequestParam(value = "guess", required = false, defaultValue = "") String guess,
                               @RequestParam(value = "reset", required = false, defaultValue = "") String reset) {
        HttpSession session = request.getSession();
        Player p = entryRepository.findByUsername((String)session.getAttribute("username"));
        if (session.getAttribute("username") == null) {
            model.addAttribute("loginmessage", "No access! Log in to gain access to that page!");
            return "index";
        }

        if (reset.equals("reset")) {
            p.quitgame(true);
            entryRepository.save(p);
            session.setAttribute("singleplayergame", null);
            return "redirect:/singleplayer";
        }

        if (session.getAttribute("singleplayergame") == null) {
            session.setAttribute("singleplayergame", new SinglePlayerGame(labelReader, resources, false));
        } else {
            SinglePlayerGame currentGame = (SinglePlayerGame) session.getAttribute("singleplayergame");
            if (currentGame.performGuess(guess)) {
                if (p.updateScore(currentGame.getGuesses(), true)) {
                    singlePlayerModelAdd(model, request.getSession(), true, true, true);
                } else {
                    singlePlayerModelAdd(model, request.getSession(), true, true, false);
                }
                entryRepository.save(p);
                return "singleplayerWon";
            } else {
                if (currentGame.gameIsOver()) {
                    singlePlayerModelAdd(model, request.getSession(), true, false, false);
                    p.quitgame(true);
                    entryRepository.save(p);
                    return "singleplayerWon";
                }
            }
        }

        singlePlayerModelAdd(model, request.getSession(), false, false, false);
        return "guesser";
    }

    private void singlePlayerModelAdd(Model model, HttpSession session, boolean gameOver, boolean correct, boolean best) {
        if (gameOver) {
            if (correct) {
                model.addAttribute("message", ((SinglePlayerGame) session.getAttribute("singleplayergame")).getGameMessage());
                if (best) {
                    model.addAttribute("wonMessage", "New Personal best!");
                }
            } else {
                model.addAttribute("message", "Game over, no attempts left!");
            }
        } else {
            model.addAttribute("message", ((SinglePlayerGame) session.getAttribute("singleplayergame")).getGameMessage());
            model.addAttribute("prevGuesses", ((SinglePlayerGame) session.getAttribute("singleplayergame")).getPrevGuesses());
            model.addAttribute("listimages", ((SinglePlayerGame) session.getAttribute("singleplayergame")).getImages());
        }
        model.addAttribute("username", session.getAttribute("username"));
        if (gameOver) session.setAttribute("singleplayergame", null);
    }

    @RequestMapping("/multiplayerJoin")
    public String chooseMultiplayerGame(Model model, HttpServletRequest request) {
        if (request.getSession().getAttribute("username") == null) {
            model.addAttribute("loginmessage", "No access! Log in to gain access to that page!");
            return "index";
        } else if (request.getSession().getAttribute("multiplayerIndexGame") != null) {
            return "redirect:/multiplayerMain";
        } else
            model.addAttribute("username", (String) request.getSession().getAttribute("username"));
        return "multiplayerChooseGame";
    }

    @RequestMapping("/multiplayerExistingGame")
    public String chooseMultiplayer(Model model, HttpServletRequest request, @RequestParam(value = "gameIndexChooseMultiplayer") Integer gameIndex) {
        if (multiPlayerGames.get(gameIndex) != null) {
            String playerRole = multiPlayerGames.get(gameIndex).giveMeARole();
            request.getSession().setAttribute("multiplayerIndexGame", gameIndex);
            request.getSession().setAttribute("multiplayerRole", playerRole);
            if (playerRole.equals("Host")) {
                multiPlayerGames.get(gameIndex).defineHostUsername((String) request.getSession().getAttribute("username"));
            }
            return "redirect:/multiplayerMain";
        }
        return "redirect:/multiplayerJoin";
    }

    @RequestMapping("/joinAvailableMultiplayerGame")
    public String joinAvailableMultiplayerGame(Model model, HttpServletRequest request) {
        Integer size = multiPlayerGames.size();
        if (size != 0) {
            for (int i = 0; i < size; i++) {
                if (multiPlayerGames.get(i).joinable()) {
                    String playerRole = multiPlayerGames.get(i).giveMeARole();
                    request.getSession().setAttribute("multiplayerIndexGame", i);
                    request.getSession().setAttribute("multiplayerRole", playerRole);
                    if (playerRole.equals("Host")) {
                        multiPlayerGames.get(i).defineHostUsername((String) request.getSession().getAttribute("username"));
                    }
                    return "redirect:/multiplayerMain";
                }
            }
            return "redirect:/multiplayerJoin";
        }
        return "redirect:/multiplayerJoin";
    }


    @RequestMapping("/multiplayerMain")
    public String multiplayer(Model model, HttpServletRequest request) {
        if (request.getSession().getAttribute("multiplayerIndexGame") == null) {
            int index = multiPlayerGames.size();
            MultiPlayerGame myMpGame = new MultiPlayerGame(labelReader, resources);
            String playerRole = myMpGame.giveMeARole();
            request.getSession().setAttribute("multiplayerRole", playerRole);
            request.getSession().setAttribute("multiplayerIndexGame", index);
            if (playerRole.equals("Host")) {
                myMpGame.defineHostUsername((String) request.getSession().getAttribute("username"));
            }
            multiPlayerGames.put(index, myMpGame);
        }
        if (request.getSession().getAttribute("multiplayerRole") == "Host") {
            return "redirect:/multiplayerHost";
        } else {
            return "redirect:/multiplayerGuesser";
        }
    }

    @RequestMapping("/multiplayerHost")
    public String showImageToHost(Model model, HttpServletRequest request) {
        if (request.getSession().getAttribute("username") == null) {
            model.addAttribute("loginmessage", "No access! Log in to gain access to that page!");
            return "index";
        }
        HttpSession session = request.getSession();
        if (session.getAttribute("multiplayerIndexGame") == null ||
                session.getAttribute("multiplayerRole") != "Host") {
            return "redirect:/multiplayerMain";
        }
        int gameIndex = (Integer) session.getAttribute("multiplayerIndexGame");
        MultiPlayerGame myMpGame = multiPlayerGames.get(gameIndex);
        if (myMpGame.getHostUsername() == null || !myMpGame.HostInGame) {
            myMpGame.defineHostUsername((String) session.getAttribute("username"));
            myMpGame.HostInGame = true;
        }
        if (myMpGame.getHostStatus() == MultiplayerStatus.WONGAME) {
            multiPlayerModelAddHost(model, session, gameIndex, myMpGame, true, false, false);
            return "multiplayerReset";
        } else if (myMpGame.getHostStatus() != MultiplayerStatus.HOST_TURN) {
            multiPlayerModelAddHost(model, session, gameIndex, myMpGame, false, true, false);
            return "multiplayerWaiting";
        }
        multiPlayerModelAddHost(model, session, gameIndex, myMpGame, false, false, false);
        return "multiplayerHost";
    }

    @RequestMapping("/multiplayerGuesser")
    public String showImageToGuesser(Model model, HttpServletRequest request) {
        if (request.getSession().getAttribute("username") == null) {
            model.addAttribute("loginmessage", "No access! Log in to gain access to that page!");
            return "index";
        }
        HttpSession session = request.getSession();
        if (session.getAttribute("multiplayerIndexGame") == null ||
                session.getAttribute("multiplayerRole") != "Guesser") {
            return "redirect:/multiplayerMain";
        }
        int gameIndex = (Integer) session.getAttribute("multiplayerIndexGame");
        MultiPlayerGame myMpGame = multiPlayerGames.get(gameIndex);

        if (!myMpGame.GuesserInGame) {
            myMpGame.GuesserInGame = true;
        }
        if (myMpGame.getGuesserStatus() == MultiplayerStatus.WONGAME) {
            multiPlayerModelAddGuesser(model, session, gameIndex, myMpGame, true, false, false);
            return "multiplayerReset";
        } else if (myMpGame.getGuesserStatus() == MultiplayerStatus.WAITING_HOST) {
            multiPlayerModelAddGuesser(model, session, gameIndex, myMpGame, false, true, false);
            return "multiplayerWaiting";
        } else {
            multiPlayerModelAddGuesser(model, session, gameIndex, myMpGame, false, false, false);
            return "multiplayerGuesser";
        }
    }

    @PostMapping("/guessImage")
    public ModelAndView guessImage(@RequestParam(value = "guess") String guess, HttpServletRequest request) throws IOException {
        int gameIndex = (Integer) request.getSession().getAttribute("multiplayerIndexGame");
        MultiPlayerGame myMpGame = multiPlayerGames.get(gameIndex);
        if (myMpGame.Guess(guess)) {
            myMpGame.incGuesses();
            Player playerGuesser = entryRepository.findByUsername((String) request.getSession().getAttribute("username"));
            Player playerHost = entryRepository.findByUsername(myMpGame.getHostUsername());
            playerGuesser.updateScore(myMpGame.getGuesses(), false);
            playerHost.updateScore(myMpGame.getGuesses(), false);
            gameOrderMoves.update(myMpGame.getCorrectLabel(), myMpGame.getGuesses(), myMpGame.getSentPicturesNumber());
            entryRepository.save(playerGuesser);
            entryRepository.save(playerHost);
            return new ModelAndView("redirect:" + "/multiplayerGuesser"); //you have won page
        } else {
            if (!guess.equals("")) {
                myMpGame.addGuess(guess);
            }
            myMpGame.incGuesses();
            return new ModelAndView("redirect:" + "/multiplayerGuesser");
        }
    }

    @PostMapping("/sendImage")
    public ModelAndView sendImage(@RequestParam(value = "partImageId") String IdOfThePicture, HttpServletRequest request) {
        if (IdOfThePicture.equals("-10")) {//cancel button
            return new ModelAndView("redirect:" + "/multiplayerHost");
        }
        int gameIndex = (Integer) request.getSession().getAttribute("multiplayerIndexGame");
        MultiPlayerGame myMpGame = multiPlayerGames.get(gameIndex);
        myMpGame.updatePictureStatus(Integer.parseInt(IdOfThePicture));
        myMpGame.sendPicture(Integer.parseInt(IdOfThePicture));
        return new ModelAndView("redirect:" + "/multiplayerHost");
    }

    @RequestMapping("/resetMultiplayer")
    public ModelAndView resetMultiplayerGame(HttpServletRequest request) {
        if (request.getSession().getAttribute("username") == null) {
            return new ModelAndView("redirect:" + "/index");
        }
        int gameIndex = (Integer) request.getSession().getAttribute("multiplayerIndexGame");
        if (multiPlayerGames.get(gameIndex).getGuesserStatus() == MultiplayerStatus.WONGAME && multiPlayerGames.get(gameIndex).getHostStatus() == MultiplayerStatus.WONGAME) {
            multiPlayerGames.put(gameIndex, multiPlayerGames.get(gameIndex).resetGame((String) request.getSession().getAttribute("multiplayerRole")));
            return new ModelAndView("redirect:" + "/multiplayerMain");
        } else {
            return new ModelAndView("redirect:" + "/multiplayerMain");
        }
    }

    @RequestMapping("/deleteMyMultiplayer")
    public ModelAndView deleteMultiplayerGame(HttpServletRequest request) {
        int gameIndex = (Integer) request.getSession().getAttribute("multiplayerIndexGame");
        String Role = (String) request.getSession().getAttribute("multiplayerRole");
        MultiPlayerGame myMpGame = multiPlayerGames.get(gameIndex);
        myMpGame.quittingGame(Role);
        if (myMpGame.readyForDeletion()) {
            multiPlayerGames.remove(gameIndex);
        }
        request.getSession().setAttribute("multiplayerIndexGame", null);
        request.getSession().setAttribute("multiplayerRole", null);
        return new ModelAndView("redirect:" + "/loggedin");
    }

    @RequestMapping("rules")
    public String rules(Model model, HttpServletRequest request) {
        model.addAttribute("username", (String) request.getSession().getAttribute("username"));
        return "rules";
    }


    private void multiPlayerModelAddHost(Model model, HttpSession session, Integer gameIndex, MultiPlayerGame myMpGame, Boolean Won, Boolean Waiting, Boolean lost) {
        Collection<Picture> values = myMpGame.get_ImagesMap().values();
        ArrayList<Picture> pictureList = new ArrayList<>(values);
        if (Won) {
            model.addAttribute("message", myMpGame.getHostMessage());
            model.addAttribute("score", "Score: " + myMpGame.getGuesses());
            model.addAttribute("username", session.getAttribute("username"));

        } else if (Waiting) {
            model.addAttribute("message", "Info: " + myMpGame.getHostMessage());
            model.addAttribute("gameIndex", "Room nr: " + gameIndex);
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("usernameShow", "User: " + session.getAttribute("username"));
        } else if (lost) {
            model.addAttribute("message", "You have lost!");
            model.addAttribute("score", "Score: " + "0");
            model.addAttribute("username", (String) session.getAttribute("username"));
        } else {
            model.addAttribute("selectedLabel", myMpGame.getCorrectLabel());
            model.addAttribute("selectedLabelShow", "Image name: " + myMpGame.getCorrectLabel());
            model.addAttribute("message", "Info: " + myMpGame.getHostMessage());
            model.addAttribute("gameIndex", "Room nr: " + gameIndex);
            model.addAttribute("listimages", pictureList);
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("usernameShow", "User: " + session.getAttribute("username"));
            model.addAttribute("prevGuesses", myMpGame.getPrevGuesses());
        }
    }


    private void multiPlayerModelAddGuesser(Model model, HttpSession session, Integer gameIndex, MultiPlayerGame myMpGame, Boolean Won, Boolean Waiting, Boolean lost) {
        if (Won) {
            model.addAttribute("message", myMpGame.getGuesserMessage());
            model.addAttribute("score", "Score: " + multiPlayerGames.get(gameIndex).getGuesses());
            model.addAttribute("username", (String) session.getAttribute("username"));
        } else if (Waiting) {
            model.addAttribute("message", "Info: " + myMpGame.getGuesserMessage());
            model.addAttribute("gameIndex", "Room nr: " + gameIndex);
            model.addAttribute("username", (String) session.getAttribute("username"));
            model.addAttribute("usernameShow", "User: " + session.getAttribute("username"));
        } else if (lost) {
            model.addAttribute("message", "You have lost!");
            model.addAttribute("score", "Score: " + "0");
            model.addAttribute("username", (String) session.getAttribute("username"));
        } else {
            model.addAttribute("listimages", myMpGame.getSentPictures());
            model.addAttribute("message", "Info: " + myMpGame.getGuesserMessage());
            model.addAttribute("prevGuesses", myMpGame.getPrevGuesses());
            model.addAttribute("numberOfGuesses", "Number of Guesses: " + myMpGame.getGuesses());
            model.addAttribute("gameIndex", "Room nr: " + gameIndex);
            model.addAttribute("username", (String) session.getAttribute("username"));
            model.addAttribute("usernameShow", "User: " + session.getAttribute("username"));

        }
    }

    private void quitMultiplayerGame(HttpSession session) {
        Integer gameIndex = (Integer) session.getAttribute("multiplayerIndexGame");
        MultiPlayerGame myMpGame = multiPlayerGames.get(gameIndex);
        Player p = entryRepository.findByUsername((String)session.getAttribute("username"));
        p.quitgame(false);
        entryRepository.save(p);
        myMpGame.quittingGame((String) session.getAttribute("multiplayerRole"));
        if (myMpGame.readyForDeletion()) {
            multiPlayerGames.remove(gameIndex);
        }
    }
}
