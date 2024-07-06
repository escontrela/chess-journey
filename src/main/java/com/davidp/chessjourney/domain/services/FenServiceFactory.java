package com.davidp.chessjourney.domain.services;

/** Factory class for {@link FenService} */
public class FenServiceFactory {

  public static FenService getFenService() {

    return FenServiceImpl.getInstance();
  }
}
