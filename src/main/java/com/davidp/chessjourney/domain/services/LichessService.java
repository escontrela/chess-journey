package com.davidp.chessjourney.domain.services;

import com.davidp.chessjourney.domain.lichess.LichessUser;

import java.util.Optional;

/**
 * Service interface for Lichess API integration.
 * Provides access to Lichess account data using OAuth access tokens per user.
 */
public interface LichessService {

  /**
   * Gets the user's account data from Lichess for the specified user ID.
   * 
   * @param userId the local user ID to get Lichess data for
   * @return the Lichess user data if successful, empty if failed or no token configured
   */
  Optional<LichessUser> getCurrentUser(long userId);
  
  /**
   * Checks if Lichess integration is available for the specified user (token configured).
   * 
   * @param userId the local user ID to check
   * @return true if Lichess access token is configured for this user
   */
  boolean isLichessAvailable(long userId);
}