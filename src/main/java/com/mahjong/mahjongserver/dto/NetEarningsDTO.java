package com.mahjong.mahjongserver.dto;

import com.mahjong.mahjongserver.domain.player.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class NetEarningsDTO {
    private final List<PlayerEarningsDTO> earnings;

    public NetEarningsDTO(List<PlayerEarningsDTO> earnings) {
        this.earnings = earnings;
        this.earnings.sort(Comparator.comparingInt(PlayerEarningsDTO::getNetEarnings).reversed());
    }

    public NetEarningsDTO(Map<Player, Integer> originalScores, List<Player> players) {
        this.earnings = new ArrayList<>();
        for (Player player : players) {
            PlayerEarningsDTO playerEarnings = new PlayerEarningsDTO(
                    player.getName(),
                    player.getSeat().toString(),
                    originalScores.get(player),
                    player.getScore()
            );
            earnings.add(playerEarnings);
        }
        this.earnings.sort(Comparator.comparingInt(PlayerEarningsDTO::getNetEarnings).reversed());
    }

    public List<PlayerEarningsDTO> getEarnings() {
        return earnings;
    }
}
