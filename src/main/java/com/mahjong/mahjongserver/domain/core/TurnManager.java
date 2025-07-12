package com.mahjong.mahjongserver.domain.core;

import com.mahjong.mahjongserver.domain.board.PileManager;
import com.mahjong.mahjongserver.domain.board.tile.TileType;
import com.mahjong.mahjongserver.domain.core.turn.data.TurnEnder;
import com.mahjong.mahjongserver.domain.board.tile.Tile;
import com.mahjong.mahjongserver.domain.game.EmptyPileException;
import com.mahjong.mahjongserver.domain.player.Player;
import com.mahjong.mahjongserver.domain.core.turn.Turn;
import com.mahjong.mahjongserver.domain.player.data.Seat;
import com.mahjong.mahjongserver.dto.BoardStateDTO;
import com.mahjong.mahjongserver.messaging.GameEventPublisher;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * A managing class that represents a game round, handling turns during a round.
 */
public class TurnManager {
    private final List<Player> playerList;
    private final GameEventPublisher gameEventPublisher;
    private final String roomId;

    private Player currentPlayer = null;
    private final List<Player> winners = new ArrayList<>();
    private Turn currentTurn = null;
    private PileManager pileManager;
    private String lastEvent;
    private int discardCount = 0;

    /**
     * Creates a turn manager instance.
     * @param playerList the players participating in the round.
     */
    public TurnManager(List<Player> playerList, GameEventPublisher gameEventPublisher, String roomId) {
        if (playerList.size() != 4) {
            throw new IllegalArgumentException("Incorrect amount of players!");
        }
        playerList.sort(Comparator.comparing(Player::getSeat));
        this.playerList = playerList;
        this.gameEventPublisher = gameEventPublisher;
        this.roomId = roomId;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Initializes a new turn.
     * @return a new turn belonging to the current player.
     */
    public Turn initializeTurn() {
        return new Turn(currentPlayer, getOtherPlayers(), boardState(),
                pileManager.getDiscardPile().getDiscardedTiles(), gameEventPublisher, roomId);
    }

    /**
     * Starts a new round.
     * @param seat the seat of the Zhong player.
     * @throws InvalidKongException if an invalid Kong is attempted.
     */
    public TurnEnder startRound(Seat seat) throws InvalidKongException, EmptyPileException {
        // INITIAL DRAWS
        winners.clear();
        pileManager = new PileManager(8);
        for (Player player : playerList) {
            int numInitialTiles;
            if (player.getSeat() == seat) {
                numInitialTiles = 17;
                currentPlayer = player;
            } else {
                numInitialTiles = 16;
            }
            for (int i = 0; i < numInitialTiles; i++) {
                Tile newTile = pileManager.drawTile();

                // replace flower tiles
                while (newTile.getTileType() == TileType.FLOWER_SEASON
                        || newTile.getTileType() == TileType.FLOWER_PLANT) {
                    player.getHandManager().addFlower(newTile);
                    handleFlowerTile(player, newTile);
                    newTile = pileManager.drawBonusTile();
                }

                player.getHandManager().addToHand(newTile);
            }
        }

        // ZHONG STARTS FIRST TURN
        TurnEnder turnEnder = startTurnFirstDraw();

        // LOOP TURNS UNTIL GAME ENDS
        while (turnEnder != TurnEnder.END_GAME_WIN && turnEnder != TurnEnder.END_GAME_DRAW
                && turnEnder != TurnEnder.END_GAME_WIN_SELFDRAW) {
            try {
                turnEnder = startNewTurn(turnEnder);
            } catch (EmptyPileException e) {
                turnEnder = TurnEnder.END_GAME_DRAW;
            }
        }
        return turnEnder;
    }

    /**
     * Used for handling when a player draws a flower tile.
     * @param player the player that drew the tile.
     * @param flowerTile the tile that was drawn.
     * @throws EmptyPileException if the pile is empty.
     */
    private void handleFlowerTile(Player player, Tile flowerTile) throws EmptyPileException {
        // reveal flower tile to all
        gameEventPublisher.sendLog(roomId, player.toStringWithSeat() + " drew flower tile: "
                + flowerTile);

        // handle flower sets
        if (player.getHandManager().getRevealedHand().newToiFormed()) {
            stealScore(player, "formed a new type of flowers", 10);
        }
        if (player.getHandManager().getRevealedHand().newGrassFormed()) {
            stealScore(player, "formed a new set of flowers", 5);
        }
    }

    /**
     * Used when one player attains a condition that allows them to take score from the other 3 players.
     * @param player the player who takes the score.
     * @param message the message corresponding to the condition.
     * @param amount the score to take from each player.
     */
    private void stealScore(Player player, String message, int amount) {
        gameEventPublisher.sendLog(roomId, player.toStringWithSeat() + " " + message + "!");

        for (Player otherPlayer : getOtherPlayers(player)) {
            otherPlayer.deductScore(amount);
            gameEventPublisher.sendLog(roomId, otherPlayer.toStringWithSeat() + ": -" + amount);
        }
        player.addScore(amount * 3);
        gameEventPublisher.sendLog(roomId, player.toStringWithSeat() + ": +" + amount * 3);
    }

    /**
     * Starts a new turn based on the previous turn ending event.
     * @param prevTurnEnder the previous turn's turn ending event.
     * @return the new turn's turn ending event.
     * @throws InvalidKongException if an invalid Kong is attempted.
     * @throws EmptyPileException when attempting to draw from an empty pile.
     */
    public TurnEnder startNewTurn(TurnEnder prevTurnEnder)
            throws InvalidKongException, EmptyPileException {
        switch (prevTurnEnder) {
            case DRAW_FLOWER -> {
                lastEvent = "flower";
                handleFlowerTile(currentPlayer, currentTurn.getDrawnTile());

                // check for flower win
                if (currentPlayer.getHandManager().getRevealedHand().getFlowers().size() == 8) {
                    if (currentPlayer.decideWin(boardState(currentPlayer))) {
                        winners.add(currentPlayer);
                        return TurnEnder.END_GAME_WIN_SELFDRAW;
                    }
                }

                // draw replacement tile and restart turn
                return startTurnBonusDraw();
            }

            case BRIGHT_KONG, DARK_KONG -> {
                if (lastEvent.equals("kong") || lastEvent.equals("double kong")) {
                    lastEvent = "double kong";
                }
                return startTurnBonusDraw();
            }

            case DISCARD_TILE -> {
                lastEvent = "discard";
                discardCount += 1;
                Tile discardedTile = currentTurn.getDiscardTile();
                pileManager.addDiscardedTile(discardedTile);
                List<Player> otherPlayers = getOtherPlayers();

                // CHECK WIN
                for (Player player : otherPlayers) {
                    if (player.getHandManager().checkWin(discardedTile)) {
                        if (player.decideWin(discardedTile, boardState(player))) {
                            player.getHandManager().addToHand(discardedTile);
                            winners.add(player);
                        }
                    }
                }
                if (!winners.isEmpty()) {
                    pileManager.takeLastDiscardedTile();
                    return TurnEnder.END_GAME_WIN;
                }

                // CHECK BRIGHT KONG
                for (Player player : otherPlayers) {
                    if (player.getHandManager().checkBrightKongFromOpponent(discardedTile)) {
                        if (player.decideBrightKongNoDraw(discardedTile, boardState(player))) {
                            currentPlayer = player;
                            gameEventPublisher.sendLog(roomId, currentPlayer.toStringWithSeat()
                                    + " performed Bright Kong: "
                                    + discardedTile + discardedTile + discardedTile + discardedTile);
                            return startTurnBrightKongFromOpponent();
                        }
                    }
                }

                // CHECK PONG
                for (Player player : otherPlayers) {
                    if (player.getHandManager().checkPong(discardedTile)) {
                        if (player.decidePong(discardedTile, boardState(player))) {
                            currentPlayer = player;
                            List<Tile> existingTiles = new ArrayList<>();
                            for (int i = 0; i < 2; i++) {
                                existingTiles.add(discardedTile);
                            }
                            gameEventPublisher.sendLog(roomId, currentPlayer.toStringWithSeat()
                                    + " performed Pong: "
                                    + discardedTile + discardedTile + discardedTile);
                            return startTurnTakeTile(existingTiles);
                        }
                    }
                }

                // other player's can't interfere no more - switch to immediate next player
                int currentIndex = playerList.indexOf(currentPlayer);
                int nextIndex = (currentIndex + 1) % playerList.size();
                currentPlayer = playerList.get(nextIndex);

                // CHECK SHEUNG
                List<List<Tile>> validSheungs = currentPlayer.getHandManager().checkSheung(discardedTile);
                if (!validSheungs.isEmpty()) {
                    if (currentPlayer.decideSheung(discardedTile, boardState())) {
                        List<Tile> pickedCombo;
                        if (validSheungs.size() == 1) {
                            pickedCombo = new ArrayList<>(validSheungs.getFirst());
                        } else {
                            pickedCombo = new ArrayList<>(currentPlayer.pickSheungCombo(validSheungs));
                        }
                        StringBuilder comboString = new StringBuilder();
                        for (Tile tile : pickedCombo) {
                            comboString.append(tile);
                        }
                        gameEventPublisher.sendLog(roomId, currentPlayer.toStringWithSeat()
                                + " performed Sheung: " + comboString);
                        pickedCombo.remove(discardedTile);
                        return startTurnTakeTile(pickedCombo);
                    }
                }

                return startTurnNormalDraw();
            }
            default -> throw new RuntimeException("Unknown TurnEnder!");
        }
    }

    /**
     * Starts a turn immediately after game start.
     * @return the turn ending event.
     */
    public TurnEnder startTurnFirstDraw() throws InvalidKongException {
        currentTurn = initializeTurn();
        return currentTurn.startTurnFirstDraw();
    }

    /**
     * Starts a turn with a normal draw.
     * @return the turn ending event.
     * @throws EmptyPileException if there are no tiles left to draw.
     * @throws InvalidKongException if an invalid kong is attempted.
     */
    public TurnEnder startTurnNormalDraw() throws EmptyPileException, InvalidKongException {
        Tile drawnTile = pileManager.drawTile();
        currentTurn = initializeTurn();
        return currentTurn.startTurnDrawTile(drawnTile);
    }

    /**
     * Starts a turn with a bonus draw.
     * @return the turn ending event.
     * @throws EmptyPileException if there are no tiles left to draw.
     * @throws InvalidKongException if an invalid kong is attempted.
     */
    public TurnEnder startTurnBonusDraw() throws EmptyPileException, InvalidKongException {
        Tile drawnTile = pileManager.drawBonusTile();
        currentTurn = initializeTurn();
        return currentTurn.startTurnDrawTile(drawnTile);
    }

    /**
     * Starts a turn by taking an opponent discard to form a group, that gets sent to the
     * revealed hand.
     * @param existingTiles the tiles (not including the discard tile) used to form the group.
     * @return the turn ending event.
     * @requires the group is valid (i.e. it is a valid Pong or Sheung when combined with the
     * discard tile).
     */
    public TurnEnder startTurnTakeTile(List<Tile> existingTiles) {
        Tile takenTile = pileManager.takeLastDiscardedTile();
        currentTurn = initializeTurn();
        return currentTurn.startTurnTakeTile(takenTile, existingTiles);
    }

    /**
     * Starts a turn by taking an opponent discard to form a Bright Kong, that gets sent to the
     * revealed hand.
     * @return the turn ending event.
     * @throws InvalidKongException if an invalid kong is attempted.
     */
    public TurnEnder startTurnBrightKongFromOpponent() throws InvalidKongException {
        Tile takenTile = pileManager.takeLastDiscardedTile();
        currentTurn = initializeTurn();
        return currentTurn.startTurnBrightKongFromOpponent(takenTile);
    }

    public List<Player> getWinners() {
        return winners;
    }

    /**
     * Gets a list of players that aren't the current player, sorted by their relative seat
     * position in relation to the current player's seat.
     * @return the list of players that are not the current player.
     */
    public List<Player> getOtherPlayers() {
        return playerList.stream()
                .filter(player -> !player.equals(currentPlayer))
                .sorted(Comparator.comparingInt(player -> {
                    int currentSeat = currentPlayer.getSeat().ordinal();
                    int playerSeat = player.getSeat().ordinal();
                    return (playerSeat - currentSeat + 4) % 4;
                }))
                .toList();
    }

    /**
     * Gets a list of players that aren't the specified player, sorted by their relative seat
     * position in relation to the current player's seat.
     * @return the list of players that are not the specified player.
     */
    public List<Player> getOtherPlayers(Player specifiedPlayer) {
        return playerList.stream()
                .filter(player -> !player.equals(specifiedPlayer))
                .sorted(Comparator.comparingInt(player -> {
                    int currentSeat = specifiedPlayer.getSeat().ordinal();
                    int playerSeat = player.getSeat().ordinal();
                    return (playerSeat - currentSeat + 4) % 4;
                }))
                .toList();
    }

    public PileManager getPileManager() {
        return pileManager;
    }

    public String getLastEvent() {
        return lastEvent;
    }

    public int getDiscardCount() {
        return discardCount;
    }

    /**
     * Gets the board state from the perspective of the current player.
     * @return the string output of the board state.
     */
    public BoardStateDTO boardState() {
        List<Player> otherPlayers = getOtherPlayers();

        return new BoardStateDTO(
                pileManager.getDiscardPile().getDiscardedTiles(),
                Map.of(
                        "left", otherPlayers.get(2).getHandManager().toOpponentView(),
                        "across", otherPlayers.get(1).getHandManager().toOpponentView(),
                        "right", otherPlayers.get(0).getHandManager().toOpponentView()
                ),
                pileManager.getUnrevealedPile().getRemainingTileCount()
        );
    }

    /**
     * Gets the board state from the perspective of the specified player.
     * @return the string output of the board state.
     */
    public BoardStateDTO boardState(Player player) {
        List<Player> otherPlayers = getOtherPlayers(player);

        return new BoardStateDTO(
                pileManager.getDiscardPile().getDiscardedTiles(),
                Map.of(
                        "left", otherPlayers.get(2).getHandManager().toOpponentView(),
                        "across", otherPlayers.get(1).getHandManager().toOpponentView(),
                        "right", otherPlayers.get(0).getHandManager().toOpponentView()
                ),
                pileManager.getUnrevealedPile().getRemainingTileCount()
        );
    }
}