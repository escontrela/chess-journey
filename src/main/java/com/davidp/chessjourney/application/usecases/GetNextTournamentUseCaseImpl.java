package com.davidp.chessjourney.application.usecases;

import com.davidp.chessjourney.application.service.LookUpTournamentsService;
import com.davidp.chessjourney.domain.Tournament;

public class GetNextTournamentUseCaseImpl implements GetNextTournamentUseCase {

    private final LookUpTournamentsService lookUpTournamentsService;

    /** Constructor que recibe el servicio de búsqueda de torneos (inyección de dependencias). */
    public GetNextTournamentUseCaseImpl(LookUpTournamentsService lookUpTournamentsService) {
        this.lookUpTournamentsService = lookUpTournamentsService;
    }

    @Override
    public Tournament execute() {
        return lookUpTournamentsService.getNextUpcomingTournament();
    }
}