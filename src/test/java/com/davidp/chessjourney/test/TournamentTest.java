package com.davidp.chessjourney.test;

import com.davidp.chessjourney.domain.Tournament;
import java.time.LocalDate;

/**
 * Simple test class to verify Tournament functionality
 */
public class TournamentTest {
    
    public static void main(String[] args) {
        testTournamentCreation();
        testTournamentCreationWithRitmo();
        testTournamentHash();
        System.out.println("All tournament tests passed!");
    }
    
    private static void testTournamentCreation() {
        System.out.println("Testing tournament creation...");
        
        Tournament tournament = new Tournament(
            "A Coruña", 
            "Santiago", 
            "Torneo de Ajedrez Santiago 2024", 
            LocalDate.of(2024, 3, 15), 
            LocalDate.of(2024, 3, 17), 
            "Centro Cultural"
        );
        
        assert tournament.getProvincia().equals("A Coruña");
        assert tournament.getConcejo().equals("Santiago");
        assert tournament.getTorneo().equals("Torneo de Ajedrez Santiago 2024");
        assert tournament.getInicio().equals(LocalDate.of(2024, 3, 15));
        assert tournament.getFin().equals(LocalDate.of(2024, 3, 17));
        assert tournament.getLocal().equals("Centro Cultural");
        assert tournament.getRitmo() == null; // Default null since not provided from HTML table
        assert tournament.getHashId() != null && !tournament.getHashId().isEmpty();
        
        System.out.println("✓ Tournament creation test passed");
    }
    
    private static void testTournamentCreationWithRitmo() {
        System.out.println("Testing tournament creation with ritmo...");
        
        Tournament tournament = new Tournament(
            "A Coruña", 
            "Santiago", 
            "Torneo de Ajedrez Santiago 2024", 
            LocalDate.of(2024, 3, 15), 
            LocalDate.of(2024, 3, 17), 
            "Centro Cultural",
            "90+30"
        );
        
        assert tournament.getProvincia().equals("A Coruña");
        assert tournament.getConcejo().equals("Santiago");
        assert tournament.getTorneo().equals("Torneo de Ajedrez Santiago 2024");
        assert tournament.getInicio().equals(LocalDate.of(2024, 3, 15));
        assert tournament.getFin().equals(LocalDate.of(2024, 3, 17));
        assert tournament.getLocal().equals("Centro Cultural");
        assert tournament.getRitmo().equals("90+30");
        assert tournament.getHashId() != null && !tournament.getHashId().isEmpty();
        
        System.out.println("✓ Tournament creation with ritmo test passed");
    }
    
    private static void testTournamentHash() {
        System.out.println("Testing tournament hash uniqueness...");
        
        Tournament tournament1 = new Tournament(
            "A Coruña", "Santiago", "Torneo Test", 
            LocalDate.of(2024, 3, 15), LocalDate.of(2024, 3, 17), "Centro Cultural"
        );
        
        Tournament tournament2 = new Tournament(
            "A Coruña", "Santiago", "Torneo Test", 
            LocalDate.of(2024, 3, 15), LocalDate.of(2024, 3, 17), "Centro Cultural"
        );
        
        Tournament tournament3 = new Tournament(
            "A Coruña", "Santiago", "Torneo Test Diferente", 
            LocalDate.of(2024, 3, 15), LocalDate.of(2024, 3, 17), "Centro Cultural"
        );
        
        // Same tournaments should have same hash
        assert tournament1.getHashId().equals(tournament2.getHashId()) : 
            "Same tournaments should have same hash";
        
        // Different tournaments should have different hash
        assert !tournament1.getHashId().equals(tournament3.getHashId()) : 
            "Different tournaments should have different hash";
        
        System.out.println("✓ Tournament hash uniqueness test passed");
        System.out.println("  Hash example: " + tournament1.getHashId());
    }
}