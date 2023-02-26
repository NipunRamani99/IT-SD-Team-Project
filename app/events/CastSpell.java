package structures.statemachine;

import akka.actor.Actor;
import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import commands.BasicCommands;
import events.CardClicked;
import events.EventProcessor;
import events.TileClicked;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.TileState;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import utils.Constants;

public class CastSpell implements EventProcessor {

    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message, GameStateMachine gameStateMachine)
    {

        // player.setMana(-1);
        // BasicCommands.playUnitAnimation(out,playerUnit, UnitAnimationType.castSpell); // spell cast ani
        // enemyUnit.setHealth((playerUnit.getSpellDamage * -1));
    }

}
