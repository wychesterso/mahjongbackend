package com.mahjong.mahjongserver.domain.room;

import com.mahjong.mahjongserver.domain.game.Game;
import com.mahjong.mahjongserver.domain.game.score.ScoreCalculator;
import com.mahjong.mahjongserver.domain.player.Player;
import com.mahjong.mahjongserver.domain.player.context.PlayerContext;
import com.mahjong.mahjongserver.domain.player.decision.PlayerDecisionHandler;
import com.mahjong.mahjongserver.domain.core.GameEventPublisher;
import com.mahjong.mahjongserver.domain.player.decision.EndGameDecision;
import com.mahjong.mahjongserver.infrastructure.TimeoutScheduler;

import java.util.HashMap;
import java.util.Map;

public class Room {
    private Player host;
    private Map<Seat, PlayerContext> playerContexts = new HashMap<>();
    private Game currentGame = null;
    private Seat windSeat = Seat.EAST;
    private Seat zhongSeat = Seat.EAST;
    private ScoreCalculator scoreCalculator;

    private GameEventPublisher gameEventPublisher;
    private String roomId;

    private final TimeoutScheduler timeoutScheduler = new TimeoutScheduler();

    private final Map<Player, EndGameDecision> endGameDecisions = new HashMap<>();

    public Room(GameEventPublisher gameEventPublisher, String roomId, ScoreCalculator scoreCalculator, Player host) {
        this.gameEventPublisher = gameEventPublisher;
        this.roomId = roomId;
        this.scoreCalculator = scoreCalculator;
        this.host = host;

        for (Seat seat : Seat.values()) {
            playerContexts.put(seat, null);
        }
    }

//============================== GETTERS ==============================//

    public Player getHost() {
        return host;
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    public Seat getWindSeat() {
        return windSeat;
    }

    public Seat getZhongSeat() {
        return zhongSeat;
    }

    public ScoreCalculator getScoreCalculator() {
        return scoreCalculator;
    }

    public GameEventPublisher getGameEventPublisher() {
        return gameEventPublisher;
    }

    public String getRoomId() {
        return roomId;
    }

    public TimeoutScheduler getTimeoutScheduler() {
        return timeoutScheduler;
    }

    //============================== SEATING ==============================//

    public PlayerContext getPlayerContext(Seat seat) {
        return playerContexts.get(seat);
    }

    public Seat getSeat(Player player) {
        for (Seat seat : Seat.values()) {
            PlayerContext context = getPlayerContext(seat);
            if (context != null && context.getPlayer() == player) {
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

    public void removePlayer(Player player) {
        for (Seat seat : Seat.values()) {
            PlayerContext ctx = playerContexts.get(seat);
            if (ctx != null && ctx.getPlayer().equals(player)) {
                removePlayer(seat);
                return;
            }
        }
    }

//============================== GAME ==============================//

    public boolean startGame() {
        // check that room is full
        for (Seat seat : Seat.values()) {
            if (playerContexts.get(seat) == null) return false;
        }

        gameEventPublisher.sendGameStart(roomId);
        currentGame = new Game(this, windSeat, zhongSeat);

        return true;
    }

    public void onGameEnd() {
        if (currentGame == null) return;

        // ask each player for decision
        for (Seat seat : Seat.values()) {
            PlayerContext context = playerContexts.get(seat);
            if (context != null) {
                context.getDecisionHandler().promptEndGameDecision(context, this);
            }
        }
    }

    public void collectEndGameDecision(Player player, EndGameDecision decision) {
        endGameDecisions.put(player, decision);

        if (endGameDecisions.size() == playerContexts.size()) {
            processEndGameDecisions();
            endGameDecisions.clear();
        }
    }

    private void processEndGameDecisions() {
        boolean allWantAnotherGame = endGameDecisions.values().stream()
                .allMatch(decision -> decision == EndGameDecision.NEXT_GAME);

        if (allWantAnotherGame) {
            rotateSeatsAfterGame();
            startGame();
        } else {
            // TODO: Rotate in new players or bots to replace exiting players
            gameEventPublisher.sendSessionEnded(roomId); // close room
        }
    }

    private void rotateSeatsAfterGame() {
        zhongSeat = zhongSeat.next();
        if (zhongSeat == Seat.EAST) windSeat = windSeat.next();
    }
}
