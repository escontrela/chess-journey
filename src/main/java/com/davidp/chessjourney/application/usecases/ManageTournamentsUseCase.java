package com.davidp.chessjourney.application.usecases;

import com.davidp.chessjourney.application.config.AppProperties;
import com.davidp.chessjourney.application.service.LookUpTournamentsService;
import com.davidp.chessjourney.application.service.TournamentsManagementService;
import com.davidp.chessjourney.domain.Tournament;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Use case for managing tournament data.
 * Handles the logic of checking if tournaments were accessed today,
 * and if not, scrapes new data and saves it to the database.
 */
public class ManageTournamentsUseCase {

    private final LookUpTournamentsService lookUpService;
    private final TournamentsManagementService managementService;
    private final AppProperties appProperties;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ManageTournamentsUseCase(LookUpTournamentsService lookUpService,
                                   TournamentsManagementService managementService,
                                   AppProperties appProperties) {
        this.lookUpService = lookUpService;
        this.managementService = managementService;
        this.appProperties = appProperties;
    }

    /**
     * Gets upcoming tournaments. If tournaments haven't been accessed today,
     * performs web scraping and saves new data to the database.
     * 
     * @param limit maximum number of tournaments to return (up to 10)
     * @return list of upcoming tournaments
     */
    public List<Tournament> getUpcomingTournaments(int limit) {
        // Ensure limit doesn't exceed 10
        limit = Math.min(limit, 10);

        String today = LocalDate.now().format(DATE_FORMAT);
        String lastAccessDate = appProperties.getLastTournamentAccessDate();

        // If today is the first access or tournaments weren't accessed today, scrape new data
        if (lastAccessDate == null || lastAccessDate.trim().isEmpty() || !today.equals(lastAccessDate)) {
            try {
                System.out.println("Scraping tournament data from Galicia website...");
                List<Tournament> scrapedTournaments = lookUpService.scrapeTournaments();
                
                System.out.println("Found " + scrapedTournaments.size() + " tournaments. Saving to database...");
                List<Tournament> savedTournaments = managementService.saveTournaments(scrapedTournaments);
                
                System.out.println("Successfully saved " + savedTournaments.size() + " tournaments.");
                
                // Update last access date
                appProperties.setLastTournamentAccessDate(today);
                
            } catch (Exception e) {
                System.err.println("Error scraping tournaments: " + e.getMessage());
                // Continue to return existing data from database
            }
        } else {
            System.out.println("Tournaments already accessed today. Using cached data from database.");
        }

        // Return upcoming tournaments from database
        return managementService.getUpcomingTournaments(limit);
    }

    /**
     * Gets all tournaments from the database.
     * 
     * @return list of all tournaments
     */
    public List<Tournament> getAllTournaments() {
        return managementService.getAllTournaments();
    }

    /**
     * Forces a refresh of tournament data by scraping the website
     * regardless of when it was last accessed.
     * 
     * @return list of newly scraped tournaments
     */
    public List<Tournament> refreshTournaments() {
        try {
            System.out.println("Force refreshing tournament data...");
            List<Tournament> scrapedTournaments = lookUpService.scrapeTournaments();
            
            List<Tournament> savedTournaments = managementService.saveTournaments(scrapedTournaments);
            
            // Update last access date
            String today = LocalDate.now().format(DATE_FORMAT);
            appProperties.setLastTournamentAccessDate(today);
            
            System.out.println("Successfully refreshed " + savedTournaments.size() + " tournaments.");
            return savedTournaments;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to refresh tournament data", e);
        }
    }
}