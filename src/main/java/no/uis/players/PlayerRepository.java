package no.uis.players;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PlayerRepository extends CrudRepository<Player, Long> {
    Player findByUsername(String username);

    @Query("SELECT username FROM Player ")
    List<String> hentAlleBrukernavn();
}
	