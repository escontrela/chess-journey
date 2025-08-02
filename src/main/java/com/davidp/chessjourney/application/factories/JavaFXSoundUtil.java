package com.davidp.chessjourney.application.factories;

import javafx.scene.media.AudioClip;
import java.net.URL;
import java.util.EnumMap;
import java.util.Optional;

public class JavaFXSoundUtil<E extends Enum<E>> {
    private final EnumMap<E, AudioClip> soundCache;
    private final ClassLoader classLoader = getClass().getClassLoader();

    public JavaFXSoundUtil(Class<E> enumClass, SoundResourceProvider<E> provider) {
        soundCache = new EnumMap<>(enumClass);
        for (E type : enumClass.getEnumConstants()) {
            String resource = provider.getResourceName(type);
            URL url = classLoader.getResource("assets/sounds/" + resource);
            if (url != null) {
                soundCache.put(type, new AudioClip(url.toExternalForm()));
            }
        }
    }

    public void playSound(E type) {
        Optional.ofNullable(soundCache.get(type)).ifPresent(AudioClip::play);
    }

    public interface SoundResourceProvider<T> {
        String getResourceName(T type);
    }
}
