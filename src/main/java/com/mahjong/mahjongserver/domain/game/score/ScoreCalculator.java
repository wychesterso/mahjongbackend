package com.mahjong.mahjongserver.domain.game.score;

import com.mahjong.mahjongserver.domain.game.Game;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.room.Seat;

public interface ScoreCalculator {
    ScoringContext calculateScore(Game game, Seat seat, int lumZhongCount);
}
