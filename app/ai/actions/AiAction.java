package ai.actions;

import structures.GameState;
import structures.statemachine.State;

/**
 * AiAction interface represents an action which will be performed by the AI.
 * Each action will be processed into a corresponding state change.
 */
public interface AiAction {
    public State processAction(GameState gameState);
}
