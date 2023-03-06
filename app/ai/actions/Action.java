package ai.actions;

import structures.GameState;
import structures.statemachine.State;

public interface Action {
    public State processAction(GameState gameState);
}
