package com.mahjong.mahjongserver.dto.mapper;

import com.mahjong.mahjongserver.domain.game.Game;
import com.mahjong.mahjongserver.domain.game.claim.ClaimOption;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
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
        List<Tile> concealedTiles = isSelf ? hand.getConcealedTiles() : null;
        List<List<Tile>> darkKongs = isSelf ? hand.getDarkKongs() : null;

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

    public static TableDTO fromTable(Table table, Seat selfSeat) {
        Map<Seat, HandDTO> handDTOs = new EnumMap<>(Seat.class);
        for (Seat seat : Seat.values()) {
            handDTOs.put(seat, fromHand(table.getHand(seat), seat == null || seat == selfSeat));
        }

        return new TableDTO(
                table.getBoard().getDiscardPile(),
                table.getBoard().getDrawPileSize(),
                selfSeat,
                handDTOs
        );
    }

    public static ScoringContextDTO fromScoringContext(ScoringContext scoringContext) {
        List<TileGroupDTO> tileGroupDTOS = new ArrayList<>();
        tileGroupDTOS.add(fromTileGroup(scoringContext.getFlowers(), "flowers"));
        for (List<Tile> pair : scoringContext.getConcealedPairs()) {
            tileGroupDTOS.add(fromTileGroup(pair, "pair"));
        }
        for (List<Tile> group : scoringContext.getConcealedSheungs()) {
            tileGroupDTOS.add(fromTileGroup(group, "concealed sheung"));
        }
        for (List<Tile> group : scoringContext.getRevealedSheungs()) {
            tileGroupDTOS.add(fromTileGroup(group, "revealed sheung"));
        }
        for (List<Tile> group : scoringContext.getConcealedPongs()) {
            tileGroupDTOS.add(fromTileGroup(group, "concealed pong"));
        }
        for (List<Tile> group : scoringContext.getRevealedPongs()) {
            tileGroupDTOS.add(fromTileGroup(group, "revealed pong"));
        }
        for (List<Tile> group : scoringContext.getBrightKongs()) {
            tileGroupDTOS.add(fromTileGroup(group, "bright kong"));
        }
        for (List<Tile> group : scoringContext.getDarkKongs()) {
            tileGroupDTOS.add(fromTileGroup(group, "dark kong"));
        }

        return new ScoringContextDTO(scoringContext.getScoringPatterns(), null);
    }

    public static TileGroupDTO fromTileGroup(Collection<Tile> tileGroup, String type) {
        return new TileGroupDTO(type, new ArrayList<>(tileGroup));
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

    public static GameStateDTO fromGame(Game game, Seat seat) {
        TableDTO tableDTO = fromTable(game.getTable(), seat);

        // determine if the player has a drawn tile
        Tile drawnTile = null;
        if (game.getCurrentSeat() == seat) {
            drawnTile = game.getTable().getHand(seat).getLastDrawnTile();
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
