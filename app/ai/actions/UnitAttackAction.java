package ai.actions;

import structures.GameState;
import structures.basic.Unit;
import structures.statemachine.State;
import structures.statemachine.UnitAttackState;
import structures.statemachine.UnitSelectedState;

public class UnitAttackAction implements AiAction{

    Unit aiUnit = null;
    Unit markedUnit = null;

    public UnitAttackAction(Unit aiUnit, Unit markedUnit) {
        this.aiUnit = aiUnit;
        this.markedUnit = markedUnit;
    }

    public State processAction(GameState gameState) {
        if(aiUnit.withinDistance(markedUnit) && aiUnit.canAttack()) {
            return new UnitAttackState(aiUnit, gameState.board.getTile(markedUnit.getPosition()), false, false);
        	//return new UnitSelectedState(aiUnit, markedUnit,gameState);
        }
        return null;
    }
}
