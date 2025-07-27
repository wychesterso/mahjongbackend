package com.mahjong.mahjongserver.api.controller;

import com.mahjong.mahjongserver.domain.core.RoomManager;
import com.mahjong.mahjongserver.domain.game.score.TaiwaneseSixteenScoreCalculator;
import com.mahjong.mahjongserver.domain.room.Seat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/room")
public class RoomController {

    private final RoomManager roomManager;

    public RoomController(RoomManager roomManager) {
        this.roomManager = roomManager;
    }

    @GetMapping
    public ResponseEntity<?> getAvailableRooms() {
        return ResponseEntity.ok(roomManager.getAllRoomsInfo());
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<?> getRoomState(@PathVariable String roomId) {
        return ResponseEntity.ok(roomManager.getRoomInfo(roomId));
    }

    /**
     * Create a new room, assigning the requesting player as host.
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createRoom(Principal principal) {
        String playerId = principal.getName();
        String roomId = UUID.randomUUID().toString().substring(0, 8);
        roomManager.createRoom(roomId, new TaiwaneseSixteenScoreCalculator(), playerId);

        return ResponseEntity.ok(Map.of("roomId", roomId));
    }

    /**
     * Join a room as a real player to a specific seat.
     */
    @PostMapping("/{roomId}/join")
    public ResponseEntity<Void> joinRoom(
            @PathVariable String roomId,
            @RequestParam Seat seat,
            Principal principal) {
        String playerId = principal.getName();
        roomManager.joinRoom(roomId, seat, playerId);
        return ResponseEntity.ok().build();
    }

    /**
     * Switch a player's seat.
     */
    @PostMapping("/{roomId}/seat")
    public ResponseEntity<?> switchSeat(
            @PathVariable String roomId,
            @RequestParam Seat newSeat,
            Principal principal) {
        String playerId = principal.getName();
        roomManager.switchSeat(roomId, newSeat, playerId);
        return ResponseEntity.ok().build();
    }

    /**
     * Assign a bot to a seat in the specified room.
     */
    @PostMapping("/{roomId}/add-bot")
    public ResponseEntity<Void> addBot(
            @PathVariable String roomId,
            @RequestParam Seat seat,
            Principal principal) {
        String playerId = principal.getName();

        roomManager.assignBotToSeat(roomId, seat, playerId);
        return ResponseEntity.ok().build();
    }

    /**
     * Remove a bot from a seat in the specified room.
     */
    @PostMapping("/{roomId}/remove-bot")
    public ResponseEntity<Void> removeBot(
            @PathVariable String roomId,
            @RequestParam Seat seat,
            Principal principal) {
        String playerId = principal.getName();

        roomManager.removeBotFromSeat(roomId, seat, playerId);
        return ResponseEntity.ok().build();
    }

    /**
     * Exit a room as a real player.
     */
    @PostMapping("/{roomId}/exit")
    public ResponseEntity<Void> exitRoom(
            @PathVariable String roomId,
            Principal principal) {
        String playerId = principal.getName();
        roomManager.exitRoom(roomId, playerId);
        return ResponseEntity.ok().build();
    }
}