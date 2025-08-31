package com.davidp.chessjourney.application.service;

import com.davidp.chessjourney.domain.lichess.LichessUser;

import java.util.Optional;

/**
 * Service interface for Lichess API integration.
 * Provides access to Lichess account data using OAuth access tokens.
 */
public interface LichessService {

  /**
   * Gets the current user's account data from Lichess.
   * 
   * @param accessToken the OAuth access token for Lichess API
   * @return the Lichess user data if successful, empty if failed or no token
   */
  Optional<LichessUser> getCurrentUser(String accessToken);
  
  /**
   * Checks if Lichess integration is available (token configured).
   * 
   * @return true if Lichess access token is configured
   */
  boolean isLichessAvailable();
}