package com.davidp.chessjourney.application.usecases;

import com.davidp.chessjourney.application.service.UserService;
import com.davidp.chessjourney.domain.services.LichessService;
import com.davidp.chessjourney.domain.User;
import com.davidp.chessjourney.domain.lichess.LichessUser;
import com.davidp.chessjourney.domain.lichess.UserData;

import java.util.Optional;

/**
 * Implementation of GetUserDataUseCase that combines local and Lichess user data.
 */
public class GetUserDataUseCaseImpl implements GetUserDataUseCase {

  private final UserService userService;
  private final LichessService lichessService;

  public GetUserDataUseCaseImpl(UserService userService, LichessService lichessService) {
    this.userService = userService;
    this.lichessService = lichessService;
  }

  @Override
  public UserData execute() {
    // Get local user data
    User localUser = userService.getActiveUser();
    long userId = localUser.getId();
    UserData userData = new UserData(localUser);

    // Try to get Lichess data if token is available for this user
    if (lichessService.isLichessAvailable(userId)) {
      Optional<LichessUser> lichessUser = lichessService.getCurrentUser(userId);
      
      if (lichessUser.isPresent()) {
        userData.setLichessUser(lichessUser.get());
        System.out.println("✅ Lichess data loaded for user: " + lichessUser.get().getUsername());
      } else {
        userData.setLichessError("Failed to load Lichess data. Check your access token.");
        System.out.println("⚠️ Failed to load Lichess data");
      }
    } else {
      System.out.println("ℹ️ No Lichess access token configured for user " + userId);
    }

    return userData;
  }
}