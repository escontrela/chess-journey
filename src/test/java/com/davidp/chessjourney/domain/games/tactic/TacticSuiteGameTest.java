package com.davidp.chessjourney.domain.games.tactic;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the TacticSuiteGame class.
 */
public class TacticSuiteGameTest {

    @Test
    void constructor_withValidParameters_createsInstance() {
        UUID id = UUID.randomUUID();
        String name = "Test Suite";
        TacticSuiteGame.Type type = TacticSuiteGame.Type.RANDOM;

        TacticSuiteGame suite = new TacticSuiteGame(id, name, type);

        assertEquals(id, suite.getId());
        assertEquals(name, suite.getName());
        assertEquals(type, suite.getType());
        assertNotNull(suite.getCreatedAt());
        assertNotNull(suite.getUpdatedAt());
        assertNull(suite.getExercises());
    }

    @Test
    void constructor_withTimestamps_preservesTimestamps() {
        UUID id = UUID.randomUUID();
        String name = "Test Suite";
        TacticSuiteGame.Type type = TacticSuiteGame.Type.FIXED;
        
        TacticSuiteGame suite = new TacticSuiteGame(id, name, type);
        
        TacticSuiteGame suiteWithTimestamps = new TacticSuiteGame(
            id, name, type, suite.getCreatedAt(), suite.getUpdatedAt());

        assertEquals(id, suiteWithTimestamps.getId());
        assertEquals(name, suiteWithTimestamps.getName());
        assertEquals(type, suiteWithTimestamps.getType());
        assertEquals(suite.getCreatedAt(), suiteWithTimestamps.getCreatedAt());
        assertEquals(suite.getUpdatedAt(), suiteWithTimestamps.getUpdatedAt());
    }

    @Test
    void equals_withSameId_returnsTrue() {
        UUID id = UUID.randomUUID();
        TacticSuiteGame suite1 = new TacticSuiteGame(id, "Suite 1", TacticSuiteGame.Type.RANDOM);
        TacticSuiteGame suite2 = new TacticSuiteGame(id, "Suite 2", TacticSuiteGame.Type.FIXED);

        assertEquals(suite1, suite2);
    }

    @Test
    void equals_withDifferentId_returnsFalse() {
        TacticSuiteGame suite1 = new TacticSuiteGame(UUID.randomUUID(), "Suite 1", TacticSuiteGame.Type.RANDOM);
        TacticSuiteGame suite2 = new TacticSuiteGame(UUID.randomUUID(), "Suite 1", TacticSuiteGame.Type.RANDOM);

        assertNotEquals(suite1, suite2);
    }

    @Test
    void hashCode_withSameId_returnsSameHashCode() {
        UUID id = UUID.randomUUID();
        TacticSuiteGame suite1 = new TacticSuiteGame(id, "Suite 1", TacticSuiteGame.Type.RANDOM);
        TacticSuiteGame suite2 = new TacticSuiteGame(id, "Suite 2", TacticSuiteGame.Type.FIXED);

        assertEquals(suite1.hashCode(), suite2.hashCode());
    }

    @Test
    void toString_containsBasicInfo() {
        UUID id = UUID.randomUUID();
        String name = "Test Suite";
        TacticSuiteGame.Type type = TacticSuiteGame.Type.RANDOM;

        TacticSuiteGame suite = new TacticSuiteGame(id, name, type);

        String result = suite.toString();
        assertTrue(result.contains(id.toString()));
        assertTrue(result.contains(name));
        assertTrue(result.contains(type.toString()));
    }
}