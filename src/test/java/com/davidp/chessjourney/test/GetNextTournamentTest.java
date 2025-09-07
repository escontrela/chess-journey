package com.davidp.chessjourney.test;

import com.davidp.chessjourney.application.usecases.GetNextTournamentUseCaseImpl;
import com.davidp.chessjourney.domain.Tournament;

/**
 * Simple test to validate the GetNextTournament functionality
 */
public class GetNextTournamentTest {
    
    public static void main(String[] args) {
        System.out.println("Testing GetNextTournament functionality...");
        
        // Create mock service
        MockLookUpTournamentsService mockService = new MockLookUpTournamentsService();
        
        // Create use case with mock service
        GetNextTournamentUseCaseImpl useCase = new GetNextTournamentUseCaseImpl(mockService);
        
        try {
            // Test getting next tournament
            Tournament nextTournament = useCase.execute();
            
            if (nextTournament != null) {
                System.out.println("✓ Successfully found next tournament:");
                System.out.println("  Name: " + nextTournament.getTorneo());
                System.out.println("  Location: " + nextTournament.getConcejo());
                System.out.println("  Start Date: " + nextTournament.getInicio());
                System.out.println("  Venue: " + nextTournament.getLocal());
                
                // Validate it's a future tournament
                if (nextTournament.getInicio().isAfter(java.time.LocalDate.now())) {
                    System.out.println("✓ Tournament is in the future as expected");
                } else {
                    System.out.println("✗ ERROR: Tournament should be in the future");
                }
            } else {
                System.out.println("✗ No tournament found");
            }
            
        } catch (Exception e) {
            System.out.println("✗ Error during test: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("Test completed.");
    }
}