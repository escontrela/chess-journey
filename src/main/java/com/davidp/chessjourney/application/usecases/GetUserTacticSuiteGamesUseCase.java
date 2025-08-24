package com.davidp.chessjourney.application.usecases;

import com.davidp.chessjourney.domain.games.tactic.TacticSuiteGame;
import java.util.List;

/**
 * Use case to retrieve all TacticSuiteGame exercises associated with a user.
 */
public interface GetUserTacticSuiteGamesUseCase {
    
    /**
     * Execute the use case to get all TacticSuiteGames for a specific user.
     * 
     * @param userId The ID of the user
     * @return List of TacticSuiteGame objects associated with the user
     */
    List<TacticSuiteGame> execute(long userId);
}