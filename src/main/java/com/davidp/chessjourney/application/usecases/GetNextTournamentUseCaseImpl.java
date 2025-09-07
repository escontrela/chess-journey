package com.davidp.chessjourney.application.usecases;

import com.davidp.chessjourney.application.service.TournamentsManagementService;
import com.davidp.chessjourney.domain.Tournament;

public class GetNextTournamentUseCaseImpl implements GetNextTournamentUseCase {

    private final TournamentsManagementService tournamentsManagementService;

    /** Constructor que recibe el servicio de gestión de torneos (inyección de dependencias). */
    public GetNextTournamentUseCaseImpl(TournamentsManagementService tournamentsManagementService) {
        this.tournamentsManagementService = tournamentsManagementService;
    }

    @Override
    public Tournament execute() {
        return tournamentsManagementService.getNextUpcomingTournament();
    }
}