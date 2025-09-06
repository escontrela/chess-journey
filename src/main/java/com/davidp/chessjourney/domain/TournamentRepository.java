package com.davidp.chessjourney.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Tournament entities.
 */
public interface TournamentRepository {

    /**
     * Saves a tournament to the database.
     * If a tournament with the same hash already exists, it won't be duplicated.
     * 
     * @param tournament the tournament to save
     * @return the saved tournament
     */
    Tournament save(Tournament tournament);

    /**
     * Saves multiple tournaments to the database.
     * Duplicates based on hash will be ignored.
     * 
     * @param tournaments the list of tournaments to save
     * @return the list of successfully saved tournaments
     */
    List<Tournament> saveAll(List<Tournament> tournaments);

    /**
     * Finds a tournament by its hash ID.
     * 
     * @param hashId the hash ID of the tournament
     * @return the tournament if found
     */
    Optional<Tournament> findByHashId(String hashId);

    /**
     * Finds all tournaments.
     * 
     * @return list of all tournaments
     */
    List<Tournament> findAll();

    /**
     * Finds upcoming tournaments starting from a given date, limited to a specific count.
     * 
     * @param fromDate the start date to search from
     * @param limit the maximum number of tournaments to return
     * @return list of upcoming tournaments
     */
    List<Tournament> findUpcomingTournaments(LocalDate fromDate, int limit);

    /**
     * Checks if a tournament with the given hash exists.
     * 
     * @param hashId the hash ID to check
     * @return true if the tournament exists
     */
    boolean existsByHashId(String hashId);

    /**
     * Deletes all tournaments.
     * Mainly for testing purposes.
     */
    void deleteAll();
}