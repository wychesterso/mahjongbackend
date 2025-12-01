package com.mahjong.mahjongserver.domain.room;

import com.mahjong.mahjongserver.domain.game.Game;
import com.mahjong.mahjongserver.domain.game.score.ScoreCalculator;
import com.mahjong.mahjongserver.domain.player.Bot;
import com.mahjong.mahjongserver.domain.player.Player;
import com.mahjong.mahjongserver.domain.player.PlayerProfile;
import com.mahjong.mahjongserver.domain.player.RealPlayer;
import com.mahjong.mahjongserver.domain.player.context.PlayerContext;
import com.mahjong.mahjongserver.domain.player.decision.BotDecisionHandler;
import com.mahjong.mahjongserver.domain.player.decision.PlayerDecisionHandler;
import com.mahjong.mahjongserver.domain.core.GameEventPublisher;
import com.mahjong.mahjongserver.domain.player.decision.EndGameDecision;
import com.mahjong.mahjongserver.domain.player.decision.RealPlayerDecisionHandler;
import com.mahjong.mahjongserver.infrastructure.TimeoutScheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a game room in the Mahjong server.
 * Manages seat assignments, players, game lifecycle, and decision handling.
 */
public class Room {
    private String roomId;
    private Player host;
    private Map<Seat, PlayerContext> playerContexts = new HashMap<>();

    private Game currentGame = null;
    private long gameNum = 0;

    private Seat windSeat = Seat.EAST;
    private Seat zhongSeat = Seat.EAST;
    private int lumZhongCount = 0;

    private ScoreCalculator scoreCalculator;
    private GameEventPublisher gameEventPublisher;
    private final TimeoutScheduler timeoutScheduler = new TimeoutScheduler();

    private final Map<Player, EndGameDecision> endGameDecisions = new HashMap<>();

    /**
     * Constructs a new Room with the given configuration.
     *
     * @param gameEventPublisher The publisher for game events.
     * @param roomId             The unique identifier for this room.
     * @param scoreCalculator    The score calculator for this room's game.
     * @param host               The initial host player of the room.
     */
    public Room(GameEventPublisher gameEventPublisher, String roomId, ScoreCalculator scoreCalculator, Player host) {
        this.gameEventPublisher = gameEventPublisher;
        this.roomId = roomId;
        this.scoreCalculator = scoreCalculator;
        this.host = host;

        for (Seat seat : Seat.values()) {
            playerContexts.put(seat, null);
        }
    }

//============================== ROOM INFO ==============================//

    /** @return The unique ID of this room. */
    public String getRoomId() {
        return roomId;
    }

    /** @return The score calculator used in this room. */
    public ScoreCalculator getScoreCalculator() {
        return scoreCalculator;
    }

    /** @return The event publisher associated with this room. */
    public GameEventPublisher getGameEventPublisher() {
        return gameEventPublisher;
    }

    /** @return The timeout scheduler for this room. */
    public TimeoutScheduler getTimeoutScheduler() {
        return timeoutScheduler;
    }

    /** @return Number of unoccupied seats in this room. */
    public int getNumEmptySeats() {
        int count = 4;
        for (PlayerContext ctx : playerContexts.values()) {
            if (ctx != null) count--;
        }
        return count;
    }

//============================== HOST ==============================//

    /** @return The current host player. */
    public Player getHost() {
        return host;
    }

    /** @return The player ID of the current host. */
    public String getHostId() {
        return host.getId();
    }

    /**
     * Sets the host of the room.
     *
     * @param player The player to assign as the host.
     */
    public void setHost(Player player) {
        host = player;
    }

//============================== PLAYERS ==============================//

    /** @return A map of seat positions to player contexts. */
    public Map<Seat, PlayerContext> getPlayerContexts() {
        return playerContexts;
    }

    /** @return A map of seat positions to player IDs for all seated players. */
    public Map<Seat, String> getPlayerNames() {
        Map<Seat, String> playerSeatMap = new HashMap<>();
        for (Map.Entry<Seat, PlayerContext> entry : playerContexts.entrySet()) {
            PlayerContext ctx = entry.getValue();
            if (ctx != null) {
                playerSeatMap.put(entry.getKey(), ctx.getPlayer().getId());
            }
        }
        return playerSeatMap;
    }

    /** @return A map of seat positions to whether each player is a bot. */
    public Map<Seat, Boolean> getBotStatuses() {
        Map<Seat, Boolean> botMap = new HashMap<>();
        for (Map.Entry<Seat, PlayerContext> entry : playerContexts.entrySet()) {
            PlayerContext ctx = entry.getValue();
            if (ctx == null) {
                botMap.put(entry.getKey(), false);
            } else {
                botMap.put(entry.getKey(), ctx.getPlayer().isBot());
            }
        }
        return botMap;
    }

    /**
     * Checks if a player with the given ID is in the room.
     *
     * @param playerId The player ID.
     * @return True if the player is present, false otherwise.
     */
    public boolean containsPlayer(String playerId) {
        for (Seat seat : Seat.values()) {
            PlayerContext context = getPlayerContext(seat);
            if (context != null) {
                Player player = context.getPlayer();
                if (player != null && player.getId().equals(playerId)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the seat assigned to the given player.
     *
     * @param player The player.
     * @return The seat assigned, or null if not seated.
     */
    public Seat getSeat(Player player) {
        for (Seat seat : Seat.values()) {
            PlayerContext context = getPlayerContext(seat);
            if (context != null && context.getPlayer().getId().equals(player.getId())) {
                return seat;
            }
        }
        return null;
    }

    /**
     * Gets the seat assigned to the player with the given ID.
     *
     * @param playerId The player ID.
     * @return The seat assigned, or null if not seated.
     */
    public Seat getSeat(String playerId) {
        for (Seat seat : Seat.values()) {
            PlayerContext context = getPlayerContext(seat);
            if (context != null && context.getPlayer().getId().equals(playerId)) {
                return seat;
            }
        }
        return null;
    }

//============================== SEATING ==============================//

    /**
     * Gets the player context assigned to the given seat.
     *
     * @param seat The seat.
     * @return The player context or null if vacant.
     */
    public PlayerContext getPlayerContext(Seat seat) {
        return playerContexts.get(seat);
    }

    /**
     * Checks if a seat is vacant.
     *
     * @param seat The seat to check.
     * @return True if vacant, false if occupied.
     */
    public boolean isVacant(Seat seat) {
        return playerContexts.get(seat) == null;
    }

    /**
     * Adds a player to the specified seat with a decision handler.
     *
     * @param seat            The seat to occupy.
     * @param player          The player to add.
     * @param decisionHandler The decision handler for the player.
     * @return True if the player was added, false if the seat was occupied.
     */
    public boolean addPlayer(Seat seat, Player player, PlayerDecisionHandler decisionHandler) {
        if (playerContexts.get(seat) == null) {
            playerContexts.put(seat, new PlayerContext(player, decisionHandler, roomId));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Adds a player to the given seat.
     *
     * @param seat                  The seat to assign.
     * @param profile               The profile of the player to add.
     * @param playerDecisionHandler The player's decision handler.
     * @return True if successfully added.
     */
    public boolean addPlayer(Seat seat, PlayerProfile profile, PlayerDecisionHandler playerDecisionHandler) {
        return addPlayer(seat, new RealPlayer(profile), playerDecisionHandler);
    }

    /**
     * Adds a bot to the given seat.
     *
     * @param seat The seat to assign.
     * @param botDecisionHandler The bot's decision handler.
     * @return True if successfully added.
     */
    public boolean addBot(Seat seat, BotDecisionHandler botDecisionHandler) {
        Bot bot = new Bot(generateNextBotId());
        return addPlayer(seat, bot, botDecisionHandler);
    }

    /**
     * Removes a bot from the given seat.
     *
     * @param seat The seat to remove bot from.
     * @return True if successfully removed.
     */
    public boolean removeBot(Seat seat, BotDecisionHandler botDecisionHandler) {
        Player player = playerContexts.get(seat).getPlayer();
        if ((currentGame != null && currentGame.isActiveGame()) || player == null || !player.isBot()) return false;
        removePlayer(seat, botDecisionHandler);
        return true;
    }

    /**
     * Generates the next available bot ID (e.g., "bot-1", "bot-2", etc.).
     *
     * @return A unique bot ID for the room.
     */
    private String generateNextBotId() {
        // collect currently used bot numbers
        Set<Integer> used = getPlayerContexts().values().stream()
                .filter(Objects::nonNull)
                .map(PlayerContext::getPlayer)
                .filter(p -> p instanceof Bot)
                .map(p -> {
                    try {
                        String id = p.getId();
                        return Integer.parseInt(id.replace("bot-", ""));
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // find first available number from 1 to 3
        for (int i = 1; i <= 3; i++) {
            if (!used.contains(i)) {
                return "bot-" + i;
            }
        }

        // all slots are used
        return null;
    }

    /**
     * Switches the player with the given ID to the new specified seat if possible.
     * The switch is only performed if the destination seat is vacant.
     *
     * @param playerId The ID of the player requesting the switch.
     * @param newSeat  The seat to switch to.
     * @return True if the switch was successful.
     */
    public boolean switchSeat(String playerId, Seat newSeat) {
        if (playerContexts.get(newSeat) != null) {
            // seat occupied
            return false;
        }

        for (Seat oldSeat : Seat.values()) {
            PlayerContext ctx = playerContexts.get(oldSeat);
            if (ctx != null && ctx.getPlayer().getId().equals(playerId)) {
                playerContexts.put(newSeat, ctx);
                playerContexts.put(oldSeat, null);
                return true;
            }
        }

        return false;
    }

    /**
     * Removes the player assigned to the given seat.
     * Replaces the player with a bot if the game is ongoing.
     *
     * @param seat The seat to clear.
     */
    public void removePlayer(Seat seat, BotDecisionHandler botDecisionHandler) {
        playerContexts.put(seat, null);
        if (currentGame != null && currentGame.isActiveGame()) {
            // replace with bot
            addBot(seat, botDecisionHandler);
        }
    }

    /**
     * Removes the player with the given ID from the room.
     *
     * @param playerId The ID of the player to remove.
     */
    public void removePlayer(String playerId, BotDecisionHandler botDecisionHandler) {
        for (Seat seat : Seat.values()) {
            PlayerContext ctx = playerContexts.get(seat);
            if (ctx != null && ctx.getPlayer().getId().equals(playerId)) {
                removePlayer(seat, botDecisionHandler);
                return;
            }
        }
    }

//============================== GAME ==============================//

    /** @return The current active game, or null if none. */
    public Game getCurrentGame() {
        return currentGame;
    }

    /** @return The current wind seat in the game. */
    public Seat getWindSeat() {
        return windSeat;
    }

    /** @return The current zhong (round leader) seat. */
    public Seat getZhongSeat() {
        return zhongSeat;
    }

    public int getLumZhongCount() {
        return lumZhongCount;
    }

    /**
     * Starts a game if all seats are filled.
     *
     * @return True if the game started, false otherwise.
     */
    public boolean startGame() {
        // check that room is full
        for (Seat seat : Seat.values()) {
            if (playerContexts.get(seat) == null) {
                System.out.println("seat " + seat + " does not have player context");
                return false;
            }
        }

        System.out.println("[Room] startGame(): announcing game_start for room=" + roomId);
        gameEventPublisher.sendGameStart(roomId);
        currentGame = new Game(this, ++gameNum, windSeat, zhongSeat);
        System.out.println("[Room] startGame(): created Game instance for room=" + roomId + ", gameNum=" + gameNum);

        return true;
    }

    /**
     * Called when a game ends to prompt players for a follow-up decision.
     */
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

    /**
     * Collects a player's decision after game ends and triggers decision resolution if all players have responded.
     *
     * @param player   The player providing the decision.
     * @param decision The decision to either continue or stop.
     */
    public void collectEndGameDecision(Player player, EndGameDecision decision) {
        endGameDecisions.put(player, decision);

        long numPlayers = playerContexts.values().stream()
                .filter(Objects::nonNull)
                .count();
        if (endGameDecisions.size() == numPlayers) {
            processEndGameDecisions();
            endGameDecisions.clear();
        }
    }

    /**
     * Processes all end game decisions and either starts a new game or ends the session.
     */
    private void processEndGameDecisions() {
        boolean allWantAnotherGame = endGameDecisions.values().stream()
                .allMatch(decision -> decision == EndGameDecision.NEXT_GAME);

        if (allWantAnotherGame) {
            if (currentGame.getWinnerSeats().contains(zhongSeat)) {
                // lum zhong and increment count
                lumZhongCount++;
            } else if (!currentGame.getWinnerSeats().isEmpty()) {
                // no lum zhong, reset count
                rotateSeatsAfterGame();
            }
            // on draw, lum zhong without incrementing count (i.e. do nothing)

            boolean success = startGame();
            if (success) {
                getCurrentGame().startGame();
            } else {
                throw new IllegalStateException("Not all seats are filled!");
            }
        } else {
            // TODO: Rotate in new players or bots to replace exiting players
            gameEventPublisher.sendSessionEnded(roomId); // close room
        }
    }

    /**
     * Rotates the dealer and round leader seats after a game ends.
     */
    private void rotateSeatsAfterGame() {
        zhongSeat = zhongSeat.next();
        lumZhongCount = 0;
        if (zhongSeat == Seat.EAST) windSeat = windSeat.next();
    }
}
