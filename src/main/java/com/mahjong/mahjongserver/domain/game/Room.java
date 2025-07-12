package com.mahjong.mahjongserver.domain.game;

import com.mahjong.mahjongserver.domain.core.TurnManager;
import com.mahjong.mahjongserver.domain.player.Player;
import com.mahjong.mahjongserver.domain.player.data.Seat;
import com.mahjong.mahjongserver.messaging.GameEventPublisher;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private final String id;
    private final List<Player> players;
    private final List<Seat> availableSeats;
    private final GameEventPublisher publisher;

    public Room(String id, List<Player> players, GameEventPublisher publisher) {
        this.id = id;
        this.players = players;
        this.availableSeats = new ArrayList<>(List.of(Seat.EAST, Seat.SOUTH, Seat.WEST, Seat.NORTH));
        for (Player player : players) {
            Seat seat = player.getSeat();
            if (seat != null) {
                availableSeats.remove(seat);
            }
        }
        this.publisher = publisher;
    }

    public String getId() {
        return id;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Seat> getAvailableSeats() {
        return availableSeats;
    }

    public GameEventPublisher getPublisher() {
        return publisher;
    }

    public void configurePlayers() {

    }

    public Game createGame() {
        return new Game(players, publisher, id);
    }
}