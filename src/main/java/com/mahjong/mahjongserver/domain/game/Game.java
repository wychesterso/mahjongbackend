package com.mahjong.mahjongserver.domain.game;

import com.mahjong.mahjongserver.domain.game.score.HandChecker;
import com.mahjong.mahjongserver.domain.player.Player;
import com.mahjong.mahjongserver.domain.player.context.PlayerContext;
import com.mahjong.mahjongserver.domain.player.decision.Decision;
import com.mahjong.mahjongserver.domain.room.Room;
import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.Table;
import com.mahjong.mahjongserver.domain.room.board.Board;
import com.mahjong.mahjongserver.domain.room.board.Hand;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.domain.room.board.tile.TileClassification;
import com.mahjong.mahjongserver.dto.mapper.DTOMapper;
import com.mahjong.mahjongserver.dto.table.TableDTO;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private final Table table = new Table();
    private Seat currentSeat;
    private int numDraws = 0;

    private final Room room;
    private static final long TIMEOUT_MILLIS = 10_000;

    public Game(Room room) {
        this.room = room;
        currentSeat = Seat.EAST;
    }

    public Game(Room room, Seat seat) {
        this.room = room;
        currentSeat = seat;
    }

//============================== GETTERS ==============================//

    public Table getTable() {
        return table;
    }

    public Board getBoard() {
        return table.getBoard();
    }

    public Seat getCurrentSeat() {
        return currentSeat;
    }

//============================== EVENTS ==============================//

    public void startGame() {
        dealStartingHands();
        startTurnWithoutDraw();
    }

    public void startTurnWithoutDraw() {

    }

    public void startTurnWithDraw() {

    }

    // legacy!
    private void runGame() {
        dealStartingHands();
        boolean drawTileOnTurnStart = false;

        // check for win / dark kong

        while (true) {
            Hand currentHand = table.getHand(currentSeat);

            if (drawTileOnTurnStart) {
                Tile drawnTile = drawTile(currentHand);

                // check for win / dark kong / bright kong
                // if found, prompt decision and break/continue loop or pass accordingly

                // prompt a discard
                promptDiscardOnDraw(drawnTile);

            } else {
                // prompt a discard
                promptDiscard();
            }

            // check other players' hands for win / pong / sheung
            // if found, prompt decision and break/continue loop or pass accordingly

            // if no one picks up discarded tile, move to next player
            currentSeat = currentSeat.next();
            drawTileOnTurnStart = true;
        }
    }

//============================== DRAW TILES ==============================//

    private Tile drawTile(Hand hand) {
        Tile tile = getBoard().drawTile();
        while (tile.getTileType().getClassification() == TileClassification.FLOWER) {
            hand.addFlower(tile);
            tile = getBoard().drawBonusTile();
        }
        hand.addTile(tile);
        updateTableState();
        return tile;
    }

    private void dealStartingHands() {
        for (Seat gameSeat : Seat.values()) {
            Hand hand = table.getHand(gameSeat);
            for (int i = 0; i < 16; i++) {
                drawTile(hand);
            }
        }
        drawTile(table.getHand(currentSeat));
    }

//============================== PROMPT FRONTEND ==============================//

    private void promptDecision(Tile discardedTile, Seat discarder, List<Decision> availableOptions) {
        PlayerContext ctx = getPlayerContext();
        ctx.getDecisionHandler().promptDecision(ctx, fromTable(), discardedTile, discarder, availableOptions);
    }

    private void promptSheungCombo(Tile discardedTile, List<List<Tile>> validCombos) {
        PlayerContext ctx = getPlayerContext();
        ctx.getDecisionHandler().promptSheungCombo(ctx, discardedTile, validCombos);
    }

    private void promptDiscard() {
        PlayerContext ctx = getPlayerContext();
        ctx.getDecisionHandler().promptDiscard(ctx, fromTable());
    }

    private void promptDiscardOnDraw(Tile drawnTile) {
        PlayerContext ctx = getPlayerContext();
        ctx.getDecisionHandler().promptDiscardOnDraw(ctx, fromTable(), drawnTile);

        room.getTimeoutScheduler().schedule("discard:" + ctx.getPlayer().getId(), () -> {
            // handleAutoDiscard(ctx.getPlayer());
        }, TIMEOUT_MILLIS);
    }

//============================== UPDATE FRONTEND ==============================//

    /**
     * Sends a personalized update of the current game table state to all players.
     */
    private void updateTableState() {
        for (Seat seat : Seat.values()) {
            PlayerContext ctx = room.getPlayerContext(seat);
            room.getGameEventPublisher().sendTableUpdate(
                    ctx.getPlayer().getId(),
                    DTOMapper.fromTable(table, seat)
            );
        }
    }

//============================== HANDLE RESPONSES FROM FRONTEND ==============================//

    public void handleDiscard(Player player, Tile discardedTile) {
        // 1. Validate it’s the correct player’s turn
        Seat playerSeat = room.getSeat(player);
        if (playerSeat != currentSeat) {
            throw new IllegalStateException("Not this player's turn");
        }

        // 2. Remove tile from hand and update discard pile
        Hand hand = table.getHand(currentSeat);
        hand.discardTile(discardedTile);
        getBoard().putInDiscardPile(discardedTile);

        updateTableState();

        // 3. Check if other players can claim this tile (win, pong, sheung)
        List<ClaimOption> claimOptions = checkForClaimsOnDiscard(discardedTile);

        if (!claimOptions.isEmpty()) {
            // promptClaimDecisions(discardedTile, claimOptions);
            // Exit here — wait for responses to come in from players
            return;
        }

        // 4. If no one claims, proceed to next player’s turn
        currentSeat = currentSeat.next();
        startTurnWithoutDraw();  // kicks off next draw/discard cycle
    }

//============================== HELPERS ==============================//

    private PlayerContext getPlayerContext() {
        return room.getPlayerContext(currentSeat);
    }

    private TableDTO fromTable() {
        return DTOMapper.fromTable(table, currentSeat);
    }

    private List<ClaimOption> checkForClaimsOnDiscard(Tile discardedTile) {
        List<ClaimOption> claims = new ArrayList<>();

        for (Seat seat : Seat.values()) {
            if (seat == currentSeat) continue; // skip discarder

            Hand hand = table.getHand(seat);

            if (HandChecker.checkWin(hand, discardedTile)) {
                claims.add(new ClaimOption(seat, Decision.WIN, null));
            }

            if (HandChecker.checkBrightKong(hand, discardedTile)) {
                claims.add(new ClaimOption(seat, Decision.BRIGHT_KONG, null));
            }

            if (HandChecker.checkPong(hand, discardedTile)) {
                claims.add(new ClaimOption(seat, Decision.PONG, null));
            }

            if (HandChecker.checkSheung(hand, discardedTile)) {
                claims.add(new ClaimOption(
                        seat,
                        Decision.SHEUNG,
                        HandChecker.getSheungCombos(hand, discardedTile)
                ));
            }
        }

        return claims;
    }

    private List<ClaimOption> checkForClaimsOnDraw() {
        List<ClaimOption> claims = new ArrayList<>();

        Hand hand = table.getHand(currentSeat);

        if (HandChecker.checkWin(hand)) {
            claims.add(new ClaimOption(currentSeat, Decision.WIN, null));
        }

        if (HandChecker.checkDarkKong(hand)) {
            claims.add(new ClaimOption(currentSeat, Decision.DARK_KONG, null));
        }

        if (HandChecker.checkBrightKong(hand)) {
            claims.add(new ClaimOption(currentSeat, Decision.BRIGHT_KONG, null));
        }

        return claims;
    }
}
