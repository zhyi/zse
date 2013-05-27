/*
 * Copyright (C) 2013 Zhao Yi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package zhyi.zse.message;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A very simple message service.
 *
 * @param <T> The message's type.
 *
 * @author Zhao Yi
 */
public class MessageService<T> {
    private List<MessageListener<? super T>> listeners;

    /**
     * Constructs a new message service.
     */
    public MessageService() {
        listeners = new CopyOnWriteArrayList<>();
    }

    /**
     * Subscribes a message listener to receive messages.
     *
     * @param l The message listener to be subscribed.
     */
    public void subscribe(MessageListener<? super T> l) {
        listeners.add(l);
    }

    /**
     * Unsubscribes a message listener.
     *
     * @param l The message listener to be unsubscribed.
     */
    public void unsubscribe(MessageListener<? super T> l) {
        listeners.remove(l);
    }

    /**
     * Publishes a message to all subscribed message listeners.
     *
     * @param message The message to be published.
     */
    public void publish(T message) {
        for (MessageListener<? super T> l : listeners) {
            l.onMessage(message);
        }
    }
}
