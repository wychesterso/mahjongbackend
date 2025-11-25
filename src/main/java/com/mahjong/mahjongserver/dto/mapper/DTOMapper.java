package com.mahjong.mahjongserver.dto.mapper;

import com.mahjong.mahjongserver.domain.game.Game;
import com.mahjong.mahjongserver.domain.game.GameAction;
import com.mahjong.mahjongserver.domain.game.claim.ClaimOption;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringPattern;
import com.mahjong.mahjongserver.domain.player.decision.Decision;
import com.mahjong.mahjongserver.domain.room.Room;
import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.Table;
import com.mahjong.mahjongserver.domain.room.board.Hand;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.dto.state.*;

import java.util.*;

public class DTOMapper {
    public static HandDTO fromHand(Hand hand, boolean isSelf) {
        List<Tile> concealedTiles = isSelf ? hand.getConcealedTiles() : List.of();
        List<List<Tile>> darkKongs = isSelf ? hand.getDarkKongs() : List.of();

        return new HandDTO(
                concealedTiles,
                hand.getConcealedTileCount(),
                hand.getSheungs(),
                hand.getPongs(),
                hand.getBrightKongs(),
                darkKongs,
                hand.getDarkKongCount(),
                hand.getFlowers()
        );
    }

    public static TableDTO fromTable(Table table, Seat selfSeat, long tableVersion) {
        Map<Seat, HandDTO> handDTOs = new EnumMap<>(Seat.class);
        for (Seat seat : Seat.values()) {
            handDTOs.put(seat, fromHand(table.getHand(seat), seat == null || seat == selfSeat));
        }

        return new TableDTO(
                tableVersion,
                table.getBoard().getDiscardPile(),
                table.getBoard().getDrawPileSize(),
                selfSeat,
                handDTOs
        );
    }

    public static ScoringContextDTO fromScoringContext(ScoringContext scoringContext) {
        return new ScoringContextDTO(
                scoringContext.getScoringPatterns().stream().map(DTOMapper::fromScoringPattern).toList(),
                scoringContext.getFlowers(),
                scoringContext.getRevealedGroups(),
                scoringContext.getConcealedGroups(),
                scoringContext.getWinningGroup(),
                scoringContext.getWinningTile()
        );
    }

    public static ScoringPatternDTO fromScoringPattern(ScoringPattern scoringPattern) {
        return new ScoringPatternDTO(
                scoringPattern.name(),
                scoringPattern.getName(),
                scoringPattern.getDescription(),
                scoringPattern.getValue());
    }

    public static RoomInfoDTO fromRoom(Room room) {
        return new RoomInfoDTO(
                room.getRoomId(),
                room.getHostId(),
                room.getNumEmptySeats(),
                room.getPlayerNames(),
                room.getBotStatuses()
        );
    }

    public static GameStateDTO fromGame(Game game, Seat seat, long gameStateVersion, long tableVersion) {
        TableDTO tableDTO = fromTable(game.getTable(), seat, tableVersion);

        // determine if the player has a drawn tile
        Tile drawnTile = null;
        if (game.getCurrentAction() == GameAction.DRAW && game.getCurrentSeat() == seat) {
            Hand hand = game.getTable().getHand(seat);
            if (hand.getConcealedTileCount() != 0) {
                drawnTile = game.getTable().getHand(seat).getLastDrawnTile();
            }
        }

        // only show decisions for current player if there are expected claims for them
        List<Decision> availableDecisions = null;
        if (game.getCurrentSeat() == seat && game.getExpectedClaims().containsKey(seat)) {
            availableDecisions = game.getExpectedClaims().get(seat).stream()
                    .map(ClaimOption::getDecision)
                    .distinct()
                    .toList();
        }

        // map expected claimants: Map<Seat, List<Decision>>
        Map<Seat, List<Decision>> expectedClaimants = new EnumMap<>(Seat.class);
        for (Map.Entry<Seat, List<ClaimOption>> entry : game.getExpectedClaims().entrySet()) {
            expectedClaimants.put(
                    entry.getKey(),
                    entry.getValue().stream()
                            .map(ClaimOption::getDecision)
                            .distinct()
                            .toList()
            );
        }

        return new GameStateDTO(
                gameStateVersion,
                tableDTO,
                game.getCurrentSeat(),
                game.getWindSeat(),
                game.getZhongSeat(),
                game.getRoom().getPlayerNames(),
                expectedClaimants,
                game.getBoard().getLastDiscardedTile(),
                game.getCurrentSeat(),
                availableDecisions,
                drawnTile,
                game.isActiveGame(),
                game.getWinnerSeats(),
                game.getNumDraws()
        );
    }
}
