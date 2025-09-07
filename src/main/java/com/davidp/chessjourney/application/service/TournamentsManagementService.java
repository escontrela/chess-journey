package com.davidp.chessjourney.application.service;

import com.davidp.chessjourney.domain.Tournament;
import com.davidp.chessjourney.domain.TournamentRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Service for managing tournament data in the database.
 */
public class TournamentsManagementService {

    private final TournamentRepository tournamentRepository;

    public TournamentsManagementService(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    /**
     * Saves a list of tournaments to the database.
     * Duplicates (based on hash) will be automatically ignored.
     * 
     * @param tournaments the list of tournaments to save
     * @return the list of successfully saved tournaments
     */
    public List<Tournament> saveTournaments(List<Tournament> tournaments) {
        return tournamentRepository.saveAll(tournaments);
    }

    /**
     * Retrieves all tournaments from the database.
     * 
     * @return list of all tournaments
     */
    public List<Tournament> getAllTournaments() {
        return tournamentRepository.findAll();
    }

    /**
     * Retrieves upcoming tournaments starting from yesterday, limited to a specific count.
     * 
     * @param limit the maximum number of tournaments to return
     * @return list of upcoming tournaments
     */
    public List<Tournament> getUpcomingTournaments(int limit) {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return tournamentRepository.findUpcomingTournaments(yesterday, limit);
    }

    /**
     * Checks if tournaments exist in the database.
     * 
     * @return true if there are any tournaments in the database
     */
    public boolean hasTournaments() {
        return !tournamentRepository.findAll().isEmpty();
    }

    /**
     * Gets the next upcoming tournament (closest future tournament from today).
     * 
     * @return the next upcoming tournament, or null if no future tournaments are available
     */
    public Tournament getNextUpcomingTournament() {
        LocalDate today = LocalDate.now();
        List<Tournament> upcomingTournaments = tournamentRepository.findUpcomingTournaments(today, 1);
        return upcomingTournaments.isEmpty() ? null : upcomingTournaments.get(0);
    }

    /**
     * Deletes all tournaments from the database.
     * Mainly for testing purposes.
     */
    public void deleteAllTournaments() {
        tournamentRepository.deleteAll();
    }
}