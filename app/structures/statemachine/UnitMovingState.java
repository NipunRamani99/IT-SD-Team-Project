package structures.statemachine;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import commands.BasicCommands;
import events.EventProcessor;
import events.UnitMoving;
import events.UnitStopped;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;

public class UnitMovingState implements State {
    Tile targetTile = null;
    Unit selectedUnit = null;

    public UnitMovingState(ActorRef out, Unit selectedUnit, Tile startTile, Tile targetTile, GameState gameState) {
        this.selectedUnit = selectedUnit;
        this.targetTile = targetTile;
        startTile.setUnit(null);
        BasicCommands.moveUnitToTile(out, selectedUnit, targetTile);
    }
    @Override
    public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event, GameStateMachine gameStateMachine) {
            if(event instanceof UnitStopped) {
                targetTile.setUnit(selectedUnit);
                selectedUnit.setPositionByTile(targetTile);
                gameStateMachine.setState(new NoSelectionState());
            }
    }
}
