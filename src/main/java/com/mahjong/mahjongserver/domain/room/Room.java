package com.mahjong.mahjongserver.domain.room;

import com.mahjong.mahjongserver.domain.game.Game;
import com.mahjong.mahjongserver.domain.player.Player;
import com.mahjong.mahjongserver.domain.player.context.PlayerContext;
import com.mahjong.mahjongserver.domain.player.decision.PlayerDecisionHandler;
import com.mahjong.mahjongserver.domain.core.GameEventPublisher;

import java.util.HashMap;
import java.util.Map;

public class Room {
    private Map<Seat, PlayerContext> playerContexts = new HashMap<>();
    private Game currentGame = null;
    private Seat currentSeat = Seat.EAST;

    private GameEventPublisher gameEventPublisher;
    private String roomId;

    public Room(GameEventPublisher gameEventPublisher, String roomId) {
        this.gameEventPublisher = gameEventPublisher;
        this.roomId = roomId;

        for (Seat seat : Seat.values()) {
            playerContexts.put(seat, null);
        }
    }

//============================== GETTERS ==============================//

    public Game getCurrentGame() {
        return currentGame;
    }

    public Seat getCurrentSeat() {
        return currentSeat;
    }

    public GameEventPublisher getGameEventPublisher() {
        return gameEventPublisher;
    }

    public String getRoomId() {
        return roomId;
    }

    //============================== SEATING ==============================//

    public PlayerContext getPlayerContext(Seat seat) {
        return playerContexts.get(seat);
    }

    public Seat getSeat(Player player) {
        for (Seat seat : Seat.values()) {
            if (getPlayerContext(seat).getPlayer() == player) {
                return seat;
            }
        }
        return null;
    }

    public boolean addPlayer(Seat seat, Player player, PlayerDecisionHandler decisionHandler) {
        if (playerContexts.get(seat) == null) {
            playerContexts.put(seat, new PlayerContext(player, decisionHandler));
            return true;
        } else {
            return false;
        }
    }

    public void removePlayer(Seat seat) {
        playerContexts.put(seat, null);
    }

//============================== GAME ==============================//

    public boolean startGame() {
        for (Seat seat : Seat.values()) {
            if (playerContexts.get(seat) == null) return false;
        }
        currentGame = new Game(this, currentSeat);
        currentSeat = currentSeat.next();
        return true;
    }
}
