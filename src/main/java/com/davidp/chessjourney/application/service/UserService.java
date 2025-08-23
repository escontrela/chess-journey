package com.davidp.chessjourney.application.service;

import com.davidp.chessjourney.domain.User;
import com.davidp.chessjourney.domain.common.UserElo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for user-related operations.
 * Provides centralized access to user data including basic info and ELO ratings.
 */
public interface UserService {

  /**
   * Gets the active user data including basic information.
   *
   * @return the active user, or null if not found
   */
  User getActiveUser();

  /**
   * Gets all ELO ratings for the active user.
   *
   * @return list of UserElo for the active user, may be empty if user has no ELOs
   */
  List<UserElo> getActiveUserElos();

  /**
   * Gets a specific ELO rating for the active user by ELO type.
   *
   * @param eloTypeId the ID of the ELO type
   * @return the UserElo if it exists, Optional.empty() otherwise
   */
  Optional<UserElo> getActiveUserEloByType(UUID eloTypeId);

  /**
   * Gets the active user ID from application properties.
   *
   * @return the active user ID
   */
  long getActiveUserId();

  // Generic methods for any user ID

  /**
   * Gets user data by ID.
   *
   * @param userId the user ID
   * @return the user, or null if not found
   */
  User getUser(long userId);

  /**
   * Gets all ELO ratings for a specific user.
   *
   * @param userId the user ID
   * @return list of UserElo for the user, may be empty if user has no ELOs
   */
  List<UserElo> getUserElos(long userId);

  /**
   * Gets a specific ELO rating for a user by ELO type.
   *
   * @param userId the user ID
   * @param eloTypeId the ID of the ELO type
   * @return the UserElo if it exists, Optional.empty() otherwise
   */
  Optional<UserElo> getUserEloByType(long userId, UUID eloTypeId);
}