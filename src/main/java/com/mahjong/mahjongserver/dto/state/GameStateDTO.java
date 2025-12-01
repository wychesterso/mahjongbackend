package com.mahjong.mahjongserver.dto.state;

import com.mahjong.mahjongserver.domain.player.decision.Decision;
import com.mahjong.mahjongserver.domain.room.Seat;
import com.mahjong.mahjongserver.domain.room.board.tile.Tile;

import java.util.List;
import java.util.Map;

public record GameStateDTO(
        long gameNum,
        long gameStateVersion,
        TableDTO table,
        Seat currentTurn,
        Seat windSeat,
        Seat zhongSeat,
        Map<Seat, String> playerNames,
        Map<Seat, List<Decision>> expectedClaimants,
        Tile lastDiscardedTile,
        Seat lastDiscarder,
        List<Decision> availableDecisions, // only for this player
        Tile drawnTile, // only visible to current player
        boolean gameActive,
        List<Seat> winnerSeats,
        int numDraws,
        int remainingTiles
) {}