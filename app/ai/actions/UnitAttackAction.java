package ai.actions;

import structures.GameState;
import structures.basic.Unit;
import structures.statemachine.State;
import structures.statemachine.UnitAttackState;

public class UnitAttackAction implements Action{

    Unit aiUnit = null;
    Unit markedUnit = null;

    public UnitAttackAction(Unit aiUnit, Unit markedUnit) {
        this.aiUnit = aiUnit;
        this.markedUnit = markedUnit;
    }

    @Override
    public State processAction(GameState gameState) {
        if(aiUnit.withinDistance(markedUnit) && aiUnit.canAttack()) {
            return new UnitAttackState(aiUnit, gameState.board.getTile(markedUnit.getPosition()), false, false);
        }
        return null;
    }
}
