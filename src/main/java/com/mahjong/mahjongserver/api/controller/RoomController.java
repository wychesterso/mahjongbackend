package com.mahjong.mahjongserver.api.controller;

import com.mahjong.mahjongserver.domain.core.RoomManager;
import com.mahjong.mahjongserver.domain.game.score.TaiwaneseSixteenScoreCalculator;
import com.mahjong.mahjongserver.domain.player.Player;
import com.mahjong.mahjongserver.domain.player.RealPlayer;
import com.mahjong.mahjongserver.domain.room.Seat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/room")
public class RoomController {

    private final RoomManager roomManager;

    public RoomController(RoomManager roomManager) {
        this.roomManager = roomManager;
    }

    /**
     * Create a new room, assigning the requesting player as host.
     */
    @PostMapping("/create")
    public ResponseEntity<Void> createRoom(
            @RequestParam String roomId,
            @RequestParam String hostPlayerId) {

        Player host = new RealPlayer(hostPlayerId); // use your factory or domain model
        roomManager.createRoom(roomId, new TaiwaneseSixteenScoreCalculator(), host);
        return ResponseEntity.ok().build();
    }

    /**
     * Join a room as a real player to a specific seat.
     */
    @PostMapping("/{roomId}/join")
    public ResponseEntity<Void> joinRoom(
            @PathVariable String roomId,
            @RequestParam String playerId,
            @RequestParam Seat seat) {

        Player player = new RealPlayer(playerId);
        roomManager.joinRoom(roomId, seat, player);
        return ResponseEntity.ok().build();
    }

    /**
     * Assign a bot to a seat in the specified room.
     */
    @PostMapping("/{roomId}/add-bot")
    public ResponseEntity<Void> addBot(
            @PathVariable String roomId,
            @RequestParam Seat seat,
            @RequestParam String botId) {

        roomManager.assignBotToSeat(roomId, seat, botId);
        return ResponseEntity.ok().build();
    }

    /**
     * Exit a room as a real player.
     */
    @PostMapping("/{roomId}/exit")
    public ResponseEntity<Void> exitRoom(
            @PathVariable String roomId,
            @RequestParam String playerId) {

        Player player = new RealPlayer(playerId);
        roomManager.exitRoom(roomId, player);
        return ResponseEntity.ok().build();
    }
}