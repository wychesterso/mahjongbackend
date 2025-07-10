package com.mahjong.mahjongserver.service;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.mahjong.mahjongserver.domain.player.Player;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public List<Player> getPlayers() {
        return playerService.getAllPlayers();
    }

    @PostMapping
    public ResponseEntity<Void> createPlayer(@RequestBody Player player) {
        playerService.addPlayer(player);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{name}")
    public void updatePlayer(@PathVariable String name, @RequestBody Player updated) {
        playerService.updatePlayer(name, updated);
    }

    @DeleteMapping("/{name}")
    public void deletePlayer(@PathVariable String name) {
        playerService.removePlayer(name);
    }
}
