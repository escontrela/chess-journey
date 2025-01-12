package com.davidp.chessjourney.domain;

import java.util.List;

public interface UserRepository {

  List<User> getAll();

  User getUserById(long id);

  boolean update(User user);
}
