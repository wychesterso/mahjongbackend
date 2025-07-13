package com.mahjong.mahjongserver.dto.mapper;

import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.Table;
import com.mahjong.mahjongserver.domain.room.board.Hand;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;
import com.mahjong.mahjongserver.dto.table.HandDTO;
import com.mahjong.mahjongserver.dto.table.TableDTO;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

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
            handDTOs.put(seat, fromHand(table.getHand(seat), seat == selfSeat));
        }

        return new TableDTO(
                table.getBoard().getDiscardPile(),
                table.getBoard().getDrawPileSize(),
                selfSeat,
                handDTOs
        );
    }
}
