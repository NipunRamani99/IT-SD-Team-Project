package ai.actions;

import structures.GameState;
import structures.statemachine.State;

public interface AiAction {
    public State processAction(GameState gameState);
}
