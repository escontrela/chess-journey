package com.davidp.chessjourney.application.service;

import com.davidp.chessjourney.application.config.AppProperties;
import com.davidp.chessjourney.application.factories.RepositoryFactory;
import com.davidp.chessjourney.domain.User;
import com.davidp.chessjourney.domain.UserRepository;
import com.davidp.chessjourney.domain.common.UserElo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of UserService that provides centralized access to user data.
 * Follows singleton pattern to ensure single instance across the application.
 */
public class UserServiceImpl implements UserService {

  private static class Holder {
    private static final UserServiceImpl INSTANCE = new UserServiceImpl();
  }

  public static UserServiceImpl getInstance() {
    return Holder.INSTANCE;
  }

  private UserServiceImpl() {}

  @Override
  public User getActiveUser() {
    long userId = getActiveUserId();
    return getUser(userId);
  }

  @Override
  public List<UserElo> getActiveUserElos() {
    long userId = getActiveUserId();
    return getUserElos(userId);
  }

  @Override
  public Optional<UserElo> getActiveUserEloByType(UUID eloTypeId) {
    long userId = getActiveUserId();
    return getUserEloByType(userId, eloTypeId);
  }

  @Override
  public long getActiveUserId() {
    return AppProperties.getInstance().getActiveUserId();
  }

  // Generic methods for any user ID

  @Override
  public User getUser(long userId) {
    if (userId == 0L) {
      return null;
    }
    
    UserRepository userRepository = RepositoryFactory.createUserRepository();
    return userRepository.getUserById(userId);
  }

  @Override
  public List<UserElo> getUserElos(long userId) {
    if (userId == 0L) {
      return List.of(); // Return empty list if invalid user ID
    }
    
    UserRepository userRepository = RepositoryFactory.createUserRepository();
    return userRepository.getAllUserElos(userId);
  }

  @Override
  public Optional<UserElo> getUserEloByType(long userId, UUID eloTypeId) {
    if (userId == 0L) {
      return Optional.empty();
    }
    
    UserRepository userRepository = RepositoryFactory.createUserRepository();
    return userRepository.getUserEloByType(userId, eloTypeId);
  }
}