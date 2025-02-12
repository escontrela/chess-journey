package com.davidp.chessjourney.application.factories;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getAssetLoader;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getAudioPlayer;

import com.almasb.fxgl.audio.Sound;
import java.util.EnumMap;
import java.util.Optional;

/** This class is responsible for managing and playing game sounds. */
public class SoundServiceFactory {

  /** Enum for managing the sounds of the game. */
  public enum SoundType {
    PIECE_PLACEMENT("piece-placement.wav"),
    PIECE_PLACEMENT_ERROR("piece-placement-error.wav"),
    NEW_GAME("new-game.wav");

    private final String resourcePath;

    SoundType(final String resourcePath) {
      this.resourcePath = resourcePath;
    }

    public String resourceName() {
      return this.resourcePath;
    }
  }

  private static volatile SoundServiceFactory instance;
  private final EnumMap<SoundType, Sound> soundCache;

  private SoundServiceFactory() {

    // Initialize and preload sounds
    soundCache = new EnumMap<>(SoundType.class);

    for (SoundType type : SoundType.values()) {

      soundCache.put(type, getAssetLoader().loadSound(type.resourceName()));
    }
  }

  public static SoundServiceFactory getInstance() {
    if (instance == null) {
      synchronized (SoundServiceFactory.class) {
        if (instance == null) {
          instance = new SoundServiceFactory();
        }
      }
    }
    return instance;
  }

  /**
   * Plays the specified sound type.
   *
   * @param soundType the type of sound to play
   */
  public void playSound(SoundType soundType) {

    Optional.ofNullable(soundCache.get(soundType))
        .ifPresent(sound -> getAudioPlayer().playSound(sound));
  }
}
