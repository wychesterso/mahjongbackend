package com.mahjong.mahjongserver.domain.player;

import com.mahjong.mahjongserver.domain.board.tile.Tile;
import com.mahjong.mahjongserver.auth.UserAccount;
import com.mahjong.mahjongserver.service.PendingDecisionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class RealPlayer extends Player {
    private final UserAccount account;

    @Autowired
    private PendingDecisionService pendingDecisionService;

    public RealPlayer(UserAccount account, String name) {
        super(name);
        this.account = account;
    }

    public RealPlayer(UserAccount account, String name, int score) {
        super(name, score);
        this.account = account;
    }

    public UserAccount getAccount() {
        return account;
    }

    @Override
    public boolean decideWin(String boardState) {
        // tbc
        throw new UnsupportedOperationException("Frontend decision");
    }

    @Override
    public boolean decideWin(Tile tile, String boardState) {
        // tbc
        throw new UnsupportedOperationException("Frontend decision");
    }

    @Override
    public boolean decideSheung(Tile tile, String boardState) {
        // tbc
        throw new UnsupportedOperationException("Frontend decision");
    }

    @Override
    public boolean decidePong(Tile tile, String boardState) {
        // tbc
        throw new UnsupportedOperationException("Frontend decision");
    }

    @Override
    public boolean decideDarkKong(Tile tile, String boardState) {
        // tbc
        throw new UnsupportedOperationException("Frontend decision");
    }

    @Override
    public boolean decideBrightKong(Tile tile, String boardState) {
        // tbc
        throw new UnsupportedOperationException("Frontend decision");
    }

    @Override
    public boolean decideBrightKongNoDraw(Tile tile, String boardState) {
        // tbc
        throw new UnsupportedOperationException("Frontend decision");
    }

    @Override
    public List<Tile> pickSheungCombo(List<List<Tile>> validSheungs) {
        // tbc
        throw new UnsupportedOperationException("Frontend decision");
    }

    @Override
    public Tile pickDiscardTile(String boardState, List<Tile> discardedTiles) {
        // tbc
        throw new UnsupportedOperationException("Frontend decision");
    }

    @Override
    public Tile pickDiscardTileNoDraw(String boardState, List<Tile> discardedTiles) {
        // tbc
        throw new UnsupportedOperationException("Frontend decision");
    }
}