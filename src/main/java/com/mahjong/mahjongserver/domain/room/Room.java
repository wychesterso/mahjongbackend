package com.mahjong.mahjongserver.domain.room;

import com.mahjong.mahjongserver.domain.game.Game;
import com.mahjong.mahjongserver.domain.player.Player;
import com.mahjong.mahjongserver.messaging.GameEventPublisher;

import java.util.HashMap;
import java.util.Map;

public class Room {
    private Map<Seat, Player> seatMapping = new HashMap<>();
    private Game currentGame = null;
    private Seat currentSeat = Seat.EAST;

    private GameEventPublisher gameEventPublisher;
    private String roomId;

    public Room(GameEventPublisher gameEventPublisher, String roomId) {
        this.gameEventPublisher = gameEventPublisher;
        this.roomId = roomId;

        for (Seat seat : Seat.values()) {
            seatMapping.put(seat, null);
        }
    }

//============================== GETTERS ==============================//

    public Game getCurrentGame() {
        return currentGame;
    }

//============================== SEATING ==============================//

    public Player getPlayerAtSeat(Seat seat) {
        return seatMapping.get(seat);
    }

    public Seat getSeat(Player player) {
        for (Seat seat : Seat.values()) {
            if (getPlayerAtSeat(seat) == player) {
                return seat;
            }
        }
        return null;
    }

    public boolean addPlayer(Seat seat, Player player) {
        if (seatMapping.get(seat) == null) {
            seatMapping.put(seat, player);
            return true;
        } else {
            return false;
        }
    }

    public void removePlayer(Seat seat) {
        seatMapping.put(seat, null);
    }

//============================== GAME ==============================//

    public boolean startGame() {
        for (Seat seat : Seat.values()) {
            if (seatMapping.get(seat) == null) return false;
        }
        currentGame = new Game(gameEventPublisher, roomId, currentSeat);
        currentSeat = currentSeat.next();
        return true;
    }
}
