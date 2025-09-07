package com.davidp.chessjourney.test;

import com.davidp.chessjourney.application.service.TournamentsManagementService;
import com.davidp.chessjourney.domain.Tournament;
import com.davidp.chessjourney.domain.TournamentRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock service for testing tournament management when real database is unavailable
 */
public class MockTournamentsManagementService extends TournamentsManagementService {
    
    private final List<Tournament> mockTournaments;
    
    public MockTournamentsManagementService() {
        super(null); // We won't use the repository in mock mode
        this.mockTournaments = createMockTournaments();
    }
    
    private List<Tournament> createMockTournaments() {
        List<Tournament> tournaments = new ArrayList<>();
        
        tournaments.add(new Tournament(
            "A Coruña", 
            "Santiago de Compostela", 
            "Open Internacional de Ajedrez Santiago", 
            LocalDate.now().plusDays(5), 
            LocalDate.now().plusDays(7), 
            "Hotel Compostela",
            "90+30"
        ));
        
        tournaments.add(new Tournament(
            "Pontevedra", 
            "Vigo", 
            "Torneo de Ajedrez Rápido Vigo 2024", 
            LocalDate.now().plusDays(12), 
            LocalDate.now().plusDays(12), 
            "Club de Ajedrez Vigo",
            "15+10"
        ));
        
        tournaments.add(new Tournament(
            "Lugo", 
            "Lugo", 
            "Memorial Xadrez Lugo", 
            LocalDate.now().plusDays(20), 
            LocalDate.now().plusDays(22), 
            "Palacio de Deportes",
            "90+30"
        ));
        
        tournaments.add(new Tournament(
            "Ourense", 
            "Ourense", 
            "Campeonato Provincial de Ajedrez", 
            LocalDate.now().plusDays(35), 
            LocalDate.now().plusDays(37), 
            "Centro Cultural Ourense",
            "120+30"
        ));
        
        tournaments.add(new Tournament(
            "A Coruña", 
            "Ferrol", 
            "Torneo Juvenil de Ferrol", 
            LocalDate.now().plusDays(50), 
            LocalDate.now().plusDays(52), 
            "IES de Ferrol",
            "90+30"
        ));
        
        return tournaments;
    }
    
    @Override
    public Tournament getNextUpcomingTournament() {
        LocalDate today = LocalDate.now();
        
        return mockTournaments.stream()
                .filter(tournament -> tournament.getInicio().isAfter(today))
                .min((t1, t2) -> t1.getInicio().compareTo(t2.getInicio()))
                .orElse(null);
    }
    
    @Override
    public List<Tournament> getUpcomingTournaments(int limit) {
        LocalDate today = LocalDate.now();
        
        return mockTournaments.stream()
                .filter(tournament -> tournament.getInicio().isAfter(today))
                .sorted((t1, t2) -> t1.getInicio().compareTo(t2.getInicio()))
                .limit(limit)
                .toList();
    }
    
    @Override
    public List<Tournament> getAllTournaments() {
        return new ArrayList<>(mockTournaments);
    }
    
    @Override
    public boolean hasTournaments() {
        return !mockTournaments.isEmpty();
    }
}