package com.davidp.chessjourney.test;

import com.davidp.chessjourney.application.service.LookUpTournamentsService;
import com.davidp.chessjourney.domain.Tournament;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock service for testing tournament scraping when real website is unavailable
 */
public class MockLookUpTournamentsService extends LookUpTournamentsService {
    
    @Override
    public List<Tournament> scrapeTournaments() {
        // Return mock tournament data for testing
        List<Tournament> mockTournaments = new ArrayList<>();
        
        mockTournaments.add(new Tournament(
            "A Coruña", 
            "Santiago de Compostela", 
            "Open Internacional de Ajedrez Santiago", 
            LocalDate.now().plusDays(5), 
            LocalDate.now().plusDays(7), 
            "Clásico 90+30"
        ));
        
        mockTournaments.add(new Tournament(
            "Pontevedra", 
            "Vigo", 
            "Torneo de Ajedrez Rápido Vigo 2024", 
            LocalDate.now().plusDays(12), 
            LocalDate.now().plusDays(12), 
            "Rápido 15+10"
        ));
        
        mockTournaments.add(new Tournament(
            "Lugo", 
            "Lugo", 
            "Memorial Xadrez Lugo", 
            LocalDate.now().plusDays(20), 
            LocalDate.now().plusDays(22), 
            "Clásico 2h+30s"
        ));
        
        mockTournaments.add(new Tournament(
            "Ourense", 
            "Ourense", 
            "Campeonato Provincial de Ajedrez", 
            LocalDate.now().plusDays(35), 
            LocalDate.now().plusDays(37), 
            "Clásico 90+30"
        ));
        
        mockTournaments.add(new Tournament(
            "A Coruña", 
            "Ferrol", 
            "Torneo Juvenil de Ferrol", 
            LocalDate.now().plusDays(50), 
            LocalDate.now().plusDays(52), 
            "Rápido 25+5"
        ));
        
        System.out.println("Mock scraping completed - found " + mockTournaments.size() + " tournaments");
        return mockTournaments;
    }
}