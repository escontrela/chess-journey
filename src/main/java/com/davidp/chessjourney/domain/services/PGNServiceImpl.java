package com.davidp.chessjourney.domain.services;

/**
 * This class is responsible for implements the service for managing the PGN format.
 * <p>
 * <a href="https://en.wikipedia.org/wiki/Portable_Game_Notation">...</a>
 */
public class PGNServiceImpl implements PGNService {

  private static class Holder {

    private static final PGNServiceImpl INSTANCE = new PGNServiceImpl();
  }


  public static PGNServiceImpl getInstance() {

    return PGNServiceImpl.Holder.INSTANCE;
  }
}