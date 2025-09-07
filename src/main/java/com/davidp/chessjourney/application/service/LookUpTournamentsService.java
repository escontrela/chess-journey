package com.davidp.chessjourney.application.service;

import com.davidp.chessjourney.domain.Tournament;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for scraping tournament data from Galicia website.
 */
public class LookUpTournamentsService {

    private static final String GALICIA_URL = "https://tabladeflandes.com/gestorneos/galicia.php";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Scrapes tournament data from the Galicia website.
     * 
     * @return list of tournaments found on the website
     * @throws RuntimeException if scraping fails
     */
    public List<Tournament> scrapeTournaments() {
        List<Tournament> tournaments = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(GALICIA_URL)
                    .timeout(10000)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .get();

            // Look for table containing tournament data
            Elements tables = doc.select("table");
            
            for (Element table : tables) {
                Elements rows = table.select("tr");
                
                // Skip header row
                for (int i = 1; i < rows.size(); i++) {
                    Element row = rows.get(i);
                    Elements cols = row.select("td");
                    
                    // Expecting 7 columns: provincia, concejo, torneo, inicio, fin, local, ritmo
                    if (cols.size() >= 7) {
                        try {
                            String provincia = cleanText(cols.get(0).text());
                            String concejo = cleanText(cols.get(1).text());
                            String torneo = cleanText(cols.get(2).text());
                            String inicioStr = cleanText(cols.get(3).text());
                            String finStr = cleanText(cols.get(4).text());
                            String local = cleanText(cols.get(5).text());
                            String ritmo = cleanText(cols.get(6).text());

                            // Skip empty or invalid rows
                            if (provincia.isEmpty() || concejo.isEmpty() || torneo.isEmpty()) {
                                continue;
                            }

                            LocalDate inicio = parseDate(inicioStr);
                            LocalDate fin = parseDate(finStr);

                            if (inicio != null && fin != null) {
                                Tournament tournament = new Tournament(
                                    provincia, concejo, torneo, inicio, fin, local, ritmo
                                );
                                tournaments.add(tournament);
                            }
                        } catch (Exception e) {
                            // Log error but continue processing other rows
                            System.err.println("Error parsing tournament row: " + e.getMessage());
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to scrape tournaments from Galicia website", e);
        }

        return tournaments;
    }

    /**
     * Cleans text by trimming whitespace and removing extra spaces.
     */
    private String cleanText(String text) {
        if (text == null) return "";
        return text.trim().replaceAll("\\s+", " ");
    }

    /**
     * Gets the next upcoming tournament (closest future tournament from today).
     * 
     * @return the next upcoming tournament, or null if no future tournaments are available
     */
    public Tournament getNextUpcomingTournament() {
        List<Tournament> tournaments;
        try {
            tournaments = scrapeTournaments();
        } catch (Exception e) {
            System.err.println("Error scraping tournaments for next tournament: " + e.getMessage());
            return null; // Return null if scraping fails
        }
        
        LocalDate today = LocalDate.now();
        
        return tournaments.stream()
                .filter(tournament -> tournament.getInicio().isAfter(today))
                .min((t1, t2) -> t1.getInicio().compareTo(t2.getInicio()))
                .orElse(null);
    }

    /**
     * Parses date string in dd/MM/yyyy format.
     */
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(dateStr.trim(), DATE_FORMAT);
        } catch (DateTimeParseException e) {
            // Try alternative formats
            try {
                // Try yyyy-MM-dd format
                return LocalDate.parse(dateStr.trim());
            } catch (DateTimeParseException e2) {
                System.err.println("Unable to parse date: " + dateStr);
                return null;
            }
        }
    }
}