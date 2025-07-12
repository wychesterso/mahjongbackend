package com.mahjong.mahjongserver.domain.game;

import com.mahjong.mahjongserver.domain.core.TurnManager;
import com.mahjong.mahjongserver.domain.core.turn.data.TurnEnder;
import com.mahjong.mahjongserver.domain.player.Player;
import com.mahjong.mahjongserver.domain.player.data.Seat;
import com.mahjong.mahjongserver.dto.NetEarningsDTO;
import com.mahjong.mahjongserver.messaging.GameEventPublisher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
    private final GameEventPublisher publisher;
    private final String roomId;

    private final List<Player> players;
    private final TurnManager turnManager;
    // private final ScoreManager scoreManager;

    List<Seat> seats;
    private Seat gameSeat;
    private Seat roundSeat;
    private int lumZhongNum;
    private boolean ongoing;
    Map<Player, Map<Player, Integer>> pullZhong;

    public Game(List<Player> players, GameEventPublisher publisher, String roomId) {
        this.players = players;
        this.publisher = publisher;
        this.roomId = roomId;
        this.turnManager = new TurnManager(players, publisher, roomId);
    }

    /**
     * Starts the game.
     */
    public void start() {
        // record starting scores
        Map<Player, Integer> originalScores = recordScores();

        // initialize seat and pull variables
        resetSeatsAndPulls();

        // continuously run rounds until terminated
        boolean gameFlag = true;
        while (gameFlag) {
            gameFlag = startRound();
        }

        // resolve remaining pulls
        settleRemainingPulls();

        // display gains and losses
        publisher.sendRoomSummary(roomId, new NetEarningsDTO(originalScores, players));
    }

    public boolean startRound() {
        // display round banner
//        Prompter.printLine();
//        Prompter.printLine("\n\n\n");
//        Prompter.printLine(gameSeat.getSeatNameEng().toUpperCase() + " GAME "
//                + roundSeat.getSeatNameEng().toUpperCase() + " ROUND");
//        Prompter.printLine(gameSeat.getSeatNameChi() + "圈" + roundSeat.getSeatNameChi() + "局");
//        Prompter.printLine("\n\n\n");
//
//        // run round
//        TurnEnder turnEnder = turnManager.startRound(roundSeat);
//
//        // handle wins
//        List<Player> winners = new ArrayList<>();
//        String lastEvent = turnManager.getLastEvent();
//        int discardCount = turnManager.getDiscardCount();
//        if (turnEnder == TurnEnder.END_GAME_WIN) {
//            Player loser = turnManager.getCurrentPlayer();
//            winners.addAll(turnManager.getWinners());
//            for (Player winner : winners) {
//                // CALCULATE BEST HAND AND SCORE
//                HandManager winnerHandManager = winner.getHandManager();
//                List<List<Tile>> highestHand = new ArrayList<>();
//                List<MahjongPoint> highestPoints = new ArrayList<>();
//                int highestScore = calculateScore(highestHand, highestPoints, gameSeat,
//                        roundSeat, turnManager, winnerHandManager, winner.getSeat(),
//                        loser.getSeat(), winners.size() > 1, lumZhongNum,
//                        lastEvent, discardCount);
//
//                // DISPLAY RESULTS
//                Prompter.printLine(winner.toStringWithSeat() + " won off " + loser.toStringWithSeat() + "!");
//                Prompter.printLine(winnerHandManager.toStringSepLastDrawn());
//                Prompter.printLine(Prompter.pointsToDisplay(highestPoints));
//                Prompter.printLine("Score: " + highestScore);
//                Prompter.printLine();
//                revertPointScores();
//
//                // HANDLE PULLS
//                for (Map.Entry<Player, Map<Player, Integer>> entry : pullZhong.entrySet()) {
//                    Player leader = entry.getKey();
//                    Map<Player, Integer> leaderPulls = entry.getValue();
//                    if (leader == winner) {
//                        try {
//                            int trailingScore = leaderPulls.get(loser);
//                            if (trailingScore == 0) {
//                                throw new NullPointerException();
//                            }
//                            leaderPulls.put(loser, highestScore + (trailingScore * 2));
//                            highestScore += trailingScore;
//                        } catch (NullPointerException e) {
//                            leaderPulls.put(loser, highestScore);
//                        }
//                    } else {
//                        for (Map.Entry<Player, Integer> losing : leaderPulls.entrySet()) {
//                            Player trailer = losing.getKey();
//                            int trailingScore = losing.getValue();
//                            if (winner == trailer && loser == leader) {
//                                trailingScore = trailingScore / 2;
//                            }
//                            trailer.deductScore(trailingScore);
//                            leader.addScore(trailingScore);
//                            displayMoneyCollect(leader, trailer, trailingScore);
//                            Prompter.printLine();
//                        }
//                        leaderPulls.clear();
//                    }
//                }
//                displayPullZhong(pullZhong);
//            }
//        } else if (turnEnder == TurnEnder.END_GAME_WIN_SELFDRAW) {
//            Player winner = turnManager.getCurrentPlayer();
//            winners.add(winner);
//
//            // CALCULATE BEST HAND AND SCORE
//            HandManager winnerHandManager = winner.getHandManager();
//            List<List<Tile>> highestHand = new ArrayList<>();
//            List<MahjongPoint> highestPoints = new ArrayList<>();
//            int highestScore = calculateScore(highestHand, highestPoints, gameSeat,
//                    roundSeat, turnManager, winnerHandManager, winner.getSeat(),
//                    winner.getSeat(), false, lumZhongNum,
//                    lastEvent, discardCount);
//
//            // DISPLAY RESULTS
//            Prompter.printLine(winner.toStringWithSeat() + " won off a self-draw!");
//            Prompter.printLine(winnerHandManager.toStringSepLastDrawn());
//            Prompter.printLine(Prompter.pointsToDisplay(highestPoints));
//            Prompter.printLine("Score: " + highestScore);
//            Prompter.printLine();
//            revertPointScores();
//
//            // ADJUST SCORES BASED ON SEAT
//            int zhongScore = highestScore + 2 * (2 * lumZhongNum + 1);
//            Map<Player, Integer> lostScores = new HashMap<>();
//            for (Player loser : turnManager.getOtherPlayers()) {
//                if (roundSeat == loser.getSeat()) {
//                    lostScores.put(loser, zhongScore);
//                } else {
//                    lostScores.put(loser, highestScore);
//                }
//            }
//
//            // HANDLE PULLS
//            for (Map.Entry<Player, Map<Player, Integer>> entry : pullZhong.entrySet()) {
//                Player leader = entry.getKey();
//                Map<Player, Integer> leaderPulls = entry.getValue();
//                if (leader == winner) {
//                    for (Player loser : turnManager.getOtherPlayers()) {
//                        int lostScore = lostScores.get(loser);
//                        if (leaderPulls.containsKey(loser)) {
//                            int trailingScore = leaderPulls.get(loser);
//                            leaderPulls.put(loser, lostScore + (trailingScore * 2));
//                        } else {
//                            leaderPulls.put(loser, lostScore);
//                        }
//                    }
//                } else {
//                    for (Map.Entry<Player, Integer> losing : leaderPulls.entrySet()) {
//                        Player trailer = losing.getKey();
//                        int trailingScore = losing.getValue();
//                        if (winner == trailer) {
//                            trailingScore = trailingScore / 2;
//                        }
//                        trailer.deductScore(trailingScore);
//                        leader.addScore(trailingScore);
//                        displayMoneyCollect(leader, trailer, trailingScore);
//                        Prompter.printLine();
//                    }
//                    leaderPulls.clear();
//                }
//            }
//            displayPullZhong(pullZhong);
//        } else {
//            Prompter.printLine("Game ended in a tie!");
//            Prompter.printLine();
//        }
//        for (Player player : playerList) {
//            player.clearHand();
//        }
//
//        String response = Prompter.prompter("Start new round? (Y/N)").toLowerCase();
//        if (response.equals("y") || response.equals("yes")) {
//            // CHANGES THE ROUND SEAT
//            boolean lumZhongFlag = false;
//            if (turnEnder == TurnEnder.END_GAME_DRAW) {
//                lumZhongFlag = true;
//            } else {
//                for (Player winner : winners) {
//                    if (roundSeat == winner.getSeat()) {
//                        lumZhongFlag = true;
//                        break;
//                    }
//                }
//            }
//            if (!lumZhongFlag) {
//                int currentIndex = seats.indexOf(roundSeat);
//                int nextIndex = (currentIndex + 1) % seats.size();
//                roundSeat = seats.get(nextIndex);
//                lumZhongNum = 0;
//            } else {
//                lumZhongNum += 1;
//            }
//
//            if (roundSeat == Seat.EAST && !lumZhongFlag) {
//                // CHANGES THE GAME SEAT
//                int currentIndexGame = seats.indexOf(gameSeat);
//                int nextIndexGame = (currentIndexGame + 1) % seats.size();
//                gameSeat = seats.get(nextIndexGame);
//            }
//            return true;
//        } else {
//            return false;
//        }
        return false;
    }

    public void handleRoundResult(TurnEnder ender) {

    }

    /**
     * Records the players' current scores inside a mapping.
     * @return the mapping of players to their scores.
     */
    private Map<Player, Integer> recordScores() {
        Map<Player, Integer> scores = new HashMap<>();
        for (Player player : players) {
            scores.put(player, player.getScore());
        }
        return scores;
    }

    /**
     * Resets the seats and zhong pulls at the start of a game.
     */
    private void resetSeatsAndPulls() {
        seats = List.of(Seat.values());
        gameSeat = seats.getFirst();
        roundSeat = seats.getFirst();
        lumZhongNum = 0;
        pullZhong = new HashMap<>();
        for (Player winner : players) {
            pullZhong.put(winner, new HashMap<>());
        }
    }

    /**
     * Resolve any pulls remaining at the end of the game.
     */
    private void settleRemainingPulls() {
        for (Map.Entry<Player, Map<Player, Integer>> entry : pullZhong.entrySet()) {
            Player leader = entry.getKey();
            for (Map.Entry<Player, Integer> losing : entry.getValue().entrySet()) {
                int trailingScore = losing.getValue();
                losing.getKey().deductScore(trailingScore);
                leader.addScore(trailingScore);
            }
        }
    }
}