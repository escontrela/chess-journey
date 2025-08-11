package com.davidp.chessjourney.api;

import com.davidp.chessjourney.application.config.AppProperties;
import com.davidp.chessjourney.application.usecases.GetUserByIdUseCase;
import com.davidp.chessjourney.domain.Quote;
import com.davidp.chessjourney.domain.User;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;

/**
 * Controlador para manejar las peticiones relacionadas con el usuario activo. Proporciona un
 * endpoint para obtener los detalles del usuario activo. TODO this controller should be refactored
 * to use a service layer, and one controller for all the use cases, the controller should be more
 * generic and not tied to a specific use case.
 */
public class ActiveUserController {

  private  GetUserByIdUseCase getUserByIdUseCase = null;

  private ActiveUserController(){}

  public ActiveUserController(GetUserByIdUseCase getUserByIdUseCase) {

    this.getUserByIdUseCase = getUserByIdUseCase;

  }


  public Handler getActiveUser = ctx -> {

    long activeUserId = AppProperties.getInstance().getActiveUserId();
    User user = getUserByIdUseCase.execute(activeUserId);

    if (user == null) {

      ctx.status(404).json(new ErrorResponse("No active user found"));
      return;
    }

    ctx.json(new UserResponse(user));

  };

  private static class UserResponse {

    private final long id;
    private final String firstname;
    private final String lastname;
    private final String email;
    private final String initials;

    public UserResponse(User user) {

      this.id = user.getId();
      this.firstname = user.getFirstname();
      this.lastname = user.getLastname();
      this.email = user.getEmail();
      this.initials = user.getInitials();
    }

    // Getters necesarios para la serialización JSON
    public long getId() {
      return id;
    }

    public String getFirstname() {
      return firstname;
    }

    public String getLastname() {
      return lastname;
    }

    public String getEmail() {
      return email;
    }

    public String getInitials() {
      return initials;
    }
  }

  private static class ErrorResponse {

    private final String message;

    public ErrorResponse(String message) {
      this.message = message;
    }

    public String getMessage() {
      return message;
    }
  }
}
