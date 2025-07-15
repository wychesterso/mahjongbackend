package com.mahjong.mahjongserver.domain.game;

import com.mahjong.mahjongserver.domain.game.claim.ClaimOption;
import com.mahjong.mahjongserver.domain.game.claim.ClaimResolution;
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

import java.util.*;

public class Game {
    private final Table table = new Table();
    private Seat currentSeat;
    private int numDraws = 0;

    private final Room room;
    private Map<Seat, List<ClaimOption>> expectedClaims = new HashMap<>();
    private final Map<Seat, ClaimResolution> claimResponses = new HashMap<>();
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

    private void promptClaimDecisions(Tile discardedTile, Seat discarder, Map<Seat, List<ClaimOption>> claimMap) {
        for (Map.Entry<Seat, List<ClaimOption>> entry : claimMap.entrySet()) {
            Seat claimant = entry.getKey();
            List<ClaimOption> claimOptions = entry.getValue();

            PlayerContext ctx = room.getPlayerContext(claimant);
            Player player = ctx.getPlayer();

            List<Decision> options = claimOptions.stream()
                    .map(ClaimOption::getDecision)
                    .distinct()
                    .toList();

            promptDecision(discardedTile, discarder, claimant, options);

            // start timeout to auto-pass if no response
            room.getTimeoutScheduler().schedule(
                    "claim:" + player.getId(),
                    () -> handleAutoPassClaim(player),
                    TIMEOUT_MILLIS
            );
        }
    }

    private void promptDecision(Tile discardedTile, Seat discarder, Seat claimee, List<Decision> availableOptions) {
        PlayerContext ctx = room.getPlayerContext(claimee);
        ctx.getDecisionHandler().promptDecision(
                ctx,
                DTOMapper.fromTable(table, claimee),
                discardedTile,
                discarder,
                availableOptions
        );
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

    public void handleClaimResponse(Player player, Decision decision, List<Tile> selectedSheung) {
        room.getTimeoutScheduler().cancel("claim:" + player.getId());

        Seat claimer = room.getSeat(player);

        // save this decision
        claimResponses.put(claimer, new ClaimResolution(decision, selectedSheung));

        // wait until all expected claimants respond or timeout
        if (claimResponses.size() == expectedClaims.size()) {
            resolveClaims();
        }
    }

    public void handleAutoPassClaim(Player player) {
        handleClaimResponse(player, Decision.PASS, null);
    }

    public void resolveClaims() {
        // apply claim logic

        // reset claims
        expectedClaims.clear();
        claimResponses.clear();

        // continue the game
    }

    public void handleDiscard(Player player, Tile discardedTile) {
        // 1. validate it’s the correct player’s turn
        Seat playerSeat = room.getSeat(player);
        if (playerSeat != currentSeat) {
            throw new IllegalStateException("Not this player's turn");
        }

        // 2. remove tile from hand and update discard pile
        Hand hand = table.getHand(currentSeat);
        hand.discardTile(discardedTile);
        getBoard().putInDiscardPile(discardedTile);

        updateTableState();

        // 3. check if other players can claim this tile (win, pong, sheung)
        expectedClaims = checkForClaimsOnDiscard(discardedTile);

        if (expectedClaims.values().stream().anyMatch(list -> !list.isEmpty())) {
            promptClaimDecisions(discardedTile, currentSeat, expectedClaims);
            // exit here — wait for responses to come in from players
            return;
        }

        // 4. if no one claims, proceed to next player’s turn
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

    private Map<Seat, List<ClaimOption>> checkForClaimsOnDiscard(Tile discardedTile) {
        Map<Seat, List<ClaimOption>> claimMap = new HashMap<>();

        for (Seat seat : Seat.values()) {
            if (seat == currentSeat) continue; // skip discarder

            List<ClaimOption> claims = new ArrayList<>();
            Hand hand = table.getHand(seat);

            if (HandChecker.checkWin(hand, discardedTile)) {
                claims.add(new ClaimOption(Decision.WIN, null));
            }

            if (HandChecker.checkBrightKong(hand, discardedTile)) {
                claims.add(new ClaimOption(Decision.BRIGHT_KONG, null));
            }

            if (HandChecker.checkPong(hand, discardedTile)) {
                claims.add(new ClaimOption(Decision.PONG, null));
            }

            if (HandChecker.checkSheung(hand, discardedTile)) {
                claims.add(new ClaimOption(Decision.SHEUNG, HandChecker.getSheungCombos(hand, discardedTile)));
            }

            claimMap.put(seat, claims);
        }

        return claimMap;
    }

    private List<ClaimOption> checkForClaimsOnDraw() {
        List<ClaimOption> claims = new ArrayList<>();

        Hand hand = table.getHand(currentSeat);

        if (HandChecker.checkWin(hand)) {
            claims.add(new ClaimOption(Decision.WIN, null));
        }

        if (HandChecker.checkDarkKong(hand)) {
            claims.add(new ClaimOption(Decision.DARK_KONG, null));
        }

        if (HandChecker.checkBrightKong(hand)) {
            claims.add(new ClaimOption(Decision.BRIGHT_KONG, null));
        }

        return claims;
    }
}
