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
import java.util.function.Supplier;

public class Game {
    // gameplay info
    private final Table table = new Table();
    private Seat currentSeat;

    // game statistics
    private Seat winnerSeat = null;
    private int numDraws = 0;

    // prompting and frontend player-interaction stuff
    private final Room room;
    private Map<Seat, List<ClaimOption>> expectedClaims = new EnumMap<>(Seat.class);
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
        if (handleClaimsAfterDraw(table.getHand(currentSeat).getLastDrawnTile())) return;
        startTurnWithoutDraw();
    }

    public void startTurnWithoutDraw() {
        resetClaims();
        promptDiscard();
    }

    public void startTurnWithDraw() {
        resetClaims();

        // draw tile
        Hand hand = table.getHand(currentSeat);
        Tile drawnTile = drawTile(hand);

        // check claim options
        if (handleClaimsAfterDraw(drawnTile)) return;

        // prompt discard
        promptDiscardOnDraw(drawnTile);
    }

    public void startTurnWithBonusDraw() {
        resetClaims();

        // draw tile
        Hand hand = table.getHand(currentSeat);
        Tile drawnTile = drawBonusTile(hand);

        // check claim options
        if (handleClaimsAfterDraw(drawnTile)) return;

        // prompt discard
        promptDiscardOnDraw(drawnTile);
    }

    private boolean handleClaimsAfterDraw(Tile drawnTile) {
        List<ClaimOption> claimOptions = checkForClaimsOnDraw();
        if (!claimOptions.isEmpty()) {
            expectedClaims.put(currentSeat, claimOptions);

            promptDecision(drawnTile, null, currentSeat, claimOptions.stream()
                    .map(ClaimOption::getDecision)
                    .distinct()
                    .toList());

            room.getTimeoutScheduler().schedule(
                    "claim:" + getPlayerContext().getPlayer().getId(),
                    () -> handleAutoPassDrawDecision(getPlayerContext().getPlayer()),
                    TIMEOUT_MILLIS
            );

            return true; // claim prompt was issued
        }
        return false; // no claim options
    }

//============================== DRAW TILES ==============================//

    private Tile drawTile(Hand hand) {
        return drawAndHandleBonusTile(() -> getBoard().drawTile(), hand);
    }

    private Tile drawBonusTile(Hand hand) {
        return drawAndHandleBonusTile(() -> getBoard().drawBonusTile(), hand);
    }

    private Tile drawAndHandleBonusTile(Supplier<Tile> drawFunc, Hand hand) {
        Tile tile = drawFunc.get();
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

//============================== CHECK FOR CLAIMS ==============================//

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

            if (!claims.isEmpty()) {
                claimMap.put(seat, claims);
            }
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

        room.getTimeoutScheduler().schedule("discard:" + ctx.getPlayer().getId(), () -> {
            // handleAutoDiscard(ctx.getPlayer());
        }, TIMEOUT_MILLIS);
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

//============================== HANDLE RESPONSE FROM DISCARD ==============================//

    public void handleClaimResponseFromDiscard(Player player, Decision decision, List<Tile> selectedSheung) {
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
        handleClaimResponseFromDiscard(player, Decision.PASS, null);
    }

    public void resolveClaims() {
        // select highest priority claim (WIN > BRIGHT_KONG > PONG > SHEUNG)
        Optional<Map.Entry<Seat, ClaimResolution>> winningClaim = claimResponses.entrySet().stream()
                .filter(entry -> entry.getValue().getDecision() != Decision.PASS).min((a, b) -> {
                    int prioA = decisionPriority(a.getValue().getDecision());
                    int prioB = decisionPriority(b.getValue().getDecision());
                    if (prioA != prioB) return Integer.compare(prioA, prioB); // first by priority
                    return distanceFromCurrentSeat(a.getKey()) - distanceFromCurrentSeat(b.getKey()); // then by seat (which shouldn't be necessary, but just as a failsafe)
                });

        if (winningClaim.isPresent()) {
            Seat claimer = winningClaim.get().getKey();
            Decision decision = winningClaim.get().getValue().getDecision();
            List<Tile> sheungCombo = winningClaim.get().getValue().getSelectedSheung();

            Hand hand = table.getHand(claimer);
            Tile claimedTile = getBoard().takeFromDiscardPile(); // take last discarded tile

            switch (decision) {
                case WIN -> {
                    // end round, trigger win logic
                }
                case BRIGHT_KONG -> {
                    hand.performBrightKongFromDiscard(claimedTile);
                    currentSeat = claimer;
                    startTurnWithBonusDraw();
                }
                case PONG -> {
                    hand.performPong(claimedTile);
                    currentSeat = claimer;
                    startTurnWithoutDraw();
                }
                case SHEUNG -> {
                    hand.performSheung(claimedTile, sheungCombo);
                    currentSeat = claimer;
                    startTurnWithoutDraw();
                }
            }
        } else {
            // no one claimed — move to next seat
            currentSeat = currentSeat.next();
            startTurnWithDraw();
        }

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

        // 3. check if other players can claim this tile (win, kong, pong, sheung)
        expectedClaims = checkForClaimsOnDiscard(discardedTile);

        if (!expectedClaims.isEmpty()) {
            promptClaimDecisions(discardedTile, currentSeat, expectedClaims);
            // exit here — wait for responses to come in from players
            return;
        }

        // 4. if no one claims, proceed to next player’s turn
        currentSeat = currentSeat.next();
        startTurnWithDraw();
    }

//============================== HANDLE RESPONSE FROM DRAW ==============================//

    public void handleClaimResponseFromDraw(Player player, Decision decision) {
        room.getTimeoutScheduler().cancel("discard:" + player.getId());

        if (room.getSeat(player) != currentSeat) return;

        Hand hand = table.getHand(currentSeat);

        switch (decision) {
            case WIN -> {
                // end round, trigger win logic
            }
            case DARK_KONG -> {
                Tile kongTile = hand.getLastDrawnTile();
                hand.performDarkKong(kongTile);
                startTurnWithBonusDraw();
            }
            case BRIGHT_KONG -> {
                // promote pong into kong
                Tile kongTile = hand.getLastDrawnTile();
                hand.performBrightKongFromDraw(kongTile);
                startTurnWithBonusDraw();
            }
            case PASS -> {
                promptDiscard(); // continue turn
            }
            default -> throw new IllegalStateException("Unexpected decision: " + decision);
        }
    }

    public void handleAutoPassDrawDecision(Player player) {
        handleClaimResponseFromDraw(player, Decision.PASS);
    }

//============================== HELPERS ==============================//

    private PlayerContext getPlayerContext() {
        return room.getPlayerContext(currentSeat);
    }

    private TableDTO fromTable() {
        return DTOMapper.fromTable(table, currentSeat);
    }

    private int decisionPriority(Decision d) {
        return switch (d) {
            case WIN -> 0;
            case BRIGHT_KONG -> 1;
            case PONG -> 2;
            case SHEUNG -> 3;
            default -> Integer.MAX_VALUE; // PASS or anything unexpected
        };
    }

    private int distanceFromCurrentSeat(Seat seat) {
        Seat ref = currentSeat.next();
        int diff = (seat.ordinal() - ref.ordinal() + 4) % 4;
        return diff;
    }

    private void resetClaims() {
        expectedClaims.clear();
        claimResponses.clear();
    }
}
