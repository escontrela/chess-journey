package com.davidp.chessjourney.domain.services;

/** Factory class for {@link FenService} */
public class PGNServiceFactory {

  public static PGNService getPGNService() {

    return PGNServiceImpl.getInstance();
  }
}
