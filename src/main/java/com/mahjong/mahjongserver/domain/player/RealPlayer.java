package com.mahjong.mahjongserver.domain.player;

import com.mahjong.mahjongserver.domain.board.tile.Tile;
import com.mahjong.mahjongserver.auth.UserAccount;
import com.mahjong.mahjongserver.dto.BoardStateDTO;
import com.mahjong.mahjongserver.messaging.GameEventPublisher;
import com.mahjong.mahjongserver.service.PendingDecisionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class RealPlayer extends Player {
    private final GameEventPublisher gameEventPublisher;
    private final String roomId;
    private final UserAccount account;

    @Autowired
    private PendingDecisionService pendingDecisionService;

    public RealPlayer(String name, UserAccount account, GameEventPublisher gameEventPublisher, String roomId) {
        super(name);
        this.account = account;
        this.gameEventPublisher = gameEventPublisher;
        this.roomId = roomId;
    }

    public RealPlayer(String name, int score, UserAccount account, GameEventPublisher gameEventPublisher,
                      String roomId) {
        super(name, score);
        this.account = account;
        this.gameEventPublisher = gameEventPublisher;
        this.roomId = roomId;
    }

    public UserAccount getAccount() {
        return account;
    }

    @Override
    public boolean decideWin(BoardStateDTO boardState) {
        // tbc
        throw new UnsupportedOperationException("Frontend decision");
    }

    @Override
    public boolean decideWin(Tile tile, BoardStateDTO boardState) {
        // tbc
        throw new UnsupportedOperationException("Frontend decision");
    }

    @Override
    public boolean decideSheung(Tile tile, BoardStateDTO boardState) {
        // tbc
        throw new UnsupportedOperationException("Frontend decision");
    }

    @Override
    public boolean decidePong(Tile tile, BoardStateDTO boardState) {
        // tbc
        throw new UnsupportedOperationException("Frontend decision");
    }

    @Override
    public boolean decideDarkKong(Tile tile, BoardStateDTO boardState) {
        // tbc
        throw new UnsupportedOperationException("Frontend decision");
    }

    @Override
    public boolean decideBrightKong(Tile tile, BoardStateDTO boardState) {
        // tbc
        throw new UnsupportedOperationException("Frontend decision");
    }

    @Override
    public boolean decideBrightKongNoDraw(Tile tile, BoardStateDTO boardState) {
        // tbc
        throw new UnsupportedOperationException("Frontend decision");
    }

    @Override
    public List<Tile> pickSheungCombo(List<List<Tile>> validSheungs) {
        // tbc
        throw new UnsupportedOperationException("Frontend decision");
    }

    @Override
    public Tile pickDiscardTile(BoardStateDTO boardState, List<Tile> discardedTiles) {
        // tbc
        throw new UnsupportedOperationException("Frontend decision");
    }

    @Override
    public Tile pickDiscardTileNoDraw(BoardStateDTO boardState, List<Tile> discardedTiles) {
        // tbc
        throw new UnsupportedOperationException("Frontend decision");
    }
}