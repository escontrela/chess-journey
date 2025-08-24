package com.davidp.chessjourney.application.usecases;

import com.davidp.chessjourney.domain.games.tactic.TacticSuiteGame;
import com.davidp.chessjourney.domain.games.tactic.TacticSuiteGameRepository;
import java.util.List;

/**
 * Implementation of GetUserTacticSuiteGamesUseCase.
 */
public class GetUserTacticSuiteGamesUseCaseImpl implements GetUserTacticSuiteGamesUseCase {

    private final TacticSuiteGameRepository tacticSuiteGameRepository;

    public GetUserTacticSuiteGamesUseCaseImpl(TacticSuiteGameRepository tacticSuiteGameRepository) {
        this.tacticSuiteGameRepository = tacticSuiteGameRepository;
    }

    @Override
    public List<TacticSuiteGame> execute(long userId) {
        return tacticSuiteGameRepository.getByUserId(userId);
    }
}