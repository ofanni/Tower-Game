package json;

import lombok.Data;

import java.io.Serializable;

@Data
public class GameResults implements Serializable {
    private String playerName;
    private int moveCount;
    private String isCompleted;

    public GameResults(String playerName, int moveCount, String isCompleted) {
        this.playerName = playerName;
        this.moveCount = moveCount;
        this.isCompleted = isCompleted;
    }


}
