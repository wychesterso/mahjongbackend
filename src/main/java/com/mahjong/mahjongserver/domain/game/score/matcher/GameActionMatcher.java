package com.mahjong.mahjongserver.domain.game.score.matcher;

import com.mahjong.mahjongserver.domain.game.GameAction;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringContext;
import com.mahjong.mahjongserver.domain.game.score.data.ScoringPattern;

import java.util.Stack;

public class GameActionMatcher implements ScoringPatternMatcher {

    @Override
    public void match(ScoringContext ctx) {
        Stack<GameAction> actions = ctx.getGame().getActions();

        matchSelfDrawAfterDrawingFlower(ctx, actions);
        matchSelfDrawAfterKong(ctx, actions);
        matchRobbingKong(ctx, actions);
    }

    private void matchSelfDrawAfterDrawingFlower(ScoringContext ctx, Stack<GameAction> actions) {
        if (ctx.isSelfDraw()
                && actions.size() >= 2
                && actions.get(actions.size() - 2) == GameAction.FLOWER) {
            ctx.addScoringPattern(ScoringPattern.SELF_DRAW_AFTER_DRAWING_FLOWER);
        }
    }

    private void matchSelfDrawAfterKong(ScoringContext ctx, Stack<GameAction> actions) {
        if (ctx.isSelfDraw()
                && actions.size() >= 2
                && actions.get(actions.size() - 2) == GameAction.KONG) {
            if (actions.size() >= 4
                    && actions.get(actions.size() - 4) == GameAction.KONG) {
                ctx.addScoringPattern(ScoringPattern.SELF_DRAW_AFTER_TWO_KONGS);
            } else {
                ctx.addScoringPattern(ScoringPattern.SELF_DRAW_AFTER_KONG);
            }
        }
    }

    private void matchRobbingKong(ScoringContext ctx, Stack<GameAction> actions) {
        if (actions.peek() == GameAction.ROBBING_KONG) {
            ctx.addScoringPattern(ScoringPattern.ROBBING_KONG);
        }
    }
}
