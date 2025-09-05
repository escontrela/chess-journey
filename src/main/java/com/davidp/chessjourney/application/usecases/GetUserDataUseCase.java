package com.davidp.chessjourney.application.usecases;

import com.davidp.chessjourney.domain.lichess.UserData;

/**
 * Use case for getting combined user data (local + Lichess).
 */
public interface GetUserDataUseCase {

  /**
   * Gets combined user data for the active user.
   * Includes local user data from database and Lichess data if available.
   * 
   * @return combined user data
   */
  UserData execute();
}