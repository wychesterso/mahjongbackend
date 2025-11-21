package com.mahjong.mahjongserver.domain.game;

import com.mahjong.mahjongserver.domain.game.claim.ClaimOption;
import com.mahjong.mahjongserver.domain.game.claim.ClaimResolution;
import com.mahjong.mahjongserver.domain.game.score.HandChecker;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
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
import com.mahjong.mahjongserver.dto.state.EndGameDTO;
import com.mahjong.mahjongserver.dto.state.ScoringContextDTO;
import com.mahjong.mahjongserver.dto.state.TableDTO;

import java.util.*;
import java.util.function.Supplier;

public class Game {
    private boolean activeGame = false;

    // gameplay info
    private Table table = new Table();
    private final Seat windSeat;
    private final Seat zhongSeat;
    private Seat currentSeat;

    // game statistics and scoring
    private final List<Seat> winnerSeats = new ArrayList<>();
    private int numDraws = 0;

    // prompting and frontend player-interaction stuff
    private final Room room;
    private Map<Seat, List<ClaimOption>> expectedClaims = new EnumMap<>(Seat.class);
    private final Map<Seat, ClaimResolution> claimResponses = new HashMap<>();
    private static final long TIMEOUT_MILLIS = 10_000;
    private boolean awaitingDiscard = false;

    public Game(Room room, Seat windSeat) {
        this.room = room;
        this.windSeat = windSeat;
        zhongSeat = Seat.EAST;
        currentSeat = Seat.EAST;
    }

    public Game(Room room, Seat windSeat, Seat zhongSeat) {
        this.room = room;
        this.windSeat = windSeat;
        this.zhongSeat = zhongSeat;
        currentSeat = zhongSeat;
    }

//============================== GETTERS ==============================//

    public Room getRoom() {
        return room;
    }

    public boolean isActiveGame() {
        return activeGame;
    }

    public Table getTable() {
        return table;
    }

    public Board getBoard() {
        return table.getBoard();
    }

    public Seat getWindSeat() {
        return windSeat;
    }

    public Seat getZhongSeat() {
        return zhongSeat;
    }

    public Seat getCurrentSeat() {
        return currentSeat;
    }

    public List<Seat> getWinnerSeats() {
        return winnerSeats;
    }

    public Map<Seat, List<ClaimOption>> getExpectedClaims() {
        return expectedClaims;
    }

    public int getNumDraws() {
        return numDraws;
    }

    //============================== EVENTS ==============================//

    public void startGame() {
        resetGameState();

        activeGame = true;
        System.out.println("[Game] startGame(): activeGame set to true for room=" + room.getRoomId() + ", currentSeat=" + currentSeat);

        dealStartingHands();
        System.out.println("[Game] startGame(): finished dealing hands for room=" + room.getRoomId());
        if (handleClaimsAfterDraw(table.getHand(currentSeat).getLastDrawnTile())) return;
        startTurnWithoutDraw();
    }

    private void resetGameState() {
        table = new Table();
        winnerSeats.clear();
        numDraws = 0;

        expectedClaims.clear();
        claimResponses.clear();

        awaitingDiscard = false;

        currentSeat = zhongSeat;
    }

    public void startTurnWithoutDraw() {
        System.out.println("[Game] startTurnWithoutDraw(): room=" + room.getRoomId() + ", currentSeat=" + currentSeat);
        resetClaims();
        updateTableState();
        promptDiscard();
    }

    public void startTurnWithDraw() {
        System.out.println("[Game] startTurnWithDraw(): room=" + room.getRoomId() + ", currentSeat=" + currentSeat);
        resetClaims();
        updateTableState();

        // draw tile
        Hand hand = table.getHand(currentSeat);
        Tile drawnTile = drawTile(hand);
        numDraws++;
        if (drawnTile == null) {
            endGameByDraw();
            return;
        }

        // check claim options
        if (handleClaimsAfterDraw(drawnTile)) return;

        // prompt discard
        promptDiscardOnDraw(drawnTile);
    }

    public void startTurnWithBonusDraw() {
        resetClaims();
        updateTableState();

        // draw tile
        Hand hand = table.getHand(currentSeat);
        Tile drawnTile = drawBonusTile(hand);
        numDraws++;
        if (drawnTile == null) {
            endGameByDraw();
            return;
        }

        // check claim options
        if (handleClaimsAfterDraw(drawnTile)) return;

        // prompt discard
        promptDiscardOnDraw(drawnTile);
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
        if (tile == null) return null; // drawPile exhausted, end game

        while (tile.getTileType().getClassification() == TileClassification.FLOWER) {
            hand.addFlower(tile);
            tile = getBoard().drawBonusTile();
            if (tile == null) return null;
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

            if (currentSeat.next() == seat && HandChecker.checkSheung(hand, discardedTile)) {
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

    private boolean handleClaimsAfterDraw(Tile drawnTile) {
        List<ClaimOption> claimOptions = checkForClaimsOnDraw();
        if (!claimOptions.isEmpty()) {
            expectedClaims.put(currentSeat, claimOptions);

            promptDrawDecision(drawnTile, currentSeat, claimOptions.stream()
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

//============================== SEND PROMPTS ==============================//

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

            List<List<Tile>> sheungCombos = claimOptions.stream()
                    .filter(opt -> opt.getDecision() == Decision.SHEUNG)
                    .findFirst()
                    .map(ClaimOption::getValidCombos)
                    .orElse(Collections.emptyList());

            System.out.println("[Game] promptClaimDecision: room=" + room.getRoomId() + ", discarder=" + discarder + ", claimant=" + claimant);
            promptDiscardDecision(discardedTile, discarder, claimant, options, sheungCombos);

            // start timeout to auto-pass if no response
            room.getTimeoutScheduler().schedule(
                    "claim:" + player.getId(),
                    () -> handleAutoPassClaim(player),
                    TIMEOUT_MILLIS
            );
        }
    }

    private void promptDrawDecision(Tile drawnTile, Seat claimee, List<Decision> availableOptions) {
        System.out.println("[Game] promptDrawDecision: room=" + room.getRoomId() + ", claimee=" + claimee);

        PlayerContext ctx = room.getPlayerContext(claimee);
        ctx.getDecisionHandler().promptDecisionOnDraw(
                ctx,
                DTOMapper.fromTable(table, claimee),
                drawnTile,
                availableOptions
        );
    }

    private void promptDiscardDecision(Tile discardedTile, Seat discarder, Seat claimee,
                                       List<Decision> availableOptions, List<List<Tile>> sheungCombos) {
        System.out.println("[Game] promptDiscardDecision: room=" + room.getRoomId() + ", discarder=" + discarder + ", claimee=" + claimee);

        PlayerContext ctx = room.getPlayerContext(claimee);
        ctx.getDecisionHandler().promptDecisionOnDiscard(
                ctx,
                DTOMapper.fromTable(table, claimee),
                discardedTile,
                discarder,
                availableOptions,
                sheungCombos
        );
    }

    private void promptDiscard() {
        System.out.println("[Game] promptDiscard: room=" + room.getRoomId() + ", currentSeat=" + currentSeat);
        awaitingDiscard = true;

        PlayerContext ctx = getPlayerContext();
        ctx.getDecisionHandler().promptDiscard(ctx, fromTable());

        room.getTimeoutScheduler().schedule("discard:" + ctx.getPlayer().getId(),
                this::handleAutoDiscard, TIMEOUT_MILLIS);
    }

    private void promptDiscardOnDraw(Tile drawnTile) {
        System.out.println("[Game] promptDiscardOnDraw: room=" + room.getRoomId() + ", currentSeat=" + currentSeat);
        awaitingDiscard = true;

        PlayerContext ctx = getPlayerContext();
        ctx.getDecisionHandler().promptDiscardOnDraw(ctx, fromTable(), drawnTile);

        room.getTimeoutScheduler().schedule("discard:" + ctx.getPlayer().getId(),
                this::handleAutoDiscard, TIMEOUT_MILLIS);
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
                    DTOMapper.fromGame(this, seat)
            );
        }
    }

//============================== HANDLE FRONTEND RESPONSE - DISCARD ==============================//

    public void handleClaimResponseFromDiscard(Player player, Decision decision, List<Tile> selectedSheung) {
        Seat claimer = room.getSeat(player);
        if (claimResponses.containsKey(claimer)) {
            // already responded
            System.out.println("[Game] Ignored duplicate claim response, room=" + room.getRoomId() + ", claimer=" + claimer + ", decision=" + decision);
            return;
        }

        if (!expectedClaims.containsKey(claimer)
                || (decision != Decision.PASS && expectedClaims.get(claimer).stream().noneMatch(opt -> opt.getDecision() == decision))) {
            // ignore invalid responses
            System.out.println("[Game] Ignored invalid claim response, room=" + room.getRoomId() + ", claimer=" + claimer + ", decision=" + decision);
            return;
        }

        if (decision == Decision.SHEUNG && !containsSheungCombo(expectedClaims.get(claimer), selectedSheung)) {
            System.out.println("[Game] No sheung combo provided, room=" + room.getRoomId() + ", claimer=" + claimer);
            return;
        }

        // cancel timeout and save this decision
        room.getTimeoutScheduler().cancel("claim:" + player.getId());
        claimResponses.put(claimer, new ClaimResolution(decision, selectedSheung));
        System.out.println("[Game] Received claim response, room=" + room.getRoomId() + ", claimer=" + claimer + ", decision=" + decision);

        // wait until all expected claimants respond or timeout
        if (claimResponses.size() == expectedClaims.size()) {
            System.out.println("[Game] Resolving claims, room=" + room.getRoomId());
            resolveClaims();
        }
    }

    public void handleAutoPassClaim(Player player) {
        handleClaimResponseFromDiscard(player, Decision.PASS, null);
    }

    public void resolveClaims() {
        // 1. select all wins
        List<Map.Entry<Seat, ClaimResolution>> wins = claimResponses.entrySet().stream()
                .filter(entry -> entry.getValue().getDecision() == Decision.WIN)
                .toList();

        if (!wins.isEmpty()) {
            // note that there can be up to 3 winners
            for (Map.Entry<Seat, ClaimResolution> win : wins) {
                Seat claimer = win.getKey();
                Hand hand = table.getHand(claimer);
                Tile claimedTile = getBoard().getLastDiscardedTile(); // copy the tile and add it to each winning hand
                hand.addTile(claimedTile);

                winnerSeats.add(claimer);
            }
            getBoard().takeFromDiscardPile(); // remove the tile from discard pile

            endGameByWin();
            return;
        }

        // 2. select highest priority non-win claim (BRIGHT_KONG > PONG > SHEUNG > PASS)
        Optional<Map.Entry<Seat, ClaimResolution>> winningClaim = claimResponses.entrySet().stream()
                .filter(entry -> entry.getValue().getDecision() != Decision.PASS)
                .min((a, b) -> {
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
                default -> {
                    currentSeat = currentSeat.next();
                    startTurnWithDraw();
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
        // 1. validate the discard request
        Seat playerSeat = room.getSeat(player);
        if (!awaitingDiscard || playerSeat != currentSeat) {
            // ignore invalid discard requests
            System.out.println("[Game] Ignored duplicate/invalid discard, room=" + room.getRoomId() + ", seat=" + playerSeat + ", current=Seat=" + currentSeat + ", discardedTile=" + discardedTile + ", awaitingDiscard=" + awaitingDiscard);
            return;
        }

        awaitingDiscard = false;
        System.out.println("[Game] Received discard request, room=" + room.getRoomId() + ", seat=" + playerSeat + ", discardedTile=" + discardedTile);

        // 2. remove tile from hand and update discard pile
        Hand hand = table.getHand(currentSeat);
        if (!hand.discardTile(discardedTile)) {
            // ignore request to discard nonexistent tile -> fallback on timeout
            System.out.println("[Game] Attempting to discard nonexistent tile, room=" + room.getRoomId() + ", seat=" + playerSeat + ", discardedTile=" + discardedTile);
            return;
        }
        room.getTimeoutScheduler().cancel("discard:" + player.getId());
        getBoard().putInDiscardPile(discardedTile);

        updateTableState();

        // 3. check if other players can claim this tile (win, kong, pong, sheung)
        expectedClaims.putAll(checkForClaimsOnDiscard(discardedTile));

        if (!expectedClaims.isEmpty()) {
            promptClaimDecisions(discardedTile, currentSeat, expectedClaims);
            // exit here — wait for responses to come in from players
            return;
        }

        // 4. if no one claims, proceed to next player’s turn
        currentSeat = currentSeat.next();
        startTurnWithDraw();
    }

    public void handleAutoDiscard() {
        handleDiscard(getPlayerContext().getPlayer(), table.getHand(currentSeat).getLastDrawnTile());
    }

//============================== HANDLE FRONTEND RESPONSE - DRAW ==============================//

    public void handleClaimResponseFromDraw(Player player, Decision decision) {
        if (room.getSeat(player) != currentSeat) {
            System.out.println("[Game] Ignored invalid claim response, room=" + room.getRoomId() + ", claimer=" + player.getId() + ", decision=" + decision);
            return;
        }

        room.getTimeoutScheduler().cancel("claim:" + player.getId());

        Hand hand = table.getHand(currentSeat);

        switch (decision) {
            case WIN -> {
                winnerSeats.add(currentSeat);
                endGameByWin();
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

//============================== GAME END ==============================//

    public void endGameByDraw() {
        activeGame = false;
        updateTableState();

        room.getGameEventPublisher().sendGameEnd(
                room.getRoomId(),
                new EndGameDTO(GameResult.DRAW, Map.of(), Set.of(), DTOMapper.fromTable(table, null))
        );

        room.onGameEnd();
    }

    public void endGameByWin() {
        activeGame = false;
        updateTableState();

        // get winners and scoring
        Map<Seat, ScoringContextDTO> winners = new HashMap<>();
        for (Seat seat : winnerSeats) {
            ScoringContext scoringContext = room.getScoreCalculator().calculateScore(this, seat);
            winners.put(seat, DTOMapper.fromScoringContext(scoringContext));
        }

        // get losers
        Set<Seat> loserSeats = EnumSet.noneOf(Seat.class);
        if (winnerSeats.contains(currentSeat)) {
            // self draw
            for (Seat seat : Seat.values()) {
                if (seat != currentSeat) loserSeats.add(seat);
            }
        } else {
            // current seat is loser
            loserSeats.add(currentSeat);
        }

        room.getGameEventPublisher().sendGameEnd(
                room.getRoomId(),
                new EndGameDTO(GameResult.WIN, winners, loserSeats, DTOMapper.fromTable(table, null))
        );

        room.onGameEnd();
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
        return (seat.ordinal() - ref.ordinal() + 4) % 4;
    }

    private void resetClaims() {
        expectedClaims.clear();
        claimResponses.clear();
    }

    private boolean containsSheungCombo(List<ClaimOption> options, List<Tile> target) {
        for (ClaimOption option : options) {
            for (List<Tile> combo : option.getValidCombos()) {
                if (combo != null && isEqualCombo(combo, target)) return true;
            }
        }
        return false;
    }

    private boolean isEqualCombo(List<Tile> a, List<Tile> b) {
        if (a.size() != b.size()) return false;
        List<Tile> copyA = new ArrayList<>(a);
        List<Tile> copyB = new ArrayList<>(b);
        Collections.sort(copyA);
        Collections.sort(copyB);
        return copyA.equals(copyB);
    }
}
