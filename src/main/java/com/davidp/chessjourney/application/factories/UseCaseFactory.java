package com.davidp.chessjourney.application.factories;

import com.davidp.chessjourney.application.usecases.*;
import com.davidp.chessjourney.application.factories.ApplicationServiceFactory;
import com.davidp.chessjourney.domain.UserRepository;
import com.davidp.chessjourney.domain.common.*;
import com.davidp.chessjourney.domain.games.memory.MemoryGame;
import com.davidp.chessjourney.domain.games.tactic.TacticSuiteGameRepository;

/** Factoría para instanciar casos de uso (UseCases). */
public class UseCaseFactory {

    private UseCaseFactory() {
        // Evitar instancias
    }

    /** Crea el caso de uso GetUsersUseCase usando la implementación de repositorio adecuada. */
    public static GetUsersUseCase createGetUsersUseCase() {
        // 2) Crear el repositorio a través de la factoría de repositorios
        UserRepository userRepo = RepositoryFactory.createUserRepository();

        // 3) Instanciar la implementación del caso de uso, inyectando el repo
        return new GetUsersUseCaseImpl(userRepo);
    }

    public static GetUserByIdUseCase createGetUserByIdUseCase() {

        UserRepository userRepo = RepositoryFactory.createUserRepository();
        return new GetUserByIdUseCaseImpl(userRepo);
    }

    public static SaveUserUseCase createSaveUserUseCase() {

        UserRepository userRepo = RepositoryFactory.createUserRepository();
        return new SaveUserUseCaseImpl(userRepo);
    }

    public static SaveActiveUserUseCase createSaveActiveUserUseCase() {

        SaveActiveUserUseCase activeUserUseCase = new SaveActiveUserUseCaseImpl();
        return new SaveActiveUserUseCaseImpl();
    }


    public static  MemoryGameUseCase<MemoryGame<PiecePosition>>  createGuessMemoryGameUseCase() {
        //TODO make values objects for PiecePosition and String for GuessMemoryGameSubmit
        UserRepository userRepo = RepositoryFactory.createUserRepository();
        ExerciseRepository exerciseRepository = RepositoryFactory.createExerciseRepository();
        DifficultyLevelRepository difficultyLevelRepository = RepositoryFactory.createDifficultyLevelRepository();
        ExerciseTypeRepository exerciseTypeRepository = RepositoryFactory.createExerciseTypeRepository();


        return new GuessMemoryGameUseCaseImpl(userRepo, exerciseRepository, difficultyLevelRepository,exerciseTypeRepository);
    }

    public static  TacticGameUseCase  createTacticGameUseCase() {

        UserRepository userRepo = RepositoryFactory.createUserRepository();
        ExerciseRepository exerciseRepository = RepositoryFactory.createExerciseRepository();
        DifficultyLevelRepository difficultyLevelRepository = RepositoryFactory.createDifficultyLevelRepository();
        ExerciseTypeRepository exerciseTypeRepository = RepositoryFactory.createExerciseTypeRepository();

    return new TacticGameUseCaseImpl(
        userRepo, exerciseRepository, difficultyLevelRepository, exerciseTypeRepository);
    }


    public static MemoryGameUseCase<MemoryGame<String>>  createDefendMemoryGameUseCase() {
        //TODO make values objects for PiecePosition and String for DefendMemoryGameSubmit
        UserRepository userRepo = RepositoryFactory.createUserRepository();
        ExerciseRepository exerciseRepository = RepositoryFactory.createExerciseRepository();
        DifficultyLevelRepository difficultyLevelRepository = RepositoryFactory.createDifficultyLevelRepository();
        ExerciseTypeRepository exerciseTypeRepository = RepositoryFactory.createExerciseTypeRepository();

        return new DefendMemoryGameUseCaseImpl(userRepo, exerciseRepository, difficultyLevelRepository,exerciseTypeRepository);
    }

    public static GetAllTagsUseCase createGetAllTagsUseCase() {

        TagRepository tagRepository = RepositoryFactory.createTagRepository();
        return new GetAllTagsUseCaseImpl(tagRepository);
    }

    public static SaveUserExerciseStatsUseCase createSaveUserExerciseStatsUseCase() {

        UserRepository userRepo = RepositoryFactory.createUserRepository();
        return new SaveUserExerciseStatsUseCaseImpl(userRepo);
    }

    public static GetUserStatsForLastNDaysUseCase createGetUserStatsForLastNDaysUseCase() {

        UserRepository userRepo = RepositoryFactory.createUserRepository();
        return new GetUserStatsForLastNDaysUseCaseImpl(userRepo);
    }

    public static GetRandomQuoteUseCase createGetRandomQuoteUseCase() {
        return new GetRandomQuoteUseCase(RepositoryFactory.createQuoteRepository());
    }

    public static SaveQuoteUseCase createSaveQuoteUseCase() {
        return new SaveQuoteUseCase(RepositoryFactory.createQuoteRepository());
    }

    public static TacticSuiteGameUseCase createTacticSuiteGameUseCase() {
        UserRepository userRepo = RepositoryFactory.createUserRepository();
        ExerciseRepository exerciseRepository = RepositoryFactory.createExerciseRepository();
        DifficultyLevelRepository difficultyLevelRepository = RepositoryFactory.createDifficultyLevelRepository();
        ExerciseTypeRepository exerciseTypeRepository = RepositoryFactory.createExerciseTypeRepository();
        TacticSuiteGameRepository tacticSuiteGameRepository = RepositoryFactory.createTacticSuiteGameRepository();

        return new TacticSuiteGameUseCaseImpl(
            userRepo, exerciseRepository, difficultyLevelRepository, exerciseTypeRepository, tacticSuiteGameRepository);
    }

    public static GetUserTacticSuiteGamesUseCase createGetUserTacticSuiteGamesUseCase() {
        TacticSuiteGameRepository tacticSuiteGameRepository = RepositoryFactory.createTacticSuiteGameRepository();
        return new GetUserTacticSuiteGamesUseCaseImpl(tacticSuiteGameRepository);
    }

    public static GetUserDataUseCase createGetUserDataUseCase() {
        return new GetUserDataUseCaseImpl(
            ApplicationServiceFactory.createUserService(),
            ApplicationServiceFactory.createLichessService()
        );
    }
}