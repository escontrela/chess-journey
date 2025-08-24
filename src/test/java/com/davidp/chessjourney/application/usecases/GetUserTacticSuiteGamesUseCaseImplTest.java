package com.davidp.chessjourney.application.usecases;

import com.davidp.chessjourney.domain.games.tactic.TacticSuiteGame;
import com.davidp.chessjourney.domain.games.tactic.TacticSuiteGameRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GetUserTacticSuiteGamesUseCaseImpl.
 */
public class GetUserTacticSuiteGamesUseCaseImplTest {

    private GetUserTacticSuiteGamesUseCaseImpl useCase;
    private MockTacticSuiteGameRepository mockRepository;

    @BeforeEach
    void setUp() {
        mockRepository = new MockTacticSuiteGameRepository();
        useCase = new GetUserTacticSuiteGamesUseCaseImpl(mockRepository);
    }

    @Test
    void execute_withValidUserId_returnsTacticSuiteGames() {
        // Arrange
        long userId = 123L;
        TacticSuiteGame suite1 = new TacticSuiteGame(UUID.randomUUID(), "Suite 1", TacticSuiteGame.Type.RANDOM);
        TacticSuiteGame suite2 = new TacticSuiteGame(UUID.randomUUID(), "Suite 2", TacticSuiteGame.Type.FIXED);
        mockRepository.setMockData(Arrays.asList(suite1, suite2));

        // Act
        List<TacticSuiteGame> result = useCase.execute(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Suite 1", result.get(0).getName());
        assertEquals("Suite 2", result.get(1).getName());
        assertEquals(userId, mockRepository.getLastRequestedUserId());
    }

    @Test
    void execute_withUserWithNoSuites_returnsEmptyList() {
        // Arrange
        long userId = 456L;
        mockRepository.setMockData(Arrays.asList());

        // Act
        List<TacticSuiteGame> result = useCase.execute(userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(userId, mockRepository.getLastRequestedUserId());
    }

    // Simple mock repository for testing
    private static class MockTacticSuiteGameRepository implements TacticSuiteGameRepository {
        private List<TacticSuiteGame> mockData;
        private long lastRequestedUserId;

        public void setMockData(List<TacticSuiteGame> mockData) {
            this.mockData = mockData;
        }

        public long getLastRequestedUserId() {
            return lastRequestedUserId;
        }

        @Override
        public List<TacticSuiteGame> getByUserId(long userId) {
            this.lastRequestedUserId = userId;
            return mockData;
        }

        // Other methods not needed for this test
        @Override
        public UUID save(TacticSuiteGame tacticSuiteGame) { return null; }

        @Override
        public TacticSuiteGame getById(UUID id) { return null; }

        @Override
        public List<TacticSuiteGame> getAll() { return null; }

        @Override
        public List<TacticSuiteGame> getByType(TacticSuiteGame.Type type) { return null; }

        @Override
        public void associateUser(UUID tacticSuiteGameId, long userId) {}

        @Override
        public void removeUserAssociation(UUID tacticSuiteGameId, long userId) {}

        @Override
        public List<com.davidp.chessjourney.domain.common.Exercise> getExercisesForSuite(UUID tacticSuiteGameId) { return null; }

        @Override
        public void addExerciseToSuite(UUID tacticSuiteGameId, UUID exerciseId, int sequenceOrder) {}

        @Override
        public void removeExerciseFromSuite(UUID tacticSuiteGameId, UUID exerciseId) {}
    }
}