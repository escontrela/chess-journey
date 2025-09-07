package com.davidp.chessjourney.application.usecases;

import com.davidp.chessjourney.domain.Tournament;

/** Caso de uso para obtener el siguiente torneo más próximo. */
public interface GetNextTournamentUseCase {

    /** Retorna el siguiente torneo más próximo en el tiempo, o null si no hay torneos futuros. */
    Tournament execute();
}