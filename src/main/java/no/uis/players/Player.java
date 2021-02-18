package no.uis.players;

import javax.persistence.*;
import java.util.ArrayList;


@Entity
@SecondaryTables({
		@SecondaryTable(name = "SINGLEPLAYERTABLE",
				pkJoinColumns = @PrimaryKeyJoinColumn(name = "id")),
		@SecondaryTable(name = "MULTIPLAYERTABLE",
				pkJoinColumns = @PrimaryKeyJoinColumn(name = "id"))})
public class  Player {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String name;
	private String username;
	private String password;

	@Column(name = "MP_HIGHSCORE", table = "MULTIPLAYERTABLE")
	private Integer multiplayerscore;
	@Column(name = "MP_GAMES_PLAYED", table = "MULTIPLAYERTABLE")
	private Integer mp_games_played;
	@Column(name = "MP_AVERAGE_SCORE", table = "MULTIPLAYERTABLE")
	private Integer mp_average_score;
	@Column(name = "MP_TOTAL_SCORE", table = "MULTIPLAYERTABLE")
	private Integer mp_total_score;
	@Column(name = "MP_SCORE_TIME_STAMP", table = "MULTIPLAYERTABLE")
	private long mp_score_time_stamp;

	@Column(name = "SP_HIGHSCORE", table = "SINGLEPLAYERTABLE")
	private Integer singleplayerscore;
	@Column(name = "GAMES_PLAYED", table = "SINGLEPLAYERTABLE")
	private Integer games_played;
	@Column(name = "AVERAGE_SCORE", table = "SINGLEPLAYERTABLE")
	private Integer average_score;
	@Column(name = "TOTAL_SCORE", table = "SINGLEPLAYERTABLE")
	private Integer total_score;
	@Column(name = "SP_SCORE_TIME_STAMP", table = "SINGLEPLAYERTABLE")
	private long sp_score_time_stamp;

	public Player() { //Default constructor, error if twe don't have it

	}

	public Player(String name, String username, String password) {
		this.setName(name);
		this.setUsername(username);
		this.setPassword(password);
		this.singleplayerscore = 0;
		this.multiplayerscore = 0;
		this.total_score = 0;
		this.mp_total_score = 0;
		this.games_played = 0;
		this.mp_games_played = 0;
		this.average_score = 0;
		this.mp_average_score = 0;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getGames_played() {
		if (games_played == null) {
			this.games_played = 0;
		}
		return games_played;
	}

	public Integer getMp_games_played() {
		if (mp_games_played == null) {
			this.games_played = 0;
		}
		return mp_games_played;
	}

	public int getMultiplayerscore() { //Getters for MS and SS, will be used in leaderboard
		return multiplayerscore;
	}

	public int getSingleplayerscore() {
		return singleplayerscore;
	}

	public Integer getMp_total_score() {
		return mp_total_score;
	}

	public int getMp_average_score() {
		return mp_average_score;
	}


	public Integer getTotal_score() {
		return total_score;
	}

	public int getAverage_score() {
		return average_score;
	}


	public long getMp_score_time_stamp() {
		return mp_score_time_stamp;
	}


	public long getSp_score_time_stamp() {
		return sp_score_time_stamp;
	}


	public void quitgame(boolean singleplayer) {
		if (singleplayer && games_played != null) {
			games_played++;
			total_score += 50;
			average_score = total_score / games_played;
		} else if (!singleplayer && games_played != null) {
			mp_games_played++;
			mp_total_score += 50;
			mp_average_score = mp_total_score / mp_games_played;
		}
	}

    public void updateScoreStats(Integer score, boolean singleplayer){
        if (singleplayer){
            this.singleplayerscore = score;
            this.sp_score_time_stamp = System.currentTimeMillis();
            this.total_score = getTotal_score() + score;
            this.games_played = getGames_played() + 1;
            this.average_score = getTotal_score() / getGames_played();
        }else {
            this.mp_games_played = 0;
            this.multiplayerscore = score;
            this.mp_score_time_stamp = System.currentTimeMillis();
            this.mp_total_score = getMp_total_score() + score;
            this.mp_games_played = getMp_games_played() + 1;
            this.mp_average_score = getMp_total_score() / getMp_games_played();
        }
    }

	public boolean updateScore(Integer score, boolean singleplayer) {
		if (singleplayer) {

			if (this.singleplayerscore == 0 || score < singleplayerscore) {
				updateScoreStats(score, true);
				return true;
			} else {
			    this.total_score = getTotal_score() + score;
			    this.games_played = getGames_played() + 1;
			    this.average_score = getTotal_score() / getGames_played();
			    return false;
			}
		} else {
			if (this.multiplayerscore == 0 || score < multiplayerscore) {
                updateScoreStats(score, false);
				return true;
			} else {
			    this.mp_total_score = getMp_total_score() + score;
			    this.mp_games_played = getMp_games_played() + 1;
			    this.mp_average_score = getMp_total_score() / getMp_games_played();
			    return false;

			}
		}
	}
}

