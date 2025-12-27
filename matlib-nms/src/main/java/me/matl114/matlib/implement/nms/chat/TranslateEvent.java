package me.matl114.matlib.implement.nms.chat;

import java.util.Locale;
import lombok.Getter;
import me.matl114.matlib.algorithms.designs.event.Event;
import me.matl114.matlib.implement.nms.network.ClientInformation;

@Getter
public class TranslateEvent<T> extends Event {
    public TranslateEvent(ClientInformation information, T value, Locale locale) {
        this.client = information;
        this.value = value;
        this.locale = locale;
    }

    final ClientInformation client;
    final T value;
    final Locale locale;

    public void setCancelled(boolean var1) {
        throw new UnsupportedOperationException("This Event must not be cancelled");
    }
}
