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
 * Implementation of UserService that provides centralized access to user data. Follows singleton
 * pattern to ensure single instance across the application.
 */
public class UserServiceImpl implements UserService {

  private static class Holder {

    private static final UserServiceImpl INSTANCE =
        new UserServiceImpl(RepositoryFactory.createUserRepository());
  }

  UserRepository userRepository;

  public static UserServiceImpl getInstance() {

    return Holder.INSTANCE;
  }

  private UserServiceImpl(UserRepository userRepository) {

    this.userRepository = userRepository;
  }

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

    if (isNanUserOnProperties(userId)) {
      return null;
    }

    return userRepository.getUserById(userId);
  }

  private static boolean isNanUserOnProperties(long userId) {
    return userId == 0L;
  }

  @Override
  public List<UserElo> getUserElos(long userId) {

    if (isNanUserOnProperties(userId)) {
      return List.of();
    }

    return userRepository.getAllUserElos(userId);
  }

  @Override
  public Optional<UserElo> getUserEloByType(long userId, UUID eloTypeId) {

    if (isNanUserOnProperties(userId)) {
      return Optional.empty();
    }

    return userRepository.getUserEloByType(userId, eloTypeId);
  }
}