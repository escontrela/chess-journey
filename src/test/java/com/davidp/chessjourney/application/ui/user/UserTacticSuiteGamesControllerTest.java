package com.davidp.chessjourney.application.ui.user;

import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import com.davidp.chessjourney.application.ui.settings.SettingsViewInputScreenData;
import com.davidp.chessjourney.application.usecases.GetUserTacticSuiteGamesUseCase;
import com.davidp.chessjourney.domain.games.tactic.TacticSuiteGame;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for UserTacticSuiteGamesController functionality.
 */
public class UserTacticSuiteGamesControllerTest {

    private UserTacticSuiteGamesController controller;
    private MockGetUserTacticSuiteGamesUseCase mockUseCase;

    @BeforeEach
    void setUp() {
        controller = new UserTacticSuiteGamesController();
        mockUseCase = new MockGetUserTacticSuiteGamesUseCase();
        controller.setGetUserTacticSuiteGamesUseCase(mockUseCase);
    }

    @Test
    void setData_withValidInput_callsUseCase() {
        // Arrange
        long userId = 123L;
        Point position = new Point(100, 200);
        SettingsViewInputScreenData inputData = new SettingsViewInputScreenData(userId, position);
        
        TacticSuiteGame suite1 = new TacticSuiteGame(UUID.randomUUID(), "Test Suite 1", TacticSuiteGame.Type.RANDOM);
        TacticSuiteGame suite2 = new TacticSuiteGame(UUID.randomUUID(), "Test Suite 2", TacticSuiteGame.Type.FIXED);
        mockUseCase.setMockData(Arrays.asList(suite1, suite2));

        // Act
        controller.setData(inputData);

        // Assert
        assertEquals(userId, mockUseCase.getLastRequestedUserId());
        assertTrue(mockUseCase.wasExecuted());
    }

    @Test
    void setData_withNoTacticSuites_handlesEmptyList() {
        // Arrange
        long userId = 456L;
        Point position = new Point(150, 250);
        SettingsViewInputScreenData inputData = new SettingsViewInputScreenData(userId, position);
        
        mockUseCase.setMockData(Arrays.asList());

        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> controller.setData(inputData));
        assertEquals(userId, mockUseCase.getLastRequestedUserId());
        assertTrue(mockUseCase.wasExecuted());
    }

    // Mock implementation for testing
    private static class MockGetUserTacticSuiteGamesUseCase implements GetUserTacticSuiteGamesUseCase {
        private List<TacticSuiteGame> mockData;
        private long lastRequestedUserId;
        private boolean executed = false;

        public void setMockData(List<TacticSuiteGame> mockData) {
            this.mockData = mockData;
        }

        public long getLastRequestedUserId() {
            return lastRequestedUserId;
        }

        public boolean wasExecuted() {
            return executed;
        }

        @Override
        public List<TacticSuiteGame> execute(long userId) {
            this.lastRequestedUserId = userId;
            this.executed = true;
            return mockData;
        }
    }
}